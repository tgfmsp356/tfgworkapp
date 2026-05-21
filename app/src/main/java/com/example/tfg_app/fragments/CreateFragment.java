package com.example.tfg_app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.POJOS.Categoria;
import com.example.tfg_app.R;
import com.example.tfg_app.adapters.FotoPreviewAdapter;
import com.example.tfg_app.utils.CloudinaryHelper;
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
    private LinearLayout btnAddPhoto;
    private RecyclerView rvFotosPreview;

    // Categorías de Firestore
    private final List<Categoria> listaCategorias = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    // Fotos elegidas (todavía sin subir)
    private final List<Uri> fotosSeleccionadas = new ArrayList<>();
    private FotoPreviewAdapter fotoAdapter;

    // URLs ya subidas a Cloudinary
    private final List<String> urlsSubidas = new ArrayList<>();
    private int fotosSubidas = 0; // contador para saber cuándo terminan todas

    // Selector de imágenes (permite varias)
    private final ActivityResultLauncher<Intent> pickImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    // Si seleccionó varias
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                            fotosSeleccionadas.add(uri);
                        }
                    }
                    // Si seleccionó solo una
                    else if (result.getData().getData() != null) {
                        fotosSeleccionadas.add(result.getData().getData());
                    }
                    actualizarPreview();
                }
            });

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
        btnAddPhoto = view.findViewById(R.id.btn_add_photo);
        rvFotosPreview = view.findViewById(R.id.rv_fotos_preview);

        // Spinner de categorías
        spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spCategoria.setAdapter(spinnerAdapter);
        cargarCategorias();

        // RecyclerView de previsualización de fotos
        fotoAdapter = new FotoPreviewAdapter(fotosSeleccionadas, posicion -> {
            fotosSeleccionadas.remove(posicion);
            actualizarPreview();
        });
        rvFotosPreview.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFotosPreview.setAdapter(fotoAdapter);

        // Botón de añadir fotos
        btnAddPhoto.setOnClickListener(v -> abrirGaleria());

        btnPublicar.setOnClickListener(v -> publicarAnuncio());

        return view;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // permitir varias
        pickImagesLauncher.launch(intent);
    }

    private void actualizarPreview() {
        fotoAdapter.notifyDataSetChanged();
        // Mostrar u ocultar el RecyclerView según haya fotos o no
        if (fotosSeleccionadas.isEmpty()) {
            rvFotosPreview.setVisibility(View.GONE);
        } else {
            rvFotosPreview.setVisibility(View.VISIBLE);
        }
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

        if (listaCategorias.isEmpty()) {
            Toast.makeText(getContext(), "No hay categorías disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionSeleccionada = spCategoria.getSelectedItemPosition();
        if (posicionSeleccionada < 0 || posicionSeleccionada >= listaCategorias.size()) {
            Toast.makeText(getContext(), "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            return;
        }
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
                idAnuncio, userId, categoriaId, titulo, descripcion, precio, tiempo, true, new Date());

        btnPublicar.setEnabled(false);

        // Si no hay fotos, guardar directamente
        if (fotosSeleccionadas.isEmpty()) {
            guardarAnuncio(nuevoAnuncio);
            return;
        }

        // Si hay fotos, subirlas todas primero
        Toast.makeText(getContext(), "Subiendo fotos...", Toast.LENGTH_SHORT).show();
        urlsSubidas.clear();
        fotosSubidas = 0;
        // Rellenamos la lista con huecos para mantener el orden
        for (int i = 0; i < fotosSeleccionadas.size(); i++) {
            urlsSubidas.add(null);
        }

        for (int i = 0; i < fotosSeleccionadas.size(); i++) {
            final int indice = i;
            CloudinaryHelper.uploadImage(fotosSeleccionadas.get(i), "anuncios", new CloudinaryHelper.UploadResultCallback() {
                @Override
                public void onSuccess(String url) {
                    urlsSubidas.set(indice, url);
                    fotosSubidas++;
                    // ¿Han terminado todas?
                    if (fotosSubidas == fotosSeleccionadas.size()) {
                        // Quitar posibles nulos por si alguna falló
                        List<String> finales = new ArrayList<>();
                        for (String u : urlsSubidas) {
                            if (u != null) finales.add(u);
                        }
                        nuevoAnuncio.setImagenes(finales);
                        guardarAnuncio(nuevoAnuncio);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    fotosSubidas++;
                    if (fotosSubidas == fotosSeleccionadas.size()) {
                        List<String> finales = new ArrayList<>();
                        for (String u : urlsSubidas) {
                            if (u != null) finales.add(u);
                        }
                        nuevoAnuncio.setImagenes(finales);
                        guardarAnuncio(nuevoAnuncio);
                    }
                    Toast.makeText(getContext(), "Una foto falló: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void guardarAnuncio(Anuncio anuncio) {
        FirestoreHelper.addAnuncio(anuncio).addOnCompleteListener(task -> {
            btnPublicar.setEnabled(true);
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
        fotosSeleccionadas.clear();
        actualizarPreview();
        if (spCategoria.getAdapter() != null && spCategoria.getAdapter().getCount() > 0) {
            spCategoria.setSelection(0);
        }
    }
}