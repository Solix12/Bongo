package com.salikh.bongo.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.salikh.bongo.adapters.CommentAdapter;
import com.salikh.bongo.databinding.FragmentCommentBinding;
import com.salikh.bongo.models.CommentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Comment extends Fragment {


    private FragmentCommentBinding binding;
    private CommentAdapter adapter;
    private ArrayList<CommentModel> list;
    private FirebaseUser user;
    private String id, uid;
    private CollectionReference collectionReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() == null) {
            return;
        }

        id = getArguments().getString("id");
        uid = getArguments().getString("uid");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new CommentAdapter(getContext(), list);
        binding.recycleView.setAdapter(adapter);

        collectionReference = FirebaseFirestore.getInstance().collection("Users")
                .document(uid)
                .collection("Post Images")
                .document(id)
                .collection("Comments");


        loadCommentData();


        setListeners();

    }

    private void setListeners() {

        UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
        request.setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460__480.png"));

        user.updateProfile(request.build());

        binding.imageView7.setOnClickListener(view -> {

            String comment = binding.commentEdit.getText().toString();

            if (comment.isEmpty() || comment.equals(" ")) {

                Toast.makeText(getContext(), "Enter comment", Toast.LENGTH_SHORT).show();
                return;
            } else {


                String commentID = collectionReference.document().getId();

                Map<String, Object> map = new HashMap<>();
                map.put("uid", user.getUid());
                map.put("comment", comment);
                map.put("commentID", commentID);
                map.put("postID", id);

                map.put("name", user.getDisplayName());
                map.put("profileImageUrl", user.getPhotoUrl().toString());


                collectionReference.document(commentID)
                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            binding.commentEdit.setText("");

                        } else {
                            //failed
                        }
                    }
                });

            }

        });

    }


    private void loadCommentData() {

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;

                }

                if (value == null) {
                    return;
                }

                list.clear();
                for (QueryDocumentSnapshot snapshot : value) {

                    CommentModel model = snapshot.toObject(CommentModel.class);

                    list.add(model);

                }
                adapter.notifyDataSetChanged();
            }
        });

    }
}