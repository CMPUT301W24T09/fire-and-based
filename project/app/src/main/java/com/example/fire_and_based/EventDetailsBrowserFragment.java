package com.example.fire_and_based;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * This fragment is hosted by UserActivity.
 * This fragment may be accessed by clicking on an event in the list from EventListFragment, when in the browsing tab.
 * It displays the event details for a "browser", i.e. someone who is not registered in the event.
 * Requires a user to be passed in as an argument as a Parcelable with a key "user"
 * Also requires an event to be passed in as an argument as a Parcelable with a key "event"
 * @author Sumayya
 * To-do (UI):
 * 1. Weird looking event banner ImageView needs to be fixed.
 */
public class EventDetailsBrowserFragment extends Fragment {
    private User user;
    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_details_browser_fragment, container, false);

        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            event = getArguments().getParcelable("event");
        }

        TextView eventTitle = view.findViewById(R.id.event_title_browser);
        eventTitle.setText(event.getEventName());

        TextView eventDescription = view.findViewById(R.id.event_description_browser);
        eventDescription.setText(event.getEventDescription());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /* we will add this when event banner gets fixed in xml

        ImageView imagePreview = view.findViewById(R.id.banner_image);
        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.getBannerBitmap(clickedEvent, imagePreview);

         */

        ImageView backArrow = view.findViewById(R.id.back_arrow_browser);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        Button joinEventButton = view.findViewById(R.id.join_event_button);
        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.addEventAndAttendee(db, event.getQRcode(), user.getDeviceID(), aVoid -> {
                    Toast.makeText(requireContext(), "Successfully joined event", Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                    EventDetailsFragment fragment = new EventDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", user);
                    bundle.putParcelable("event", event);
                    bundle.putString("mode", "Attending");
                    fragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                }, e -> {
                    Toast.makeText(requireContext(), "Event is full", Toast.LENGTH_LONG).show();
                });
            }
        });

        return view;
    }
}
