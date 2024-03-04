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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public classFirebase extends AppCompatActivity {
    ListView eventList;
    ArrayList<Event> eventDataList;
    EventArrayAdapter eventArrayAdapter;

    private Button addEventButton;
    private Button deleteEventButton;
    private EditText addEventEditText;
    private EditText addEventDescriptionEditText;
    protected FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference imagesRef;
    private int lastClickedIndex = -1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firestone_test);

        addEventEditText = findViewById(R.id.event_name_edit);
        addEventDescriptionEditText = findViewById(R.id.event_description_edit);
        addEventButton = findViewById(R.id.add_event_button);
        deleteEventButton = findViewById(R.id.delete_button);

        eventList = findViewById(R.id.event_list);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
//        imagesRef = db.collections ("images");
        eventDataList = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);



        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = addEventEditText.getText().toString();
                String eventDescription = addEventDescriptionEditText.getText().toString();
                Event newEvent = new Event(eventName, eventDescription);
                eventDataList.add(newEvent);
                addEventEditText.setText("");
                addEventDescriptionEditText.setText("");
                eventArrayAdapter.notifyDataSetChanged();
                addNewEvent(newEvent);
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


        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickedIndex != -1){
                    Event eventToDelete = eventDataList.get(lastClickedIndex);
                    eventDataList.remove(lastClickedIndex);
                    lastClickedIndex = -1;
                    deleteEvent(eventToDelete);
                    eventArrayAdapter.notifyDataSetChanged();
                }
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
                        String eventDescription = doc.getString("Province");
                        Log.d("Firestore", String.format("Event(%s, %s) fetched", event,
                                eventDescription));
                        eventDataList.add(new Event(event, eventDescription));
                    }
                }
            }
        });

    }




    private void addNewEvent(Event event) {

        event.setEventBanner("Event banner test");
        db.collection("events").document(event.getEventName())
                .set(event);
    }

    private void deleteEvent(Event event){
        db.collection("events").document(event.getEventName())
                .delete();
    }

    private void updateEventBanner(Event event){
//        event.setEventBanner("New event banner");
//        db.collection("events").document(event.getEventName())
//                .set(event);
        // OR bottom is way less fucked for top one we have to set all params again
        db.collection("events").document(event.getEventName())
                .update("eventBanner", "NewEventBanner");
    }

}
