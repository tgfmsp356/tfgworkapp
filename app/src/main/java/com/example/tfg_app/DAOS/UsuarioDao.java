package com.example.tfg_app.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfg_app.POJOS.*;

import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    void insertarUsuario(Usuario usuario);

    @Query("SELECT * FROM usuario")
    List<Usuario> getAllUsuarios();
}
