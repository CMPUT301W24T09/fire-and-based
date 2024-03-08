package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A {@link Fragment} subclass used to display announcements related to a specific event.
 * This fragment is designed to be reusable and configurable through arguments passed during its creation,
 * allowing it to display different event announcements based on the event data provided.
 */
public class EventAnnouncementsFragment extends Fragment {
    /**
     * Key for the event argument passed to the fragment.
     */
    private static final String ARG_EVENT = "event";

    /**
     * Static factory method to create a new instance of
     * this fragment using the provided {@link Event} parameter.
     *
     * @param event The event whose announcements are to be displayed.
     * @return A new instance of fragment EventAnnouncementsFragment.
     */
    public static EventAnnouncementsFragment newInstance(Event event) {
        EventAnnouncementsFragment fragment = new EventAnnouncementsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EVENT, event); // Ensure your Event class implements Parcelable.
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This method will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.event_announcements_fragment, container, false);

        // Retrieve and use the event argument to update UI elements
        if (getArguments() != null) {
            Event event = getArguments().getParcelable(ARG_EVENT);
            if (event != null) {
                // Assuming there are UI elements like TextViews that need to be updated with the event data
                // Example:
                // TextView eventName = view.findViewById(R.id.eventNameTextView);
                // eventName.setText(event.getEventName());
                // Similarly, update other views in the layout with event details
            }
        }

        return view;
    }
}
