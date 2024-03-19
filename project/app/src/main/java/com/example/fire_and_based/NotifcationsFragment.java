package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This fragment is hosted by the EventDetailsFragment.
 * It displays the list of notifications for an event.
 * To-do (Firebase):
 * 1. Function that returns list of notifications (announcements) for an event.
 * 2. Function that allows us to add a notification.
 */
public class NotifcationsFragment extends Fragment {
    private Event event;

    public static NotifcationsFragment newInstance(Event event) {
        NotifcationsFragment fragment = new NotifcationsFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event); // Make sure your Event class implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_fragment, container, false);

        if (getArguments() != null) {
            Event event = getArguments().getParcelable("event");
        }

        return view;
    }
}
