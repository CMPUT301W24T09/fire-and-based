package com.example.fire_and_based;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.*;
import com.google.type.DateTime;
import com.google.type.LatLng;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


import java.util.List;
import java.util.Map;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class has all the functions for accessing the necessary data from the firebase
 * or storing the necessary data in the firebase.
 * @author Ilya, Sumayya, Tyler
 */

public class FirebaseUtil {

    private static final String USERS_COLLECTION = "users";
    private static final String EVENTS_COLLECTION = "events";

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

        CollectionReference eventsRef = db.collection(EVENTS_COLLECTION);
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
                    Long currentAttendees = doc.getLong("currentAttendees");
                    Boolean trackLocation = doc.getBoolean("trackLocation");
                    Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName,
                            qrCode));
                    eventsList.add(new Event(eventName, eventDescription, eventBanner, qrCode, eventStart, eventEnd, location, bannerQR, milestones, maxAttendees,currentAttendees, trackLocation));
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

        CollectionReference eventsRef = db.collection(USERS_COLLECTION);
        eventsRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
                //TODO wait no add a proper onFailure() thing to the callback
            }
            if (querySnapshots != null) {
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    Boolean value = doc.getBoolean("admin");
                    if (value == null || !value) {
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
        db.collection(USERS_COLLECTION).document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the user's attendingEvents and organizingEvents arrays
                List<String> attendingEvents = (List<String>) documentSnapshot.get("attendeeEvents");
                List<String> organizingEvents = (List<String>) documentSnapshot.get("organizerEvents");
                if (attendingEvents == null) attendingEvents = new ArrayList<>();
                if (organizingEvents == null) organizingEvents = new ArrayList<>();

                // Query all events
                List<String> finalAttendingEvents = attendingEvents;
                List<String> finalOrganizingEvents = organizingEvents;
                db.collection(EVENTS_COLLECTION).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String qrCode = doc.getId();
                        Log.d(TAG,"GETTING QRCODE FOR EVENT "+qrCode);
                        // Check if the user is already associated with the event

                        if (!finalAttendingEvents.contains(qrCode) && !finalOrganizingEvents.contains(qrCode)) {
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
                            Long currentAttendees = doc.getLong("currentAttendees");

                            boolean trackLocation = doc.getBoolean("trackLocation");
                            eventsList.add(new Event(eventName, eventDescription, eventBanner, qrCode, eventStart, eventEnd, location, bannerQR, milestones, maxAttendees,currentAttendees, trackLocation));
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
     * Asynchronously checks the user's involvement in the specified event.
     *
     * @param db                the database reference
     * @param userID            the ID of the user
     * @param eventID           the ID of the event to check the user's involvement
     * @param successListener   listener to be called upon successful determination of user's involvement
     *                          It receives a string indicating the user's involvement ("Attending", "Organizing", or "Browse")
     * @param failureListener   listener to be called upon failure in determining user's involvement
     *                          It receives an exception indicating the failure reason
     */
    public static void getUserInvolvementInEvent(FirebaseFirestore db, String userID, String eventID,
                                                 OnSuccessListener<String> successListener,
                                                 OnFailureListener failureListener) {
        // Retrieve user document
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the user's attendingEvents and organizingEvents arrays
                List<String> attendingEvents = (List<String>) documentSnapshot.get("attendeeEvents");
                List<String> organizingEvents = (List<String>) documentSnapshot.get("organizerEvents");

                // Check if the user is attending the event
                if (attendingEvents != null && attendingEvents.contains(eventID)) {
                    successListener.onSuccess("Attending");
                }
                // Check if the user is organizing the event
                else if (organizingEvents != null && organizingEvents.contains(eventID)) {
                    successListener.onSuccess("Organizing");
                } else {
                    successListener.onSuccess("Browse");
                }
            } else {
                // Invoke the success listener with "Browse" if user document not found
                successListener.onSuccess("Browse");
            }
        }).addOnFailureListener(e -> {
            // Invoke the failure listener on failure
            failureListener.onFailure(e);
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
        String docID = event.getQRcode();
        db.collection(EVENTS_COLLECTION).document(docID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    callback.onEventExists();
                } else {
                    db.collection(EVENTS_COLLECTION).document(docID).set(event)
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
        db.collection(USERS_COLLECTION)
                .document(user.getDeviceID())
                .set(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Delete event from database and remove references to it from users' entries
     *
     * @param db    the database
     * @param eventID the event ID
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     */
    public static void deleteEvent(FirebaseFirestore db, String eventID, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = eventID;

        // Start a new transaction
        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(db.collection(EVENTS_COLLECTION).document(docID));

            // Get the properties of the event's entry
            Map<String, String> checkedInUsers = (Map) eventSnapshot.get("checkedInUsers");
            if (checkedInUsers == null) checkedInUsers = new HashMap<>();

            List<String> organizers = new ArrayList<>((Collection) eventSnapshot.get("organizers"));

            List<String> attendees = (ArrayList) eventSnapshot.get("attendees");
            if (attendees == null) attendees = new ArrayList<>();

            // Prepare a map to hold user data
            Map<String, DocumentSnapshot> userData = new HashMap<>();

            // Read all user data at once
            Stream.of(checkedInUsers.keySet(), organizers, attendees)
                    .flatMap(Collection::stream)
                    .distinct()
                    .forEach(userID -> {
                        DocumentReference userRef = db.collection(USERS_COLLECTION).document(userID);
                        DocumentSnapshot userSnapshot = null;
                        try {
                            userSnapshot = transaction.get(userRef);
                        } catch (FirebaseFirestoreException e) {
                            failureListener.onFailure(e);
                        }
                        userData.put(userID, userSnapshot);
                    });

            // Now perform all updates
                    Map<String, String> finalCheckedInUsers = checkedInUsers;
                    List<String> finalAttendees = attendees;
                    userData.forEach((userID, userSnapshot) -> {
                DocumentReference userRef = db.collection(USERS_COLLECTION).document(userID);

                if (finalCheckedInUsers.containsKey(userID)) {
                    Map<String, Long> checkedInEvents = new HashMap<>((Map) userSnapshot.get("checkedInEvents"));
                    checkedInEvents.remove(docID);
                    transaction.update(userRef, "checkedInEvents", checkedInEvents);
                }

                if (organizers.contains(userID)) {
                    List<String> organizerEvents = new ArrayList<>((Collection) userSnapshot.get("organizerEvents"));
                    organizerEvents.remove(docID);
                    transaction.update(userRef, "organizerEvents", organizerEvents);
                }

                if (finalAttendees.contains(userID)) {
                    List<String> attendeeEvents = new ArrayList<>((Collection) userSnapshot.get("attendeeEvents"));
                    attendeeEvents.remove(docID);
                    transaction.update(userRef, "attendeeEvents", attendeeEvents);
                }
            });

            // Delete the event
            transaction.delete(db.collection(EVENTS_COLLECTION).document(docID));

            return null; // Add this line to avoid any runtime exception related to the return type.
        }).addOnSuccessListener(successListener)
        .addOnFailureListener(failureListener);
    }

    /**
     * Delete user from database and remove references to it from events' entries
     *
     * @param db    the database
     * @param user  the user to delete
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see User
     */
    public static void deleteUser(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        String docID = user.getDeviceID();

        // Prepare a list to hold events that need to be deleted
        List<String> eventsToDelete = new ArrayList<>();

        // Start a new transaction
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot userSnapshot = transaction.get(db.collection(USERS_COLLECTION).document(docID));

                    // Get the properties of the user's entry
                    Map<String, Long> checkedInEvents = (Map) userSnapshot.get("checkedInEvents");
                    if (checkedInEvents == null) checkedInEvents = new HashMap<>();
                    ArrayList<String> organizerEvents = (ArrayList<String>) userSnapshot.get("organizerEvents");
                    if (organizerEvents == null) organizerEvents = new ArrayList<>();
                    ArrayList<String> attendeeEvents = (ArrayList<String>) userSnapshot.get("attendeeEvents");
                    if (attendeeEvents == null) attendeeEvents = new ArrayList<>();

                    // Prepare a map to hold event data
                    Map<String, DocumentSnapshot> eventData = new HashMap<>();

                    // Read all event data at once
                    Stream.of(checkedInEvents.keySet(), organizerEvents, attendeeEvents)
                            .flatMap(Collection::stream)
                            .distinct()
                            .forEach(eventID -> {
                                DocumentReference eventRef = db.collection(EVENTS_COLLECTION).document(eventID);
                                DocumentSnapshot eventSnapshot = null;
                                try {
                                    eventSnapshot = transaction.get(eventRef);
                                } catch (FirebaseFirestoreException e) {
                                    failureListener.onFailure(e);
                                }
                                eventData.put(eventID, eventSnapshot);
                            });

                    // Now perform all updates
                    Map<String, Long> finalCheckedInEvents = checkedInEvents;
                    ArrayList<String> finalOrganizerEvents = organizerEvents;
                    ArrayList<String> finalAttendeeEvents = attendeeEvents;
                    eventData.forEach((eventID, eventSnapshot) -> {
                        DocumentReference eventRef = db.collection(EVENTS_COLLECTION).document(eventID);

                        if (finalCheckedInEvents.containsKey(eventID)) {
                            Map<String, String> checkedInUsers = new HashMap<>((Map) eventSnapshot.get("checkedInUsers"));
                            checkedInUsers.remove(docID);
                            transaction.update(eventRef, "checkedInUsers", checkedInUsers);
                        }

                        if (finalOrganizerEvents.contains(eventID)) {
                            List<String> organizers = new ArrayList<>((Collection) eventSnapshot.get("organizers"));
                            organizers.remove(docID);
                            transaction.update(eventRef, "organizers", organizers);

                            // If the "organizers" list is left blank, add the event to the delete list
                            if (organizers.isEmpty()) {
                                eventsToDelete.add(eventID);
                            }
                        }

                        if (finalAttendeeEvents.contains(eventID)) {
                            List<String> attendees = new ArrayList<>((Collection) eventSnapshot.get("attendees"));
                            attendees.remove(docID);
                            transaction.update(eventRef, "attendees", attendees);
                        }
                    });

                    // Delete the user
                    transaction.delete(db.collection(USERS_COLLECTION).document(docID));

                    return null; // Add this line to avoid any runtime exception related to the return type.
                }).addOnSuccessListener(aVoid -> {
                    // After the transaction, delete the events
                    Log.d("Deleting User", "Deleting events: " + Arrays.toString(eventsToDelete.toArray()));
                    for (String eventID : eventsToDelete) {
                        deleteEvent(db, eventID, successListener, failureListener);
                    }
                })
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
        String docID = event.getQRcode();
        Map<String, Object> eventMap = eventToMap(event);
        db.collection(EVENTS_COLLECTION)
                .document(docID)
                .update(eventMap)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Used to map the event for updating event with the same QR Code
     * @param event           he event
     * @see Event
     * @return Map<String, Object>
     */
    private static Map<String, Object> eventToMap(Event event) {
        Map<String, Object> map = new HashMap<>();
        // Populate the map with fields from the event. Example:
        // map.put("fieldName", event.getFieldValue());
        map.put("eventBanner", event.getEventBanner());
        map.put("eventDescription", event.getEventDescription());
        map.put("eventEnd", event.getEventEnd());
        map.put("eventStart", event.getEventStart());
        map.put("location", event.getLocation());
        map.put("maxAttendees", event.getMaxAttendees());
        map.put("trackLocation", event.isTrackLocation());

        // Add other fields as necessary...
        return map;
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
        Map<String, Object> userMap = userToMap(user);
        db.collection(USERS_COLLECTION).document(user.getDeviceID())
                .update(userMap)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }


    // map for the user to update the fields
    private static Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        // Populate the map with fields from the event. Example:
        // map.put("fieldName", event.getFieldValue());
        map.put("userName", user.getUserName());
        map.put("email", user.getEmail());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("homepage", user.getHomepage());
        map.put("profilePicture", user.getProfilePicture());
        return map;
    }

    /**
     * @param db       the database
     * @param QRCode   the QR Code of an event to get
     * @param successListener what to do in case of success
     * @param failureListener what to do in case of failure (database error)
     * @see Event
     */
    public static void getEvent(FirebaseFirestore db, String QRCode, OnSuccessListener<Event> successListener, OnFailureListener failureListener) {
        String docID = QRCode;
        // Handle any errors that occur while fetching the document
        db.collection(EVENTS_COLLECTION).document(docID).get().addOnSuccessListener(doc -> {
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
                Long currentAttendees = doc.getLong("currentAttendees");
                Boolean trackLocation = doc.getBoolean("trackLocation");

                Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName,
                        qrCode));
                Event event = new Event(eventName, eventDescription, eventBanner, qrCode, eventStart, eventEnd, location, bannerQR, milestones, maxAttendees,currentAttendees, trackLocation);
                successListener.onSuccess(event);
            } else { //document not found, does not exist
                failureListener.onFailure(new Exception("Event document not found"));
            }
        }).addOnFailureListener(failureListener);
    }

    /**
     * Asynchronously retrieves an event based on the provided poster QR code.
     *
     * @param db               the database reference
     * @param QRCode           the poster QR code to search for
     * @param successListener  listener to be called upon successful retrieval of the event
     *                         It receives the event object
     * @param failureListener  listener to be called upon failure in retrieving the event
     *                         It receives an exception indicating the failure reason
     */
    public static void getEventByPosterQR(FirebaseFirestore db, String QRCode, OnSuccessListener<Event> successListener, OnFailureListener failureListener) {
        db.collection("events")
                .whereEqualTo("bannerQR", QRCode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Check if any matching event is found
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Retrieve the first matching event
                        QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);

                        // Extract event details
                        String eventName = documentSnapshot.getString("eventName");
                        String eventDescription = documentSnapshot.getString("eventDescription");
                        String eventBanner = documentSnapshot.getString("eventBanner");
                        String eventId = documentSnapshot.getId();
                        long eventStart = documentSnapshot.getLong("eventStart");
                        long eventEnd = documentSnapshot.getLong("eventEnd");
                        String location = documentSnapshot.getString("location");
                        String bannerQR = documentSnapshot.getString("bannerQR");
                        ArrayList<Integer> milestones = (ArrayList<Integer>) documentSnapshot.get("milestones");
                        Long maxAttendees = documentSnapshot.getLong("maxAttendees");
                        boolean trackLocation = documentSnapshot.getBoolean("trackLocation");

                        // Create Event object
                        Event event = new Event(eventName, eventDescription, eventBanner, eventId, eventStart, eventEnd,
                                location, bannerQR, milestones, maxAttendees, 0L, trackLocation);

                        // Invoke the success listener with the event object
                        successListener.onSuccess(event);
                    } else {
                        // If no matching event found, invoke the success listener with null
                        failureListener.onFailure(new Exception("Event document not found"));
                    }
                })
                .addOnFailureListener(failureListener);
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
        db.collection(USERS_COLLECTION).document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> eventIDs = (List<String>) documentSnapshot.get("attendeeEvents");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>(eventIDs);
                    ArrayList<Event> events = new ArrayList<>();
                    AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                    for (String eventCode : eventCodes) {
                        String docID = eventCode;
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
        db.collection(USERS_COLLECTION).document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> eventIDs = (List<String>) documentSnapshot.get("organizerEvents");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>(eventIDs);
                    ArrayList<Event> events = new ArrayList<>();
                    AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                    for (String eventCode : eventCodes) {
                        String docID = eventCode;
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
        db.collection(USERS_COLLECTION).document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Long> eventIDs = (Map<String, Long>) documentSnapshot.get("checkedInEvents");
                if (eventIDs != null && !eventIDs.isEmpty()) {
                    ArrayList<String> eventCodes = new ArrayList<>(eventIDs.keySet());
                    Map<Event, Long> events = new HashMap<>();
                    AtomicInteger processedCount = new AtomicInteger(0); // To track processed events

                    for (String eventCode : eventCodes) {
                        String docID = eventCode;
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
        String docID = eventQR;
        db.collection(EVENTS_COLLECTION).document(docID).get().addOnSuccessListener(documentSnapshot -> {
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

//    public static void getEventUserPool(FirebaseFirestore db, String eventQR,OnEventUserPoolResultListener listener)
//    {
//        //ArrayList<String> users = new ArrayList<>();
//        String docID = cleanDocumentId(eventQR);
//        Log.d(TAG, "ATTEMPT FOR "+docID);
//
//        db.collection("events").document(docID).get().addOnSuccessListener(documentSnapshot ->
//        {
//            if (documentSnapshot.exists())
//            {
//                Log.d(TAG, "EXISTS");
//                List<String> userIDs = (List<String>) documentSnapshot.get("attendees");
//
//                if (userIDs != null && !userIDs.isEmpty())
//                {
//                    int size = userIDs.size();
//                    Log.d(TAG, "SIZE:" + size);
//                    listener.onSuccess(size); // Notify listener with the size of users
//                }
//                else
//                {
//                    Log.d(TAG, "EMPTY EMPTY EMPTY");
//                    listener.onSuccess(0); // Notify listener with 0 if userIDs is empty
//                }
//            }
//        }).addOnFailureListener(e -> {
//            Log.e(TAG, "Error getting document", e);
//            listener.onError(e); // Notify listener if an error occurs
//        });
//    }
//    public interface OnEventUserPoolResultListener {
//        void onSuccess(int userPoolSize);
//        void onError(Exception e);
//    }

//    public static void addFieldstoUser(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener ){
//        db.collection(USERS_COLLECTION).document(user.getDeviceID())
//                .update("attendeeEvents", FieldValue.arrayUnion())
//                .addOnSuccessListener(successListener)
//                .addOnFailureListener(failureListener);
//
//        db.collection(USERS_COLLECTION).document(user.getDeviceID())
//                .update("organizerEvents", FieldValue.arrayUnion())
//                .addOnSuccessListener(successListener)
//                .addOnFailureListener(failureListener);
//    }

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
        String docID = eventQR;
        db.collection(EVENTS_COLLECTION).document(docID).get().addOnSuccessListener(documentSnapshot -> {
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
        String docID = eventQR;
        db.collection(EVENTS_COLLECTION).document(docID).get().addOnSuccessListener(documentSnapshot -> {
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
        db.collection(USERS_COLLECTION).document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                user.setProfilePicture(documentSnapshot.get("profilePicture").toString());
                user.setAdmin(documentSnapshot.getBoolean("admin"));
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
        String docID = eventId;
        DocumentReference eventDoc = db.collection(EVENTS_COLLECTION).document(docID);
        DocumentReference userDoc = db.collection(USERS_COLLECTION).document(attendee);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventDoc);
                    DocumentSnapshot userSnapshot = transaction.get(userDoc);

                    List<String> attendees = (List<String>) eventSnapshot.get("attendees");
                    if (attendees == null) {
                        attendees = new ArrayList<>();
                    }

                    Long maxAttendees = (Long) eventSnapshot.get("maxAttendees");
                    if (maxAttendees != null && maxAttendees <= attendees.size() && maxAttendees != -1){
                        throw new IllegalArgumentException("Event reached max capacity");
                    }

                    attendees.add(attendee);
                    transaction.update(eventDoc, "attendees", attendees);
                    transaction.update(eventDoc, "currentAttendees", attendees.size());



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
        String docID = eventId;
        DocumentReference eventDoc = db.collection(EVENTS_COLLECTION).document(docID);
        DocumentReference userDoc = db.collection(USERS_COLLECTION).document(organizer);

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
        String docID = eventId;
        DocumentReference eventDoc = db.collection(EVENTS_COLLECTION).document(docID);
        DocumentReference userDoc = db.collection(USERS_COLLECTION).document(user);

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
        String docID = eventID;
        db.collection(EVENTS_COLLECTION).document(docID).get().addOnSuccessListener(doc -> {
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
        String docID = eventID;
        DocumentReference eventDoc = db.collection(EVENTS_COLLECTION).document(docID);
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
     * Sends the user's coordinates to the event's check-in locations.
     *
     * @param db               The instance of FirebaseFirestore.
     * @param event            The event object to which the user is checking in.
     * @param geoPoint         The GeoPoint object containing the user's coordinates.
     * @param successListener  Listener to be invoked when the operation is successful.
     * @param failureListener  Listener to be invoked when the operation fails.
     */
    // for adding a location that a user has checked in from java docs are not real java docs cannot hurt me
    public static void sendCoordinatesToEvent(FirebaseFirestore db, Event event, GeoPoint geoPoint,OnSuccessListener<Void> successListener, OnFailureListener failureListener ){
        db.collection(EVENTS_COLLECTION).document(event.getQRcode())
                .update("checkInLocations", FieldValue.arrayUnion(geoPoint))
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Retrieves the check-in locations for a specific event.
     *
     * @param db               The instance of FirebaseFirestore.
     * @param event            The event object for which check-in locations are to be retrieved.
     * @param successListener  Listener to be invoked with the list of check-in locations when the operation is successful.
     * @param failureListener  Listener to be invoked when the operation fails.
     */
    public static void getEventCheckInLocations(FirebaseFirestore db, Event event, OnSuccessListener<List<GeoPoint>> successListener, OnFailureListener failureListener) {
        db.collection(EVENTS_COLLECTION).document(event.getQRcode())
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

    /**
     * Removes a user from a specific event.
     *
     * @param db               The instance of FirebaseFirestore.
     * @param user             The user object to be removed from the event.
     * @param event            The event object from which the user is to be removed.
     * @param successListener  Listener to be invoked when the operation is successful.
     * @param failureListener  Listener to be invoked when the operation fails.
     */
    public static void removeUserFromEvent(FirebaseFirestore db, User user, Event event, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {

        Task<Void> removeEventFromUser = db.collection(USERS_COLLECTION).document(user.getDeviceID())
                .update("attendeeEvents", FieldValue.arrayRemove(event.getQRcode()));

        Task<Void> removeUserFromEvent = db.collection(EVENTS_COLLECTION).document(event.getQRcode())
                .update("attendees", FieldValue.arrayRemove(user.getDeviceID()));

        Task<Void> updateCurrentAttendeeAmount = db.collection(EVENTS_COLLECTION).document(event.getQRcode())
                .update("currentAttendees", event.getCurrentAttendees() - 1L);

//        Map<String, Object> checkedInEventsUpdate = new HashMap<>();
//        checkedInEventsUpdate.put("checkedInEvents" + event.getQRcode(), 0); // Constructs a map with the key and sets its value to 0

        Task<Void> updateCheckedInEventValue = db.collection(EVENTS_COLLECTION).document(event.getQRcode())
                .update("checkedInUsers." + user.getDeviceID(), FieldValue.delete());

        // Use Tasks.whenAll() to wait for all updates to complete
        Task<Void> combinedTask = Tasks.whenAll(removeEventFromUser, removeUserFromEvent, updateCheckedInEventValue, updateCurrentAttendeeAmount);

        combinedTask.addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }


}
