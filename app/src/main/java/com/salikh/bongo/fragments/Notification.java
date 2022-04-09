package com.salikh.bongo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.salikh.bongo.databinding.FragmentNotificationBinding;


public class Notification extends Fragment {

    private FragmentNotificationBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}