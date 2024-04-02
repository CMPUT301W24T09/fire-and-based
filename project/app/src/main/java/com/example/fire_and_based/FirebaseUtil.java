package com.example.fire_and_based;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.*;
import com.google.type.DateTime;
import com.google.type.LatLng;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;


import java.util.List;
import java.util.Map;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This class has all the functions for accessing the necessary data from the firebase
 * or storing the necessary data in the firebase.
 * @author Ilya, Sumayya
 * To-do:
 * 1. Need a function that gets all images from the database
 * 2. getAllEventsUserNotIn has issues. see below for more details.
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
                    String qrCode = doc.getId();
                    Long eventStart = doc.getLong("eventStart");
                    Long eventEnd = doc.getLong("eventEnd");
                    String location = doc.getString("location");
                    String bannerQR = doc.getString("bannerQR");
                    ArrayList<Integer> milestones = (ArrayList<Integer>) doc.get("milestones");
                    Long maxAttendees = doc.getLong("maxAttendees");
                    Boolean trackLocation = doc.getBoolean("trackLocation");
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
     * Asynchronously get all users in the database (who are not admins)
     *
     * @param db       the database reference
     * @param callback the callback function
     * @see getAllNonAdminUsersCallback
     * @see User
     */
    public static void getAllNonAdminUsers(FirebaseFirestore db, getAllNonAdminUsersCallback callback) {
        ArrayList<User> usersList = new ArrayList<>();

        CollectionReference eventsRef = db.collection("users");
        eventsRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
                //TODO wait no add a proper onFailure() thing to the callback
            }
            if (querySnapshots != null) {
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    if (!doc.getBoolean("admin")) {
                        String deviceID = doc.getString("deviceID");
                        String userName = doc.getString("userName");
                        String profilePicture = doc.getString("profilePicture");
                        String firstName = doc.getString("firstName");
                        String lastName = doc.getString("lastName");
                        String phoneNumber = doc.getString("phoneNumber");
                        String email = doc.getString("email");
                        String homepage = doc.getString("homepage");
                        String messageID = doc.getString("messageID");
                        Boolean admin = false;
                        Log.d("Firestore", String.format("User(%s, %s) fetched", userName,
                                deviceID));
                        usersList.add(new User(deviceID, userName, profilePicture, firstName, lastName, phoneNumber, email, homepage, admin, messageID));
                    }
                }
                Log.d("Firestore", String.format("Fetched %d users", usersList.size()));
                callback.onCallback(usersList);
            }
        });
    }

    /**
     * Callback interface to get list of all users
     *
     * @see User
     */
    public interface getAllNonAdminUsersCallback {
        void onCallback(List<User> list);
    }


    /**
     * Asynchronously get all events in the database that the given user is not registered for or organizing
     *
     * @param db       the database reference
     * @param callback the callback function
     * @see getAllEventsCallback
     * @see Event
     */
    public static void getAllEventsUserIsNotIn(FirebaseFirestore db, String userID, getAllEventsCallback callback) {
        ArrayList<Event> eventsList = new ArrayList<>();

        // Retrieve user document
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the user's attendingEvents and organizingEvents arrays
                List<String> attendingEvents = (List<String>) documentSnapshot.get("attendeeEvents");
                List<String> organizingEvents = (List<String>) documentSnapshot.get("organizerEvents");

                // Query all events
                db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String qrCode = doc.getId();
                        Log.d(TAG,"GETTING QRCODE FOR EVENT "+qrCode);
                        // Check if the user is already associated with the event

                        if (!attendingEvents.contains(qrCode) && !organizingEvents.contains(qrCode)) {
                            // Event not associated with the user, add it to the events list
                            String eventName = doc.getString("eventName");
                            String eventDescription = doc.getString("eventDescription");
                            String eventBanner = doc.getString("eventBanner");
                            long eventStart = doc.getLong("eventStart");
                            long eventEnd = doc.getLong("eventEnd");
                            String location = doc.getString("location");
                            String bannerQR = doc.getString("bannerQR");
                            ArrayList<Integer> milestones = (ArrayList<Integer>) doc.get("milestones");
                            Long maxAttendees = (doc.getLong("maxAttendees"));
                            boolean trackLocation = doc.getBoolean("trackLocation");
                            eventsList.add(new Event(eventName, eventDescription, eventBanner, qrCode, eventStart, eventEnd, location, bannerQR, milestones, maxAttendees, trackLocation));
                        }
                    }
                    // Invoke the callback with the list of events
                    callback.onCallback(eventsList);
                }).addOnFailureListener(e -> {
                    Log.e("FirebaseUtil", "Error getting events", e);
                    callback.onCallback(Collections.emptyList()); // Invoke the callback with an empty list on failure
                });
            } else {
                Log.e("FirebaseUtil", "User document not found");
                callback.onCallback(Collections.emptyList()); // Invoke the callback with an empty list if user document not found
            }
        }).addOnFailureListener(e -> {
            Log.e("FirebaseUtil", "Error getting user document", e);
            callback.onCallback(Collections.emptyList()); // Invoke the callback with an empty list on failure
        });
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
     * Adds an event to the Firestore database. If an event with the same ID already exists, it will not be added, and the onEventExists function will be invoked
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
     * @param event the event
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

    /**
     * Delete user from database
     *
     * @param db    the database
     * @param user  the user to delete
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see User
     */
    public static void deleteUser(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(user.getDeviceID());
        db.collection("users")
                .document(docID)
                .delete()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Update an event with the same QR Code
     *
     * @param db             the database
     * @param event          the new event data (will replace the one with the same qr code)
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     */
    public static void updateEvent(FirebaseFirestore db, Event event, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(event.getQRcode());
        db.collection("events")
                .document(docID)
                .set(event)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Update a user with the same Device ID
     *
     * @param db                the database
     * @param user              the user data to replace
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see User
     */
    public static void updateUser(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection("users").document(user.getDeviceID())
                .set(user)
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
                String qrCode = doc.getId();
                Long eventStart = doc.getLong("eventStart");
                Long eventEnd = doc.getLong("eventEnd");
                String location = doc.getString("location");
                String bannerQR = doc.getString("bannerQR");
                ArrayList<Integer> milestones = (ArrayList<Integer>) doc.get("milestones");
                Long maxAttendees = doc.getLong("maxAttendees");
                Boolean trackLocation = doc.getBoolean("trackLocation");

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
     * Asynchronously get list of Events a user is attending
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
                    AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                    for (String eventCode : eventCodes) {
                        String docID = cleanDocumentId(eventCode);
                        getEvent(db, docID, event -> {
                            events.add(event);
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == eventCodes.size()) {
                                successListener.onSuccess(events);
                            }
                        }, e -> {
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == eventCodes.size()) {
                                successListener.onSuccess(events);
                            }
                        });
                    }
                } else {
                    successListener.onSuccess(new ArrayList<Event>());
                }
            } else {
                failureListener.onFailure(new Exception("User document not found"));
            }
        }).addOnFailureListener(failureListener);
    }

    /**
     * Asynchronously get list of Events a user is organizing
     *
     * @param db       the database reference
     * @param userID   the ID of the user to get events of
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     * @see User
     */
    public static void getUserOrganizingEvents(FirebaseFirestore db, String userID, OnSuccessListener<ArrayList<Event>> successListener, OnFailureListener failureListener) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> eventIDs = (List<String>) documentSnapshot.get("organizerEvents");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>(eventIDs);
                    ArrayList<Event> events = new ArrayList<>();
                    AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                    for (String eventCode : eventCodes) {
                        String docID = cleanDocumentId(eventCode);
                        getEvent(db, docID, event -> {
                            if (event != null) { // Check if event is not null (valid)
                                events.add(event);
                                int currentCount = processedCount.incrementAndGet();
                                if (currentCount == eventCodes.size()) {
                                    successListener.onSuccess(events);
                                }
                            }
                        }, e -> {
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == eventCodes.size()) {
                                successListener.onSuccess(events);
                            }
                        });
                    }
                } else {
                    successListener.onSuccess(new ArrayList<Event>());
                }
            } else {
                failureListener.onFailure(new Exception("User document not found"));
            }
        }).addOnFailureListener(failureListener);
    }


    /**
     * Asynchronously get map of Events a user has checked in to and how many times they checked in
     *
     * @param db       the database reference
     * @param userID   the ID of the user to get events of
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     * @see User
     */
    public static void getUserCheckedInEvents(FirebaseFirestore db, String userID, OnSuccessListener<Map<Event, Long>> successListener, OnFailureListener failureListener) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Long> eventIDs = (Map<String, Long>) documentSnapshot.get("checkedInEvents");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>(eventIDs.keySet());
                    Map<Event, Long> events = new HashMap<>();
                    AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                    for (String eventCode : eventCodes) {
                        String docID = cleanDocumentId(eventCode);
                        getEvent(db, docID, event -> {
                            events.put(event, eventIDs.get(eventCode));
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == eventCodes.size()) {
                                successListener.onSuccess(events);
                            }
                        }, e -> {
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == eventCodes.size()) {
                                successListener.onSuccess(events);
                            }
                        });
                    }
                } else {
                    successListener.onSuccess(new HashMap<>());
                }
            } else {
                failureListener.onFailure(new Exception("User document not found"));
            }
        }).addOnFailureListener(failureListener);
    }




    /**
     * Asynchronously get list of Users attending an event
     *
     * @param db       the database reference
     * @param eventQR   the ID of the event to get attendees of
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     * @see User
     */
    public static void getEventAttendees(FirebaseFirestore db, String eventQR, OnSuccessListener<ArrayList<User>> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(eventQR);
        db.collection("events").document(docID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> userIDs = (List<String>) documentSnapshot.get("attendees");
                if (userIDs != null && !userIDs.isEmpty()) {
                    ArrayList<User> users = new ArrayList<>();
                    AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                    for (String userID : userIDs) {
                        getUserObject(db, userID, user -> {
                            users.add(user);
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == userIDs.size()) {
                                successListener.onSuccess(users);
                            }
                        }, e -> {
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == userIDs.size()) {
                                successListener.onSuccess(users);
                            }
                        });
                    }
                } else {
                    successListener.onSuccess(new ArrayList<User>());
                }
            } else {
                failureListener.onFailure(new Exception("User document not found"));
            }
        }).addOnFailureListener(failureListener);
    }

    /**
     * Asynchronously get list of Users checked in to an event
     *
     * @param db       the database reference
     * @param eventQR   the ID of the event to get users checked in of
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     * @see User
     */
    public static void getEventCheckedInUsers(FirebaseFirestore db, String eventQR, OnSuccessListener<Map<User, Long>> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(eventQR);
        db.collection("events").document(docID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Long> userIDs = (Map<String, Long>) documentSnapshot.get("checkedInUsers");
                AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                if (userIDs != null && !userIDs.isEmpty()) {
                    Map<User, Long> users = new HashMap<>();
                    for (String userID : userIDs.keySet()) {
                        getUserObject(db, userID, user -> {
                            users.put(user, userIDs.get(userID));
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == userIDs.size()) {
                                successListener.onSuccess(users);
                            }
                        }, e -> {
                            int currentCount = processedCount.incrementAndGet();
                            if (currentCount == userIDs.size()) {
                                successListener.onSuccess(users);
                            }
                        });
                    }
                } else {
                    successListener.onSuccess(new HashMap<>());
                }
            } else {
                failureListener.onFailure(new Exception("User document not found"));
            }
        }).addOnFailureListener(failureListener);
    }

    /**
     * Asynchronously get list of Users organizing an event
     *
     * @param db       the database reference
     * @param eventQR   the ID of the event to get organizers of
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     * @see User
     */
    public static void getEventOrganizers(FirebaseFirestore db, String eventQR, OnSuccessListener<ArrayList<User>> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(eventQR);
        db.collection("events").document(docID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> userIDs = (List<String>) documentSnapshot.get("organizers");
                if (userIDs != null && !userIDs.isEmpty()) {
                    ArrayList<User> users = new ArrayList<>();
                    for (String userID : userIDs) {
                        getUserObject(db, userID, user -> {
                            users.add(user);
                            if (users.size() == userIDs.size()) {
                                successListener.onSuccess(users);
                            }
                        }, failureListener);
                    }
                } else {
                    successListener.onSuccess(new ArrayList<User>());
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
                if (user != null) {
                    user.setAdmin(documentSnapshot.getBoolean("admin"));
                }
                Log.d(TAG, String.format("Username: %s First: %s Last: %s Phone: %s Email: %s ID: %s", user.getUserName(), user.getFirstName(), user.getLastName()
                        , user.getPhoneNumber(), user.getEmail(), user.getDeviceID()));
                successListener.onSuccess(user);
            } else {
                failureListener.onFailure(new Exception("User document does not exist."));
            }
        }).addOnFailureListener(failureListener);
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
        String docID = cleanDocumentId(eventId);
        DocumentReference eventDoc = db.collection("events").document(docID);
        DocumentReference userDoc = db.collection("users").document(attendee);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventDoc);
                    DocumentSnapshot userSnapshot = transaction.get(userDoc);

                    List<String> attendees = (List<String>) eventSnapshot.get("attendees");
                    if (attendees == null) {
                        attendees = new ArrayList<>();
                    }

                    Long maxAttendees = (Long) eventSnapshot.get("maxAttendees");
                    if (maxAttendees != null && maxAttendees <= attendees.size()){
                        throw new IllegalArgumentException("Event reached max capacity");
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
     * Adds the eventID to the user's organized events field,
     * and the attendee ID to the event's organizers field.
     * Both succeed or fail simultaneously
     *
     * @param db       the database reference
     * @param eventId  the event ID to add and be added to
     * @param organizer the user ID to add and be added to
     */
    public static void addEventAndOrganizer(FirebaseFirestore db, String eventId, String organizer, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(eventId);
        DocumentReference eventDoc = db.collection("events").document(docID);
        DocumentReference userDoc = db.collection("users").document(organizer);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventDoc);
                    DocumentSnapshot userSnapshot = transaction.get(userDoc);

                    List<String> organizers = (List<String>) eventSnapshot.get("organizers");

                    if (organizers == null) {
                        organizers = new ArrayList<>();
                    }
                    organizers.add(organizer);
                    transaction.update(eventDoc, "organizers", organizers);


                    List<String> organizerEvents = (List<String>) userSnapshot.get("organizerEvents");
                    if (organizerEvents == null) {
                        organizerEvents = new ArrayList<>();
                    }
                    organizerEvents.add(eventId);
                    transaction.update(userDoc, "organizerEvents", organizerEvents);

                    return null;
                }).addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Adds the eventID to the user's checked in events field and vice versa, or increment both values
     * Both succeed or fail simultaneously
     *
     * @param db       the database reference
     * @param eventId  the event ID to add and be added to
     * @param user the user ID to add and be added to
     */
    public static void addEventAndCheckedInUser(FirebaseFirestore db, String eventId, String user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = cleanDocumentId(eventId);
        DocumentReference eventDoc = db.collection("events").document(docID);
        DocumentReference userDoc = db.collection("users").document(user);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventDoc);
                    DocumentSnapshot userSnapshot = transaction.get(userDoc);

                    List<String> attendees = (List<String>) eventSnapshot.get("attendees");
                    if (attendees == null || !attendees.contains(user)) {
                        throw new FirebaseFirestoreException("User is not registered for the event", FirebaseFirestoreException.Code.ABORTED);
                    }

                    Map<String, Long> users = (Map<String, Long>) eventSnapshot.get("checkedInUsers");

                    if (users == null) {
                        users = new HashMap<String, Long>();
                    }

                    users.merge(user, 1L, Long::sum);

                    transaction.update(eventDoc, "checkedInUsers", users);


                    Map<String, Long> checkedInEvents = (Map<String, Long>) userSnapshot.get("checkedInEvents");
                    if (checkedInEvents == null) {
                        checkedInEvents = new HashMap<>();
                    }
                    checkedInEvents.merge(eventId, 1L, Long::sum);
                    transaction.update(userDoc, "checkedInEvents", checkedInEvents);

                    return null;
                }).addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Get all announcements from the event
     * @param db db reference
     * @param eventID id of event
     * @param successListener successfull completion of operation
     * @param failureListener db error
     */
    public static void getAnnouncements(FirebaseFirestore db, String eventID, OnSuccessListener<ArrayList<Announcement>> successListener, OnFailureListener failureListener){
        String docID = cleanDocumentId(eventID);
        db.collection("events").document(docID).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                if (doc.contains("announcements")) {
                    ArrayList<HashMap<String, Object>> announcementMaps = (ArrayList<HashMap<String, Object>>) doc.get("announcements");
                    ArrayList<Announcement> announcements = new ArrayList<>();
                    for (HashMap<String, Object> announcementMap : announcementMaps) {
                        String content = (String) announcementMap.get("content");
                        long timestamp = (long) announcementMap.get("timestamp");
                        String sender = (String) announcementMap.get("sender");
                        Announcement announcement = new Announcement(doc.getString("eventName"), content, timestamp, sender, eventID);
                        announcements.add(announcement);
                    }
                    successListener.onSuccess(announcements);
                } else {
                    successListener.onSuccess(new ArrayList<>());
                }
            } else {
                failureListener.onFailure(new Exception("Event with id " + eventID + " not found"));
            }
        }).addOnFailureListener(failureListener);
    }

    /**
     *
     * @param db db reference
     * @param eventID id of event
     * @param announcement announcement to add
     * @param successListener successful completion of operation
     * @param failureListener db error
     */
    public static void saveAnnouncement(FirebaseFirestore db, String eventID, Announcement announcement, OnSuccessListener<Void> successListener, OnFailureListener failureListener){
        String docID = cleanDocumentId(eventID);
        DocumentReference eventDoc = db.collection("events").document(docID);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventDoc);
                    List<Announcement> announcements = (List<Announcement>) eventSnapshot.get("announcements");
                    if (announcements == null) {
                        announcements = new ArrayList<>();
                    }
                    announcements.add(announcement);
                    transaction.update(eventDoc, "announcements", announcements);
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



    // for adding a location that a user has checked in from java docs are not real java docs cannot hurt me
    public static void sendCoordinatesToEvent(FirebaseFirestore db, Event event, GeoPoint geoPoint,OnSuccessListener<Void> successListener, OnFailureListener failureListener ){
        db.collection("events").document(event.getQRcode())
                .update("checkInLocations", FieldValue.arrayUnion(geoPoint))
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }


    public static void getEventCheckInLocations(FirebaseFirestore db, Event event, OnSuccessListener<List<GeoPoint>> successListener, OnFailureListener failureListener) {
        db.collection("events").document(event.getQRcode())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("checkInLocations")) {
                        List<GeoPoint> checkInLocations = (List<GeoPoint>) documentSnapshot.get("checkInLocations");
                        successListener.onSuccess(checkInLocations);
                    } else {
                        // Handle the case where the event doesn't have a 'checkInLocations' field or doesn't exist
                        successListener.onSuccess(new ArrayList<>()); // Return an empty list as fallback
                    }
                })
                .addOnFailureListener(failureListener);
    }

}
