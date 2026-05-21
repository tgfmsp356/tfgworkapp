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

public class ImagenPagerAdapter extends RecyclerView.Adapter<ImagenPagerAdapter.VH> {

    private final List<String> urls;

    public ImagenPagerAdapter(List<String> urls) {
        this.urls = urls;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos un ImageView que ocupe todo el espacio
        ImageView iv = new ImageView(parent.getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new VH(iv);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String url = urls.get(position);
        Glide.with(holder.imageView.getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imageView;
        VH(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}