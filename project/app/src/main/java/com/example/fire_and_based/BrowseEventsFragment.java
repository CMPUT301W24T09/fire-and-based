package com.example.fire_and_based;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BrowseEventsFragment extends Fragment {
    protected ListView eventList;
    protected EventArrayAdapter eventAdapter;
    protected ArrayList<Event> dataList;
    protected int lastClickedIndex;
    public User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_list_fragment, container, false);

        dataList = new ArrayList<>();

        eventList = view.findViewById(R.id.event_list);


        eventAdapter = new EventArrayAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);

        FloatingActionButton create_event_button = view.findViewById(R.id.create_event_button);
        create_event_button.setVisibility(View.GONE);


        // this updates the data list that displays
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAllEvents(db, list -> {
            // This is where you handle the data once it's loaded.
            Log.println(Log.DEBUG, "BrowseEventsList", "Refreshing events list...");
            Log.println(Log.DEBUG, "BrowseEventsList", "Old event data list size: " + dataList.size());
            dataList.clear();
            Log.println(Log.DEBUG, "BrowseEventsList", "Cleared old events");
            eventAdapter.notifyDataSetChanged();
            for (Event event : list) {
                Log.println(Log.DEBUG, "BrowseEventsList", "Adding " + event.getEventName());
                dataList.add(event);
                eventAdapter.notifyDataSetChanged();
            }
            Log.println(Log.DEBUG, "BrowseEventsList", "Event data list size after load" + dataList.size());
        });


//         event list on click handler
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedIndex = position;
                Event clickedEvent = dataList.get(lastClickedIndex);
//                updateEventBanner(clickedEvent);
                Intent intent = new Intent(requireActivity(), EventInfoActivity.class);
                intent.putExtra("event",  clickedEvent);
                startActivity(intent);

            }
        });

        return view;
    }
}