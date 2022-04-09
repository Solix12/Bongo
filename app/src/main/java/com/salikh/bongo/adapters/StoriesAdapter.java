package com.salikh.bongo.adapters;


import static com.salikh.bongo.activity.VIewStoryActivity.FILE_TYPE;
import static com.salikh.bongo.activity.VIewStoryActivity.VIDEO_URL_KEY;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.salikh.bongo.R;
import com.salikh.bongo.activity.StoryAddActivity;
import com.salikh.bongo.activity.VIewStoryActivity;
import com.salikh.bongo.databinding.StoriesItemBinding;
import com.salikh.bongo.models.StoriesModel;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {


    private final ArrayList<StoriesModel> list;
    Activity activity;

    public StoriesAdapter(ArrayList<StoriesModel> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == 0) {
            holder.binding.addStore.setVisibility(View.VISIBLE);

            holder.binding.addStore.setOnClickListener(view -> {

                activity.startActivity(new Intent(activity, StoryAddActivity.class));
            });
        } else {


            Glide.with(activity)
                    .load(list.get(position).getUrl())
                    .placeholder(R.drawable.defaunt_icon)
                    .timeout(6500)
                    .into(holder.binding.imageProfile);

            holder.binding.imageProfile.setOnClickListener(view -> {

                if (position == 0) {

                    //new stories

                    Dexter.withContext(activity)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                        activity.startActivity(new Intent(activity, StoryAddActivity.class));


                                    } else {
                                        Toast.makeText(activity, "Pleas allow permission from settings", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();

                } else {
                    //open stories

                    Intent intent = new Intent(activity, VIewStoryActivity.class);
                    intent.putExtra(VIDEO_URL_KEY, list.get(position).getUrl());
                    intent.putExtra(FILE_TYPE, list.get(position).getType());
                    activity.startActivity(intent);

                }

            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final StoriesItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = StoriesItemBinding.bind(itemView);
        }
    }
}
