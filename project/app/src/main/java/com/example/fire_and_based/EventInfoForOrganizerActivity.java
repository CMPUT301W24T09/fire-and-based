package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class EventInfoForOrganizerActivity extends AppCompatActivity {
    public Event clickedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info_for_organizer_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedEvent = getIntent().getParcelableExtra("event");
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
                    .add(R.id.fragment_container_view, new EventAttendeesFragment())
                    .commit();
        }

        NavigationBarView bottomNavigationView = findViewById(R.id.event_info_for_organizer_nav);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.attendees_item) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, new EventAttendeesFragment())
                            .commit();
                }
                if (item.getItemId() == R.id.promotion_item) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, new EventPromotionFragment())
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_qr_code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.view_qr_code_item) {
            Intent intent = new Intent(EventInfoForOrganizerActivity.this, QRCodeViewer.class);
            intent.putExtra("name", clickedEvent.getEventName());
            intent.putExtra("code", clickedEvent.getQRcode());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
