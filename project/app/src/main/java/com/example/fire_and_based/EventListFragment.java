package com.example.fire_and_based;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is a fragment hosted by the UserActivity.
 * It displays the list of events for the Browse tab, the Attending tab, and the Organizing tab.
 * Requires a user to be passed in as an argument as a Parcelable with a key "user"
 * Also requires a mode to be passed in as an argument as a String with a key "mode"
 * Note that a mode may be "Browse", "Attending", or "Organizing"
 * To-do (Firebase):
 * 1. need a function that gets all events, EXCEPT those events that the user is organizing or is attending.
 * 2. need a function that gets all events that the user is organizing
 * To-do (UI):
 * 1. Ask Ilya about QR code scanner, see below
 */

public class EventListFragment extends Fragment {
    protected User user;
    protected ListView eventList;
    protected EventArrayAdapter eventAdapter;
    protected ArrayList<Event> dataList;
    protected int lastClickedIndex;
    protected String mode;
    protected FirebaseFirestore db;

    // ask Ilya what to do about this
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
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

        TextView toolbarTitle = view.findViewById(R.id.event_list_toolbar_title);
        toolbarTitle.setText(String.format("%s Events", mode));

        ImageView qrscannerButton = view.findViewById(R.id.qr_code_scanner);
        qrscannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });

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
        });

        return view;
    }

    /**
     * Updates the event list based on the current mode.
     * Fetches events from the database and refreshes the data displayed in the adapter.
     */
    public void updateEventList() {
        if (Objects.equals(mode, "Browse")) {
            FirebaseUtil.getAllEvents(db, new FirebaseUtil.getAllEventsCallback() {
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
            FirebaseUtil.getUserEvents(db, user.getDeviceID(), new FirebaseUtil.UserEventsAndFetchCallback() {
                @Override
                public void onEventsFetched(ArrayList<Event> events) {
                    dataList.clear();
                    for (Event event : events) {
                        dataList.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("FirebaseError", "Error fetching user events: " + e.getMessage());
                }
            });
        }

        if (Objects.equals(mode, "Organizing")) {
            // must be updated when we have the Firebase function to get list of events user is organizing
            FirebaseUtil.getAllEvents(db, new FirebaseUtil.getAllEventsCallback() {
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
    }

    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);

        qrLauncher.launch(options);
    }
}