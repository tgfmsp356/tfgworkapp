package com.example.tfg_app.POJOS;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.Date;

public class Anuncio {

    @NonNull
    private String id = "";
    private String id_usuario;
    private String categoria_id;
    private String titulo;
    private String descripcion;
    private double precio_hora;
    private int tiempo_entrega;
    private boolean activo;
    private Date fecha_creacion;

    public Anuncio() {}


    public Anuncio(@NonNull String id, String id_usuario, String categoria_id, String titulo, String descripcion, double precio_hora, int tiempo_entrega, boolean activo, Date fecha_creacion) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.categoria_id = categoria_id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio_hora = precio_hora;
        this.tiempo_entrega = tiempo_entrega;
        this.activo = activo;
        this.fecha_creacion = fecha_creacion;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(String categoria_id) {
        this.categoria_id = categoria_id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio_hora() {
        return precio_hora;
    }

    public void setPrecio_hora(double precio_hora) {
        this.precio_hora = precio_hora;
    }

    public int getTiempo_entrega() {
        return tiempo_entrega;
    }

    public void setTiempo_entrega(int tiempo_entrega) {
        this.tiempo_entrega = tiempo_entrega;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
}

