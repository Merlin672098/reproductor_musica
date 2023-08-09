package com.example.prueba12;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListasReproduccion extends AppCompatActivity {
    private EditText et_nombre;
    private RecyclerView recyclerView;
    private ListasAdapter adapter;
    private List<Listas> listas;

    public void limpiar() {
        //limpieza del formulario
        et_nombre.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas_reproduccion);

        Button btn_añadirLista = findViewById(R.id.btn_añadirLista);
        et_nombre = findViewById(R.id.et_nombre);
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "prueba", null, 1);

        recyclerView = findViewById(R.id.rv_listas);
        listas = new ArrayList<>();
        adapter = new ListasAdapter(this, listas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //boton para añadir listas de reproducción
        btn_añadirLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreLista = et_nombre.getText().toString();
                if (nombreLista.length() > 0) {
                    SQLiteDatabase bd = admin.getWritableDatabase();
                    ContentValues registro = new ContentValues();
                    registro.put("nombre_lista", nombreLista);
                    bd.insert("lista_reproduccion", null, registro);
                    Toast.makeText(ListasReproduccion.this, "Se añadio el nombre a la lista de reproducción", Toast.LENGTH_LONG).show();
                    limpiar();
                    bd.close();

                    // Actualizar la lista de reproducción y notificar al adaptador
                    listas.clear();
                    listas.addAll(obtenerListasDeReproduccion(admin));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ListasReproduccion.this, "No ingreso el nombre de la lista", Toast.LENGTH_LONG).show();
                }
            }
        });

        //obtenemos los datosss
        listas.addAll(obtenerListasDeReproduccion(admin));
        adapter.notifyDataSetChanged();
    }

    private List<Listas> obtenerListasDeReproduccion(AdminSQLiteOpenHelper admin) {
        List<Listas> listas = new ArrayList<>();

        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("SELECT id, nombre_lista FROM lista_reproduccion", null);
        int columnIndexId = cursor.getColumnIndex("id");
        int columnIndexNombre = cursor.getColumnIndex("nombre_lista");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(columnIndexId);
            String nombre = cursor.getString(columnIndexNombre);
            listas.add(new Listas(id, nombre));
        }

        cursor.close();
        bd.close();

        return listas;
    }
}



