package com.salikh.bongo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.salikh.bongo.adapters.HomeAdapter;
import com.salikh.bongo.adapters.StoriesAdapter;
import com.salikh.bongo.databinding.FragmentHomeBinding;
import com.salikh.bongo.models.HomeModel;
import com.salikh.bongo.models.StoriesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    private FragmentHomeBinding binding;
    private HomeAdapter adapter;
    private ArrayList<HomeModel> list;
    private FirebaseUser user;
    private DocumentReference reference;
    private StoriesAdapter adapterStories;
    private ArrayList<StoriesModel> listStories;


    private List<String> commentList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        reference = FirebaseFirestore.getInstance().collection("Posts").document(user.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getActivity());
        commentList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recycleView.setLayoutManager(linearLayoutManager);
        binding.recycleView.setAdapter(adapter);

        listStories = new ArrayList<>();
        adapterStories = new StoriesAdapter(listStories, getActivity());
        listStories.add(new StoriesModel("", "", "", "", ""));
        binding.storeRV.setAdapter(adapterStories);

        binding.storeRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.storeRV.setHasFixedSize(true);

        loadDataFromFireStone();

        adapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {

                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likeList.contains(user.getUid()) && isChecked) {
                    likeList.remove(user.getUid());
                } else {
                    likeList.add(user.getUid());
                }

                Map<String, Object> map = new HashMap<>();
                map.put("likes", likeList);

                reference.update(map);

            }

            @Override
            public void setCommentCount(TextView textView) {

                Activity activity = getActivity();

                commentCount.observe((LifecycleOwner) activity, new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {

                        textView.setText("" + commentCount.getValue());


                    }
                });


            }
        });

    }


    private void loadDataFromFireStone() {


        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid());

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value == null) {
                    return;
                }


                List<String> uidList = (List<String>) value.get("following");

                if (uidList == null || uidList.isEmpty())
                    return;

                collectionReference.whereIn("uid", uidList)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (error != null) {
                                    return;
                                }
                                if (value == null) {
                                    return;
                                }

                                for (QueryDocumentSnapshot snapshot : value) {

                                    snapshot.getReference().collection("Post Images")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                                        if (!snapshot.exists())
                                                            return;

                                                        HomeModel model = snapshot.toObject(HomeModel.class);


                                                        list.add(new HomeModel(
                                                                model.getUserName(),
                                                                model.getTimeTamp(),
                                                                model.getProfileImage(),
                                                                model.getImageUrl(),
                                                                model.getUid(),
                                                                model.getDescription(),
                                                                model.getId(),
                                                                model.getLikes()
                                                        ));

                                                        snapshot.getReference().collection("Comments").get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                        if (task.isSuccessful()) {
                                                                            QuerySnapshot snapshots = task.getResult();
                                                                            int count = 0;
                                                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                                                count++;
                                                                            }
                                                                            commentCount.setValue(count);

                                                                        }

                                                                    }
                                                                });

                                                    }
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });


                                }
                            }
                        });

                loadStores(uidList);
            }
        });

        // .collection("Post Images");

    }

    void loadStores(List<String> followingList) {
        Query query = FirebaseFirestore.getInstance().collection("Stories");
        query.whereIn("uid", followingList).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    return;
                }

                if (value == null) {
                    return;
                }

                for (QueryDocumentSnapshot snapshot : value) {

                    if (!value.isEmpty()) {
                        StoriesModel model = snapshot.toObject(StoriesModel.class);
                        listStories.add(model);
                    }
                }
                adapterStories.notifyDataSetChanged();

            }
        });
    }

}