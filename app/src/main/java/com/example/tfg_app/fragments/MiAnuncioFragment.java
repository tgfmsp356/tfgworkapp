package com.example.tfg_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.R;
import com.example.tfg_app.adapters.MiAnuncioAdapter;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MiAnuncioFragment extends Fragment {

    private RecyclerView rvMiAnuncio;
    private TextView tvVacio;
    private ProgressBar progress;
    private Toolbar toolbar;

    private MiAnuncioAdapter adapter;
    private final List<Anuncio> listaAnuncios = new ArrayList<>();

    public MiAnuncioFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mianuncio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMiAnuncio = view.findViewById(R.id.rv_mis_anuncios);
        tvVacio = view.findViewById(R.id.tv_vacio);
        progress = view.findViewById(R.id.progress_mis_anuncios);
        toolbar = view.findViewById(R.id.toolbar_mis_anuncios);

        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Lista en cuadrícula de 2 columnas (igual que el Home)
        rvMiAnuncio.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MiAnuncioAdapter(listaAnuncios);
        rvMiAnuncio.setAdapter(adapter);

        adapter.setOnAnuncioClickListener(anuncio -> {
            AnuncioDetalleFragment detalle = AnuncioDetalleFragment.newInstance(anuncio.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detalle)
                    .addToBackStack(null)
                    .commit();
        });

        // confirmar y borrar
        adapter.setOnAnuncioBorrarListener(this::confirmarBorrado);

        cargarMiAnuncio();
    }

    private void cargarMiAnuncio() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        FirestoreHelper.getAnunciosPorUsuario(user.getUid())
                .addOnSuccessListener(querySnapshot -> {
                    progress.setVisibility(View.GONE);
                    listaAnuncios.clear();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : querySnapshot) {
                        Anuncio anuncio = doc.toObject(Anuncio.class);
                        anuncio.setId(doc.getId());
                        listaAnuncios.add(anuncio);
                    }
                    adapter.notifyDataSetChanged();
                    comprobarLista();
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar tus anuncios: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmarBorrado(Anuncio anuncio) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Borrar anuncio")
                .setMessage("¿Seguro que quieres borrar \"" + anuncio.getTitulo() + "\"? Esta acción no se puede deshacer.")
                .setPositiveButton("Borrar", (dialog, which) -> borrarAnuncio(anuncio))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarAnuncio(Anuncio anuncio) {
        FirestoreHelper.deleteAnuncio(anuncio.getId())
                .addOnSuccessListener(aVoid -> {
                    int index = listaAnuncios.indexOf(anuncio);
                    if (index != -1) {
                        listaAnuncios.remove(index);
                        adapter.notifyItemRemoved(index);
                    }
                    comprobarLista();
                    Toast.makeText(getContext(), "Anuncio borrado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al borrar: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void comprobarLista() {
        tvVacio.setVisibility(listaAnuncios.isEmpty() ? View.VISIBLE : View.GONE);
    }
}