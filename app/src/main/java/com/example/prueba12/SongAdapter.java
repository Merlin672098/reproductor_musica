package com.example.prueba12;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private OnItemClickListener listener;
    //cancionActualSonando
    private int currentPlaying = 0;
    private int selected = -1;
    private OnAddToFavoritesListener addToFavoritesListener;
    private boolean esFavorito;

    public SongAdapter(Context context, List<Song> songList, OnItemClickListener listener, OnAddToFavoritesListener addToFavoritesListener, boolean esFavorito) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
        this.addToFavoritesListener = addToFavoritesListener;
        this.esFavorito = esFavorito;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(holder.getAdapterPosition());
        holder.songTitle.setText(song.getTitle());
        holder.artistName.setText(song.getArtist());

        if (holder.getAdapterPosition() == selected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        if (holder.getAdapterPosition() == currentPlaying) {
            holder.itemView.setBackgroundColor(Color.GRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }


        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.menuButton, holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(song);
                selected = holder.getAdapterPosition();
                currentPlaying = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }



    public void setSelected(int position) {
        selected = position;
    }

    public void setCurrentPlaying(int position) {
        currentPlaying = position;
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView artistName;
        ImageButton menuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            artistName = itemView.findViewById(R.id.artist_name);
            menuButton = itemView.findViewById(R.id.btn_lista);
        }
    }

    //mostrar menu de opciones
    private void showPopupMenu(ImageButton menuButton, int position) {
        PopupMenu popupMenu = new PopupMenu(context, menuButton);
        popupMenu.inflate(R.menu.menu_opciones);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_option1:
                        Song selectedSong = songList.get(position);
                        long selectedId = selectedSong.getId();
                        String selectedName = selectedSong.getTitle();
                        String selectedArtist = selectedSong.getArtist();
                        String selectedDate = selectedSong.getFilePath();
                        addToFavoritesListener.onAddToFavoritesWithStatus(selectedId, selectedName, selectedArtist, selectedDate, true);

                        Toast.makeText(context, "Cancion agregada a favoritos", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.menu_option2:
                        //Añadir a una lista

                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public interface OnAddToFavoritesListener {
        void onAddToFavoritesWithStatus(long songId,
                                        String selectedName,
                                        String selectedArtist,
                                        String selectedDate,
                                        boolean esFavorito);
    }



    public interface OnItemClickListener {
        void onItemClick(Song song);
    }
}
