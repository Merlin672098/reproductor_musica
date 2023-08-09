package com.example.prueba12;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Tendencias extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendencias);


        ImageButton btn_recomendados = findViewById(R.id.btn_recomendados);

        //redirigimos a la vista de favoritos
        btn_recomendados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Tendencias.this,Recomendados.class);
                startActivity(i);
            }
        });
    }
}