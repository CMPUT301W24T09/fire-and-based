package com.example.fire_and_based;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * This activity hosts the EventListFragment, UserListFragment, ImageListFragment, AdminEventDetailsFragment, and AdminProfileFragment.
 * Holds the bottom navigation bar for admin view of app.
 * @author Sumayya
 */
public class AdminActivity extends AppCompatActivity {

    /**
     * Initializes the activity, sets the content view, and displays the initial fragment.
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);

        if (savedInstanceState == null) {
            EventListFragment fragment = new EventListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("mode", "Admin");
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view_admin, fragment)
                    .setReorderingAllowed(true)
                    .commit();
        }

        NavigationBarView bottomNavigationView = findViewById(R.id.bottom_nav_admin);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.events_item) {
                    EventListFragment fragment = new EventListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("mode", "Admin");
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view_admin, fragment)
                            .setReorderingAllowed(true)
                            .commit();
                }

                if (item.getItemId() == R.id.users_item) {
                    UserListFragment fragment = new UserListFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view_admin, fragment)
                            .setReorderingAllowed(true)
                            .commit();
                }

                if (item.getItemId() == R.id.images_item) {
                    ImageListFragment fragment = new ImageListFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_view_admin, fragment)
                            .setReorderingAllowed(true)
                            .commit();
                }

                return true;
            }
        });
    }
}
