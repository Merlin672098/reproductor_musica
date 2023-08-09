package com.example.prueba12;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListasAdapter extends RecyclerView.Adapter<ListasAdapter.ViewHolder> {

    private Context context;
    private List<Listas> listas;

    public ListasAdapter(Context context, List<Listas> listas) {
        this.context = context;
        this.listas = listas;
    }

    public void setListas(List<Listas> listas) {
        this.listas = listas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listas lista = listas.get(position);
        holder.nombreLista.setText(lista.getTitle());
        String nombreLista = lista.getTitle();
        long listaId = lista.getId();

        holder.imageLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //abrimos la vista "generica" donde le pasamos los datos que sacamos del select
                Intent intent = new Intent(context, ListasGenericas.class);
                intent.putExtra("listaId", listaId);
                intent.putExtra("nombreLista", nombreLista);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreLista;
        ImageButton imageLista;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreLista = itemView.findViewById(R.id.nombreLista);
            imageLista = itemView.findViewById(R.id.btn_lista);
        }
    }
}

