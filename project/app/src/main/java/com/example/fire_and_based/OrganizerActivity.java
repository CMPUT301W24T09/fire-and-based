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
    private ArrayList<String> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_list);

        dataList = new ArrayList<>();
        dataList.add("Event 1");
        dataList.add("Event 2");
        eventList = findViewById(R.id.event_list);
        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);
    }
}
