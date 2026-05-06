package com.example.tfg_app.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfg_app.POJOS.*;

import java.util.List;

@Dao
public interface ImagenAnuncioDao {
    @Insert
    void insertarImagenAnuncio(ImagenAnuncio imagenAnuncio);

    @Query("SELECT * FROM imagen_anuncio")
    List<ImagenAnuncio> getAllImagenesAnuncio();
}
