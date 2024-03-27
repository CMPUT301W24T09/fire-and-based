package com.example.fire_and_based;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This fragment is hosted by the EventDetailsFragment.
 * It displays the list of notifications for an event.
 * @author Sumayya
 * To-do (Firebase):
 * 1. Function that returns list of notifications (announcements) for an event.
 * 2. Function that allows us to add a notification.
 */
public class AnnouncementsFragment extends Fragment {
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
     * @return A new instance of NotificationsFragment with the specified event data.
     */
    public static AnnouncementsFragment newInstance(Event event, String mode) {
        AnnouncementsFragment fragment = new AnnouncementsFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putString("mode", mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.announcements_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            mode = getArguments().getString("mode");
        }

        dataList = new ArrayList<>();
        announcementList = view.findViewById(R.id.announcements_list);

        announcementAdapter = new AnnouncementArrayAdapter(requireContext(), dataList);
        announcementList.setAdapter(announcementAdapter);

        db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAnnouncements(db, event.getQRcode(), announcements -> {
            dataList.clear();
            for (Announcement announcement: announcements) {
                dataList.add(announcement);
                announcementAdapter.notifyDataSetChanged();
            }
        }, e -> {
            Log.e("FirebaseError", "Error fetching user events: " + e.getMessage());
        });

        return view;
    }
}
