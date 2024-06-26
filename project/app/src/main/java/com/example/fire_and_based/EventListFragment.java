package com.example.fire_and_based;

import android.app.AlertDialog;
import android.content.Context;
import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContextCompat.getSystemService;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import android.location.Geocoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
 * @author Sumayya, Tyler, Aiden, Ilya
 */

public class EventListFragment extends Fragment {
    protected User user;
    protected ListView eventList;
    protected EventArrayAdapter eventAdapter;
    protected ArrayList<Event> dataList;
    protected int lastClickedIndex;
    protected String mode;
    protected String filter;
    protected int filterIndex;
    protected FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    public Event scannedEvent;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Define a request code

    LatLng userLocation = new LatLng(53.5, -113.5);

    public SearchView searchView;
    private AutoCompleteTextView autoCompleteTextView;
    private Context context;


    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    db = FirebaseFirestore.getInstance();
                    String QRCode = QRCodeGenerator.getValidChars(result.getContents());
                    FirebaseUtil.getEvent(db, QRCode, event -> {
                        scannedEvent = event;
                        FirebaseUtil.addEventAndCheckedInUser(db, event.getQRcode(), user.getDeviceID(), aVoid -> {
                            if (event.isTrackLocation()) {
                                new AlertDialog.Builder(context)
                                        .setTitle("User Location Data") // Set the dialog title
                                        .setMessage("This event uses location tracking. \nDo you want to share your location where you check in?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // User clicked Yes, call getLocation()
                                                getLocation();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show(); // Display the dialog
                            }
                            Toast.makeText(getContext(), "Checked in", Toast.LENGTH_SHORT).show();
                            getLocation();
                            executeFragmentTransaction(event, "Attending");
                        }, e -> {
                            Toast.makeText(getContext(), "Failed to check in. Make sure you are registered", Toast.LENGTH_SHORT).show();
                            FirebaseUtil.getUserInvolvementInEvent(db, user.getDeviceID(), event.getQRcode(), mode -> {
                                Log.d("HELLOOOOO", mode);
                                executeFragmentTransaction(event, mode);
                            }, exception -> {});
                        });
                    }, e -> {
                        FirebaseUtil.getEventByPosterQR(db, QRCode, event -> {
                            FirebaseUtil.getUserInvolvementInEvent(db, user.getDeviceID(), event.getQRcode(), mode -> {
                                executeFragmentTransaction(event, mode);
                            }, exception -> {});
                        }, exception -> {
                            Toast.makeText(getContext(), "Unknown QR code", Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            }
    );

    /**
     * Inflates the event list fragment layout.
     *
     * @param inflater           LayoutInflater object to inflate fragment views.
     * @param container          Parent view to attach the fragment's UI.
     * @param savedInstanceState Previous saved state of the fragment.
     * @return Root view of the inflated layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_list_fragment, container, false);
        context = getContext();

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

        db = FirebaseFirestore.getInstance();
        updateEventList();


        searchView = view.findViewById(R.id.search);
        int closeButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) searchView.findViewById(closeButtonId);
        closeButton.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (eventAdapter != null) {
                    eventAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        autoCompleteTextView = view.findViewById(R.id.sort_menu);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Log.e("ITEM", selectedItem);
                eventAdapter.sortEvents(selectedItem, searchView.getQuery());
            }
        });

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
     * Called when the fragment is visible to the user and actively running.
     * This is generally tied to the Activity's lifecycle and occurs after onStart() when the activity
     * is visible, but before the activity is interacted with (before it gains focus).
     * <p>
     * This method clears the text in the AutoCompleteTextView and clears focus from the SearchView.
     * If an eventAdapter is set, it also clears the query text in the SearchView.
     * </p>
     */
    @Override
    public void onResume() {
        super.onResume();
        autoCompleteTextView.setText("");
        View rootView = searchView.getRootView();
        rootView.requestFocus();
        searchView.clearFocus();
        searchView.setQuery("", false);
    }

    /**
     * Updates the event list based on the current mode.
     * Fetches events from the database and refreshes the data displayed in the adapter.
     */
    public void updateEventList() {
        if (Objects.equals(mode, "Browse")) {
            FirebaseUtil.getAllEventsUserIsNotIn(db, user.getDeviceID(), this::setUp);
        }

        if (Objects.equals(mode, "Attending")) {
            FirebaseUtil.getUserEvents(db, user.getDeviceID(), this::setUp, e -> {
                    Log.e("FirebaseError", "Error fetching user events: " + e.getMessage());
                });
        }

        if (Objects.equals(mode, "Organizing")) {
            FirebaseUtil.getUserOrganizingEvents(db, user.getDeviceID(), this::setUp, e -> {
                Log.e("FirebaseError", "Error fetching organizing events: " + e.getMessage());
            });
        }

        if (Objects.equals(mode, "Admin")) {
            FirebaseUtil.getAllEvents(db, this::setUp);
        }
    }

    /**
     * Sets up the event list with the provided list of events.
     *
     * @param list The list of events to populate the event list with.
     */
    private void setUp(List<Event> list) {
        dataList.clear();
        dataList.addAll(list);
        eventAdapter = new EventArrayAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);
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

    /**
     * Launches the QR code scanner.
     */
    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);

        qrLauncher.launch(options);
    }

    /**
     * Retrieves the device's last known location and sends it to the event database.
     * If location permissions are not granted, requests permission.
     */
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
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                        }
                    }
                });
    }

    /**
     * Requests permission to access the device's location.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    public ArrayList<String> filterSetup(AutoCompleteTextView listSorter)
    {
        String[] stringArray = getResources().getStringArray(R.array.eventSorting);
        List<String> stringList = Arrays.asList(stringArray);
        ArrayList<String> sortList = new ArrayList<>(stringList);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter(requireContext(), R.layout.drop_down_options, sortList);
        listSorter.setAdapter(sortAdapter);

        return sortList;
    }

    /**
     * Filters the list of events based on the provided search string and filter criteria.
     *
     * @param events       The list of events to filter.
     * @param searchString The search string used to filter events by event name.
     * @return The filtered list of events based on the search string and filter criteria.
     */
    public ArrayList<Event> listAlgorithm(ArrayList<Event> events, String searchString)
    {

        Log.d(TAG,"SEARCHSTRING: "+searchString);

        if(searchString != null)
        {
            Log.d(TAG,"SEARCHSTRING: "+searchString);
            ArrayList<Event> filteredEvents = new ArrayList<>();
            for (Event event : events)
            {
                if (event.getEventName().contains(searchString))
                {
                    filteredEvents.add(event);
                }
            }
            events = filteredEvents;
            Log.d(TAG,"FILTEREDSIZE: "+events.size());

        }
            if (Objects.equals(filter, "Popular")) {
                Collections.sort(events, Comparator.comparing(Event::getCurrentAttendees).reversed());
                return events;
            } else if (Objects.equals(filter, "Upcoming")) {
                Collections.sort(events, Comparator.comparing(Event::getEventStart));
                return events;
            } else if (Objects.equals(filter, "Nearby")) {
//            getUserLocation();
//            Collections.sort(events, new Comparator<Event>() {
//                @Override
//                public int compare(Event event1, Event event2) {
//                    LatLng eventCoords1 = MapFragment.geocodeAddress(event1.getLocation());
//                    LatLng eventCoords2 = MapFragment.geocodeAddress(event2.getLocation());
//                    double distance1 = calculateDistance(userLocation, eventCoords1);
//                    double distance2 = calculateDistance(userLocation, eventCoords2);
//                    return Double.compare(distance1, distance2);
//                }
//            });
                return events;
            }
        Log.d(TAG,"FILTEREDSIZE: "+events.size());


        return events;


    }

    /**
     * Calculates the distance between two geographical points using their latitude and longitude coordinates.
     *
     * @param point1 The latitude and longitude coordinates of the first point.
     * @param point2 The latitude and longitude coordinates of the second point.
     * @return The distance between the two points in meters.
     */
    private double calculateDistance(LatLng point1, LatLng point2) {
        Location location1 = new Location("");
        location1.setLatitude(point1.latitude);
        location1.setLongitude(point1.longitude);

        Location location2 = new Location("");
        location2.setLatitude(point2.latitude);
        location2.setLongitude(point2.longitude);

        return location1.distanceTo(location2);
    }


    /**
     * Retrieves the user's current location if location permissions are granted.
     * If permissions are not granted, requests the necessary permissions.
     */
    public void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions not granted, request them
            requestLocationPermission();
        } else {
            // Permissions are already granted, fetch the location directly
            fetchLocation();
        }
    }

    /**
     * Handles the result of the permission request for location access.
     *
     * @param requestCode  The request code passed in requestPermissions().
     * @param permissions  The requested permissions. Must be non-null.
     * @param grantResults The grant results for the corresponding permissions. Must be non-null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions were granted
                fetchLocation();
            } else {
                // Permissions were denied // we can still call fetch i think - just to get the default return alternatively u can set defaults here
                fetchLocation();
            }
        }
    }


    /**
     * Fetches the user's last known location using the Fused Location Provider Client.
     * If permissions are not granted, this method returns without fetching the location.
     * If permissions are granted but the last known location is unavailable, default coordinates are used.
     */
    private void fetchLocation() {

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // only gets called if permission
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // return if no perms
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                // Location obtained, use it
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                userLocation = new LatLng(latitude, longitude); // u can also set the function here
            } else {
                userLocation = new LatLng(53.5, -113.5); // do what u need here
            }
        });
    }

}
