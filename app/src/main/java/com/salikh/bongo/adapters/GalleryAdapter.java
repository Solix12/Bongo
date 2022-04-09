package com.salikh.bongo.adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salikh.bongo.R;
import com.salikh.bongo.databinding.ImageItemsBinding;
import com.salikh.bongo.models.GalleryImages;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {


    private final ArrayList<GalleryImages> list;
    private SendImage onSendImage;

    public GalleryAdapter(ArrayList<GalleryImages> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        int color = Color.parseColor("#ffffff");

        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(list.get(position).getPicUri())
                .placeholder(new ColorDrawable(color))
                .into(holder.binding.imageView);


        holder.binding.imageView.setOnClickListener(view -> {
            chooseImage(list.get(position).getPicUri());
        });

    }

    private void chooseImage(Uri picUri) {

        onSendImage.onSend(picUri);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void sendImage(SendImage sendImage) {
        this.onSendImage = sendImage;
    }


    public interface SendImage {
        void onSend(Uri picUri);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageItemsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ImageItemsBinding.bind(itemView);
        }
    }

}
