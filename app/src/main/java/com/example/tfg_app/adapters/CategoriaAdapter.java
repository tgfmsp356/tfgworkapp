package com.example.tfg_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tfg_app.POJOS.Categoria;
import com.example.tfg_app.R;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> categorias;
    private OnCategoriaClickListener listener;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);
    }

    public CategoriaAdapter(List<Categoria> categorias, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.tvNombre.setText(categoria.getName());

        // Convertir el nombre del icono (String) al drawable real
        String nombreIcono = categoria.getIcono();
        int iconResId = 0;
        if (nombreIcono != null && !nombreIcono.isEmpty()) {
            iconResId = holder.itemView.getContext().getResources().getIdentifier(
                    nombreIcono,
                    "drawable",
                    holder.itemView.getContext().getPackageName()
            );
        }
        // Si no se encuentra el icono, usar uno por defecto
        if (iconResId == 0) {
            iconResId = R.drawable.ic_category;
        }
        holder.ivIcono.setImageResource(iconResId);

        holder.itemView.setOnClickListener(v -> listener.onCategoriaClick(categoria));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivIcono;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_categoria_nombre);
            ivIcono = itemView.findViewById(R.id.iv_categoria_icono);
        }
    }
}