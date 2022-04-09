package com.salikh.bongo.activity;

import static com.salikh.bongo.utils.Constants.PREF_DIRECTORY;
import static com.salikh.bongo.utils.Constants.PREF_NAME;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.salikh.bongo.R;
import com.salikh.bongo.databinding.ActivityMainBinding;
import com.salikh.bongo.fragments.Add;
import com.salikh.bongo.fragments.Home;
import com.salikh.bongo.fragments.Notification;
import com.salikh.bongo.fragments.Profile;
import com.salikh.bongo.fragments.Search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements Search.OnDataPass {

    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setBars();


        setFragmentMain();
        setNavBar();


    }

    private void setNavBar() {

        binding.bottomBar.setItemIconSize(70);
        binding.bottomBar.setItemIconTint(Color.parseColor("#000000"));
        binding.bottomBar.setItemIconTintActive(Color.parseColor("#6c6cfe"));
        binding.bottomBar.setBarIndicatorColor(Color.parseColor("#FFC1C1FA"));

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String directory = preferences.getString(PREF_DIRECTORY, "");


        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


                switch (i) {
                    case 0: {
                        transaction.replace(R.id.container, new Home());
                        IS_SEARCHED_USER = false;
                        break;
                    }
                    case 1: {
                        transaction.replace(R.id.container, new Search());
                        IS_SEARCHED_USER = false;
                        break;
                    }
                    case 2: {
                        transaction.replace(R.id.container, new Add());
                        IS_SEARCHED_USER = false;
                        break;
                    }
                    case 3: {
                        transaction.replace(R.id.container, new Notification());
                        IS_SEARCHED_USER = false;
                        break;
                    }
                    case 4: {
                        transaction.replace(R.id.container, new Profile());
                        IS_SEARCHED_USER = false;
                        break;
                    }
                }
                transaction.commit();
                return true;
            }
        });


    }

    private Bitmap loadProfileImage(String directory) {
        try {
            File file = new File(directory, "profile.png");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setFragmentMain() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new Home());
        transaction.commit();
    }

    private void setBars() {
        getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
    }

    @Override
    public void onBackPressed() {
        binding.bottomBar.setItemActiveIndex(0);
        IS_SEARCHED_USER = false;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new Home());
        transaction.commit();


    }

    @Override
    public void onChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER = true;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new Profile());
        transaction.commit();
    }


}
