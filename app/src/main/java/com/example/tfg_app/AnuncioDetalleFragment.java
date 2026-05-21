package com.example.tfg_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.adapters.GaleriaAdapter;
import com.example.tfg_app.database.FirestoreHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AnuncioDetalleFragment extends Fragment {

    private String anuncioId;
    private TextView tvPrecio, tvTitulo, tvNombreUsuario, tvCategoria, tvDescripcion;
    private ImageView ivUsuario;
    private ViewPager2 vpImagenes;
    private List<String> listaUrls;
    private GaleriaAdapter galeriaAdapter;

    public static AnuncioDetalleFragment newInstance(String id) {
        AnuncioDetalleFragment fragment = new AnuncioDetalleFragment();
        Bundle args = new Bundle();
        args.putString("anuncio_id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            anuncioId = getArguments().getString("anuncio_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anuncio_detalle, container, false);

        tvPrecio = view.findViewById(R.id.tv_detalle_precio);
        tvTitulo = view.findViewById(R.id.tv_detalle_titulo);
        tvNombreUsuario = view.findViewById(R.id.tv_usuario_nombre);
        tvCategoria = view.findViewById(R.id.tv_detalle_categoria);
        tvDescripcion = view.findViewById(R.id.tv_detalle_descripcion);
        vpImagenes = view.findViewById(R.id.vp_detalle_imagenes);
        ivUsuario = view.findViewById(R.id.iv_usuario_foto);
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);

        listaUrls = new ArrayList<>();
        galeriaAdapter = new GaleriaAdapter(listaUrls);
        vpImagenes.setAdapter(galeriaAdapter);

        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        cargarDatosAnuncio();
        cargarImagenes();

        return view;
    }

    private void cargarDatosAnuncio() {
        if (anuncioId == null) return;

        FirestoreHelper.getAnuncio(anuncioId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot doc = task.getResult();
                Anuncio anuncio = doc.toObject(Anuncio.class);
                if (anuncio != null) {
                    mostrarDatos(anuncio);
                }
            }
        });
    }

    private void mostrarDatos(Anuncio anuncio) {
        tvPrecio.setText(String.format("%.2f €", anuncio.getPrecio_hora()));
        tvTitulo.setText(anuncio.getTitulo());
        tvDescripcion.setText(anuncio.getDescripcion());

        FirestoreHelper.getCategoria(anuncio.getCategoria_id()).addOnCompleteListener(taskCat -> {
            if (taskCat.isSuccessful() && taskCat.getResult() != null) {
                String nombreCat = taskCat.getResult().getString("name");
                tvCategoria.setText(nombreCat != null ? nombreCat : anuncio.getCategoria_id());
            } else {
                tvCategoria.setText(anuncio.getCategoria_id());
            }
        });


        FirestoreHelper.getUsuario(anuncio.getId_usuario()).addOnCompleteListener(taskUser -> {
            if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                String nombre = taskUser.getResult().getString("nombre");
                String apellidos = taskUser.getResult().getString("apellidos");
                if (nombre != null && apellidos != null) {
                    tvNombreUsuario.setText(nombre + " " + apellidos);
                } else {
                    tvNombreUsuario.setText("Usuario Desconocido");
                }
            }
        });
    }

    private void cargarImagenes() {
        if (anuncioId == null) return;

        FirestoreHelper.getCollection("imagenes_anuncio")
                .whereEqualTo("anuncio_id", anuncioId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listaUrls.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String url = doc.getString("url");
                            if (url != null) {
                                listaUrls.add(url);
                            }
                        }
                        if (listaUrls.isEmpty()) {
                            // URL por defecto si no hay imágenes
                            listaUrls.add("https://res.cloudinary.com/demo/image/upload/sample.jpg");
                        }
                        galeriaAdapter.notifyDataSetChanged();
                    }
                });
    }
}
