package com.example.fire_and_based;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class AttendeeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_activity);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, new BrowseEventsFragment())
                    .commit();
        }

        NavigationView navigationView = findViewById(R.id.drawer_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.browse_events_button) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, new BrowseEventsFragment())
                            .commit();
                }
                if (item.getItemId() == R.id.edit_profile_button) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view, new EditProfileFragment())
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            if (item.getItemId() == R.id.browse_events_button) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, new BrowseEventsFragment())
                        .commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                actionBarDrawerToggle.syncState();
            }
            if (item.getItemId() == R.id.edit_profile_button) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, new EditProfileFragment())
                        .commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                actionBarDrawerToggle.syncState();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
            /*
            case R.id.menu_qr_code:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, new QRCodeFragment())
                        .commit();
                return true;

            case R.id.menu_profile:
                // Switch to Profile Fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, new ProfileFragment())
                        .commit();
                return true;
            case R.id.menu_switch:
                // Switch to Switch Fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_view, new SwitchFragment())
                        .commit();
                return true;

        }

             */

