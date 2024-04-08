package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventPostersFragment extends Fragment {
    protected ListView eventList;
    protected EventPosterAdapter eventAdapter;
    protected ArrayList<Event> dataList;

    /**
     * Inflates the layout for the event posters fragment and populates it with event data.
     *
     * @param inflater           The LayoutInflater object.
     * @param container          The parent view to attach the fragment's UI.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the inflated layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_posters_fragment, container, false);

        dataList = new ArrayList<>();
        eventList = view.findViewById(R.id.event_poster_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAllEvents(db, events -> {
            dataList.clear();
            dataList.addAll(events);
            eventAdapter = new EventPosterAdapter(requireContext(), dataList);
            eventList.setAdapter(eventAdapter);
        });

        return view;
    }
}
