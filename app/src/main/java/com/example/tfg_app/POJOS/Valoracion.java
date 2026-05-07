package com.example.tfg_app.POJOS;
import androidx.annotation.NonNull;



public class Valoracion {
    @NonNull
    private String id = "";
    private int anuncio_id;
    private int usuario_id;
    private int valoracion;
    private String comentario;

    //Se podria poner fecha del comentario

    public Valoracion() {}

    public Valoracion(@NonNull String id, int anuncio_id, int usuario_id, int valoracion, String comentario) {
        this.id = id;
        this.anuncio_id = anuncio_id;
        this.usuario_id = usuario_id;
        this.valoracion = valoracion;
        this.comentario = comentario;
    }

    @NonNull
    public String getId() {
        return id;
    }


    public void setId(@NonNull String id) {
        this.id = id;
    }

    public int getanuncio_id() {
        return anuncio_id;
    }

    public void setanuncio_id(int anuncio_id) {
        this.anuncio_id = anuncio_id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
