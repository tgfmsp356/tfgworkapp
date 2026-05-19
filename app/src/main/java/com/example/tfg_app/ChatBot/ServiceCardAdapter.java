package com.example.tfg_app.ChatBot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfg_app.R;

import java.util.List;

public class ServiceCardAdapter extends RecyclerView.Adapter<ServiceCardAdapter.VH> {

    private final List<AgentApiClient.Servicio> servicios;

    public ServiceCardAdapter(List<AgentApiClient.Servicio> servicios) {
        this.servicios = servicios;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AgentApiClient.Servicio s = servicios.get(position);

        h.tvTitulo.setText(s.titulo != null ? s.titulo : "Sin título");
        h.tvCategoria.setText(s.categoriaNombre != null ? s.categoriaNombre : "");
        h.tvPrecio.setText(String.format("%.0f €/h", s.precioHora));
        h.tvUsuario.setText(s.usuarioNombre != null ? "por " + s.usuarioNombre : "");
        h.tvEntrega.setText(s.tiempoEntrega > 0
                ? "Entrega en " + s.tiempoEntrega + " día" + (s.tiempoEntrega > 1 ? "s" : "")
                : "");

        if (s.valoracionMedia != null && s.valoracionMedia > 0) {
            h.ratingBar.setVisibility(View.VISIBLE);
            h.ratingBar.setRating(s.valoracionMedia.floatValue());
            h.tvValoracion.setVisibility(View.VISIBLE);
            h.tvValoracion.setText(String.format("%.1f", s.valoracionMedia));
        } else {
            h.ratingBar.setVisibility(View.GONE);
            h.tvValoracion.setVisibility(View.GONE);
        }

        if (s.imagenUrl != null && !s.imagenUrl.isEmpty()) {
            Glide.with(h.ivImagen.getContext())
                    .load(s.imagenUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_grid)  // icono de servicios como placeholder
                    .into(h.ivImagen);
        } else {
            h.ivImagen.setImageResource(R.drawable.ic_grid);
        }
    }

    @Override
    public int getItemCount() { return servicios.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView  ivImagen;
        TextView   tvTitulo, tvCategoria, tvPrecio, tvUsuario, tvEntrega, tvValoracion;
        RatingBar  ratingBar;

        VH(@NonNull View v) {
            super(v);
            ivImagen    = v.findViewById(R.id.iv_service_image);
            tvTitulo    = v.findViewById(R.id.tv_service_title);
            tvCategoria = v.findViewById(R.id.tv_service_category);
            tvPrecio    = v.findViewById(R.id.tv_service_price);
            tvUsuario   = v.findViewById(R.id.tv_service_user);
            tvEntrega   = v.findViewById(R.id.tv_service_delivery);
            tvValoracion = v.findViewById(R.id.tv_service_rating);
            ratingBar   = v.findViewById(R.id.rating_bar);
        }
    }
}
