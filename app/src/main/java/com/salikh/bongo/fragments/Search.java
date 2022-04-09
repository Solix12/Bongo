package com.salikh.bongo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.salikh.bongo.adapters.UserAdapter;
import com.salikh.bongo.databinding.FragmentSearchBinding;
import com.salikh.bongo.models.Users;

import java.util.ArrayList;

public class Search extends Fragment {

    OnDataPass onDataPass;
    private FragmentSearchBinding binding;
    private UserAdapter adapter;
    private ArrayList<Users> list;
    private CollectionReference reference;
    private FirebaseUser user;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onDataPass = (OnDataPass) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseFirestore.getInstance().collection("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.recycleView.setHasFixedSize(true);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new UserAdapter(list);
        binding.recycleView.setAdapter(adapter);


        loadUserData();

        searchUser();

        setListeners();


    }

    private void setListeners() {


        adapter.onUserClicked(new UserAdapter.OnUserClicked() {
            @Override
            public void onClicked(String uid) {
                onDataPass.onChange(uid);
            }
        });


    }

    private void searchUser() {

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty()) {
                    loadUserData();
                }

                reference.orderBy("search").startAt(newText).endAt(newText + "\uf8ff")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            list.clear();
                            for (DocumentSnapshot snapshot : task.getResult()) {


                                if (!snapshot.exists()) {
                                    return;
                                }
                                Users users = snapshot.toObject(Users.class);
                                if (!users.getUid().equals(user.getUid()))
                                    list.add(users);
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                });


                return false;
            }
        });

    }

    private void loadUserData() {


        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                    Users users = snapshot.toObject(Users.class);

                    if (!users.getUid().equals(user.getUid()))
                        list.add(users);

                }
                adapter.notifyDataSetChanged();

            }
        });


    }


    public interface OnDataPass {
        void onChange(String uid);
    }
}