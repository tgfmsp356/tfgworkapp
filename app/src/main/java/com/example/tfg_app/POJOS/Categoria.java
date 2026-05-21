package com.example.tfg_app.POJOS;

import androidx.annotation.NonNull;

public class Categoria {

    @NonNull
    private String id = "";
    private String name;
    private String icono;

    public Categoria() {}


    public Categoria(@NonNull String id, String name, String icono) {
        this.id = id;
        this.name = name;
        this.icono = icono;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }
}
