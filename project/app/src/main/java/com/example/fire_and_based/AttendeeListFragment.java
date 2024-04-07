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
 * This class is a fragment hosted by the AttendeeFragment.
 * It displays the list of attendees for the All Attendees tab and the Checked In tab.
 * @author Sumayya
 * To-do (UI):
 * 1. Set listener on attendee list to show attendee profiles
 */
public class AttendeeListFragment extends Fragment {
    private Event event;
    private boolean checkedIn;
    private ArrayList<User> dataList;
    private ListView attendeeList;
    private AttendeeArrayAdapter attendeeAdapter;
    private FirebaseFirestore db;

    /**
     * Creates a new instance of the InfoFragment with the provided event data.
     *
     * @param event The Event object to be associated with the fragment.
     * @param checkedIn a boolean (true if we want to see the list of checked-in attendees, false if we want the list of all attendees)
     * @return A new instance of InfoFragment with the specified event data.
     */
    public static AttendeeListFragment newInstance(Event event, boolean checkedIn) {
        AttendeeListFragment fragment = new AttendeeListFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putBoolean("checkedIn", checkedIn);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the layout and initializes the attendee list based on the event and checked-in status.
     * Retrieves event and checked-in status from arguments.
     * Populates the attendee list from Firebase Firestore.
     *
     * @param inflater           The LayoutInflater object.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The previous saved state of the fragment.
     * @return The View for the fragment's UI, or null.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_list_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            checkedIn = getArguments().getBoolean("checkedIn");
        }

        dataList = new ArrayList<>();
        attendeeList = view.findViewById(R.id.attendee_list);

        attendeeAdapter = new AttendeeArrayAdapter(requireContext(), dataList);
        attendeeList.setAdapter(attendeeAdapter);

        db = FirebaseFirestore.getInstance();
        updateEventList();

        return view;
    }

    /**
     * Updates the list of attendees based on the checked-in status.
     * If checkedIn is true, retrieves and updates the list of checked-in users.
     * If checkedIn is false, retrieves and updates the list of all attendees.
     */
    public void updateEventList() {
        if (checkedIn) {
            FirebaseUtil.getEventCheckedInUsers(db, event.getQRcode(), userIntegerMap -> {
                dataList.clear();
                for (User user : userIntegerMap.keySet()) {
                    dataList.add(user);
                    attendeeAdapter.notifyDataSetChanged();
                }
            }, e -> {
                Log.e("FirebaseError", "Error fetching attendees: " + e.getMessage());
            });
        } else {
            FirebaseUtil.getEventAttendees(db, event.getQRcode(), users -> {
                dataList.clear();
                for (User user : users) {
                    dataList.add(user);
                    attendeeAdapter.notifyDataSetChanged();
                }
            }, e -> {
                Log.e("FirebaseError", "Error fetching attendees: " + e.getMessage());
            });
        }
    }
}
