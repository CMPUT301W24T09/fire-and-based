package com.example.fire_and_based;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.qrcode.encoder.QRCode;

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
 * @author Sumayya, Tyler
 */
public class EventDetailsFragment extends Fragment {
    private User user;
    private Event event;
    private String mode;
    private ViewPager2 viewPager;
    private FragmentStateAdapter adapter;
    private ImageDownloader imageDownloader = new ImageDownloader();

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Define a request code
    public Button checkedInButton;

    /**
     * Creates the view for the event details fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState This fragment is being re-constructed from a previous saved state
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_details_fragment, container, false);

        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            event = getArguments().getParcelable("event");
            mode = getArguments().getString("mode");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());


        TextView eventTitle = view.findViewById(R.id.event_title);
        eventTitle.setText(event.getEventName());

        TextView eventLocation = view.findViewById(R.id.locationText);
        eventLocation.setText(event.getLocation());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /* we will add this when event banner gets fixed in xml

        ImageView imagePreview = view.findViewById(R.id.banner_image);
        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.getBannerBitmap(clickedEvent, imagePreview);

         */

        ImageView imagePreview = view.findViewById(R.id.banner_image);
        if (event.getBannerQR() != null)
        {
            imageDownloader.getBannerBitmap(event,imagePreview);
        }

        TextView eventDate = view.findViewById(R.id.dateText);
        Long startLong = event.getEventStart();
        String startString = event.dateFromLong(startLong);
        eventDate.setText(startString);


        //Button checkedInButton = view.findViewById(R.id.checked_in_button);
        checkedInButton = view.findViewById(R.id.checked_in_button);
        Button editDetailsButton = view.findViewById(R.id.edit_details_button);
        Button attendeeListButton = view.findViewById(R.id.attendee_list_button);

        if (Objects.equals(mode, "Attending")) {
            editDetailsButton.setVisibility(View.GONE);
            attendeeListButton.setVisibility(View.GONE);
            FirebaseUtil.getEventCheckedInUsers(db, event.getQRcode(), userLongMap -> {
                for (Map.Entry<User, Long> entry: userLongMap.entrySet()) {
                    if (Objects.equals(entry.getKey().getDeviceID(), user.getDeviceID())) {
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

        ImageView backArrow = view.findViewById(R.id.back_arrow_browser);
        editDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEventFragment fragment = new EditEventFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", event);
                bundle.putParcelable("user", user);
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, fragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //ImageView backArrow = view.findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        viewPager = view.findViewById(R.id.event_details_viewpager);
        adapter = new EventDetailsAdapter(this, event, mode, user);
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

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            /**
             * This method will be invoked when a new page becomes selected. Animation is not
             * necessarily complete.
             *
             * @param position Position index of the new selected page.
             */
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 2) {
                    viewPager.setUserInputEnabled(false);
                } else {
                    viewPager.setUserInputEnabled(true);
                }
            }
        });

        return view;
    }


    /**
     * Retrieves the user's location and sends it to the event in the Firebase database upon checking in.
     * If location permission is not granted, requests permission.
     */
    private void getLocation() {
        if (!event.isTrackLocation()){
            return;
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        if (!checkedInButton.getText().toString().equals("Check In")){
            checkedInButton.setText("Check In");
//            Toast.makeText(getContext(), "You are already checked in for this event!", Toast.LENGTH_SHORT).show();
        } else {


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                                FirebaseUtil.sendCoordinatesToEvent(db, event, geoPoint, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Successfully Checked In", Toast.LENGTH_SHORT).show();
                                        checkedInButton.setText("Check Out");
                                    }
                                }, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error sending location to database :( ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                    }
                });
        }
    }

    /**
     * Requests the necessary location permissions from the user.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }
}
