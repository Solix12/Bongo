package com.salikh.bongo.fragments;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.salikh.bongo.activity.MainActivity;
import com.salikh.bongo.activity.ReplacerActivity;
import com.salikh.bongo.databinding.FragmentCreateAccauntBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CreateAccountFragment extends Fragment {

    public static final String EMAIL_REGEX = "^(.+)@(.+)$";
    private FragmentCreateAccauntBinding binding;
    private FirebaseAuth auth;

    public CreateAccountFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCreateAccauntBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();

    }

    private void setListeners() {

        binding.loginBtn.setOnClickListener(view -> {
            ((ReplacerActivity) getActivity()).setFragment(new LogInFragment());
        });

        binding.singUpBtn.setOnClickListener(view -> {

            String name = binding.editName.getText().toString();
            String email = binding.editEmail.getText().toString();
            String pass = binding.editPass.getText().toString();

            if (name.isEmpty() || name.equals(" ")) {
                binding.editName.setError("Please input name");
                return;
            }

            if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                binding.editEmail.setError("Please input email");
                return;
            }

            if (pass.isEmpty() || pass.length() < 6) {
                binding.editPass.setError("Password must be more then 6 letters");
                return;
            }

            binding.progressSignUp.setVisibility(View.VISIBLE);
            binding.singUpBtn.setVisibility(View.INVISIBLE);

            createAccount(name, email, pass);

        });

    }

    private void createAccount(String name, String email, String pass) {

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();

                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                            request.setDisplayName(name);
                            request.setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460__480.png"));


                            user.updateProfile(request.build());

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Email verification link send", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            uploadUser(user, name, email, pass);

                        } else {
                            binding.progressSignUp.setVisibility(View.INVISIBLE);
                            binding.singUpBtn.setVisibility(View.VISIBLE);
                            String exception = task.getException().getMessage();
                            Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void uploadUser(FirebaseUser user, String name, String email, String pass) {

        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("email", email);
        map.put("profileImage", " ");
        map.put("uid", user.getUid());
        map.put("status", " ");
        map.put("password", pass);
        map.put("search", name.toLowerCase());

        map.put("followers", list);
        map.put("following", list1);


        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            binding.progressSignUp.setVisibility(View.INVISIBLE);
                            binding.singUpBtn.setVisibility(View.VISIBLE);
                            assert getActivity() != null;
                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            getActivity().finish();

                        } else {
                            binding.progressSignUp.setVisibility(View.INVISIBLE);
                            binding.singUpBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}