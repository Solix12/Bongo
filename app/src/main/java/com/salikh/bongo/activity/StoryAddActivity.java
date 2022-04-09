package com.salikh.bongo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.TrimType;
import com.gowtham.library.utils.TrimVideo;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.salikh.bongo.databinding.ActivityStoryAddBinding;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StoryAddActivity extends AppCompatActivity {

    private static final int SELECT_VIDEO = 101;
    StylishAlertDialog alertDialog;
    private ActivityStoryAddBinding binding;
    private FirebaseUser user;
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));

                    binding.videoView.setVideoURI(uri);
                    binding.videoView.start();

                    binding.buttonStories.setVisibility(View.VISIBLE);
                    binding.buttonStories.setOnClickListener(view -> {
                        binding.buttonStories.setVisibility(View.GONE);
                        binding.videoView.pause();
                        uploadFilesToStorage(uri, "video");

                    });


                } else {
                    Toast.makeText(this, "Data is null", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/* video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, "image/* video/*");
        startActivityForResult(intent, SELECT_VIDEO);

        setBars();


    }

    private void uploadFilesToStorage(Uri uri, String type) {

        alertDialog = new StylishAlertDialog(this, StylishAlertDialog.PROGRESS);
        alertDialog.setTitleText("Uploading...")
                .setCancelable(false);

        alertDialog.show();

        File file = new File(uri.getPath());

        String fileName = "";

        if (type.equals("image")) {
            fileName = System.currentTimeMillis() + ".png";
        } else {
            fileName = System.currentTimeMillis() + ".mp4";
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Stories/" + fileName);


        storageReference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                assert task.getResult() != null;
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
                    uploadVideoDataFirebase(String.valueOf(uri1), type);
                });


            } else {
                alertDialog.dismissWithAnimation();
                String error = task.getException().getMessage();
                Toast.makeText(StoryAddActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadVideoDataFirebase(String url, String type) {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Stories");

        String id = reference.document().getId();


        Map<String, Object> map = new HashMap<>();
        map.put("videoUrl", url);
        map.put("id", id);
        map.put("uid", user.getUid());
        map.put("type", type);
        map.put("name", user.getUid());


        reference.document(id)
                .set(map);
        alertDialog.dismissWithAnimation();

        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_VIDEO) {

            Uri uri = data.getData();

            if (uri.toString().contains("image")) {

                binding.videoView.setVisibility(View.GONE);
                binding.imageView.setVisibility(View.VISIBLE);


                Glide.with(StoryAddActivity.this)
                        .load(uri)
                        .into(binding.imageView);

                binding.buttonStories.setVisibility(View.VISIBLE);
                binding.buttonStories.setOnClickListener(view -> {


                    binding.buttonStories.setVisibility(View.GONE);


                    uploadFilesToStorage(uri, "image");

                });


            } else if (uri.toString().contains("video")) {
                TrimVideo.activity(String.valueOf(uri))
                        .setCompressOption(new CompressOption()) //empty constructor for default compress option
                        .setTrimType(TrimType.MIN_MAX_DURATION)
                        .setMinToMax(5, 30)
                        .setHideSeekBar(true)
                        .start(this, startForResult);
            }


        }
    }

    private void setBars() {
        getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
    }

}