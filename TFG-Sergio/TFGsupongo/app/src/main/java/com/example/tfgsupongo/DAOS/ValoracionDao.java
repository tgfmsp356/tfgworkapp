package com.example.tfgsupongo.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfgsupongo.POJOS.Valoracion;

import java.util.List;

@Dao
public interface ValoracionDao {
    @Insert
    void insertarValoracion(Valoracion valoracion);

    @Query("SELECT * FROM valoracion")
    List<Valoracion> getAllValoraciones();
}
