package com.example.myrssadrian.ui.juegos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class JuegosDBController extends SQLiteOpenHelper {
    //hacemos estaticas las variables a utilizar en la bbdd para asi no dar lugar a equivocacion
    public static final String TABLA = "juegos";
    public static final String ID = "id";
    public static final String NOMBRE = "nombre";
    public static final String PLATAFORMA = "plataforma";
    public static final String IMAGEN = "imagen";
    public static final String IMAGEN2 = "imagen2";
    public static final String FECHA = "fecha";
    public static final String PRECIO = "precio";
    public static final String SQLCREATE = "CREATE TABLE juegos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, plataforma TEXT, imagen TEXT, fecha TEXT, precio FLOAT)";

    public JuegosDBController(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLCREATE);//ejecutamos la bbdd
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS juegos");

        //se crea la nueva base de datos
        onCreate(db);
    }
}