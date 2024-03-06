package com.example.fire_and_based;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        Button testButton = findViewById(R.id.create_event_button);
        String eventName = clickedEvent.getEventName();
        testButton.setText(eventName);

        TextView titleText = findViewById(R.id.event_name_text);
        titleText.setText(clickedEvent.getEventName());

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEvent.this, Firebase.class);
                startActivity(intent);
            }
        });
    }
}
