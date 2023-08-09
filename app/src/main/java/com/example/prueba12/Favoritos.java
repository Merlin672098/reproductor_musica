package com.example.prueba12;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Favoritos extends AppCompatActivity {
    private MainActivity mainActivity;
    private RecyclerView recyclerView;
    public FavAdapter adapter;
    private List<Song> songList;
    //permiso para ver los archivos
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<MaterialButton> controllers;

    //posicion de los botones toca añadir 2 mas uno para bucle y otro para aleatorio
    //el bucle puede ser un random pero hay 2 tipos de random

    TextView nombreCancion;
    private MediaPlayer mediaPlayer;

    private int currentSongIndex = 0;
    private List<Long> favoriteSongIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        recyclerView = findViewById(R.id.rv_favoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteSongIds = (List<Long>) getIntent().getSerializableExtra("favoriteSongIds");

        //inicializamossss
        songList = new ArrayList<>();

        //crear el adaptador con la lista de canciones
        adapter = new FavAdapter(this, songList);
        recyclerView.setAdapter(adapter);

        //cargar las canciones en la lista songList
        loadSongs();
    }

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
        ); if (cursor != null && cursor.moveToFirst()) {
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
                if (esFavorito) {
                    Song song = new Song(id, title, artist, path, esFavorito);
                    songList.add(song);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }



    private void playSong(String filePath) {
        try {
            mediaPlayer.reset(); //reinica
            mediaPlayer.setDataSource(filePath); //establece los datos
            mediaPlayer.prepare();
            mediaPlayer.start();//iniciar
            controllers.get(com.example.prueba12.MainActivity.ci.play).setIconResource(R.drawable.baseline_pause_48);
            nombreCancion.setText(songList.get(0).getTitle()); //muestra el nombre de la cancion
            nombreCancion.setVisibility(View.VISIBLE);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //con esto inicia otra cancion despues de que termina una
                    //solo llamamos al metodo de next :v
                    playNextSong();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        controllers.get(com.example.prueba12.MainActivity.ci.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cambiamos los botones de play a stop
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    controllers.get(com.example.prueba12.MainActivity.ci.play).setIconResource(R.drawable.baseline_play_arrow_48);
                } else {
                    mediaPlayer.start();
                    controllers.get(com.example.prueba12.MainActivity.ci.play).setIconResource(R.drawable.baseline_pause_48);
                }
            }
        });

        controllers.get(com.example.prueba12.MainActivity.ci.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop(); //detiene la canción
                mediaPlayer.reset(); //reinicia el mediaPlayer
                controllers.get(com.example.prueba12.MainActivity.ci.play).setIconResource(R.drawable.baseline_play_arrow_48); // actualiza el ícono del botón de "play/pause"
            }
        });
    }
    private void playNextSong(){
        if (songList.size() > 0) {
            currentSongIndex++;
            if (currentSongIndex >= songList.size()) {
                currentSongIndex = 0;
            }
            Song nextSong = songList.get(currentSongIndex);
            playSong(nextSong.getFilePath());
            nombreCancion.setText(nextSong.getTitle());
        }else{
            Toast.makeText(this, "No hay una siguiente", Toast.LENGTH_SHORT).show();
        }
    }

    private void añadirCancion(){

    }

    private void playPrevSong(){
        if (songList.size() > 0) {
            currentSongIndex--;
            if (currentSongIndex < 0) {
                currentSongIndex = songList.size() - 1;
            }
            Song prevSong = songList.get(currentSongIndex);
            playSong(prevSong.getFilePath());
            nombreCancion.setText(prevSong.getTitle());
        }else{
            Toast.makeText(this, "No hay una anterior", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopSong() {
        MediaPlayer mediaPlayer = new MediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            controllers.get(com.example.prueba12.MainActivity.ci.play).setIconResource(R.drawable.baseline_play_arrow_48);
            nombreCancion.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopSong();
    }
}
