package com.example.tfg_app.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreHelper {
    private static FirebaseFirestore db;

    public static FirebaseFirestore getDb() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    public static Task<DocumentSnapshot> getUsuario(String uid) {
        return getDb().collection("usuarios").document(uid).get();


    }

    public static Task<DocumentSnapshot> getAnuncio(String id) {
        return getDb().collection("anuncios").document(id).get();
    }

    public static Task<DocumentSnapshot> getFavorito(String id) {
        return getDb().collection("favoritos").document(id).get();
    }

    public static Task<DocumentSnapshot> getCategoria(String id) {
        return getDb().collection("categorias").document(id).get();
    }

    public static Task<DocumentSnapshot> getValoracion(String id) {
        return getDb().collection("valoraciones").document(id).get();
    }

    public static Task<DocumentSnapshot> getImagenAnuncio(String id) {
        return getDb().collection("imagenes_anuncio").document(id).get();
    }

    public static Task<QuerySnapshot> getAnuncios() {
        return getDb().collection("anuncios").get();
    }

    public static Task<QuerySnapshot> getCategorias() {
        return getDb().collection("categorias").get();
    }

    public static CollectionReference getCollection(String collectionName) {
        return getDb().collection(collectionName);
    }
}
