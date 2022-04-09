package com.salikh.bongo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.salikh.bongo.R;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (user == null) {
                    startActivity(new Intent(SplashActivity.this, ReplacerActivity.class));

                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));

                }

                finish();

            }
        }, 1000);


        setBars();

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler = null;
    }

    private void setBars() {
        getWindow().setStatusBarColor(Color.parseColor("#6c6cfe"));
        getWindow().setNavigationBarColor(Color.parseColor("#7f4cff"));
    }
}