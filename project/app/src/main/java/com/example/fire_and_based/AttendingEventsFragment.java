package com.example.fire_and_based;

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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import java.lang.Exception;

/**
 * This class displays the list of events a user is signed up for.
 */
public class AttendingEventsFragment extends Fragment {
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> dataList;
    private int lastClickedIndex;
    private static final String ARG_USER = "currentUser";

    public static AttendingEventsFragment newInstance(User user) {
        AttendingEventsFragment fragment = new AttendingEventsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

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

        User user = getArguments().getParcelable(ARG_USER);

        // this updates the data list that displays
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.getUserEvents(db, user.getDeviceID(), new FirebaseUtil.UserEventsAndFetchCallback() {
                    @Override
                    public void onEventsFetched(ArrayList<Event> events) {
                        for (Event event : events) {
                            dataList.add(event);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("FirebaseError", "Error fetching user events: " + e.getMessage());
                    }
                });

//         event list on click handler
                eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        lastClickedIndex = position;
                        Event clickedEvent = dataList.get(lastClickedIndex);
//                updateEventBanner(clickedEvent);
                        Intent intent = new Intent(requireActivity(), EventInfoActivity.class);   // need to change this to the arrow idk how
                        intent.putExtra("event", clickedEvent);
                        intent.putExtra("signed up", true);
                        startActivity(intent);

                    }
                });

        return view;
    }
}