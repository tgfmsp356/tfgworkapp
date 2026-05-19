package com.example.tfg_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tfg_app.POJOS.Usuario;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvFullName, tvEmail, tvDescription, tvRegisterDate;
    private FirebaseAuth auth;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvDescription = view.findViewById(R.id.tv_description);
        tvRegisterDate = view.findViewById(R.id.tv_register_date);

        auth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            obtenerDatosUsuario(uid);
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerDatosUsuario(String uid) {
        FirestoreHelper.getUsuario(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        if (usuario != null) {
                            mostrarDatos(usuario);
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void mostrarDatos(Usuario usuario) {
        if (usuario.getNombre_usuario() != null) tvUsername.setText("@" + usuario.getNombre_usuario());
        if (usuario.getNombre() != null) tvFullName.setText(usuario.getNombre());
        if (usuario.getEmail() != null) tvEmail.setText(usuario.getEmail());
        if (usuario.getDescripcion() != null && !usuario.getDescripcion().isEmpty()) {
            tvDescription.setText(usuario.getDescripcion());
        }
        if (usuario.getFecha_registro() != null) {
            tvRegisterDate.setText("Miembro desde: " + usuario.getFecha_registro());
        }
    }
}
