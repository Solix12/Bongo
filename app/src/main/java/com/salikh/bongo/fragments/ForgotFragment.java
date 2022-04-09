package com.salikh.bongo.fragments;

import static com.salikh.bongo.fragments.CreateAccountFragment.EMAIL_REGEX;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.salikh.bongo.activity.ReplacerActivity;
import com.salikh.bongo.databinding.FragmentForgotBinding;


public class ForgotFragment extends Fragment {


    private FragmentForgotBinding binding;
    private FirebaseAuth auth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.singUpBtn.setOnClickListener(view1 -> {
            ((ReplacerActivity) getActivity()).setFragment(new LogInFragment());
        });


        binding.loginBtn.setOnClickListener(view1 -> {


            String email = binding.editEmail.getText().toString();

            if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                binding.editEmail.setError("Input valid email");
                return;
            }
            binding.progressSignUp.setVisibility(View.VISIBLE);
            binding.loginBtn.setVisibility(View.INVISIBLE);
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password reset email send successfully", Toast.LENGTH_SHORT).show();
                                binding.editEmail.setText("");
                                binding.progressSignUp.setVisibility(View.INVISIBLE);
                                binding.loginBtn.setVisibility(View.VISIBLE);
                            } else {
                                String errMessage = task.getException().getMessage();
                                Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
                                binding.progressSignUp.setVisibility(View.INVISIBLE);
                                binding.loginBtn.setVisibility(View.VISIBLE);
                            }

                        }
                    });

        });

    }
}