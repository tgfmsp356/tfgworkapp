package com.example.tfg_app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.R;
import com.example.tfg_app.adapters.ImagenPagerAdapter;
import com.example.tfg_app.database.FirestoreHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnuncioDetalleFragment extends Fragment {

    private static final String ARG_ANUNCIO_ID = "anuncio_id";

    private ViewPager2 vpImagenes;
    private TextView tvPrecio, tvTitulo, tvDescripcion, tvCategoria, tvUsuarioNombre;
    private Toolbar toolbar;
    private com.google.android.material.button.MaterialButton btnContacto;

    // Datos para el botón de contacto
    private String emailDueno = null;
    private String tituloAnuncio = null;

    //creacion del fragment por id de anuncio
    public static AnuncioDetalleFragment newInstance(String anuncioId) {
        AnuncioDetalleFragment fragment = new AnuncioDetalleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ANUNCIO_ID, anuncioId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anuncio_detalle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vpImagenes = view.findViewById(R.id.vp_detalle_imagenes);
        tvPrecio = view.findViewById(R.id.tv_detalle_precio);
        tvTitulo = view.findViewById(R.id.tv_detalle_titulo);
        tvDescripcion = view.findViewById(R.id.tv_detalle_descripcion);
        tvCategoria = view.findViewById(R.id.tv_detalle_categoria);
        tvUsuarioNombre = view.findViewById(R.id.tv_usuario_nombre);
        toolbar = view.findViewById(R.id.toolbar);

        btnContacto = view.findViewById(R.id.btn_comprar);
        btnContacto.setOnClickListener(v -> contactarVendedor());

        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        String anuncioId = null;
        if (getArguments() != null) {
            anuncioId = getArguments().getString(ARG_ANUNCIO_ID);
        }

        if (anuncioId == null) {
            Toast.makeText(getContext(), "Anuncio no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        cargarAnuncio(anuncioId);
    }

    private void cargarAnuncio(String anuncioId) {
        FirestoreHelper.getAnuncio(anuncioId).addOnSuccessListener(documentSnapshot -> {
            android.util.Log.d("DETALLE_DEBUG", "anuncioId recibido = " + anuncioId);
            android.util.Log.d("DETALLE_DEBUG", "exists = " + documentSnapshot.exists());
            android.util.Log.d("DETALLE_DEBUG", "data = " + documentSnapshot.getData());
            if (documentSnapshot.exists()) {
                Anuncio anuncio = documentSnapshot.toObject(Anuncio.class);
                android.util.Log.d("DETALLE_DEBUG", "anuncio objeto = " + (anuncio == null ? "NULL" : anuncio.getTitulo() + " / " + anuncio.getPrecio_hora()));
                if (anuncio != null) {
                    anuncio.setId(documentSnapshot.getId());
                    mostrarDatos(anuncio);
                }
            } else {
                Toast.makeText(getContext(), "El anuncio ya no existe", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Error al cargar el anuncio", Toast.LENGTH_SHORT).show());
    }

    private void mostrarDatos(Anuncio anuncio) {
        android.util.Log.d("DETALLE_DEBUG", "mostrarDatos ejecutado. tvTitulo null? " + (tvTitulo == null) + " tvPrecio null? " + (tvPrecio == null));

        tvTitulo.setText(anuncio.getTitulo());
        tvDescripcion.setText(anuncio.getDescripcion());
        tvPrecio.setText(String.format(Locale.getDefault(), "%.2f €", anuncio.getPrecio_hora()));

        List<String> imagenes = anuncio.getImagenes();
        if (imagenes == null) {
            imagenes = new ArrayList<>();
        }
        vpImagenes.setAdapter(new ImagenPagerAdapter(imagenes));

        // Cargar nombre real del usuario y de la categoría
        cargarNombreUsuario(anuncio.getId_usuario());
        cargarNombreCategoria(anuncio.getCategoria_id());
    }

    private void cargarNombreUsuario(String idUsuario) {
        if (idUsuario == null) {
            tvUsuarioNombre.setText("Usuario desconocido");
            return;
        }
        FirestoreHelper.getUsuario(idUsuario).addOnSuccessListener(doc -> {
            if (doc.exists()) {
                emailDueno = doc.getString("email");
                String nombre = doc.getString("nombre");
                String nombreUsuario = doc.getString("nombre_usuario");
                // Preferimos el nombre completo; si no hay, el nombre de usuario
                if (nombre != null && !nombre.isEmpty()) {
                    tvUsuarioNombre.setText(nombre);
                } else if (nombreUsuario != null) {
                    tvUsuarioNombre.setText("@" + nombreUsuario);
                } else {
                    tvUsuarioNombre.setText("Usuario");
                }
            } else {
                tvUsuarioNombre.setText("Usuario");
            }
        }).addOnFailureListener(e -> tvUsuarioNombre.setText("Usuario"));
    }

    private void cargarNombreCategoria(String categoriaId) {
        if (categoriaId == null) {
            tvCategoria.setText("Sin categoría");
            return;
        }
        FirestoreHelper.getCategoria(categoriaId).addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String nombre = doc.getString("name");
                tvCategoria.setText(nombre != null ? nombre : "Sin categoría");
            } else {
                tvCategoria.setText("Sin categoría");
            }
        }).addOnFailureListener(e -> tvCategoria.setText("Sin categoría"));
    }

    private void contactarVendedor() {
        if (emailDueno == null || emailDueno.isEmpty()) {
            Toast.makeText(getContext(), "No hay un correo de contacto disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String asunto = "Interés en tu anuncio: " + (tituloAnuncio != null ? tituloAnuncio : "");
        String cuerpo = "Hola,\n\n"
                + "He visto tu anuncio"
                + (tituloAnuncio != null ? " \"" + tituloAnuncio + "\"" : "")
                + " y estoy interesado/a. ¿Podrías darme más información?\n\n"
                + "Gracias.";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ emailDueno });
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        intent.putExtra(Intent.EXTRA_TEXT, cuerpo);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No hay ninguna app de correo instalada", Toast.LENGTH_SHORT).show();
        }
    }
}