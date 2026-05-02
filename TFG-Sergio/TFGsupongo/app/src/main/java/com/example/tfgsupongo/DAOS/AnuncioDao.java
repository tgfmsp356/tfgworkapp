package com.example.tfgsupongo.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.tfgsupongo.POJOS.Anuncio;
import com.example.tfgsupongo.POJOS.Categoria;

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