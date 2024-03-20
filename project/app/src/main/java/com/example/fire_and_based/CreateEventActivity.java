package com.example.fire_and_based;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

/**
 * This class is the activity for creating a new event.
 * It can be accessed by clicking the create event button on UserActivity.
 * To-do (Firebase):
 * 1. (I think?) FirebaseUtil.addEventToDB needs to be updated to add event to user's list of organizing events
 * 2. AIDEN: run the app and click the blue plus in middle of screen -> you see the white rectangle at the top, its an imageview, i want you to set an onclick for image uploading there
 * 3. ILYA: run the app and see the two QR code buttons, there is zero code for them and no id's either go to create_event_and_edit.xml and connect those buttons to the qr code upload / generate functionality
 */
public class CreateEventActivity extends AppCompatActivity {
    private User user;
    public Date newEventDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_and_edit);

        // get user being passed into activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = getIntent().getParcelableExtra("user");
        }

        // get all textfields
        EditText eventName = findViewById(R.id.event_name_editable);
        EditText eventDescription = findViewById(R.id.event_description_editable);
        EditText eventDate = findViewById(R.id.event_date_editable); // Make sure to replace 'your_date_button_id' with the actual ID of your button in the layout
        EditText eventTime = findViewById(R.id.event_time_editable); // Initialize it as per your actual layout component
        EditText eventLocation = findViewById(R.id.event_location_editable);
        EditText eventMaxAttendees = findViewById(R.id.event_maximum_attendees_editable);


        // set textfield to the current date ( for making things more obvious and easier for them to click )
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        eventDate.setText(String.format("%d-%d-%d", mDay, mMonth + 1, mYear)); // Adding 1 to month as Calendar.MONTH is zero-based


        // onclick for the event date that opens a calendar ui for them to select a date
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {

                            eventDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });



        // Onclick for the event time to open a clock ui for them to select a time
        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = c.get(Calendar.HOUR_OF_DAY); // current hour
                int mMinute = c.get(Calendar.MINUTE); // current minute

                // Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                        (view, hourOfDay, minute) -> {
                            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                            eventTime.setText(formattedTime);
                        }, mHour, mMinute, false); // 'false' for 12-hour clock or 'true' for 24-hour clock
                timePickerDialog.show();
            }
        });


        // Cancel Button in the top left -> goes back to browse IDK where else to go kekw - seems ok for now
        TextView cancelButton = findViewById(R.id.cancel_event_creation);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, UserActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


        // Create Event Submission
        Button createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HANDLE SUBMIT
                // CREATE EVENT
                // CHECK THAT EACH PARAMETER IS PROPER
                String eventNameString = eventName.getText().toString();
                String eventDescriptionString = eventDescription.getText().toString();
                String eventDateString = eventDate.getText().toString();
                String eventTimeString = eventTime.getText().toString();
                String eventLocationString = eventLocation.getText().toString();
                String eventMaxAttendeesString = eventMaxAttendees.getText().toString();

                if (eventNameString.length() < 5){
                    Toast.makeText(getApplicationContext(), "Title not long enough", Toast.LENGTH_SHORT).show();
                } else if (eventDescriptionString.length() < 5){
                    Toast.makeText(getApplicationContext(), "Desciption not long enough", Toast.LENGTH_SHORT).show();
                } else if (eventDateString.length() < 1){
                    Toast.makeText(getApplicationContext(), "You need an event Date", Toast.LENGTH_SHORT).show();
                } else if (eventTimeString.length() < 1){
                    Toast.makeText(getApplicationContext(), "You need an event time", Toast.LENGTH_SHORT).show();
                } else if (eventLocationString.length() < 5){
                    Toast.makeText(getApplicationContext(), "Your event needs a location", Toast.LENGTH_SHORT).show();
                } else if (eventMaxAttendeesString.length() < 1){
                    Toast.makeText(getApplicationContext(), "Set the maximum number of attendees", Toast.LENGTH_SHORT).show();

                    // add handling for qr code and banner as well
                    // add more handling here if needed

                } else {
                    // we can create the event object
                    // Event newEvent = new Event(.....)

                    // then pass to database
                    // user was passed into this activity it is a public User object named 'user'
                    // use that to write to database and properly store in user events
                    // also make sure that in the event the user is an organizer :salute:
                }




            }
        });
    }
}
