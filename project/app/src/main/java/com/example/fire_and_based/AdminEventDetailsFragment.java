package com.example.fire_and_based;

import static com.example.fire_and_based.EditEventFragment.convertTimestampToCalendarDateAndTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

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
        View view = inflater.inflate(R.layout.admin_event_details_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
        }

        TextView eventTitle = view.findViewById(R.id.admin_event_title);
        EditText eventDescription = view.findViewById(R.id.admin_event_description);
        EditText eventStartDate = view.findViewById(R.id.start_date);
        EditText eventEndDate = view.findViewById(R.id.event_date);
        EditText eventStartTime = view.findViewById(R.id.edit_time);
        EditText eventLocation = view.findViewById(R.id.event_location);
        EditText eventAttendeeAmount = view.findViewById(R.id.max_attendees);

        eventTitle.setText(event.getEventName());
        eventDescription.setText(event.getEventDescription());
        Long eventStartDateLong = event.getEventStart();          // sets start date after converting
        String[] dateTime = convertTimestampToCalendarDateAndTime(eventStartDateLong);
        String startCalendar = dateTime[0]; // date
        String startTime = dateTime[1]; // time
        eventStartDate.setText(startCalendar);
        eventStartTime.setText(startTime);
        Long eventEndDateLong = event.getEventEnd();         // sets end date after converting
        String endCalendar = convertTimestampToCalendarDateAndTime(eventEndDateLong)[0];
        eventEndDate.setText(endCalendar);
        eventLocation.setText(event.getLocation());
        eventAttendeeAmount.setText(event.getMaxAttendees().toString());

        TextView cancel = view.findViewById(R.id.admin_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        TextView deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getParentFragmentManager();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUtil.deleteEvent(db, event.getQRcode(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Event deleted from database :) ", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < 2; i++) {
                            fragmentManager.popBackStackImmediate();
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Cannot delete event from database", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        return view;
    }
}
