package com.example.prueba12;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE lista_reproduccion (id_lista INTEGER PRIMARY KEY AUTOINCREMENT, nombre_lista TEXT)");
        db.execSQL("CREATE TABLE relacion_cancion_lista (id_relacion INTEGER PRIMARY KEY AUTOINCREMENT, id_cancion INTEGER, id_lista INTEGER, FOREIGN KEY(id_lista) REFERENCES lista_reproduccion(id_lista))");
        db.execSQL("CREATE TABLE favoritos (id_favorito INTEGER PRIMARY KEY, title TEXT, artista TEXT, data TEXT)");
    }


    //manejamos las versiones de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void insertarCancion(String nombreLista, long idCancion) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("nombre_lista", nombreLista);
        db.insert("lista_reproduccion", null, contentValues);

        ContentValues relacionValues = new ContentValues();
        relacionValues.put("id_cancion", idCancion);
        relacionValues.put("id_lista", getLastInsertedId(db));
        db.insert("relacion_cancion_lista", null, relacionValues);
    }

    private long getLastInsertedId(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        long lastId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
            cursor.close();
        }
        return lastId;
    }
}
