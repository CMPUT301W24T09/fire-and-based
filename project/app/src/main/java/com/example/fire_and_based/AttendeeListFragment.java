package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This class is a fragment hosted by the AttendeeFragment.
 * It displays the list of attendees for the All Attendees tab and the Checked In tab.
 * To-do (Firebase):
 * 1.
 * To-do (UI):
 * 1.
 */
public class AttendeeListFragment extends Fragment {
    private Event event;
    private boolean checkedIn;

    /**
     * Creates a new instance of the InfoFragment with the provided event data.
     *
     * @param event The Event object to be associated with the fragment.
     * @param checkedIn a boolean (true if we want to see the list of checkedin attendees, false if we want the list of all attendees)
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_list_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            checkedIn = getArguments().getBoolean("checkedIn");
        }

        return view;
    }
}
