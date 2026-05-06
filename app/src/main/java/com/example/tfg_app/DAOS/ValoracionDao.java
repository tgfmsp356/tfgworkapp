package com.example.tfg_app.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfg_app.POJOS.*;

import java.util.List;

@Dao
public interface ValoracionDao {
    @Insert
    void insertarValoracion(Valoracion valoracion);

    @Query("SELECT * FROM valoracion")
    List<Valoracion> getAllValoraciones();
}
