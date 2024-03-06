package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventCreation extends AppCompatActivity {
    private Button createEventSubmit;
    private EditText eventTitle;
    private EditText eventDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_creation);

        createEventSubmit = findViewById(R.id.create_new_event_submit);
        eventTitle = findViewById(R.id.event_title_input);
        eventDescription = findViewById(R.id.event_description_input);
        createEventSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitleString = eventTitle.getText().toString();
                String eventDescriptionString = eventDescription.getText().toString();
                Event newEvent = new Event(eventTitleString, eventDescriptionString, null, null);
                addNewEvent(newEvent);


                // PROBABLY NEED TO GO TO QR CODE HERE ILYA W NEW INTENT STUFF (?)
                Intent intent = new Intent(EventCreation.this, Firebase.class);
                startActivity(intent);
                // PROBABLY NEED TO GO TO QR CODE HERE ILYA W NEW INTENT STUFF (?)
                // check event class it has a QR code param - feel free to change the typing to what you need
                // its currently just a string :salute:

            }

        });



    }

    // ADD EVENT TO DATABASE HERE
    // WE WILL NEED TO UPDATE THIS PARTICULAR ONE WHEN THE QR CODE IS GENERATED IN THE NEXT SCREEN


    private void addNewEvent(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.addEventToDB(db, event);
    }
}
