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
import java.util.Map;

/**
 * This fragment is hosted by the AttendeeFragment.
 * It displays the list of attendees for the All Attendees tab and the Checked In tab.
 * @author Sumayya
 */
public class AttendeeCheckedInFragment extends Fragment {
    private Event event;
    private ArrayList<User> dataList;
    private ArrayList<Long> checkedInList;
    private ListView attendeeList;
    private CheckedInAttendeeAdapter attendeeAdapter;
    private FirebaseFirestore db;

    /**
     * Creates a new instance of the AttendeeCheckedInFragment with the provided event data.
     *
     * @param event The Event object to be associated with the fragment.
     * @return A new instance of AttendeeCheckedInFragment with the specified event data.
     */
    public static AttendeeCheckedInFragment newInstance(Event event) {
        AttendeeCheckedInFragment fragment = new AttendeeCheckedInFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
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
        }

        dataList = new ArrayList<>();
        checkedInList = new ArrayList<>();
        attendeeList = view.findViewById(R.id.attendee_list);

        attendeeAdapter = new CheckedInAttendeeAdapter(requireContext(), dataList, checkedInList);
        attendeeList.setAdapter(attendeeAdapter);

        db = FirebaseFirestore.getInstance();
        updateAttendeeList();

        return view;
    }

    /**
     * Updates the list of attendees based on the checked-in status.
     * If checkedIn is true, retrieves and updates the list of checked-in users.
     * If checkedIn is false, retrieves and updates the list of all attendees.
     */
    public void updateAttendeeList() {
        FirebaseUtil.getEventCheckedInUsers(db, event.getQRcode(), userIntegerMap -> {
            dataList.clear();
            checkedInList.clear();
            for (Map.Entry<User, Long> entry : userIntegerMap.entrySet()) {
                User user = entry.getKey();
                long checkedInValue = entry.getValue();
                dataList.add(user);
                checkedInList.add(checkedInValue);
            }
            attendeeAdapter.notifyDataSetChanged();
        }, e -> {
            Log.e("FirebaseError", "Error fetching attendees: " + e.getMessage());
        });
    }
}
