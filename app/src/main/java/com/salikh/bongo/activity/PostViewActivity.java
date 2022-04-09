package com.salikh.bongo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.salikh.bongo.R;

public class PostViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Intent intent = getIntent();

        String action = intent.getAction();

        Uri uri = intent.getData();

        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        String query = uri.getQuery();
        setBars();

        //          URL url = new URL(scheme+"://"+host+path.replace("Post Images","Post%20Images")+"?"+query);

        FirebaseStorage.getInstance().getReference().child(uri.getLastPathSegment())
                .getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        ImageView imageView = findViewById(R.id.sendImage);

                        Glide.with(PostViewActivity.this)
                                .load(uri.toString())
                                .timeout(6500)
                                .into(imageView);
                    }
                });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(PostViewActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(PostViewActivity.this, ReplacerActivity.class));
        }

    }

    private void setBars() {
        getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
    }
}