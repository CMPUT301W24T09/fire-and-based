package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A {@link Fragment} subclass that displays a list of events the current user is attending.
 * This fragment manages a list view to show the events and integrates with Firestore to retrieve
 * the list of attending events.
 */
public class AttendingEventsFragment extends Fragment {
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> dataList;
    private int lastClickedIndex;
    public User currentUser;

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Returns the View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.event_list_fragment, container, false);

        // Initialize the adapter and list view to display the attending events
        dataList = new ArrayList<>();
        eventList = view.findViewById(R.id.event_list);
        eventAdapter = new EventArrayAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);

        // Hide the create event button as it's not needed in this view
        if (getArguments() != null) {
            currentUser = (User) getArguments().getParcelable("currentUser");
        }


        FloatingActionButton create_event_button = view.findViewById(R.id.create_event_button);
        create_event_button.setVisibility(View.GONE);

        // Fetch and update the list of attending events from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        dataList.clear();
        eventAdapter.notifyDataSetChanged();
        dataList.addAll(currentUser.getUserEvents());
        eventAdapter.notifyDataSetChanged();


        // Set an item click listener to handle navigation to the event's detailed information
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedIndex = position;
                Event clickedEvent = dataList.get(lastClickedIndex);
                Intent intent = new Intent(requireActivity(), EventInfoActivity.class);
                intent.putExtra("event", clickedEvent);
                intent.putExtra("signed up", true);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        db.collection("users").document(currentUser.getDeviceID()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    dataList.clear();
                    eventAdapter.notifyDataSetChanged();
                    dataList.addAll(currentUser.getUserEvents());
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }
}