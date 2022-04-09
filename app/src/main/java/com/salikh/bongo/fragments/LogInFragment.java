package com.salikh.bongo.fragments;

import static com.salikh.bongo.fragments.CreateAccountFragment.EMAIL_REGEX;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.salikh.bongo.R;
import com.salikh.bongo.activity.MainActivity;
import com.salikh.bongo.activity.ReplacerActivity;
import com.salikh.bongo.databinding.FragmentLogInBinding;

import java.util.HashMap;
import java.util.Map;


public class LogInFragment extends Fragment {

    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    private FragmentLogInBinding binding;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.app_name))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();
    }

    private void setListeners() {

        binding.loginBtn.setOnClickListener(view -> {


            String email = binding.editEmail.getText().toString();
            String pass = binding.editPass.getText().toString();

            if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                binding.editEmail.setError("Input valid email");
                return;
            }

            if (pass.isEmpty() || pass.length() < 6) {
                binding.editPass.setError("Input 6 digit valid password");
                return;
            }
            binding.progressSignUp.setVisibility(View.VISIBLE);
            binding.loginBtn.setVisibility(View.INVISIBLE);
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                FirebaseUser user = auth.getCurrentUser();

                                if (!user.isEmailVerified()) {
                                    Toast.makeText(getContext(), "Please Sign Up", Toast.LENGTH_SHORT).show();
                                }

                                sendUserToMainActivity();

                            } else {
                                String exception = task.getException().getMessage();
                                Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                                binding.progressSignUp.setVisibility(View.INVISIBLE);
                                binding.loginBtn.setVisibility(View.VISIBLE);
                            }

                        }
                    });


        });

        binding.cardView.setOnClickListener(view -> {

            signIn();

        });

        binding.singUpBtn.setOnClickListener(v -> {


            ((ReplacerActivity) getActivity()).setFragment(new CreateAccountFragment());

        });

        binding.forgotPass.setOnClickListener(view -> {
            ((ReplacerActivity) getActivity()).setFragment(new ForgotFragment());
        });

    }

    private void sendUserToMainActivity() {

        if (getActivity() == null) {
            return;
        }

        binding.progressSignUp.setVisibility(View.VISIBLE);
        binding.loginBtn.setVisibility(View.INVISIBLE);
        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            updateUi(user);
                        } else {
                            Log.w("TAG", "signInWithCredential : " + task.getException());
                        }
                    }
                });
    }

    private void updateUi(FirebaseUser user) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());


        Map<String, Object> map = new HashMap<>();

        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profileImage", String.valueOf(account.getPhotoUrl()));
        map.put("uid", user.getUid());
        map.put("following", 0);
        map.put("followers", 0);
        map.put("status", " ");

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            binding.progressSignUp.setVisibility(View.INVISIBLE);
                            binding.singUpBtn.setVisibility(View.VISIBLE);
                            assert getActivity() != null;
                            sendUserToMainActivity();

                        } else {
                            binding.progressSignUp.setVisibility(View.INVISIBLE);
                            binding.singUpBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

}