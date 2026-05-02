package com.example.tfgsupongo.DAOS;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tfgsupongo.POJOS.Usuario;

import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    void insertarUsuario(Usuario usuario);

    @Query("SELECT * FROM usuario")
    List<Usuario> getAllUsuarios();
}
