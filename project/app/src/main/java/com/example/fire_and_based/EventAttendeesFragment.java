package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This class displays the list of attendees.
 * Outstanding issues: needs to be updated to actually display the list of attendees,
 * needs to be connected to the proper xml file.
 */
public class EventAttendeesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_list, container, false);
        return view;
    }
}
