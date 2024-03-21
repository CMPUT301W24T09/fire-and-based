package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This activity hosts the EventListFragment, ViewProfileFragment, EventDetailsFragment, and AttendeeFragment.
 * Holds the bottom navigation bar for the entire app.
 * Requires a user to be passed in as an argument as a Parcelable with a key "user"
 */
public class UserActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = getIntent().getParcelableExtra("user");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ImageView createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.addEventToDB(db, new Event("My Special Event", "An amazing gathering", "Beautiful Venue", "ABC123", 1625126400000L, 1625184000000L, "Central Park", "XYZ456", new ArrayList<>(), 200L, true), new FirebaseUtil.AddEventCallback() {
                    @Override
                    public void onEventAdded() {

                    }

                    @Override
                    public void onEventExists() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });

        if (savedInstanceState == null) {
            replaceEventListFragment("Browse");
        }

        NavigationBarView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.browse_item) {
                    replaceEventListFragment("Browse");
                }
                if (item.getItemId() == R.id.attending_item) {
                    replaceEventListFragment("Attending");
                }
                if (item.getItemId() == R.id.organizing_item) {
                    replaceEventListFragment("Organizing");
                }
                if (item.getItemId() == R.id.profile_item) {
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", user);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .setReorderingAllowed(true)
                            .commit();
                }
                return true;
            }
        });
    }

    /**
     * Replaces the current fragment with a new instance of EventListFragment based on the specified mode.
     *
     * @param mode The mode to set as an argument for the new fragment
     */
    private void replaceEventListFragment(String mode) {
        EventListFragment fragment = new EventListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putString("mode", mode);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .setReorderingAllowed(true)
                .commit();
    }
}