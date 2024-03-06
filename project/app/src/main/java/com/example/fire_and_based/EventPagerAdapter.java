package com.example.fire_and_based;

import android.os.Bundle;
import android.support.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventPagerAdapter extends FragmentStateAdapter {

    public EventPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new EventAnnouncementsFragment();
            case 2:
                return new EventMapFragment();
            default:
                return new EventOverviewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}