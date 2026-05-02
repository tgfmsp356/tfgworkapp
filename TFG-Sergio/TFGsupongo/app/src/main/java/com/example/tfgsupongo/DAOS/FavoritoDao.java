package com.example.tfgsupongo.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfgsupongo.POJOS.Favorito;

@Dao
public interface FavoritoDao {
    @Insert
    void insertarFavorito(Favorito favorito);

    @Query("SELECT * FROM favorito")
    void getAllFavoritos();
}
