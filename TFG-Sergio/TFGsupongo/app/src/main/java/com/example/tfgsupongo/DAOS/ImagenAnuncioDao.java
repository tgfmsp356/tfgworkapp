package com.example.tfgsupongo.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfgsupongo.POJOS.ImagenAnuncio;

import java.util.List;

@Dao
public interface ImagenAnuncioDao {
    @Insert
    void insertarImagenAnuncio(ImagenAnuncio imagenAnuncio);

    @Query("SELECT * FROM imagen_anuncio")
    List<ImagenAnuncio> getAllImagenesAnuncio();
}
