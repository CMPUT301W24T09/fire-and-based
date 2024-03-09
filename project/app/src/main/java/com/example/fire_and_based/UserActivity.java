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

/**
 * This activity hosts the BrowseEventsFragment, AttendingEventsFragment, and OrganizingEventsFragment.
 * It is the main page for the entire app and provides different views for browsing events based on
 * whether a user is signed up for an event, organizing an event, etc.
 */
public class UserActivity extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = getIntent().getParcelableExtra("currentUser");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            BrowseEventsFragment fragment = new BrowseEventsFragment();
            bundle.putParcelable("currentUser", currentUser);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .commit();
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

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