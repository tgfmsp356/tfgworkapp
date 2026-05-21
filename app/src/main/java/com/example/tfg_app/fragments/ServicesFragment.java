package com.example.tfg_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tfg_app.POJOS.Categoria;
import com.example.tfg_app.R;
import com.example.tfg_app.adapters.CategoriaAdapter;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment {

    private RecyclerView rvCategorias;
    private CategoriaAdapter adapter;
    private List<Categoria> listaCategorias;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        rvCategorias = view.findViewById(R.id.rv_categorias);
        listaCategorias = new ArrayList<>();
        
        // Configurar el RecyclerView con un Grid de 2 columnas
        adapter = new CategoriaAdapter(listaCategorias, categoria -> {
            // Acción al pulsar una categoría (por ahora un Toast)
            Toast.makeText(getContext(), "Has pulsado: " + categoria.getName(), Toast.LENGTH_SHORT).show();
        });

        rvCategorias.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvCategorias.setAdapter(adapter);

        cargarCategorias();

        return view;
    }

    private void cargarCategorias() {
        FirestoreHelper.getCategorias().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                listaCategorias.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Categoria cat = doc.toObject(Categoria.class);
                    if (cat != null) {
                        cat.setId(doc.getId());
                        listaCategorias.add(cat);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
