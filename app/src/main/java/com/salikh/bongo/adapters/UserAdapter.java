package com.salikh.bongo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.salikh.bongo.R;
import com.salikh.bongo.databinding.UserItemsBinding;
import com.salikh.bongo.models.Users;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final ArrayList<Users> list;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private OnUserClicked onUserClicked;

    public UserAdapter(ArrayList<Users> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = list.get(position);


        holder.binding.name.setText(users.getName());
        holder.binding.profession.setText(users.getStatus());

        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(users.getProfileImage())
                .placeholder(R.drawable.defaunt_icon)
                .timeout(6500)
                .into(holder.binding.imageProfileNotification);

        holder.itemView.setOnClickListener(view -> {

            onUserClicked.onClicked(list.get(position).getUid());

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void onUserClicked(OnUserClicked onUserClicked) {
        this.onUserClicked = onUserClicked;
    }

    public interface OnUserClicked {
        void onClicked(String uid);
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {

        private final UserItemsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = UserItemsBinding.bind(itemView);
        }


    }

}
