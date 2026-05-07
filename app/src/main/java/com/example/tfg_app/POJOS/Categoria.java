package com.example.tfg_app.POJOS;

import androidx.annotation.NonNull;

public class Categoria {

    @NonNull
    private String id = "";
    private String nome;
    private String icono;

    public Categoria() {}


    public Categoria(@NonNull String id, String nome, String icono) {
        this.id = id;
        this.nome = nome;
        this.icono = icono;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String geticono() {
        return icono;
    }

    public void seticono(String icono) {
        this.icono = icono;
    }
}
