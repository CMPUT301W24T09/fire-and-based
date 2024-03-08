package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Activity for displaying detailed information about an event, including
 * overview, announcements, and a map. Users can navigate through these sections
 * via a BottomNavigationView.
 */
public class EventInfoActivity extends AppCompatActivity {
    /**
     * The event for which information is displayed.
     */
    public Event clickedEvent;
    /**
     * Flag indicating whether the user has signed up for the event.
     */
    private boolean signedUp;
    public User currentUser;

    /**
     * Initializes the activity, its views, and fragments based on the event details.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info_activity);

        // Retrieve the event object and signed up flag from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = getIntent().getParcelableExtra("currentUser");
            clickedEvent = getIntent().getParcelableExtra("event");
            signedUp = getIntent().getBooleanExtra("signed up", false);
        }
        Button registerButton = findViewById(R.id.register_button);
        if (signedUp) {
            registerButton.setVisibility(View.GONE);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(clickedEvent.getEventName());

        // Set up the toolbar with the event name and a navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load the default fragment if there's no saved instance state
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, EventOverviewFragment.newInstance(clickedEvent))
                    .commit();
        }
        // Set up the bottom navigation view for switching between fragments
        NavigationBarView bottomNavigationView = findViewById(R.id.event_info_nav);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.overview_item) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, EventOverviewFragment.newInstance(clickedEvent))
                            .commit();
                }
                if (item.getItemId() == R.id.announcements_item) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, EventAnnouncementsFragment.newInstance(clickedEvent))
                            .commit();
                }
                if (item.getItemId() == R.id.map_item) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, EventMapFragment.newInstance(clickedEvent))
                            .commit();
                }
                return true;
            }
        });
        // Hide certain menu items if the user hasn't signed up for the event
        if (!signedUp) {
            Menu menu = bottomNavigationView.getMenu();
            MenuItem announcementsItem = menu.findItem(R.id.announcements_item);
            announcementsItem.setVisible(false);
            MenuItem mapItem = menu.findItem(R.id.map_item);
            mapItem.setVisible(false);
        }
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, currentUser.getFirstName());
                currentUser.addEvent(clickedEvent);
                Log.d(TAG, currentUser.getUserEvents().get(2).getEventName());
                FirebaseUtil.addAttendingEvent(FirebaseFirestore.getInstance(), currentUser);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("currentUser", currentUser);
                resultIntent.putExtra("clickedEvent", clickedEvent);
                setResult(EventInfoActivity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    /**
     * Inflates the menu; this adds items to the action bar if it is present.
     *
     * @param menu The options menu in which the items are placed.
     * @return True for the menu to be displayed; if false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (signedUp) {
            getMenuInflater().inflate(R.menu.scanner, menu);
            return true;
        }
        return false;
    }

    /**
     * Handles action bar item clicks.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.scanner_item) {
            // Navigate to the EventCheckIn activity
            Intent intent = new Intent(EventInfoActivity.this, EventCheckIn.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}