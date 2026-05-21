package com.example.tfg_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.POJOS.Categoria;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateFragment extends Fragment {

    private TextInputEditText etTitulo, etDescripcion, etPrecio, etTiempo;
    private Spinner spCategoria;
    private MaterialButton btnPublicar;

    // Categorías cargadas desde Firestore
    private final List<Categoria> listaCategorias = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        etTitulo = view.findViewById(R.id.et_titulo);
        etDescripcion = view.findViewById(R.id.et_descripcion);
        etPrecio = view.findViewById(R.id.et_precio);
        etTiempo = view.findViewById(R.id.et_tiempo);
        spCategoria = view.findViewById(R.id.spinner_categoria);
        btnPublicar = view.findViewById(R.id.btn_publicar);

        // Adaptador del spinner (de momento vacío, se rellena al cargar Firestore)
        spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spCategoria.setAdapter(spinnerAdapter);

        cargarCategorias();

        btnPublicar.setOnClickListener(v -> publicarAnuncio());

        return view;
    }

    private void cargarCategorias() {
        FirestoreHelper.getCategorias().addOnSuccessListener(query -> {
            listaCategorias.clear();
            List<String> nombres = new ArrayList<>();
            for (DocumentSnapshot doc : query) {
                Categoria cat = doc.toObject(Categoria.class);
                if (cat != null) {
                    cat.setId(doc.getId());
                    listaCategorias.add(cat);
                    nombres.add(cat.getName() != null ? cat.getName() : "(sin nombre)");
                }
            }
            spinnerAdapter.clear();
            spinnerAdapter.addAll(nombres);
            spinnerAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show());
    }

    private void publicarAnuncio() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String tiempoStr = etTiempo.getText().toString().trim();

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descripcion)
                || TextUtils.isEmpty(precioStr) || TextUtils.isEmpty(tiempoStr)) {
            Toast.makeText(getContext(), "Por favor, rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que haya categorías y una seleccionada
        if (listaCategorias.isEmpty()) {
            Toast.makeText(getContext(), "No hay categorías disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionSeleccionada = spCategoria.getSelectedItemPosition();
        if (posicionSeleccionada < 0 || posicionSeleccionada >= listaCategorias.size()) {
            Toast.makeText(getContext(), "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }
        // Guardamos el ID real de la categoría, no el nombre
        String categoriaId = listaCategorias.get(posicionSeleccionada).getId();

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
                categoriaId,
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
        if (spCategoria.getAdapter() != null && spCategoria.getAdapter().getCount() > 0) {
            spCategoria.setSelection(0);
        }
    }
}