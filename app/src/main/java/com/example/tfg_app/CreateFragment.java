package com.example.tfg_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.UUID;

public class CreateFragment extends Fragment {

    private TextInputEditText etTitulo, etDescripcion, etPrecio, etTiempo, etCategoria;
    private MaterialButton btnPublicar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        etTitulo = view.findViewById(R.id.et_titulo);
        etDescripcion = view.findViewById(R.id.et_descripcion);
        etPrecio = view.findViewById(R.id.et_precio);
        etTiempo = view.findViewById(R.id.et_tiempo);
        etCategoria = view.findViewById(R.id.et_categoria);
        btnPublicar = view.findViewById(R.id.btn_publicar);

        btnPublicar.setOnClickListener(v -> publicarAnuncio());

        return view;
    }

    private void publicarAnuncio() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String tiempoStr = etTiempo.getText().toString().trim();
        String categoriaId = etCategoria.getText().toString().trim();

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(precioStr) || TextUtils.isEmpty(tiempoStr)) {
            Toast.makeText(getContext(), "Por favor, rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int tiempo = Integer.parseInt(tiempoStr);
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión para publicar", Toast.LENGTH_SHORT).show();
            return;
        }

        String idAnuncio = UUID.randomUUID().toString();
        Anuncio nuevoAnuncio = new Anuncio(
                idAnuncio,
                userId,
                categoriaId.isEmpty() ? "default" : categoriaId,
                titulo,
                descripcion,
                precio,
                tiempo,
                true,
                new Date()
        );

        FirestoreHelper.addAnuncio(nuevoAnuncio).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Anuncio publicado con éxito", Toast.LENGTH_LONG).show();
                limpiarCampos();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            } else {
                Toast.makeText(getContext(), "Error al publicar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCampos() {
        etTitulo.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        etTiempo.setText("");
        etCategoria.setText("");
    }
}
