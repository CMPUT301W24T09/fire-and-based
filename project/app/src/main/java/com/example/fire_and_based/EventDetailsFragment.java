package com.example.fire_and_based;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

/**
 * This fragment is hosted by UserActivity.
 * It displays the event details for an attendee or an organizer (depends on the mode)
 * This fragment may be accessed by clicking on an event in the list from EventListFragment, when in the attending or organizing tab.
 * It also hosts InfoFragment, NotificationsFragment, and MapFragment.
 *
 * Requires a user to be passed in as an argument as a Parcelable with a key "user"
 * Also requires an event to be passed in as an argument as a Parcelable with a key "event"
 * Also requires a mode to be passed in as an argument as a String with a key "mode"
 * Note that mode may be either "Attending" or "Organizing"
 *
 * To-do (UI):
 * 1. Delay in displaying checked in status
 * 2. Make edit details button functional
 *
 * @author Sumayya
 */
public class EventDetailsFragment extends Fragment {
    private User user;
    private Event event;
    private String mode;
    private ViewPager2 viewPager;
    private FragmentStateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_details_fragment, container, false);

        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            event = getArguments().getParcelable("event");
            mode = getArguments().getString("mode");
        }

        TextView eventTitle = view.findViewById(R.id.event_title);
        eventTitle.setText(event.getEventName());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /* we will add this when event banner gets fixed in xml

        ImageView imagePreview = view.findViewById(R.id.banner_image);
        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.getBannerBitmap(clickedEvent, imagePreview);

         */

        Button checkedInButton = view.findViewById(R.id.checked_in_button);
        Button editDetailsButton = view.findViewById(R.id.edit_details_button);
        Button attendeeListButton = view.findViewById(R.id.attendee_list_button);

        if (Objects.equals(mode, "Attending")) {
            editDetailsButton.setVisibility(View.GONE);
            attendeeListButton.setVisibility(View.GONE);
            FirebaseUtil.getUserCheckedInEvents(db, user.getDeviceID(), eventLongMap -> {
                for (Map.Entry<Event, Long> entry: eventLongMap.entrySet()) {
                    if (Objects.equals(entry.getKey().getQRcode(), event.getQRcode())) {
                        checkedInButton.setText("Checked in");
                    }
                }
            }, e -> {
                Log.e("FirebaseError", "Error fetching user checked in events: " + e.getMessage());
            });
        }
        if (Objects.equals(mode, "Organizing")) {
            checkedInButton.setVisibility(View.GONE);
            attendeeListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AttendeeFragment fragment = new AttendeeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("event", event);
                    fragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        ImageView backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });


        viewPager = view.findViewById(R.id.event_details_viewpager);
        adapter = new EventDetailsAdapter(this, event, mode);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.event_details_tablayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Info");
                    break;
                case 1:
                    tab.setText("Notifications");
                    break;
                case 2:
                    tab.setText("Map");
                    break;
            }
        }).attach();

        return view;
    }
}
