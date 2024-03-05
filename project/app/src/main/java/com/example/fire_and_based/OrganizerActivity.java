package com.example.fire_and_based;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OrganizerActivity extends AppCompatActivity {
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_list);

        dataList = new ArrayList<Event>();
        dataList.add(new Event("Event 1", "1"));
        dataList.add(new Event("Event 2", "2"));
        dataList.add(new Event("Event 3", "3"));
        eventList = findViewById(R.id.event_list);
        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);
    }
}
