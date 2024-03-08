package com.example.fire_and_based;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {

    // FOR THHIS CLASS THERE ARE TWO ARGS YOU NEED
    // THE DATABASE AND THE OBJECT
    // EXAMPLE HERE

    // this is what another file would look like ( not this one )
    // private void addNewEvent(Event event) {
    //        FirebaseFirestore db = FirebaseFirestore.getInstance();
    //        FirebaseUtil.addEventToDB(db, event);
    //    }



    //get all events asynchronously
    public interface FirestoreCallback {
        void onCallback(List<Event> list);
    }

    public static void getAllEvents(FirebaseFirestore db, FirestoreCallback callback){
        ArrayList<Event> eventsList = new ArrayList<>();

        CollectionReference eventsRef = db.collection("events");
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventName = doc.getString("eventName");
                        String eventDescription = doc.getString("eventDescription");
                        String eventBanner = doc.getString("eventBanner");
                        String qrCode = doc.getString("QRcode");
                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName,
                                qrCode));
                        eventsList.add(new Event(eventName, eventDescription, eventBanner, qrCode));
                    }
                    Log.d("Firestore", String.format("Fetched %d events", eventsList.size()));
                    callback.onCallback(eventsList);
                    eventsList.clear();
                }
            }
        });
    }





    public static void addEventToDB (FirebaseFirestore db, Event event){
        // NEED TO ADD SAFETY NET OR ELSE WE HAVE DUPES WILL DO EVENTUALLY BUT AFTER FRIDAY DUE DATE
        // I THINK THIS ALSO WORKS FOR UPDATING BUT OVERWRITES PREVIOUS TOO
        db.collection("events").document(event.getQRcode()).set(event);
    }
    public static void eventExists(FirebaseFirestore db, Event event){
        db.collection("events").document();
    }
    public static void addUserToDB (FirebaseFirestore db, User user){
        db.collection("users").document(user.getDeviceID()).set(user);
    }

    public static void deleteEvent(FirebaseFirestore db, Event event){
        db.collection("events").document(event.getQRcode()).delete();
    }
    public static void updateEventBanner(FirebaseFirestore db, Event event, String newEventBanner){
        db.collection("events").document(event.getQRcode())
                .update("eventBanner", newEventBanner);
    }

    public static void updateEventDescription(FirebaseFirestore db, Event event, String eventBanner){
        db.collection("events").document(event.getQRcode())
                .update("eventBanner", eventBanner);
    }

    public static void updateUserProfileImageUrl(FirebaseFirestore db, User user, String profilePictureURL){
        db.collection("users").document(user.getDeviceID())
                .update("profilePicture", profilePictureURL);
    }



    // HERE YOU GET THE BANNER URL FROM THE FIREBASE BY GIVING IT AN EVENT OBJECT
    // BELOW I HAVE THE CODE YOU CAN JUST COPY PASTE
    public static void getEventBannerUrl(FirebaseFirestore db, Event event, final EventBannerCallback callback) {
        db.collection("events").document(event.getQRcode()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String bannerUrl = documentSnapshot.getString("eventBanner");
                    if (bannerUrl != null) {
                        callback.onBannerUrlFetched(bannerUrl);
                    } else {
                        // Handle the case where the eventBanner field is missing
                        callback.onError(new Exception("Banner URL not found in the document"));
                    }
                } else {
                    // Handle the case where the document does not exist
                    callback.onError(new Exception("Event document not found"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur while fetching the document
                callback.onError(e);
            }
        });
    }
    public interface EventBannerCallback {
        void onBannerUrlFetched(String bannerUrl);
        void onError(Exception e);
    }

//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUtil.getEventBannerUrl(db, clickedEvent, new FirebaseUtil.EventBannerCallback() {
//        @Override
//        public void onBannerUrlFetched(String bannerUrl) {
//            // DO WHAT U NEED TO IN HERE TO DISPLAY STUFF
//        }
//        @Override
//        public void onError(Exception e) {
//           // HANDLE THE ERROR
//        }
//    });





    // GET EVENT NAMES OF EACH EVENT A USER IS REGISTERED IN
    public static void getUserEvents(FirebaseFirestore db, String userID, final UserEventsCallback callback) {
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // brother what is going on here W LLM btw
                    List<Map<String, Object>> rawEvents = (List<Map<String, Object>>) documentSnapshot.get("events");
                    if (rawEvents != null && !rawEvents.isEmpty()) {
                        ArrayList<String> eventNames = new ArrayList<>();
                        for (Map<String, Object> rawEvent : rawEvents) {
                            if (rawEvent.containsKey("eventName")) {
                                String eventName = (String) rawEvent.get("eventName");
                                eventNames.add(eventName);
                            }
                        }
                        callback.onEventNamesFetched(eventNames);
                    } else {
                        callback.onError(new Exception("User exists but has no Events"));
                    }
                } else {
                    callback.onError(new Exception("User document not found"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {   // Handle any errors that occur while fetching the document
                callback.onError(e);
            }
        });
    }

    public interface UserEventsCallback {
        void onEventNamesFetched(ArrayList<String> eventNames);
        void onError(Exception e);
    }

    // HERES HOW TO USE IT
//     FirebaseUtil.getUserEvents(db, userId, new FirebaseUtil.UserEventsCallback() {
//
//        @Override
//        public void onEventNamesFetched(ArrayList<String> eventNames) {
//            // U HAVE ANN ARRAY OF EVENT NAMES NOW U CAN RENDER THE DATA ON A VIEW OR SOMETHIGN
              // IN HERE DO THAT
//        }
//        @Override
//        public void onError(Exception e) {
//            // DEAL WITH ERROrS
//        }
//    });

}
