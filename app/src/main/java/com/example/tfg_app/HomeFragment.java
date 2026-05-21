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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.adapters.AnuncioAdapter;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvAnuncios;
    private AnuncioAdapter adapter;
    private List<Anuncio> listaAnuncios;
    private TextView tvHomeTitle;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvAnuncios = view.findViewById(R.id.rv_anuncios);
        tvHomeTitle = view.findViewById(R.id.tv_home_title);

        listaAnuncios = new ArrayList<>();
        adapter = new AnuncioAdapter(listaAnuncios, anuncio -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AnuncioDetalleFragment.newInstance(anuncio.getId()))
                    .addToBackStack(null)
                    .commit();
        });
        
        rvAnuncios.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvAnuncios.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        obtenerNombreUsuario();
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

    private void cargarAnuncios() {
        FirestoreHelper.getAnuncios().addOnSuccessListener(queryDocumentSnapshots -> {
            listaAnuncios.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Anuncio anuncio = document.toObject(Anuncio.class);
                anuncio.setId(document.getId());
                listaAnuncios.add(anuncio);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar anuncios", Toast.LENGTH_SHORT).show());
    }
}
