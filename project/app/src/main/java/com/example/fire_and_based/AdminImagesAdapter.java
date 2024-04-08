package com.example.fire_and_based;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

/**
 * This is the Adapter that allows us to swipe between tab views in the ImageListFragment.
 * @author Sumayya
 */
public class AdminImagesAdapter extends FragmentStateAdapter {

    /**
     * @param fragment if the {@link ViewPager2} lives directly in a {@link Fragment} subclass.
     * @see FragmentStateAdapter#FragmentStateAdapter(FragmentActivity)
     */
    public AdminImagesAdapter(@NonNull Fragment fragment) {
        super(fragment);
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
                return new ProfilePicturesFragment();
            default:
                return new EventPostersFragment();
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
