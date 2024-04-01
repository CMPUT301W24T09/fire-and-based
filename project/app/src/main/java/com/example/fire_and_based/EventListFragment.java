package com.example.fire_and_based;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;

/**
 * This class is a fragment hosted by the UserActivity and/or Admin Activity
 * It displays the list of events.
 * Requires a user to be passed in as an argument as a Parcelable with a key "user"
 * Also requires a mode to be passed in as an argument as a String with a key "mode"
 * Note that a mode may be "Browse", "Attending", "Organizing", or "Admin"
 * @author Sumayya, Ilya
 * Issue: if you can a qr code for an event you're not registered in, it takes a long, long time for a failure message to pop up
 */

public class EventListFragment extends Fragment {
    protected User user;
    protected ListView eventList;
    protected EventArrayAdapter eventAdapter;
    protected ArrayList<Event> dataList;
    protected int lastClickedIndex;
    protected String mode;
    protected FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    public Event scannedEvent;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Define a request code



    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    db = FirebaseFirestore.getInstance();
                    FirebaseUtil.addEventAndCheckedInUser(db, result.getContents(), user.getDeviceID(), aVoid -> {
                        Toast.makeText(requireContext(), "Checked in!", Toast.LENGTH_LONG).show();
                        // write logic that gets user Lat and Long here so that it can be wrote to the databse
                        FirebaseUtil.getEvent(db, result.getContents(), new OnSuccessListener<Event>() {
                            @Override
                            public void onSuccess(Event event) {
                                scannedEvent = event;
                                getLocation();
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error finding that event make sure it exists!", Toast.LENGTH_SHORT).show();
                            }
                        });


//                        FirebaseUtil.getEvent(db, result.getContents(), event -> {
//                            executeFragmentTransaction(event, "Attending");
//                        }, e -> {
//                            Log.e("FirebaseError", "Error fetching event: " + e.getMessage());
//                        });
                    }, e -> {
                        Toast.makeText(requireContext(), "Failed. Make sure you are registered.", Toast.LENGTH_LONG).show();
                    });
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_list_fragment, container, false);

        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
            mode = getArguments().getString("mode");
        }

        // for getting user location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());


        TextView toolbarTitle = view.findViewById(R.id.event_list_toolbar_title);
        if (Objects.equals(mode, "Admin")) {
            toolbarTitle.setText("Browse Events");
        } else {
            toolbarTitle.setText(String.format("%s Events", mode));
        }

        ImageView qrscannerButton = view.findViewById(R.id.qr_code_scanner);
        if (Objects.equals(mode, "Admin")) {
            qrscannerButton.setVisibility(View.GONE);
        } else {
            qrscannerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchQRScanner();
                }
            });
        }

        dataList = new ArrayList<>();
        eventList = view.findViewById(R.id.event_list);

        eventAdapter = new EventArrayAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);

        db = FirebaseFirestore.getInstance();
        updateEventList();

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedIndex = position;
                Event clickedEvent = dataList.get(lastClickedIndex);
                executeFragmentTransaction(clickedEvent, mode);
            }
        });

        return view;
    }

    /**
     * Updates the event list based on the current mode.
     * Fetches events from the database and refreshes the data displayed in the adapter.
     */
    public void updateEventList() {
        if (Objects.equals(mode, "Browse")) {
            FirebaseUtil.getAllEventsUserIsNotIn(db, user.getDeviceID(), new FirebaseUtil.getAllEventsCallback() {
                @Override
                public void onCallback(List<Event> list) {
                    dataList.clear();
                    for (Event event : list) {
                        dataList.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        if (Objects.equals(mode, "Attending")) {
            FirebaseUtil.getUserEvents(db, user.getDeviceID(), events -> {
                    dataList.clear();
                    for (Event event : events) {
                        dataList.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                }, e -> {
                    Log.e("FirebaseError", "Error fetching user events: " + e.getMessage());
                });
        }

        if (Objects.equals(mode, "Organizing")) {
            FirebaseUtil.getUserOrganizingEvents(db, user.getDeviceID(), events -> {
                dataList.clear();
                for (Event event : events) {
                    dataList.add(event);
                    eventAdapter.notifyDataSetChanged();
                }
            }, e -> {
                Log.e("FirebaseError", "Error fetching organizing events: " + e.getMessage());
            });
        }

        if (Objects.equals(mode, "Admin")) {
            FirebaseUtil.getAllEvents(db, new FirebaseUtil.getAllEventsCallback() {
                @Override
                public void onCallback(List<Event> list) {
                    dataList.clear();
                    for (Event event: list) {
                        dataList.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Executes a fragment transaction based on the provided event and mode.
     * Depending on the mode, it replaces the current fragment with the appropriate event details fragment.
     *
     * @param clickedEvent The event associated with the clicked item.
     * @param mode the mode ("Browse", "Attending", "Organizing", "Admin")
     */
    private void executeFragmentTransaction(Event clickedEvent, String mode) {
        if (Objects.equals(mode, "Admin")) {
            Fragment fragment = new AdminEventDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", clickedEvent);
            fragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view_admin, fragment)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        } else {
            Fragment fragment = null;
            if (Objects.equals(mode, "Browse")) {
                fragment = new EventDetailsBrowserFragment();
            } else {
                fragment = new EventDetailsFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            bundle.putParcelable("event", clickedEvent);
            bundle.putString("mode", mode);
            fragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);

        qrLauncher.launch(options);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
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

                            FirebaseUtil.sendCoordinatesToEvent(db, scannedEvent, geoPoint, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "It wrote to db u did it kid proud of u ", Toast.LENGTH_SHORT).show();
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
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        updateEventList();
        // Call your method to refresh the events list here
    }
}
