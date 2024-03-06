package com.example.fire_and_based;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static void addEventToDB (FirebaseFirestore db, Event event){
        // NEED TO ADD SAFETY NET OR ELSE WE HAVE DUPES WILL DO EVENTUALLY BUT AFTER FRIDAY DUE DATE
        // I THINK THIS ALSO WORKS FOR UPDATING BUT OVERWRITES PREVIOUS TOO
        db.collection("events").document(event.getEventName()).set(event);
    }
    public static void addUserToDB (FirebaseFirestore db, User user){
        db.collection("users").document(user.getDeviceID()).set(user);
    }

    public static void deleteEvent(FirebaseFirestore db, Event event){
        db.collection("events").document(event.getEventName()).delete();
    }



}
