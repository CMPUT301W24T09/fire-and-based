package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserActivity extends AppCompatActivity {
    /**
     * The current user of the app, retrieved from the intent that started this activity.
     */
    public User currentUser;

    /**
     * Called when the activity is starting. This is where the activity initializes the views
     * in its content view and retrieves the current user from the intent's extras.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        // Retrieve the User object passed as an extra from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = getIntent().getParcelableExtra("currentUser");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the fragment container with BrowseEventsFragment if it's the first creation
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            BrowseEventsFragment fragment = new BrowseEventsFragment();
            bundle.putParcelable("currentUser", currentUser);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .commit();
        }

        // Set up BottomNavigationView and handle item selection
        NavigationBarView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Bundle bundle = new Bundle();
                if (item.getItemId() == R.id.browse_events_item) {
                    BrowseEventsFragment fragment = new BrowseEventsFragment();
                    bundle.putParcelable("currentUser", currentUser);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .commit();
                }
                if (item.getItemId() == R.id.my_attended_events) {
                    AttendingEventsFragment fragment = new AttendingEventsFragment();
                    bundle.putParcelable("currentUser", currentUser);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .commit();
                }
                if (item.getItemId() == R.id.my_organized_events) {
                    OrganizingEventsFragment fragment = new OrganizingEventsFragment();
                    bundle.putParcelable("currentUser", currentUser);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .commit();
                }
                return true;
            }
        });
    }

    /**
     * Initializes the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which the items are placed.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_item) {
            Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}