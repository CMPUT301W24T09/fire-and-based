package com.example.fire_and_based;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.net.Uri;

import androidx.annotation.NonNull;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.*;

import org.jetbrains.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.HashMap;


import java.util.List;
import java.util.Map;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class FirebaseUtil {

    // FOR THHIS CLASS THERE ARE TWO ARGS YOU NEED
    // THE DATABASE AND THE OBJECT
    // EXAMPLE HERE

    // this is what another file would look like ( not this one )
    // private void addNewEvent(Event event) {
    //        FirebaseFirestore db = FirebaseFirestore.getInstance();
    //        FirebaseUtil.addEventToDB(db, event);
    //    }

    /**
     * Asynchronously get all events in the database
     *
     * @param db       the database reference
     * @param callback the callback function
     * @see getAllEventsCallback
     * @see Event
     */
    public static void getAllEvents(FirebaseFirestore db, getAllEventsCallback callback) {
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
                    for (QueryDocumentSnapshot doc : querySnapshots) {
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

    /**
     * Callback interface to get list of all events
     *
     * @see Event
     */
    public interface getAllEventsCallback {
        void onCallback(List<Event> list);
    }


    /**
     * This interface defines the callback methods for the addEventToDB operation.
     *
     * @see Event
     */
    interface AddEventCallback {
        /**
         * Called when the event is successfully added to the database.
         */
        void onEventAdded();

        /**
         * Called when an event with the same ID already exists in the database.
         */
        void onEventExists();

        /**
         * Called when there's an error during the operation.
         *
         * @param e The exception that occurred.
         */
        void onError(Exception e);
    }

    /**
     * Adds an event to the Firestore database. If an event with the same ID already exists, it will not be added.
     *
     * @param db       The Firestore database instance.
     * @param event    The event to add.
     * @param callback The callback to handle the different outcomes of the operation.
     * @see AddEventCallback
     * @see Event
     */
    public static void addEventToDB(FirebaseFirestore db, Event event, AddEventCallback callback) {
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events").document(docID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    callback.onEventExists();
                } else {
                    db.collection("events").document(docID).set(event)
                            .addOnSuccessListener(aVoid -> callback.onEventAdded())
                            .addOnFailureListener(callback::onError);
                }
            } else {
                callback.onError(task.getException());
            }
        });
    }

    //TODO change these to be async like add event

    /**
     * Add a user to the database
     *
     * @param db   the database
     * @param user the user
     * @see User
     */
    public static void addUserToDB(FirebaseFirestore db, User user) {
        db.collection("users").document(user.getDeviceID()).set(user);
    }

    /**
     * Delete event from database
     *
     * @param db    the database
     * @param event the user
     * @see Event
     */
    public static void deleteEvent(FirebaseFirestore db, Event event) {
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events").document(docID).delete();
    }

    //TODO add more update classes?

    /**
     * Update an event's banner
     *
     * @param db             the database
     * @param event          the user
     * @param newEventBanner the url to a new banner
     * @see Event
     */
    public static void updateEventBanner(FirebaseFirestore db, Event event, String newEventBanner) {
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events").document(docID)
                .update("eventBanner", newEventBanner);
    }

    /**
     * Update an event's banner
     *
     * @param db                the database
     * @param user              the user
     * @param profilePictureURL the url to a new pfp
     * @see Event
     */
    public static void updateUserProfileImageUrl(FirebaseFirestore db, User user, String profilePictureURL) {
        db.collection("users").document(user.getDeviceID())
                .update("profilePicture", profilePictureURL);
    }

    // get an event object from Firebase by QR Code

    /**
     * @param db       the database
     * @param QRCode   the QR Code of an event to get
     * @param callback the callback function
     * @see GetEventCallback
     * @see Event
     */
    public static void getEvent(FirebaseFirestore db, String QRCode, final GetEventCallback callback) {
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

    /**
     * Callback function for getting an event from the database
     */
    public interface GetEventCallback {
        void onEventFetched(Event event);

        void onError(Exception e);
    }

//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUtil.getEvent(db, qrCode, new FirebaseUtil.EventCallback() {
//        @Override
//        public void onEventFetched(Event event) {
//            // DO WHAT U NEED TO IN HERE TO DISPLAY STUFF
//        }
//        @Override
//        public void onError(Exception e) {
//           // HANDLE THE ERROR
//        }
//    });


// EVEN BETTER JUST GET THE ENTIRE EVENT OBJECTS A USER IS IN
// (sorry I deleted the other function, you can get it from VCS if needed but its kinda obsolete)

    /**
     * Asynchronously get list of Events a user is in
     *
     * @param db       the database reference
     * @param userID   the ID of the user to get events of
     * @param callback the callback function UserEventsAndFetchCallback()
     * @see UserEventsAndFetchCallback
     * @see Event
     * @see User
     */
    public static void getUserEvents(FirebaseFirestore db, String userID, final UserEventsAndFetchCallback callback) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> eventIDs = (List<String>) documentSnapshot.get("events");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>(eventIDs);
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

    public static void addAttendingEvent(FirebaseFirestore db, User user) {
        String uuid = user.getDeviceID();
        ArrayList<Event> attending = user.getUserEvents();
        DocumentReference docsRef = db.collection("users").document(uuid);
        Map<String, Object> attendingMap = new HashMap<>();
        attendingMap.put("userEvents", attending);

        docsRef.set(attendingMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Added attending event");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failure.");
                    }
                });

    }

    /**
     * Callback for getting a user's registered events
     *
     * @see User
     * @see Event
     */
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


    public static void getUserObject(FirebaseFirestore db, String userID, final UserObjectCallback callback) {
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    FirebaseUtil.getUserEvents(db, userID, new UserEventsAndFetchCallback() {
                        @Override
                        public void onEventsFetched(ArrayList<Event> events) {
                            user.setUserRegisteredEvents(events);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    });
                    Log.d(TAG, String.format("Username: %s First: %s Last: %s Phone: %s Email: %s ID: %s Null: %s", user.getUserName(), user.getFirstName(), user.getLastName()
                            , user.getPhoneNumber(), user.getEmail(), user.getDeviceID(), user.getUserEvents()));
                    callback.onUserFetched(user);
                } else {
                    callback.onError(new Exception("User document does not exist."));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NotNull Exception e) {
                callback.onError(e);
            }
        });
    }

    public interface UserObjectCallback {
        void onUserFetched(User user);

        void onError(Exception e);
    }


    public static void updateProfileInfo(FirebaseFirestore db, User user) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", user.getFirstName());
        updates.put("lastName", user.getLastName());
        updates.put("email", user.getEmail());
        updates.put("phoneNumber", user.getPhoneNumber());
        db.collection("users").document(user.getDeviceID())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Document Successfully Updated");
                    }
                });
    }


    /**
     * Callback for checking if a given user is in a given event
     *
     * @see User
     * @see Event
     */
    public interface UserEventQRCodeCallback {
        void onEventFound(Event event);

        void onEventNotFound(Event event);

        void onError(Exception e);
    }

    /**
     * Async checking if a given user is in a given event
     *
     * @param db          the database reference
     * @param userID      the user ID
     * @param eventQRCode the event ID
     * @param callback    the callback
     * @see User
     * @see Event
     */
    public static void checkUserInEventByQRCode(FirebaseFirestore db, String userID, String eventQRCode, final UserEventQRCodeCallback callback) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> eventIDs = (List<String>) documentSnapshot.get("events");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    for (String qrCode : eventIDs) {
                        if (qrCode.equals(eventQRCode)) {
                            String docID = cleanDocumentId(qrCode);
                            db.collection("events").document(docID).get().addOnSuccessListener(document -> {
                                String eventName = document.getString("eventName");
                                String eventDescription = document.getString("eventDescription");
                                String eventBanner = document.getString("eventBanner");
                                Event event = new Event(eventName, eventDescription, eventBanner, qrCode);
                                Log.println(Log.DEBUG, "FirebaseUtil", "User already in event");
                                callback.onEventFound(event);
                            }).addOnFailureListener(callback::onError);
                            return;
                        }
                    }
                    Log.println(Log.DEBUG, "FirebaseUtil", "User not in event, searching for event...");
                    //IF EVENT NOT FOUND WE STILL NEED IT
                    String docID = cleanDocumentId(eventQRCode);
                    db.collection("events").document(docID).get().addOnSuccessListener(document -> {
                        String eventName = document.getString("eventName");
                        String eventDescription = document.getString("eventDescription");
                        String eventBanner = document.getString("eventBanner");
                        Event event = new Event(eventName, eventDescription, eventBanner, eventQRCode);
                        Log.println(Log.DEBUG, "FirebaseUtil", "Found event the user isnt in!");
                        callback.onEventNotFound(event);
                    }).addOnFailureListener(callback::onError);
                    return;
                } else {
                    Log.println(Log.DEBUG, "FirebaseUtil", "User not in ANY event, searching for event...");
                    //IF EVENT NOT FOUND WE STILL NEED IT
                    String docID = cleanDocumentId(eventQRCode);
                    Log.println(Log.DEBUG, "FirebaseUtil", "User not in ANY event, searching for event... " + docID);
                    db.collection("events").document(docID).get().addOnSuccessListener(document -> {
                        String eventName = document.getString("eventName");
                        String eventDescription = document.getString("eventDescription");
                        String eventBanner = document.getString("eventBanner");
                        Event event = new Event(eventName, eventDescription, eventBanner, eventQRCode);
                        Log.println(Log.DEBUG, "FirebaseUtil", "Found event the user isnt in!" + eventName);
                        callback.onEventNotFound(event);
                    }).addOnFailureListener(callback::onError);
                    return;
                }
            } else {
                callback.onError(new Exception("User document not found"));
            }
        }).addOnFailureListener(callback::onError);
    }


    //TODO need to add user to events and events to user

    /**
     * Generic shared callback interface for success/fail
     */
    interface Callback {
        void onSuccess();

        void onFailure(Exception e);
    }

    /**
     * Adds the eventID to the user's attended events field,
     * and the attendee ID to the event's attendees field.
     * Both succeed or fail simultaneously
     *
     * @param db       the database reference
     * @param eventId  the event ID to add and be added to
     * @param attendee the user ID to add and be added to
     * @param callback the callback function
     */
    public static void addEventAndAttendee(FirebaseFirestore db, String eventId, String attendee, Callback callback) {
        DocumentReference eventDoc = db.collection("events").document(eventId);
        DocumentReference userDoc = db.collection("users").document(attendee);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventDoc);
                    DocumentSnapshot userSnapshot = transaction.get(userDoc);

                    List<String> attendees = (List<String>) eventSnapshot.get("attendees");
                    if (attendees == null) {
                        attendees = new ArrayList<>();
                    }
                    attendees.add(attendee);
                    transaction.update(eventDoc, "attendees", attendees);


                    List<String> attendeeEvents = (List<String>) userSnapshot.get("attendeeEvents");
                    if (attendeeEvents == null) {
                        attendeeEvents = new ArrayList<>();
                    }
                    attendeeEvents.add(eventId);
                    transaction.update(userDoc, "attendeeEvents", attendeeEvents);

                    return null;
                }).addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }


    /**
     * Given QR Codes may contain invalid characters, have to replace
     *
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


    static void addEventToUser(FirebaseFirestore db, User userObject, Event eventToBeAdded) {
        String userDocId = userObject.getDeviceID();
        // Assuming eventToBeAdded can be serialized directly; otherwise, convert it to a Map
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventName", eventToBeAdded.getEventName()); // and so on for other Event fields

        db.collection("users").document(userDocId)
                .update("userEvents", FieldValue.arrayUnion(eventMap))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully added to user!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding event to user", e));
    }
}