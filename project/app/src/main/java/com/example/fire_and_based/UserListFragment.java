package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a fragment hosted by Admin Activity
 * It displays the list of users.
 * @author Sumayya
 * To-do (UI):
 * 1. Set up listener to click on a user and display the user profile (waiting on XML from Carson)
 */
public class UserListFragment extends Fragment {
    protected ListView userList;
    protected UserArrayAdapter userAdapter;
    protected ArrayList<User> dataList;
    protected int lastClickedIndex;
    protected FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_list_fragment, container, false);

        dataList = new ArrayList<>();
        userList = view.findViewById(R.id.user_list);

        userAdapter = new UserArrayAdapter(requireContext(), dataList);
        userList.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAllNonAdminUsers(db, new FirebaseUtil.getAllNonAdminUsersCallback() {
            @Override
            public void onCallback(List<User> list) {
                dataList.clear();
                for (User user: list) {
                    dataList.add(user);
                    userAdapter.notifyDataSetChanged();
                }
            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {            }
        });

        return view;
    }
}
