package com.example.tfg_app.POJOS;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<String> imagenes = new ArrayList<>();

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


    public String getCategoria_id() {
        return categoria_id;
    }


    public String getTitulo() {
        return titulo;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public double getPrecio_hora() {
        return precio_hora;
    }


    public Date getFecha_creacion() {
        return fecha_creacion;
    }


    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }
}

