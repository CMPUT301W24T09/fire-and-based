package com.example.fire_and_based;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class EventPagerAdapter extends FragmentStateAdapter {
    private Event clickedEvent;

    public EventPagerAdapter(FragmentActivity fragmentActivity, Event event) {
        super(fragmentActivity);
        this.clickedEvent = event;
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        // Pass clickedEvent to fragments via newInstance method
        switch (position) {
            case 0:
                return EventOverviewFragment.newInstance(clickedEvent);
            case 1:
                return EventAnnouncementsFragment.newInstance(clickedEvent);
            case 2:
                return EventMapFragment.newInstance(clickedEvent);
            default:
                throw new IllegalStateException("Invalid fragment position");
        }
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}