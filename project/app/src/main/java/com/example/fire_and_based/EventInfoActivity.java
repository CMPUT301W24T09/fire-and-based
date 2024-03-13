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
import android.widget.Toast;
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
 * This activity hosts the EventOverviewFragment, EventAnnouncementsFragment, and EventMapFragment.
 * It will only display the announcements tab and the map tab if the user is signed up for the event.
 * Similarly, it will not display the button to scan a qrcode to check in if a user is not signed up for the event.
 */

public class EventInfoActivity extends AppCompatActivity {
    private Event clickedEvent;
    private boolean signedUp;
    public User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = getIntent().getParcelableExtra("currentUser");
            clickedEvent = getIntent().getParcelableExtra("event");
            signedUp = getIntent().getBooleanExtra("signed up", false);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(clickedEvent.getEventName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("currentUser", currentUser);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, EventOverviewFragment.newInstance(clickedEvent))
                    .commit();
        }

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

        Button registerButton = findViewById(R.id.register_button);
        if (signedUp) {
            registerButton.setVisibility(View.GONE);
        } else {
            Menu menu = bottomNavigationView.getMenu();
            MenuItem announcementsItem = menu.findItem(R.id.announcements_item);
            announcementsItem.setVisible(false);
            MenuItem mapItem = menu.findItem(R.id.map_item);
            mapItem.setVisible(false);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.addEventAndAttendee(db, clickedEvent.getQRcode(), currentUser.getDeviceID(), new FirebaseUtil.Callback() {
                    @Override
                    public void onSuccess() {
                        getIntent().putExtra("currentUser", currentUser);
                        getIntent().putExtra("event", clickedEvent);
                        getIntent().putExtra("signed up", true);
                        Toast.makeText(EventInfoActivity.this, "Success! You are now registered.", Toast.LENGTH_LONG).show();
                        recreate();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("FirebaseError", "Error registering user: " + e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (signedUp) {
            getMenuInflater().inflate(R.menu.scanner, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.scanner_item) {
            Intent intent = new Intent(EventInfoActivity.this, EventCheckIn.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}