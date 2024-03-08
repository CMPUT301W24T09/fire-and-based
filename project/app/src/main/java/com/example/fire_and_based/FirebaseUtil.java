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

import java.nio.charset.StandardCharsets;

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
        // WILL override an event with the same QR code. Need to fix but async stuff strikes again
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events").document(docID).set(event);
    }
    public static void eventExists(FirebaseFirestore db, Event event){
        db.collection("events");
    }
    public static void addUserToDB (FirebaseFirestore db, User user){
        db.collection("users").document(user.getDeviceID()).set(user);
    }

    public static void deleteEvent(FirebaseFirestore db, Event event){
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events").document(docID).delete();
    }
    public static void updateEventBanner(FirebaseFirestore db, Event event, String newEventBanner){
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events").document(docID)
                .update("eventBanner", newEventBanner);
    }

    public static void updateEventDescription(FirebaseFirestore db, Event event, String eventBanner){
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events").document(docID)
                .update("eventBanner", eventBanner);
    }

    public static void updateUserProfileImageUrl(FirebaseFirestore db, User user, String profilePictureURL){
        db.collection("users").document(user.getDeviceID())
                .update("profilePicture", profilePictureURL);
    }



    // get an event object from Firebase by QR Code
    public static void getEvent(FirebaseFirestore db, String QRCode, final EventCallback callback) {
        String docID = cleanDocumentId(QRCode);
        // Handle any errors that occur while fetching the document
        db.collection("events").document(docID).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String eventName = doc.getString("eventName");
                String eventDescription = doc.getString("eventDescription");
                String eventBanner = doc.getString("eventBanner");
                String qrCode = doc.getString("QRcode");
                Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName,
                        qrCode));
                Event event = new Event(eventName, eventDescription, eventBanner, qrCode);
                callback.onEventFetched(event);
            } else { //document not found, does not exist
                callback.onError(new Exception("Event document not found"));
            }
        }).addOnFailureListener(callback::onError);
    }
    public interface EventCallback {
        void onEventFetched(Event event);
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




// EVEN BETTER JUST GET THE ENTIRE EVENT OBJECTS A USER IS IN
// (sorry I deleted the other function, you can get it from VCS if needed but its kinda obsolete)
    public static void getUserEvents(FirebaseFirestore db, String userID, final UserEventsAndFetchCallback callback) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> rawEvents = (List<Map<String, Object>>) documentSnapshot.get("events");
                if (rawEvents != null && !rawEvents.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>();
                    for (Map<String, Object> rawEvent : rawEvents) {
                        if (rawEvent.containsKey("QRcode")) {
                            String eventCode = (String) rawEvent.get("QRcode");
                            eventCodes.add(eventCode);
                        }
                    }

                    ArrayList<Event> events = new ArrayList<>();
                    for (String eventCode : eventCodes) {
                        String docID = cleanDocumentId(eventCode);
                        db.collection("events").document(docID).get().addOnSuccessListener(document -> {
                            String eventName = document.getString("eventName");
                            String eventDescription = document.getString("eventDescription");
                            String eventBanner = document.getString("eventBanner");
                            String qrCode = document.getString("QRcode");
                            Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName, qrCode));
                            Event event = new Event(eventName, eventDescription, eventBanner, qrCode);
                            events.add(event);

                            if (events.size() == eventCodes.size()) {
                                callback.onEventsFetched(events);
                            }
                        }).addOnFailureListener(callback::onError);
                    }
                } else {
                    callback.onError(new Exception("User exists but has no Events"));
                }
            } else {
                callback.onError(new Exception("User document not found"));
            }
        }).addOnFailureListener(callback::onError);
    }

    public interface UserEventsAndFetchCallback {
        void onEventsFetched(ArrayList<Event> events);
        void onError(Exception e);
    }

    //example call:
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    String userID = "yourUserID";
//
//        FirebaseUtil.getUserEvents(db, userID, new FirebaseUtil.UserEventsAndFetchCallback() {
//        @Override
//        public void onEventsFetched(ArrayList<Event> events) {
//            events.forEach(event -> System.out.println("Fetched event: " + event.getEventName()));
//        }
//
//        @Override
//        public void onError(Exception e) {
//            System.err.println("An error occurred: " + e.getMessage());
//        }
//    });


    /**
     * Given QR Codes may contain invalid characters, have to replace
     * @param docId the possibly invalid string
     * @return a valid Firebase ID
     */
    private static String cleanDocumentId(String docId) {
        // Replace forward slashes with backward slashes
        docId = docId.replace("/", "\\");

        // Replace single period or double periods with %
        docId = docId.replace(".", "%");
        docId = docId.replace("..", "%%");

        // Replace any characters matching the regular expression __.*__ with $$$
        docId = docId.replaceAll("__.*__", "$$$");

        // Ensure the string is no longer than 1500 bytes
        byte[] docIdBytes = docId.getBytes(StandardCharsets.UTF_8);
        if (docIdBytes.length > 1500) {
            docId = new String(docIdBytes, 0, 1500, StandardCharsets.UTF_8);
        }

        return docId;
    }


}
