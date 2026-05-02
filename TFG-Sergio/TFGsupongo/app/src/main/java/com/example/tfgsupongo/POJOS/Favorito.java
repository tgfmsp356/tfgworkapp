package com.example.tfgsupongo.POJOS;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorito")
public class Favorito {

    @PrimaryKey
    @NonNull
    private String id = "";
    private String usuario_id;
    private String anuncio_id;

    public Favorito() {}


    public Favorito(@NonNull String id, String usuario_id, String anuncio_id) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.anuncio_id = anuncio_id;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(String usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getAnuncio_id() {
        return anuncio_id;
    }

    public void setAnuncio_id(String anuncio_id) {
        this.anuncio_id = anuncio_id;
    }
}
