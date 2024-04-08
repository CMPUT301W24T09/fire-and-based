package com.example.fire_and_based;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This fragment is hosted by the EventDetailsFragment.
 * It displays the list of notifications for an event.
 * @author Sumayya
 * To-do (Firebase):
 * 1. Function that returns list of notifications (announcements) for an event.
 * 2. Function that allows us to add a notification.
 */
public class AnnouncementsFragment extends Fragment {
    private User user;
    private Event event;
    private String mode;
    private ArrayList<Announcement> dataList;
    private ListView announcementList;
    private AnnouncementArrayAdapter announcementAdapter;
    private FirebaseFirestore db;

    /**
     * Creates a new instance of the NotficationsFragment with the provided event data.
     *
     * @param event The Event object to be associated with the fragment.
     * @param mode the mode ("Organizing" or "Attending")
     * @param user the current user
     * @return A new instance of NotificationsFragment with the specified event data.
     */
    public static AnnouncementsFragment newInstance(Event event, String mode, User user) {
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putString("mode", mode);
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes UI elements and displays announcements for the event.
     * Handles posting new announcements based on user permissions.
     * @param inflater The LayoutInflater object for inflating views.
     * @param container The parent view to which the fragment's UI should be attached.
     * @param savedInstanceState Previous saved state of the fragment.
     * @return The view of the inflated layout for the fragment.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.announcements_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            mode = getArguments().getString("mode");
            user = getArguments().getParcelable("user");
        }

        dataList = new ArrayList<>();
        announcementList = view.findViewById(R.id.announcements_list);

        announcementAdapter = new AnnouncementArrayAdapter(requireContext(), dataList);
        announcementList.setAdapter(announcementAdapter);


        ImageView profilePic = view.findViewById(R.id.profile_picture_announcements);
        ImageDownloader downloadGuys = new ImageDownloader();
        downloadGuys.getProfilePicBitmap(user, (CircleImageView) profilePic);


        db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAnnouncements(db, event.getQRcode(), announcements -> {
            dataList.clear();
            for (Announcement announcement: announcements) {
                dataList.add(announcement);
                Collections.sort(dataList, new Comparator<Announcement>() {
                    @Override
                    public int compare(Announcement a1, Announcement a2) {
                        // Compare timestamps in reverse order to sort by most recent
                        return Long.compare(a2.getTimestamp(), a1.getTimestamp());
                    }
                });
                announcementAdapter.notifyDataSetChanged();
            }
        }, e -> {
            Log.e("FirebaseError", "Error fetching user events: " + e.getMessage());
        });

        ConstraintLayout newPost = view.findViewById(R.id.new_post);
        if (Objects.equals(mode, "Attending")) {
            newPost.setVisibility(View.GONE);
        } else {
            Button postButton = view.findViewById(R.id.post_button);
            TextView announcement_textview = view.findViewById(R.id.announcement_editable);
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String announcement_content = announcement_textview.getText().toString().trim();
                    if (announcement_content.isEmpty()) {
                        Toast.makeText(requireContext(), "Notification may not be empty", Toast.LENGTH_LONG).show();
                    } else {
                        AnnouncementUtil.newAnnouncement(db, announcement_content, event, aVoid -> {
                            announcement_textview.setText("");
                            Toast.makeText(AnnouncementsFragment.this.getActivity(), "Announcement issued successfully", Toast.LENGTH_SHORT).show();
                            dataList.clear();
                            FirebaseUtil.getAnnouncements(db, event.getQRcode(), announcements -> {
                                for (Announcement announcement : announcements) {
                                    dataList.add(announcement);
                                    Collections.sort(dataList, new Comparator<Announcement>() {
                                        @Override
                                        public int compare(Announcement a1, Announcement a2) {
                                            // Compare timestamps in reverse order to sort by most recent
                                            return Long.compare(a2.getTimestamp(), a1.getTimestamp());
                                        }
                                    });
                                    announcementAdapter.notifyDataSetChanged();
                                }
                            }, e -> {});
                        }, e -> {
                            Toast.makeText(AnnouncementsFragment.this.getActivity(), "An error has occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }, new Handler(Looper.getMainLooper()));
                    }
                }
            });
        }

        return view;
    }
}
