package com.example.fire_and_based;

import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

/**
 * This fragment is hosted by EventDetailsFragment.
 * It displays the info for an event.
 * @author Sumayya, Tyler
 * To-do (UI):
 * 1. Something wrong with XML
 */
public class InfoFragment extends Fragment {
    private Event event;
    private String mode;
    private User user;

    /**
     * Creates a new instance of the InfoFragment with the provided event data.
     *
     * @param event The Event object to be associated with the fragment.
     * @param mode the mode ("Organizing" or "Attending")
     * @return A new instance of InfoFragment with the specified event data.
     */
    public static InfoFragment newInstance(Event event, String mode, User user) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putString("mode", mode);
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);

        if (getArguments() != null) {
            event = getArguments().getParcelable("event");
            mode = getArguments().getString("mode");
            user = getArguments().getParcelable("user");
        }

        TextView eventDescription = view.findViewById(R.id.event_description);
        eventDescription.setText(event.getEventDescription());




        Button leaveEventButton = view.findViewById(R.id.additional_actions_button);
        if (Objects.equals(mode, "Organizing")) {
            leaveEventButton.setVisibility(View.INVISIBLE);
        }
        if (Objects.equals(mode, "Attending")) {
            leaveEventButton.setText("Leave Event");
            int resolvedColor = ContextCompat.getColor(requireContext(), R.color.red);
            leaveEventButton.setBackgroundColor(resolvedColor);
        }

        leaveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUtil.removeUserFromEvent(db, user, event, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "See you next time!", Toast.LENGTH_SHORT).show();


                        // decrement your thing here aiden


                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Something went wrong please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // qr code viewer button
        MaterialButton QRCodeViewerButton = view.findViewById(R.id.view_qr_code);
        QRCodeViewerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayQR(event.getQRcode(), event.getBannerQR(), event.getEventName());
            }
        });



        return view;
    }


    public void displayQR(String QRCode, String PosterQRCode, String eventName) {
        QRCodeDisplayFragment fragment = new QRCodeDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventQR", QRCode);
        bundle.putString("eventName", eventName);
        bundle.putString("posterQR", PosterQRCode);
        fragment.setArguments(bundle);
        fragment.show(getParentFragmentManager(), "QRCodeDisplayFragment");
    }
}
