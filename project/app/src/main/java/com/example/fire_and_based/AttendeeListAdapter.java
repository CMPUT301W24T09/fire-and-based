package com.example.fire_and_based;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

/**
 * This is the Adapter that allows us to swipe between tab views in the AttendeeFragment.
 * @author Sumayya
 */
public class AttendeeListAdapter extends FragmentStateAdapter {
    private Event event;

    /**
     * @param fragment if the {@link ViewPager2} lives directly in a {@link Fragment} subclass.
     * @param event the event we will display details for
     * @see FragmentStateAdapter#FragmentStateAdapter(FragmentActivity)
     */
    public AttendeeListAdapter(@NonNull Fragment fragment, Event event) {
        super(fragment);
        this.event = event;
    }

    /**
     * Provide a new Fragment associated with the specified position.
     * <p>
     * The adapter will be responsible for the Fragment lifecycle:
     *
     * @param position
     * @see ViewPager2#setOffscreenPageLimit
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return AttendeeCheckedInFragment.newInstance(event);
            default:
                return AttendeeListFragment.newInstance(event);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
