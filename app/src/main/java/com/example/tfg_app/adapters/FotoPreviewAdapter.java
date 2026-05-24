package com.example.tfg_app.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfg_app.R;

import java.util.List;

public class FotoPreviewAdapter extends RecyclerView.Adapter<FotoPreviewAdapter.VH> {

    private final List<Uri> fotos;
    private final OnQuitarListener listener;

    // Para avisar al fragment de que se quiere quitar una foto
    public interface OnQuitarListener {
        void onQuitar(int posicion);
    }

    public FotoPreviewAdapter(List<Uri> fotos, OnQuitarListener listener) {
        this.fotos = fotos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.foto_layout, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Uri uri = fotos.get(position);

        Glide.with(h.ivFoto.getContext()).load(uri).into(h.ivFoto);

        // La primera foto es la portada
        if (position == 0) {
            h.tvPortada.setVisibility(View.VISIBLE);
        } else {
            h.tvPortada.setVisibility(View.GONE);
        }

        h.ivQuitar.setOnClickListener(view -> {
            if (listener != null) {
                listener.onQuitar(h.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivFoto, ivQuitar;
        TextView tvPortada;

        VH(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.iv_foto);
            ivQuitar = itemView.findViewById(R.id.iv_quitar);
            tvPortada = itemView.findViewById(R.id.tv_portada);
        }
    }
}