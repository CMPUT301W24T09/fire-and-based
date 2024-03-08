package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventInfoActivity extends AppCompatActivity {
    public Event clickedEvent;
    private boolean signedUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedEvent = getIntent().getParcelableExtra("event");
            signedUp = getIntent().getBooleanExtra("signed up", false);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(clickedEvent.getEventName());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        if (!signedUp) {
            Menu menu = bottomNavigationView.getMenu();
            MenuItem announcementsItem = menu.findItem(R.id.announcements_item);
            announcementsItem.setVisible(false);
            MenuItem mapItem = menu.findItem(R.id.map_item);
            mapItem.setVisible(false);
        }
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