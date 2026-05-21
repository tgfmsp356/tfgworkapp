package com.example.tfg_app.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.POJOS.Categoria;
import com.example.tfg_app.R;
import com.example.tfg_app.adapters.AnuncioAdapter;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvAnuncios;
    private AnuncioAdapter adapter;
    private List<Anuncio> listaAnuncios;
    private TextView tvHomeTitle;
    private EditText etSearch;
    private ImageView ivFiltros;

    // Categorías cargadas desde Firestore (para el desplegable de filtros)
    private final List<Categoria> listaCategorias = new ArrayList<>();

    // Estado de los filtros actualmente aplicados
    private String filtroCategoriaId = null;
    private Double filtroPrecioMin = null;
    private Double filtroPrecioMax = null;
    private int filtroOrden = AnuncioAdapter.ORDEN_RECIENTES;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvAnuncios = view.findViewById(R.id.rv_anuncios);
        tvHomeTitle = view.findViewById(R.id.tv_home_title);
        etSearch = view.findViewById(R.id.et_search);
        ivFiltros = view.findViewById(R.id.iv_filtros);

        listaAnuncios = new ArrayList<>();
        adapter = new AnuncioAdapter(listaAnuncios);

        rvAnuncios.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvAnuncios.setAdapter(adapter);

        adapter.setOnAnuncioClickListener(anuncio -> abrirDetalle(anuncio));

        // Filtrado en vivo mientras se escribe
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarTodosLosFiltros();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Botón de filtros
        ivFiltros.setOnClickListener(v -> mostrarDialogoFiltros());

        return view;
    }

    private void abrirDetalle(Anuncio anuncio) {
        AnuncioDetalleFragment detalle = AnuncioDetalleFragment.newInstance(anuncio.getId());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detalle)
                .addToBackStack(null)   // permite volver atrás con el botón de retroceso
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtenerNombreUsuario();
        cargarCategorias();
        cargarAnuncios();
    }

    private void obtenerNombreUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirestoreHelper.getUsuario(user.getUid()).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nombre = documentSnapshot.getString("nombre");
                    if (nombre != null) {
                        tvHomeTitle.setText("Elegidos para " + nombre);
                    }
                }
            });
        }
    }

    private void cargarCategorias() {
        FirestoreHelper.getCategorias().addOnSuccessListener(query -> {
            listaCategorias.clear();
            for (DocumentSnapshot doc : query) {
                Categoria cat = doc.toObject(Categoria.class);
                if (cat != null) {
                    cat.setId(doc.getId());
                    listaCategorias.add(cat);
                }
            }
        });
    }

    private void cargarAnuncios() {
        FirestoreHelper.getAnuncios().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Anuncio> nuevaLista = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Anuncio anuncio = document.toObject(Anuncio.class);
                anuncio.setId(document.getId());
                nuevaLista.add(anuncio);
            }
            adapter.actualizarLista(nuevaLista);
            aplicarTodosLosFiltros();
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar anuncios", Toast.LENGTH_SHORT).show());
    }

    /**
     * Llama al adapter con el texto del buscador + los filtros del diálogo.
     */
    private void aplicarTodosLosFiltros() {
        adapter.aplicarFiltros(
                etSearch.getText().toString(),
                filtroCategoriaId,
                filtroPrecioMin,
                filtroPrecioMax,
                filtroOrden
        );
    }

    private void mostrarDialogoFiltros() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.filtros, null);

        Spinner spinnerCategoria = dialogView.findViewById(R.id.spinner_categoria);
        EditText etPrecioMin = dialogView.findViewById(R.id.et_precio_min);
        EditText etPrecioMax = dialogView.findViewById(R.id.et_precio_max);
        Spinner spinnerOrden = dialogView.findViewById(R.id.spinner_orden);

        // --- Spinner de categorías: "Todas" + nombres reales ---
        List<String> nombresCategorias = new ArrayList<>();
        nombresCategorias.add("Todas");
        for (Categoria cat : listaCategorias) {
            nombresCategorias.add(cat.getName() != null ? cat.getName() : "(sin nombre)");
        }
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, nombresCategorias);
        spinnerCategoria.setAdapter(catAdapter);

        // --- Spinner de orden ---
        String[] opcionesOrden = {"Más recientes", "Precio: menor a mayor", "Precio: mayor a menor"};
        ArrayAdapter<String> ordenAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, opcionesOrden);
        spinnerOrden.setAdapter(ordenAdapter);

        // Prellenar con los valores actuales
        spinnerOrden.setSelection(filtroOrden);
        if (filtroPrecioMin != null) etPrecioMin.setText(String.valueOf(filtroPrecioMin));
        if (filtroPrecioMax != null) etPrecioMax.setText(String.valueOf(filtroPrecioMax));
        // Preseleccionar categoría actual
        if (filtroCategoriaId != null) {
            for (int i = 0; i < listaCategorias.size(); i++) {
                if (filtroCategoriaId.equals(listaCategorias.get(i).getId())) {
                    spinnerCategoria.setSelection(i + 1); // +1 por "Todas"
                    break;
                }
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Filtrar anuncios")
                .setView(dialogView)
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    // Categoría
                    int posCat = spinnerCategoria.getSelectedItemPosition();
                    if (posCat == 0) {
                        filtroCategoriaId = null; // "Todas"
                    } else {
                        filtroCategoriaId = listaCategorias.get(posCat - 1).getId();
                    }

                    // Precio
                    filtroPrecioMin = parseDoubleOrNull(etPrecioMin.getText().toString());
                    filtroPrecioMax = parseDoubleOrNull(etPrecioMax.getText().toString());

                    // Orden
                    filtroOrden = spinnerOrden.getSelectedItemPosition();

                    aplicarTodosLosFiltros();
                })
                .setNeutralButton("Limpiar", (dialog, which) -> {
                    filtroCategoriaId = null;
                    filtroPrecioMin = null;
                    filtroPrecioMax = null;
                    filtroOrden = AnuncioAdapter.ORDEN_RECIENTES;
                    aplicarTodosLosFiltros();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private Double parseDoubleOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}