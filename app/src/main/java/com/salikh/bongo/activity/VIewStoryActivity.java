package com.salikh.bongo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.salikh.bongo.databinding.ActivityViewStoryBinding;

public class VIewStoryActivity extends AppCompatActivity {


    public static final String VIDEO_URL_KEY = "videoURL";
    public static final String FILE_TYPE = "fileType";
    private ActivityViewStoryBinding binding;
    private SimpleExoPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setBars();
        String url = getIntent().getStringExtra(VIDEO_URL_KEY);
        String type = getIntent().getStringExtra(FILE_TYPE);


        if (url == null || url.isEmpty()) {
            finish();
        }

        if (type.contains("image")) {

            binding.imageView8.setVisibility(View.VISIBLE);
            binding.videoView.setVisibility(View.GONE);

            Glide.with(getApplicationContext())
                    .load(url)
                    .into(binding.imageView8);

        } else {

            binding.videoView.setVisibility(View.VISIBLE);
            binding.imageView8.setVisibility(View.GONE);


            MediaItem item = MediaItem.fromUri(url);

            player = new SimpleExoPlayer.Builder(this).build();
            player.setMediaItem(item);
            binding.videoView.setPlayer(player);

            player.play();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
    }


    private void setBars() {
        getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
    }

}