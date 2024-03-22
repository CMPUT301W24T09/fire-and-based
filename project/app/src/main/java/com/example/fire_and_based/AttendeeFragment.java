package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * This fragment is hosted by UserActivity.
 * It can be accessed by clicking the "Attendee List" button in EventDetailsFragment when the fragment is in organizing mode.
 * It also hosts the AttendeeListFragment.
 * To-do (UI):
 * 1. Fix up toolbar, need back button and count of attendees.
 * 2. When Firebase steps done, need to make a UserArrayAdapter
 */
public class AttendeeFragment extends Fragment {
    private Event event;
    private ViewPager2 viewPager;
    private FragmentStateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }

        viewPager = view.findViewById(R.id.attendee_list_viewpager);
        adapter = new AttendeeListAdapter(this, event);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.attendee_list_tablayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All Attendees");
                    break;
                case 1:
                    tab.setText("Checked In");
                    break;
            }
        }).attach();

        return view;
    }
}
