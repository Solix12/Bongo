package com.salikh.bongo.fragments;

import static android.app.Activity.RESULT_OK;
import static com.salikh.bongo.activity.MainActivity.IS_SEARCHED_USER;
import static com.salikh.bongo.activity.MainActivity.USER_ID;
import static com.salikh.bongo.utils.Constants.PREF_DIRECTORY;
import static com.salikh.bongo.utils.Constants.PREF_NAME;
import static com.salikh.bongo.utils.Constants.PREF_STORED;
import static com.salikh.bongo.utils.Constants.PREF_URL;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salikh.bongo.R;
import com.salikh.bongo.databinding.FragmentProfileBinding;
import com.salikh.bongo.databinding.ProfileImageItemsBinding;
import com.salikh.bongo.models.PostImageModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Profile extends Fragment {


    private FragmentProfileBinding binding;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private List<Object> followersList, followingList, followingList_2;
    private DocumentReference userRef, myRef;


    private FirestoreRecyclerAdapter<PostImageModel, PostImageHolder> adapter;
    private String userUID;
    private boolean isMyProfile = true;
    private boolean isFollowed;

    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());

        if (IS_SEARCHED_USER) {
            isMyProfile = false;
            userUID = USER_ID;

            loadData();

        } else {
            isMyProfile = true;
            userUID = user.getUid();
        }

        if (isMyProfile) {
            binding.button.setVisibility(View.GONE);
            binding.edit.setVisibility(View.VISIBLE);
        } else {

            binding.button.setVisibility(View.VISIBLE);
            binding.edit.setVisibility(View.GONE);

        }

        userRef = FirebaseFirestore.getInstance().collection("Users")
                .document(userUID);


        loadBasiData();

        binding.recycleView.setHasFixedSize(true);
        binding.recycleView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        loadPostImages();
        binding.recycleView.setAdapter(adapter);


        setListeners();


    }

    private void loadData() {

        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.e("tab", error.getMessage());
                    return;
                }
                if (value == null || !value.exists()) {
                    return;
                }

                followingList_2 = (List<Object>) value.get("following");

            }
        });

    }

    private void setListeners() {
        binding.edit.setOnClickListener(view1 -> {


            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), Profile.this);


        });

        binding.button.setOnClickListener(view -> {


            if (isFollowed) {
                binding.button.setText("Follow");
                followersList.remove(user.getUid());

                followingList_2.remove(userUID);

                Map<String, Object> map_2 = new HashMap<>();
                map_2.put("following", followingList_2);

                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);

                userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        System.out.println();
                                    } else {
                                        Log.e("taggg", task.getException().getMessage());
                                    }
                                }
                            });

                        } else {
                            Log.e("TAaaa", "" + task.getException().getMessage());
                        }
                    }
                });


            } else {
                binding.button.setText("Unfollow");
                followersList.add(user.getUid());

                followingList_2.add(userUID);

                Map<String, Object> map_2 = new HashMap<>();
                map_2.put("following", followingList_2);

                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);

                userRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            myRef.update(map_2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        System.out.println();
                                    } else {
                                        Log.e("taggg", task.getException().getMessage());
                                    }
                                }
                            });

                        } else {
                            Log.e("TAaaa", "" + task.getException().getMessage());
                        }
                    }
                });


            }


        });


    }

    private void loadBasiData() {


        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error != null) {
                    return;
                }

                assert value != null;
                if (value.exists()) {

                    String name = value.getString("name");
                    String status = value.getString("status");
                    String profileURL = value.getString("profileImage");

                    followersList = (List<Object>) value.get("followers");
                    followingList = (List<Object>) value.get("following");

                    binding.textView5.setText(name);
                    binding.userProfile.setText(name);
                    binding.textView10.setText(status);
                    binding.followersText.setText(String.valueOf(followersList.size()));
                    binding.followingText.setText(String.valueOf(followingList.size()));


                    try {
                        Glide.with(getContext().getApplicationContext())
                                .load(profileURL)
                                .placeholder(R.drawable.defaunt_icon)
                                .timeout(6500)
                                .into(binding.imageProfile);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (followersList.contains(user.getUid())) {
                        binding.button.setText("Unfollow");
                        isFollowed = true;
                    } else {
                        isFollowed = false;
                        binding.button.setText("Follow");
                    }

                }
            }
        });
    }

    private void storeProfileImage(Bitmap bitmap, String url) {

        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isStorred = preferences.getBoolean(PREF_STORED, false);
        String urlString = preferences.getString(PREF_URL, "");

        SharedPreferences.Editor editor = preferences.edit();

        if (isStorred && urlString.equals(url)) {
            return;
        }

        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());

        File directory = contextWrapper.getDir("image_data", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }

        File path = new File(directory, "profile.png");

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(path);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {


            try {
                assert outputStream != null;
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        editor.putBoolean(PREF_STORED, true);
        editor.putString(PREF_URL, url);
        editor.putString(PREF_DIRECTORY, directory.getAbsolutePath());
        editor.apply();


    }

    private void loadPostImages() {


        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(userUID);

        Query query = reference.collection("Post Images");

        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_image_items, parent, false);
                return new PostImageHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {

                int color = Color.parseColor("#ffffff");


                Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(model.getImageUrl())
                        .placeholder(new ColorDrawable(color))
                        .into(holder.binding.imageView);
                count = getItemCount();
                binding.postText.setText(String.valueOf(count));

            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            assert result != null;
            Uri image = result.getUri();


            uploadImage(image);

        }

    }

    private void uploadImage(Uri uri) {


        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Profile Images/" + System.currentTimeMillis());

        reference.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageURL = uri.toString();

                                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                                            request.setPhotoUri(uri);

                                            user.updateProfile(request.build());

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("profileImage", imageURL);

                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(user.getUid())
                                                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Updated Successful", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "Error updated", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });


                                        }
                                    });

                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private class PostImageHolder extends RecyclerView.ViewHolder {

        private final ProfileImageItemsBinding binding;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);

            binding = ProfileImageItemsBinding.bind(itemView);
        }
    }
}