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


    public static Task<DocumentSnapshot> getCategoria(String id) {
        return getDb().collection("categorias").document(id).get();
    }


    public static Task<QuerySnapshot> getAnuncios() {
        return getDb().collection("anuncios").get();
    }

    public static Task<QuerySnapshot> getCategorias() {
        return getDb().collection("categorias").get();
    }

    public static Task<Void> addAnuncio(com.example.tfg_app.POJOS.Anuncio anuncio) {
        return getDb().collection("anuncios").document(anuncio.getId()).set(anuncio);
    }

    public static Task<Void> addUsuario(com.example.tfg_app.POJOS.Usuario usuario) {
        return getDb().collection("usuarios").document(usuario.getId()).set(usuario);
    }

    public static CollectionReference getCollection(String collectionName) {
        return getDb().collection(collectionName);
    }

    public static Task<Void> updateUsuarioField(String uid, String field, Object value) {
        return getDb().collection("usuarios").document(uid).update(field, value);
    }

    public static Task<Void> deleteAnuncio(String anuncioId) {
        return getDb().collection("anuncios").document(anuncioId).delete();
    }

    public static Task<QuerySnapshot> getAnunciosPorUsuario(String uid) {
        return getDb().collection("anuncios").whereEqualTo("id_usuario", uid).get();
    }
}
