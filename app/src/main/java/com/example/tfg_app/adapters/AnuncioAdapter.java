package com.example.tfg_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.Collections;
import java.util.Comparator;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.tfg_app.database.FirestoreHelper;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    // Lista completa (todos los anuncios) y lista visible (filtrada)
    private List<Anuncio> listaCompleta;
    private List<Anuncio> listaFiltrada;

    public AnuncioAdapter(List<Anuncio> listaAnuncios) {
        this.listaCompleta = listaAnuncios;
        this.listaFiltrada = new ArrayList<>(listaAnuncios);
    }

    // Listener para el click en una tarjeta
    public interface OnAnuncioClickListener {
        void onAnuncioClick(Anuncio anuncio);
    }

    private OnAnuncioClickListener clickListener;

    public void setOnAnuncioClickListener(OnAnuncioClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio, parent, false);
        return new AnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        Anuncio anuncio = listaFiltrada.get(position);
        holder.tvTitulo.setText(anuncio.getTitulo());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "%.2f €", anuncio.getPrecio_hora()));

        // Cargar la portada (primera imagen de la lista)
        if (anuncio.getImagenes() != null && !anuncio.getImagenes().isEmpty()) {
            String portadaUrl = anuncio.getImagenes().get(0);
            Glide.with(holder.ivAnuncio.getContext())
                    .load(portadaUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivAnuncio);
        } else {
            // Sin imágenes: mostrar un fondo por defecto
            holder.ivAnuncio.setImageResource(R.drawable.ic_launcher_background);
        }

        // Texto provisional mientras carga
        holder.tvUsuarioNombre.setText("...");
        holder.tvCategoria.setText("...");

// Cargar nombre del usuario
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

// Cargar nombre de la categoría
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
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    /**
     * Filtra la lista por título según el texto introducido.
     * Si el texto está vacío, muestra todos los anuncios.
     */
    public void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto == null || texto.trim().isEmpty()) {
            listaFiltrada.addAll(listaCompleta);
        } else {
            String query = texto.toLowerCase(Locale.getDefault()).trim();
            for (Anuncio anuncio : listaCompleta) {
                if (anuncio.getTitulo() != null
                        && anuncio.getTitulo().toLowerCase(Locale.getDefault()).contains(query)) {
                    listaFiltrada.add(anuncio);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Llamar cuando se recargan los anuncios desde Firestore,
     * para mantener sincronizadas ambas listas.
     */
    public void actualizarLista(List<Anuncio> nuevaLista) {
        this.listaCompleta = new ArrayList<>(nuevaLista);
        this.listaFiltrada = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvPrecio, tvUsuarioNombre, tvCategoria;
        ImageView ivAnuncio;

        public AnuncioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
            tvUsuarioNombre = itemView.findViewById(R.id.tv_usuario_nombre);
            tvCategoria = itemView.findViewById(R.id.tv_categoria);
            ivAnuncio = itemView.findViewById(R.id.iv_anuncio);
        }
    }

    // Constantes para el criterio de ordenación
    public static final int ORDEN_RECIENTES   = 0;
    public static final int ORDEN_PRECIO_ASC   = 1;
    public static final int ORDEN_PRECIO_DESC  = 2;

    /**
     * Aplica todos los filtros a la vez sobre la lista completa.
     *
     * @param texto       Texto de búsqueda por título (puede ser null/vacío).
     * @param categoriaId ID de categoría a filtrar (null = todas).
     * @param precioMin   Precio mínimo (null = sin mínimo).
     * @param precioMax   Precio máximo (null = sin máximo).
     * @param orden       Criterio de ordenación (ver constantes ORDEN_*).
     */
    public void aplicarFiltros(String texto, String categoriaId,
                               Double precioMin, Double precioMax, int orden) {
        listaFiltrada.clear();

        String query = (texto == null) ? "" : texto.toLowerCase(Locale.getDefault()).trim();

        for (Anuncio anuncio : listaCompleta) {
            // Filtro por título
            boolean coincideTitulo = query.isEmpty()
                    || (anuncio.getTitulo() != null
                    && anuncio.getTitulo().toLowerCase(Locale.getDefault()).contains(query));

            // Filtro por categoría
            boolean coincideCategoria = (categoriaId == null)
                    || categoriaId.equals(anuncio.getCategoria_id());

            // Filtro por precio
            boolean coincidePrecioMin = (precioMin == null) || anuncio.getPrecio_hora() >= precioMin;
            boolean coincidePrecioMax = (precioMax == null) || anuncio.getPrecio_hora() <= precioMax;

            if (coincideTitulo && coincideCategoria && coincidePrecioMin && coincidePrecioMax) {
                listaFiltrada.add(anuncio);
            }
        }

        // Ordenación
        switch (orden) {
            case ORDEN_PRECIO_ASC:
                Collections.sort(listaFiltrada, Comparator.comparingDouble(Anuncio::getPrecio_hora));
                break;
            case ORDEN_PRECIO_DESC:
                Collections.sort(listaFiltrada, (a, b) -> Double.compare(b.getPrecio_hora(), a.getPrecio_hora()));
                break;
            case ORDEN_RECIENTES:
            default:
                // Más recientes primero (por fecha de creación descendente)
                Collections.sort(listaFiltrada, (a, b) -> {
                    if (a.getFecha_creacion() == null || b.getFecha_creacion() == null) return 0;
                    return b.getFecha_creacion().compareTo(a.getFecha_creacion());
                });
                break;
        }

        notifyDataSetChanged();
    }
}