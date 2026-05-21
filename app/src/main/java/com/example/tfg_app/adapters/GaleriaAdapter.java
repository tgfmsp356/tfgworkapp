package com.example.tfg_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tfg_app.R;
import java.util.List;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.GaleriaViewHolder> {

    private List<String> urlsImagenes;

    public GaleriaAdapter(List<String> urlsImagenes) {
        this.urlsImagenes = urlsImagenes;
    }

    @NonNull
    @Override
    public GaleriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagen_galeria, parent, false);
        return new GaleriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaleriaViewHolder holder, int position) {
        String url = urlsImagenes.get(position);
        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.ivImagen);
    }

    @Override
    public int getItemCount() {
        return urlsImagenes.size();
    }

    static class GaleriaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;

        public GaleriaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.iv_galeria_imagen);
        }
    }
}
