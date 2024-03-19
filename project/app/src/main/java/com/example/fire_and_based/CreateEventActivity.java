package com.example.fire_and_based;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This class is the activity for creating a new event.
 * It can be accessed by clicking the create event button on UserActivity.
 * To-do (Firebase):
 * 1. (I think?) FirebaseUtil.addEventToDB needs to be updated to add event to user's list of organizing event
 */
public class CreateEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_and_edit);
    }
}
