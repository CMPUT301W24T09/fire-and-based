package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textview.MaterialTextView;

public class ImageListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_list_fragment, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.image_list_viewpager);
        AdminImagesAdapter adapter = new AdminImagesAdapter(this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.images_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Event Posters");
                    break;
                case 1:
                    tab.setText("Profile Pictures");
                    break;
            }
        }).attach();

        return view;
    }
}
