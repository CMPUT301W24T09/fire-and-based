package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This class displays the promotion for an event: making an event poster,
 * and generating a QR code for promotion.
 * Outstanding issues: Need to actually make xml for this and set up the qr code for promotion.
 */
public class EventPromotionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_promotion_fragment, container, false);
        return view;
    }
}
