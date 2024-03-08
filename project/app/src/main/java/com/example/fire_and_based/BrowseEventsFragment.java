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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

/**
 * Fragment for browsing a list of all events.
 * It showcases events fetched from Firestore in a ListView, allowing the user to select
 * an event to see more details.
 */
public class BrowseEventsFragment extends Fragment {

    protected ListView eventList;
    protected EventArrayAdapter eventAdapter;
    protected ArrayList<Event> dataList;
    protected int lastClickedIndex;
    public User currentUser;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     * This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.event_list_fragment, container, false);

        // Initialize the data list and adapter for the event list view
        dataList = new ArrayList<>();
        eventList = view.findViewById(R.id.event_list);
        eventAdapter = new EventArrayAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);

        // Hide the create event button in this fragment
        FloatingActionButton create_event_button = view.findViewById(R.id.create_event_button);
        create_event_button.setVisibility(View.GONE);

        // Fetch all events from Firestore and update the ListView
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.getAllEvents(db, list -> {
            Log.println(Log.DEBUG, "BrowseEventsList", "Refreshing events list...");
            dataList.clear();
            dataList.addAll(list);
            eventAdapter.notifyDataSetChanged();
        });

        // Handle clicks on events to open the EventInfoActivity
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedIndex = position;
                Event clickedEvent = dataList.get(lastClickedIndex);
                // Navigate to the EventInfoActivity with the selected event details
                Intent intent = new Intent(requireActivity(), EventInfoActivity.class);
                intent.putExtra("event", clickedEvent);
                startActivity(intent);
            }
        });

        return view;
    }
}
