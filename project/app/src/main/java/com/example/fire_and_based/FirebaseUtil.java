package com.example.fire_and_based;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    // FOR THHIS CLASS THERE ARE TWO ARGS YOU NEED
    // THE DATABASE AND THE OBJECT
    // EXAMPLE HERE

    // private void addNewEvent(Event event) {
    //        FirebaseFirestore db = FirebaseFirestore.getInstance();
    //        FirebaseUtil.addEventToDB(db, event);
    //    }


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
    public static void updateEventBanner(FirebaseFirestore db, Event event, String newEventBanner){
        db.collection("events").document(event.getEventName())
                .update("eventBanner", newEventBanner);
    }

    public static void updateEventDescription(FirebaseFirestore db, Event event, String eventBanner){
        db.collection("events").document(event.getEventName())
                .update("eventBanner", eventBanner);
    }

    public static void updateUserProfileImageUrl(FirebaseFirestore db, User user, String profilePictureURL){
        db.collection("users").document(user.getDeviceID())
                .update("profilePicture", profilePictureURL);
    }






}
