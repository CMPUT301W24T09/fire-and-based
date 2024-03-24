package com.example.fire_and_based;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

/**
 * This is the Adapter that allows us to swipe between tab views in the EventDetailsFragment.
 * @author Sumayya
 */
public class EventDetailsAdapter extends FragmentStateAdapter {
    private Event event;
    private String mode;

    /**
     * @param fragment if the {@link ViewPager2} lives directly in a {@link Fragment} subclass.
     * @param event the event we will display details for
     * @param mode the mode (either "Attending or Organizing)
     * @see FragmentStateAdapter#FragmentStateAdapter(FragmentActivity)
     */
    public EventDetailsAdapter(@NonNull Fragment fragment, Event event, String mode) {
        super(fragment);
        this.event = event;
        this.mode = mode;
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
                return NotifcationsFragment.newInstance(event);
            case 2:
                return MapFragment.newInstance(event);
            default:
                return InfoFragment.newInstance(event, mode);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 3;
    }
}
