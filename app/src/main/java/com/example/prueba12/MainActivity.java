package com.example.prueba12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public SongAdapter adapter;
    private List<Song> songList;
    //permiso para ver los archivos
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<MaterialButton> controllers;
    private List<Long> favoriteSongIds = new ArrayList<>();

    //posicion de los botones toca añadir 2 mas uno para bucle y otro para aleatorio
    //el bucle puede ser un random pero hay 2 tipos de random
    public static class ci {
        static final int prev = 0;
        static final int stop = 1;
        static final int play = 2;
        static final int next = 3;
    }
    TextView nombreCancion;
    private MediaPlayer mediaPlayer;

    private int currentSongIndex = 0;

    private Song currentSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //lista de botones
        controllers = Arrays.asList(
                findViewById(R.id.prev),
                findViewById(R.id.stop),
                findViewById(R.id.play),
                findViewById(R.id.next)
        );
        nombreCancion = findViewById(R.id.nombreCancion);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //botones para diferentes vistas falta el boton para visualizar la cancion solo
        //podemos hacer un onclick quiza pero esto 1ro :v
        //podemos implementar onclick en un text view ?
        Button btn_favoritos = findViewById(R.id.btn_favoritos);
        Button btn_tendencias = findViewById(R.id.btn_tendencias);
        Button btn_listas = findViewById(R.id.btn_listas);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "prueba", null, 1);
        songList = new ArrayList<>();
        boolean esFavorito = false;
        adapter = new SongAdapter(this, songList, new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    stopSong();
                }
                //reset antes de una nueva canción
                mediaPlayer.reset();

                playSong(song.getFilePath());
                nombreCancion.setText(song.getTitle());
                currentSong = song;
                currentSongIndex = songList.indexOf(currentSong);
            }
        }, new SongAdapter.OnAddToFavoritesListener() {
            @Override
            public void onAddToFavoritesWithStatus(long songId, String selectedName, String selectedArtist, String selectedDate, boolean esFavorito) {
                for (Song song : songList) {
                    if (song.getId() == songId) {
                        // Verificar si el estado actual es diferente al nuevo estado
                        if (song.getEsFavorite() != esFavorito) {
                            // Actualizar el estado de esFavorito en la canción
                            song.setEsFavorite(esFavorito);
                            String mensaje = "El estado de 'esFavorito' para la canción con ID " + songId + " ha sido cambiado a " + esFavorito;
                            showToast(mensaje);
                            //agregamos el id a mi lista de idsfavoritosss
                            if (esFavorito) {
                                favoriteSongIds.add(songId);
                                SQLiteDatabase bd = admin.getWritableDatabase();
                                ContentValues registro = new ContentValues();
                                registro.put("id_favorito", songId);
                                registro.put("title", selectedName);
                                registro.put("artista", selectedArtist);
                                registro.put("data", selectedDate);
                                bd.insert("favoritos", null, registro);
                                Toast.makeText(MainActivity.this, "Se añadió el nombre a favoritos", Toast.LENGTH_LONG).show();
                                bd.close();

                                // Actualizar la lista de reproducción y notificar al adaptador
                                //listas.clear();
                                //listas.addAll(obtenerListasDeReproduccion(admin));
                                //adapter.notifyDataSetChanged();
                            } else {
                                favoriteSongIds.remove(songId);
                            }
                        } else {
                            String mensaje = "El estado de 'esFavorito' para la canción con ID " + songId + " ya está en " + esFavorito + ". No se realizaron cambios.";
                            showToast(mensaje);
                        }
                        break;
                    }
                }
                // Notificar al adaptador sobre los cambios en los datos
                adapter.notifyDataSetChanged();
            }
        }, esFavorito);


        //redirigimos a la vista de favoritos
        btn_favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Favoritos.class);
                intent.putExtra("favoriteSongIds", new ArrayList<>(favoriteSongIds));
                startActivity(intent);
            }
        });

        //redirigimos a la vista de favoritos
        btn_listas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ListasReproduccion.class);
                startActivity(i);
            }
        });
        //redirigimos a la vista de favoritos
        btn_tendencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Tendencias.class);
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadSongs();
        }
    }
    private void showToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public List<Long> getFavoriteSongIds() {
        return favoriteSongIds;
    }
    //API MediaStore para cargar canciones del teléfono como una base de datosss
    private void loadSongs() {
        mediaPlayer = new MediaPlayer();
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
        );

        //botón de play
        controllers.get(ci.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList.size() > 0) {
                    playSong(songList.get(currentSongIndex).getFilePath());
                }
            }
        });
        //botón de pause
        controllers.get(ci.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSong();
            }
        });
        //botón de siguiente
        controllers.get(ci.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });
        //botón de anterior
        controllers.get(ci.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevSong();
            }
        });
        //datos de las canciones que podemos usar :v
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                long id = cursor.getLong(idIndex);
                String title = cursor.getString(titleIndex);
                String artist = cursor.getString(artistIndex);
                String path = cursor.getString(dataIndex);
                boolean esFavorito = favoriteSongIds.contains(id);
                Song song = new Song(id, title, artist, path, esFavorito);
                songList.add(song);
            } while (cursor.moveToNext());

            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }
    //metodo para agregar un ID de cancion a la lista de favoritos
    public void addFavoriteSongId(long songId) {
        if (!favoriteSongIds.contains(songId)) {
            favoriteSongIds.add(songId);
        }
    }

    //metodo para eliminar un ID de cancion de la lista de favoritos
    public void removeFavoriteSongId(long songId) {
        favoriteSongIds.remove(songId);
    }


    private void playSong(String filePath) {
        try {
            mediaPlayer.reset(); //reinica
            mediaPlayer.setDataSource(filePath); //establece los datos
            mediaPlayer.prepare();
            mediaPlayer.start();//iniciar
            controllers.get(ci.play).setIconResource(R.drawable.baseline_pause_48);
            nombreCancion.setText(songList.get(0).getTitle()); //muestra el nombre de la cancion
            nombreCancion.setVisibility(View.VISIBLE);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //con esto inicia otra cancion despues de que termina una
                    //solo llamamos al metodo de next para pasar de cancion :V
                    playNextSong();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        controllers.get(ci.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cambiamos los botones de play a stop
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    controllers.get(ci.play).setIconResource(R.drawable.baseline_play_arrow_48);
                } else {
                    mediaPlayer.start();
                    controllers.get(ci.play).setIconResource(R.drawable.baseline_pause_48);
                }
            }
        });

        controllers.get(ci.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop(); //detiene la canción
                mediaPlayer.reset(); //reinicia el mediaPlayer
                controllers.get(ci.play).setIconResource(R.drawable.baseline_play_arrow_48);//actualizamos el icono
            }
        });
    }
    private void playNextSong() {
        if (songList.size() > 0) {
            int previousSongIndex = currentSongIndex;
            currentSongIndex++;
            if (currentSongIndex >= songList.size()) {
                currentSongIndex = 0;
            }
            Song nextSong = songList.get(currentSongIndex);
            playSong(nextSong.getFilePath());
            nombreCancion.setText(nextSong.getTitle());
            //cambiamos el color de la cancion actual que esta sonando
            adapter.setSelected(currentSongIndex);
            adapter.setCurrentPlaying(currentSongIndex);
            //notificamos al adaptador que se cambio de cancion
            adapter.notifyItemChanged(previousSongIndex);
            adapter.notifyItemChanged(currentSongIndex);
        } else {
            Toast.makeText(this, "No hay una siguiente", Toast.LENGTH_SHORT).show();
        }
    }



    private void playPrevSong() {
        if (songList.size() > 0) {
            int previousSongIndex = currentSongIndex;
            currentSongIndex--;
            if (currentSongIndex < 0) {
                currentSongIndex = songList.size() - 1;
            }
            Song prevSong = songList.get(currentSongIndex);
            playSong(prevSong.getFilePath());
            nombreCancion.setText(prevSong.getTitle());
            //cambiamos el color de la cancion actual que esta sonando
            adapter.setSelected(currentSongIndex);
            adapter.setCurrentPlaying(currentSongIndex);
            //notificamos al adaptador que se cambio de cancion
            adapter.notifyItemChanged(previousSongIndex);
            adapter.notifyItemChanged(currentSongIndex);
        } else {
            Toast.makeText(this, "No hay una anterior", Toast.LENGTH_SHORT).show();
        }
    }


    private void stopSong() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            controllers.get(ci.play).setIconResource(R.drawable.baseline_play_arrow_48);
            nombreCancion.setVisibility(View.INVISIBLE);
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        stopSong();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "Permiso denegado para leer el almacenamiento externo.", Toast.LENGTH_SHORT).show();
            }
        }
    }




}
