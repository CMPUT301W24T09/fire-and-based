package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventMapFragment extends Fragment {
    private static final String ARG_EVENT = "event";


    public static EventMapFragment newInstance(Event event) {
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT, event); // Make sure your Event class implements Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.event_map_fragment, container, false);
        if (getArguments() != null) {
            Event event = getArguments().getParcelable(ARG_EVENT);
            if (event != null) {

                // Update UI elements with the event data
//                TextView eventName = view.findViewById(R.id.overview_event_title);
//                eventName.setText("WEWEWOEWOEO");
//                eventName.setText(event.getEventName()); // Assuming getEventName() is a method in your Event class
                // Similarly, update other views in the layout
            }
        }

        return view;
    }
}
