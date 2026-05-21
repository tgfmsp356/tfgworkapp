package com.example.tfg_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg_app.POJOS.Anuncio;
import com.example.tfg_app.R;

import java.util.List;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private List<Anuncio> listaAnuncios;
    private OnAnuncioClickListener listener;

    public interface OnAnuncioClickListener {
        void onAnuncioClick(Anuncio anuncio);
    }

    public AnuncioAdapter(List<Anuncio> listaAnuncios, OnAnuncioClickListener listener) {
        this.listaAnuncios = listaAnuncios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio, parent, false);
        return new AnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        Anuncio anuncio = listaAnuncios.get(position);
        holder.tvTitulo.setText(anuncio.getTitulo());
        holder.tvPrecio.setText(String.format("%.2f €", anuncio.getPrecio_hora()));
        holder.itemView.setOnClickListener(v -> listener.onAnuncioClick(anuncio));
    }

    @Override
    public int getItemCount() {
        return listaAnuncios.size();
    }

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvPrecio;

        public AnuncioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
        }
    }
}
