package com.example.tfg_app.POJOS;

import androidx.annotation.NonNull;

public class ImagenAnuncio {

    @NonNull
    private String id = "";
    private String anuncio_id;
    private String url;

    public ImagenAnuncio() {}


    public ImagenAnuncio(@NonNull String id, String anuncio_id, String url) {
        this.id = id;
        this.anuncio_id = anuncio_id;
        this.url = url;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getAnuncio_id() {
        return anuncio_id;
    }

    public void setAnuncio_id(String anuncio_id) {
        this.anuncio_id = anuncio_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
