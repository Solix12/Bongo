package com.salikh.bongo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.salikh.bongo.R;
import com.salikh.bongo.activity.ReplacerActivity;
import com.salikh.bongo.databinding.HomeItemsBinding;
import com.salikh.bongo.models.HomeModel;

import java.util.ArrayList;
import java.util.List;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final ArrayList<HomeModel> list;
    private final Activity context;

    OnPressed onPressed;

    public HomeAdapter(ArrayList<HomeModel> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HomeModel model = list.get(position);

        int color = Color.parseColor("#ffffff");

        holder.binding.userName.setText(model.getUserName());
        holder.binding.time.setText(String.valueOf(model.getTimeTamp()));

        List<String> likesList = list.get(position).getLikes();

        int countLike = likesList.size();

        holder.binding.likeText.setText(String.valueOf(countLike));


        holder.binding.likeBtn.setChecked(likesList.contains(user.getUid()));


        Glide.with(context.getApplicationContext())
                .load(model.getProfileImage())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.binding.imageProfileItem);

        Glide.with(context.getApplicationContext())
                .load(model.getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.binding.imageView6);

        holder.setListeners(position,
                list.get(position).getId(),
                list.get(position).getUserName(),
                list.get(position).getUid(),
                list.get(position).getLikes(),
                list.get(position).getImageUrl());

        onPressed.setCommentCount(holder.binding.commentText);

        /*list.get(position).getSnapshot().getReference().collection("Comment")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            return;
                        }
                        if (value == null){
                            return;
                        }

                        for (QueryDocumentSnapshot snapshot : value){

                            // TODO: 01.04.2022 komment yoziladgon joy

                        }
                    }
                }); */
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void OnPressed(OnPressed onPressed) {
        this.onPressed = onPressed;
    }

    public interface OnPressed {
        void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked);

        void setCommentCount(TextView textView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final HomeItemsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = HomeItemsBinding.bind(itemView);
        }

        public void setListeners(int position, String id, String userName, String uid, List<String> likes, String imageUrl) {

            binding.likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    onPressed.onLiked(position, id, uid, likes, isChecked);

                }
            });
            binding.commentBtn.setOnClickListener(view -> {

                Intent intent = new Intent(context, ReplacerActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("uid", uid);
                intent.putExtra("isComment", true);

                context.startActivity(intent);


            });

            binding.sendBtn.setOnClickListener(view -> {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, imageUrl);
                intent.setType("text/*");
                context.startActivity(Intent.createChooser(intent, "Share link using ..."));

            });

        }
    }
}
