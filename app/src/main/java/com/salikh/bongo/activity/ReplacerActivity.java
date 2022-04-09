package com.salikh.bongo.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.salikh.bongo.databinding.ActivityReplacerBinding;
import com.salikh.bongo.fragments.Comment;
import com.salikh.bongo.fragments.CreateAccountFragment;
import com.salikh.bongo.fragments.LogInFragment;

public class ReplacerActivity extends AppCompatActivity {

    private ActivityReplacerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReplacerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        boolean isComment = getIntent().getBooleanExtra("isComment", false);

        if (isComment) {
            setFragment(new Comment());
        } else {
            setFragment(new LogInFragment());
        }

        setBars();

    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (fragment instanceof CreateAccountFragment) {
            fragmentTransaction.addToBackStack(null);
        }

        if (fragment instanceof Comment) {

            String id = getIntent().getStringExtra("id");
            String uid = getIntent().getStringExtra("uid");

            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("uid", uid);
            fragment.setArguments(bundle);

        }

        fragmentTransaction.replace(binding.freamLayaut.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setBars() {
        getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
    }
}