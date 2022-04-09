package com.salikh.bongo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.salikh.bongo.R;
import com.salikh.bongo.databinding.CommentItemBinding;
import com.salikh.bongo.models.CommentModel;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {


    private final Context context;
    private final ArrayList<CommentModel> list;

    public CommentAdapter(Context context, ArrayList<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context)
                .load(list.get(position).getProfileImageUrl())
                .placeholder(R.drawable.defaunt_icon)
                .into(holder.binding.imageProfileNotification);

        holder.binding.name.setText(list.get(position).getName());
        holder.binding.comment.setText(list.get(position).getComment());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        private final CommentItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CommentItemBinding.bind(itemView);

        }
    }
}
