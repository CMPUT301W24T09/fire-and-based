package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfilePicturesFragment extends Fragment {

    protected GridView userList;
    protected ProfilePictureAdapter userAdapter;
    protected ArrayList<User> dataList;
    protected FirebaseFirestore db;

    /**
     * Inflates the layout for the user list fragment and initializes its views.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view of the inflated layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_pictures_fragment, container, false);

        dataList = new ArrayList<>();
        userList = view.findViewById(R.id.profile_pic_list);

        db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAllNonAdminUsers(db, users -> {
            dataList.clear();
            dataList.addAll(users);
            userAdapter = new ProfilePictureAdapter(requireContext(), dataList);
            userList.setAdapter(userAdapter);
        });

        return view;
    }
}
