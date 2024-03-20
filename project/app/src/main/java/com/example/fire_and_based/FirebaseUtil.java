package com.example.fire_and_based;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.*;
import com.google.type.DateTime;

import java.util.ArrayList;

import java.util.HashMap;


import java.util.List;
import java.util.Map;

import java.nio.charset.StandardCharsets;

/**
 * This class has all the functions for accessing the necessary data from the firebase
 * or storing the necessary data in the firebase.
 */

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
        eventsRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
                //TODO wait no add a proper onFailure() thing to the callback
            }
            if (querySnapshots != null) {
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    String eventName = doc.getString("eventName");
                    String eventDescription = doc.getString("eventDescription");
                    String eventBanner = doc.getString("eventBanner");
                    String qrCode = doc.getString("QRcode");
                    long eventStart = doc.getLong("eventStart");
                    long eventEnd = doc.getLong("eventEnd");
                    String location = doc.getString("location");
                    String bannerQR = doc.getString("bannerQR");
                    ArrayList<Integer> milestones = (ArrayList<Integer>) doc.get("milestones");
                    int maxAttendees = Math.toIntExact(doc.getLong("maxAttendees"));
                    boolean trackLocation = doc.getBoolean("trackLocation");
                    Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName,
                            qrCode));
                    eventsList.add(new Event(eventName, eventDescription, eventBanner, qrCode, eventStart, eventEnd, location, bannerQR, milestones, maxAttendees, trackLocation));
                }
                Log.d("Firestore", String.format("Fetched %d events", eventsList.size()));
                callback.onCallback(eventsList);
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

    /**
     * Add a user to the database
     *
     * @param db   the database
     * @param user the user
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see User
     */
    public static void addUserToDB(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection("users")
                .document(user.getDeviceID())
                .set(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Delete event from database
     *
     * @param db    the database
     * @param event the user
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     */
    public static void deleteEvent(FirebaseFirestore db, Event event, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events")
                .document(docID)
                .delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    //TODO add more update classes?

    /**
     * Update an event's banner
     *
     * @param db             the database
     * @param event          the user
     * @param newEventBanner the url to a new banner
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     */
    public static void updateEventBanner(FirebaseFirestore db, Event event, String newEventBanner, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events")
                .document(docID)
                .update("eventBanner", newEventBanner)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Update user's pfp
     *
     * @param db                the database
     * @param user              the user
     * @param profilePictureURL the url to a new pfp
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     */
    public static void updateUserProfileImageUrl(FirebaseFirestore db, User user, String profilePictureURL, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection("users").document(user.getDeviceID())
                .update("profilePicture", profilePictureURL)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    // get an event object from Firebase by QR Code

    /**
     * @param db       the database
     * @param QRCode   the QR Code of an event to get
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     */
    public static void getEvent(FirebaseFirestore db, String QRCode, OnSuccessListener<Event> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(QRCode);
        // Handle any errors that occur while fetching the document
        db.collection("events").document(docID).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String eventName = doc.getString("eventName");
                String eventDescription = doc.getString("eventDescription");
                String eventBanner = doc.getString("eventBanner");
                String qrCode = doc.getString("QRcode");
                long eventStart = doc.getLong("eventStart");
                long eventEnd = doc.getLong("eventEnd");
                String location = doc.getString("location");
                String bannerQR = doc.getString("bannerQR");
                ArrayList<Integer> milestones = (ArrayList<Integer>) doc.get("milestones");
                int maxAttendees = Math.toIntExact(doc.getLong("maxAttendees"));
                boolean trackLocation = doc.getBoolean("trackLocation");

                Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName,
                        qrCode));
                Event event = new Event(eventName, eventDescription, eventBanner, qrCode, eventStart, eventEnd, location, bannerQR, milestones, maxAttendees, trackLocation);
                successListener.onSuccess(event);
            } else { //document not found, does not exist
                failureListener.onFailure(new Exception("Event document not found"));
            }
        }).addOnFailureListener(failureListener);
    }


    /**
     * Asynchronously get list of Events a user is in
     *
     * @param db       the database reference
     * @param userID   the ID of the user to get events of
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     * @see User
     */
    public static void getUserEvents(FirebaseFirestore db, String userID, OnSuccessListener<ArrayList<Event>> successListener, OnFailureListener failureListener) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> eventIDs = (List<String>) documentSnapshot.get("attendeeEvents");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>(eventIDs);
                    ArrayList<Event> events = new ArrayList<>();
                    for (String eventCode : eventCodes) {
                        String docID = cleanDocumentId(eventCode);
                        getEvent(db, docID, event -> {
                            events.add(event);
                            if (events.size() == eventCodes.size()) {
                                successListener.onSuccess(events);
                            }
                        }, failureListener);
                    }
                } else {
                    failureListener.onFailure(new Exception("User exists but has no Events"));
                }
            } else {
                failureListener.onFailure(new Exception("User document not found"));
            }
        }).addOnFailureListener(failureListener);
    }

    /**
     * Get a User object in the database from their ID
     * @param db db reference
     * @param userID the ID of the user
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     */
    public static void getUserObject(FirebaseFirestore db, String userID, OnSuccessListener<User> successListener, OnFailureListener failureListener) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                Log.d(TAG, String.format("Username: %s First: %s Last: %s Phone: %s Email: %s ID: %s", user.getUserName(), user.getFirstName(), user.getLastName()
                        , user.getPhoneNumber(), user.getEmail(), user.getDeviceID()));
                successListener.onSuccess(user);
            } else {
                failureListener.onFailure(new Exception("User document does not exist."));
            }
        }).addOnFailureListener(failureListener);
    }

    /**
     * Update a User object in the database
     * @param db db reference
     * @param user complete user object to be replaced
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     */
    public static void updateProfileInfo(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", user.getFirstName());
        updates.put("lastName", user.getLastName());
        updates.put("email", user.getEmail());
        updates.put("phoneNumber", user.getPhoneNumber());
        updates.put("homepage", user.getHomepage());
        db.collection("users").document(user.getDeviceID())
                .update(updates)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
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
                            getEvent(db, eventQRCode, callback::onEventFound, callback::onError);
                            return;
                        }
                    }
                    Log.println(Log.DEBUG, "FirebaseUtil", "User not in event, searching for event...");
                    //IF EVENT NOT FOUND WE STILL NEED IT
                    getEvent(db, eventQRCode, callback::onEventNotFound, callback::onError);
                    return;
                } else {
                    Log.println(Log.DEBUG, "FirebaseUtil", "User not in ANY event, searching for event...");
                    //IF EVENT NOT FOUND WE STILL NEED IT
                    Log.println(Log.DEBUG, "FirebaseUtil", "User not in ANY event, searching for event... " + eventQRCode);
                    getEvent(db, eventQRCode, callback::onEventNotFound, callback::onError);
                    return;
                }
            } else {
                callback.onError(new Exception("User document not found"));
            }
        }).addOnFailureListener(callback::onError);
    }


    /**
     * Adds the eventID to the user's attended events field,
     * and the attendee ID to the event's attendees field.
     * Both succeed or fail simultaneously
     *
     * @param db       the database reference
     * @param eventId  the event ID to add and be added to
     * @param attendee the user ID to add and be added to
     */
    public static void addEventAndAttendee(FirebaseFirestore db, String eventId, String attendee, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        DocumentReference eventDoc = db.collection("events").document(eventId);
        DocumentReference userDoc = db.collection("users").document(attendee);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventDoc);
                    DocumentSnapshot userSnapshot = transaction.get(userDoc);

                    List<String> attendees = (List<String>) eventSnapshot.get("attendees");
                    Integer maxAttendees = (Integer) eventSnapshot.get("maxAttendees");
                    if (maxAttendees != null && maxAttendees <= attendees.size()){
                        throw new IllegalArgumentException("Event reached max capacity");
                    }

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
                }).addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
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

}