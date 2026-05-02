package com.example.tfgsupongo;

import android.content.Context;

import com.example.tfgsupongo.DAOS.AnuncioDao;

import com.example.tfgsupongo.DAOS.CategoriaDao;
import com.example.tfgsupongo.DAOS.FavoritoDao;
import com.example.tfgsupongo.DAOS.ImagenAnuncioDao;
import com.example.tfgsupongo.DAOS.UsuarioDao;
import com.example.tfgsupongo.DAOS.ValoracionDao;
import com.example.tfgsupongo.POJOS.Anuncio;
import com.example.tfgsupongo.POJOS.Categoria;
import com.example.tfgsupongo.POJOS.Favorito;
import com.example.tfgsupongo.POJOS.ImagenAnuncio;
import com.example.tfgsupongo.POJOS.Usuario;
import com.example.tfgsupongo.POJOS.Valoracion;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Usuario.class, Categoria.class, Anuncio.class, ImagenAnuncio.class, Favorito.class, Valoracion.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract CategoriaDao categoriaDao();
    public abstract AnuncioDao anuncioDao();
    public abstract ImagenAnuncioDao imagenAnuncioDao();
    public abstract FavoritoDao favoritoDao();
    public abstract ValoracionDao valoracionDao();


    private static volatile MyDatabase INSTANCE;

    public static MyDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (MyDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, ""/*Aqui poner nombre de la base de datos*/).build();
                }
            }
        }
        return INSTANCE;
    }
}
