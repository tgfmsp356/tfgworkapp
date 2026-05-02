package com.example.tfgsupongo.POJOS;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categoria")
public class Categoria {
    @PrimaryKey
    @NonNull
    private String id = "";
    private String nome;
    private String incono;

    public Categoria() {}


    public Categoria(@NonNull String id, String nome, String incono) {
        this.id = id;
        this.nome = nome;
        this.incono = incono;
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

    public String getIncono() {
        return incono;
    }

    public void setIncono(String incono) {
        this.incono = incono;
    }
}
