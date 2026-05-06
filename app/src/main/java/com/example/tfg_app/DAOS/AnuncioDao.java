package com.example.tfg_app.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfg_app.POJOS.*;

import java.util.List;

@Dao
public interface AnuncioDao {
    @Insert
    void insertarAnuncio(Anuncio anuncio);

    @Query("SELECT * FROM anuncio")
    List<Anuncio> getAllAnuncios();

    @Query("SELECT * FROM anuncio where categoria_id = :categoria_id")
    List<Anuncio> getAnunciosByCategoria(int categoria_id);
}