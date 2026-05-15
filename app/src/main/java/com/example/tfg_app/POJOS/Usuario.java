package com.example.tfg_app.POJOS;

import androidx.annotation.NonNull;

public class Usuario {

    @NonNull
    private String id = "";
    private String firebase_uid;
    private String nombre;
    private String nombre_usuario;
    private String email;
    private String foto_perfil;
    private String descripcion;
    private String fecha_registro;

    public Usuario() {}

    public Usuario(@NonNull String id, String firebase_uid, String nombre, String nombre_usuario, String email, String foto_perfil, String descripcion, String fecha_registro) {
        this.id = id;
        this.firebase_uid = firebase_uid;
        this.nombre = nombre;
        this.nombre_usuario = nombre_usuario;
        this.email = email;
        this.foto_perfil = foto_perfil;
        this.descripcion = descripcion;
        this.fecha_registro = fecha_registro;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getFirebase_uid() {
        return firebase_uid;
    }

    public void setFirebase_uid(String firebase_uid) {
        this.firebase_uid = firebase_uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto_perfil() {
        return foto_perfil;
    }

    public void setFoto_perfil(String foto_perfil) {
        this.foto_perfil = foto_perfil;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(String fecha_registro) {
        this.fecha_registro = fecha_registro;
    }
}
