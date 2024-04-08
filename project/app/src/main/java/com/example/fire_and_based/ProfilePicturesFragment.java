package com.example.fire_and_based;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    protected int lastClickedIndex;

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
            for (User user : users) {
                Log.d("HELLO", user.getDeviceID());
                if (!(user.getProfilePicture().contains("defaultProfiles")))
                {
                    //Means its using custom pic, add it to the list
                    dataList.add(user);
                }
            }
            userAdapter = new ProfilePictureAdapter(requireContext(), dataList);
            userList.setAdapter(userAdapter);
        });

        ImageDownloader downloader = new ImageDownloader();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedIndex = position;
                User clickedUser = dataList.get(lastClickedIndex);


                new AlertDialog.Builder(view.getContext())
                        .setTitle("Deleting User Picture") // Set the dialog title
                        .setMessage("Deleting this image will leave user with their default picture") // Set the dialog message
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked Delete, delete image
                                downloader.deletePic(clickedUser);
                                dataList.remove(clickedUser);
                                userAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked cancel, do nothing
                            }
                        })
                        .show(); // Display the dialog
            }
        });
        return view;
    }
}
