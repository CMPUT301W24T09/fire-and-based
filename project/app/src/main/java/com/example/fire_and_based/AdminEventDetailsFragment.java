package com.example.fire_and_based;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * This fragment is hosted by AdminActivity.
 * It displays the event details for an admin.
 * This fragment may be accessed by clicking on an event in the list from EventListFragment, when in admin mode.
 * Requires an event to be passed in as an argument as a Parcelable with a key "event"
 * @author Sumayya
 */
public class AdminEventDetailsFragment extends Fragment {
    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_event_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }

        TextView title = view.findViewById(R.id.edit_event_title);
        title.setText(event.getEventName());

        TextView cancel = view.findViewById(R.id.cancel_edit_event);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
