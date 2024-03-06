package com.example.fire_and_based;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class ViewEvent extends AppCompatActivity {
    public Event clickedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedEvent = getIntent().getParcelableExtra("event");
        }

//        TextView titleText = findViewById(R.id.event_title);
//        titleText.setText(clickedEvent.getEventName());

        TextView titleText2 = findViewById(R.id.event_title2);
        titleText2.setText(clickedEvent.getEventName());

        TextView eventDescription = findViewById(R.id.event_description);
        eventDescription.setText(clickedEvent.getEventDescription());

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEvent.this, Firebase.class);
                startActivity(intent);
            }
        });

        Button deleteEventButton = findViewById(R.id.deleteEventButton);
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUtil.deleteEvent(db, clickedEvent);

                Intent intent = new Intent(ViewEvent.this, Firebase.class);
                startActivity(intent);
            }
        });
    }
}
