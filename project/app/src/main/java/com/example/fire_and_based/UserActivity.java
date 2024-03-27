package com.example.fire_and_based;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * This activity hosts the EventListFragment, ViewProfileFragment, EventDetailsFragment, EventDetailsBrowserFragment, CreateEventFragment, and AttendeeFragment.
 * Holds the bottom navigation bar for the entire app.
 * Requires a user to be passed in as an argument as a Parcelable with a key "user"
 * @author Sumayya, Tyler
 * To-do (UI):
 * 1. Fix ugly plus button
 * 2. Highlight title in addition to icon when tab is selected in bottom nav.
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


        ImageView createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventFragment fragment = new CreateEventFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, fragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();

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
     * @param mode The mode to set as an argument for the new fragment ("Browse", "Attending", or "Organizing")
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