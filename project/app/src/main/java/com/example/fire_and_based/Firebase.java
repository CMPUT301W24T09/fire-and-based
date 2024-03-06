package com.example.fire_and_based;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Firebase extends AppCompatActivity {
    ListView eventList;
    ArrayList<Event> eventDataList;
    EventArrayAdapter eventArrayAdapter;

    private Button createEventButton;
    private Button deleteEventButton;
    private EditText addEventEditText;
    private EditText addEventDescriptionEditText;
    protected FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference imagesRef;
    private int lastClickedIndex = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firestone_test);

        createEventButton = findViewById(R.id.create_event_button);
        eventList = findViewById(R.id.event_list);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");

        eventDataList = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Firebase.this, EventCreation.class);
                startActivity(intent);
            }
        });


        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedIndex = position;
                Event clickedEvent = eventDataList.get(lastClickedIndex);
//                updateEventBanner(clickedEvent);
                Intent intent = new Intent(Firebase.this, ViewEvent.class);
                intent.putExtra("event",  clickedEvent);
                startActivity(intent);

            }
        });


        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String event = doc.getId();
                        String eventDescription = doc.getString("eventDescription");
                        Log.d("Firestore", String.format("Event(%s, %s) fetched", event,
                                eventDescription));
                        eventDataList.add(new Event(event, eventDescription, null, null));
                        eventArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

}