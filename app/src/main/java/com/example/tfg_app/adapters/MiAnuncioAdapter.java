package com.example.tfg_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.R;
import com.example.tfg_app.database.FirestoreHelper;

import java.util.List;
import java.util.Locale;

public class MiAnuncioAdapter extends RecyclerView.Adapter<MiAnuncioAdapter.MiAnuncioViewHolder> {

    private final List<Anuncio> listaAnuncios;

    // Listener para abrir el detalle al pulsar la tarjeta
    public interface OnAnuncioClickListener {
        void onAnuncioClick(Anuncio anuncio);
    }

    // Listener para borrar al pulsar la papelera
    public interface OnAnuncioBorrarListener {
        void onAnuncioBorrar(Anuncio anuncio);
    }

    private OnAnuncioClickListener clickListener;
    private OnAnuncioBorrarListener borrarListener;

    public MiAnuncioAdapter(List<Anuncio> listaAnuncios) {
        this.listaAnuncios = listaAnuncios;
    }

    public void setOnAnuncioClickListener(OnAnuncioClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnAnuncioBorrarListener(OnAnuncioBorrarListener listener) {
        this.borrarListener = listener;
    }

    @NonNull
    @Override
    public MiAnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mianuncio, parent, false);
        return new MiAnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiAnuncioViewHolder holder, int position) {
        Anuncio anuncio = listaAnuncios.get(position);

        holder.tvTitulo.setText(anuncio.getTitulo());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "%.2f €", anuncio.getPrecio_hora()));

        // Portada (primera imagen)
        if (anuncio.getImagenes() != null && !anuncio.getImagenes().isEmpty()) {
            Glide.with(holder.ivAnuncio.getContext())
                    .load(anuncio.getImagenes().get(0))
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivAnuncio);
        } else {
            holder.ivAnuncio.setImageResource(R.drawable.ic_launcher_background);
        }

        // Texto provisional mientras carga
        holder.tvUsuarioNombre.setText("...");
        holder.tvCategoria.setText("...");

        // Nombre del usuario
        if (anuncio.getId_usuario() != null) {
            FirestoreHelper.getUsuario(anuncio.getId_usuario()).addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String nombre = doc.getString("nombre");
                    holder.tvUsuarioNombre.setText(nombre != null ? nombre : "Usuario");
                } else {
                    holder.tvUsuarioNombre.setText("Usuario");
                }
            }).addOnFailureListener(e -> holder.tvUsuarioNombre.setText("Usuario"));
        } else {
            holder.tvUsuarioNombre.setText("Usuario");
        }

        // Nombre de la categoría
        if (anuncio.getCategoria_id() != null) {
            FirestoreHelper.getCategoria(anuncio.getCategoria_id()).addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String nombre = doc.getString("name");
                    holder.tvCategoria.setText(nombre != null ? nombre : "Sin categoría");
                } else {
                    holder.tvCategoria.setText("Sin categoría");
                }
            }).addOnFailureListener(e -> holder.tvCategoria.setText("Sin categoría"));
        } else {
            holder.tvCategoria.setText("Sin categoría");
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAnuncioClick(anuncio);
            }
        });

        holder.btnBorrar.setOnClickListener(v -> {
            if (borrarListener != null) {
                borrarListener.onAnuncioBorrar(anuncio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAnuncios.size();
    }

    public static class MiAnuncioViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvPrecio, tvUsuarioNombre, tvCategoria;
        ImageView ivAnuncio, btnBorrar;

        public MiAnuncioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
            tvUsuarioNombre = itemView.findViewById(R.id.tv_usuario_nombre);
            tvCategoria = itemView.findViewById(R.id.tv_categoria);
            ivAnuncio = itemView.findViewById(R.id.iv_anuncio);
            btnBorrar = itemView.findViewById(R.id.btn_borrar);
        }
    }
}