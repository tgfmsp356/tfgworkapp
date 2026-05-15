package com.example.tfg_app.POJOS;
import androidx.annotation.NonNull;





public class Valoracion {
    
    @NonNull
    private String id = "";
    private int aniuncio_id;
    private int usuario_id;
    private int valoracion;
    private String comentario;

    

    public Valoracion() {}

    public Valoracion(@NonNull String id, int aniuncio_id, int usuario_id, int valoracion, String comentario) {
        this.id = id;
        this.aniuncio_id = aniuncio_id;
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

    public int getAniuncio_id() {
        return aniuncio_id;
    }

    public void setAniuncio_id(int aniuncio_id) {
        this.aniuncio_id = aniuncio_id;
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
