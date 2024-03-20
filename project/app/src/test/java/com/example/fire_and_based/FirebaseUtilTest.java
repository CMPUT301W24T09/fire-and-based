package com.example.fire_and_based;

import static com.example.fire_and_based.FirebaseUtil.addEventToDB;
import static com.example.fire_and_based.FirebaseUtil.getAllEvents;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FirebaseUtilTest {
    private FirebaseFirestore mockFirestore = mock(FirebaseFirestore.class);
    private CollectionReference mockCollectionReference = mock(CollectionReference.class);
    private DocumentReference mockDocRef = mock(DocumentReference.class);
    private DocumentSnapshot mockDocSnapshot = mock(DocumentSnapshot.class);
    private Task<DocumentSnapshot> mockGetTask = mock(Task.class);
    private Task<Void> mockSetTask = mock(Task.class);
    private QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
    private QueryDocumentSnapshot mockQueryDocumentSnapshot = mock(QueryDocumentSnapshot.class);
    private Iterator<QueryDocumentSnapshot> mockQDSIterator = mock(Iterator.class);

    @Before
    public void defineFirebaseMockBehavior(){
        when(mockFirestore.collection(anyString())).thenAnswer(invocation -> {
            String collectionName = invocation.getArgument(0);
            if ("events".equals(collectionName) || "users".equals(collectionName)) {
                return mockCollectionReference; // Existing collections
            } else {
                // Simulate that the collection does not exist
                throw new FirebaseFirestoreException("Collection not found: " + collectionName,
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }
        });

        //does not mean the document always *exists*, just have a reference to a potential
        //and execute the task. Can define result of mockTask as needed
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocRef);
        when(mockDocRef.get()).thenReturn(mockGetTask);

        when(mockGetTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockGetTask);
            return mockGetTask;
        });

        when(mockDocRef.set(any())).thenAnswer(invocation -> {
            Object argument = invocation.getArgument(0);
            if (argument instanceof Event || argument instanceof User) {
                return mockSetTask;
            } else {
                throw new IllegalArgumentException("Invalid object type: " + argument.getClass().getSimpleName());
            }
        });
    }

    @Test
    public void testGetAllEventsGetOne() throws InterruptedException {

        when(mockQuerySnapshot.iterator()).thenReturn(mockQDSIterator);
        when(mockQDSIterator.hasNext()).thenReturn(true, false);
        when(mockQDSIterator.next()).thenReturn(mockQueryDocumentSnapshot);

        Event mockEvent = new Event("eventName", "eventDescription", "eventBanner", "QRcode", 0, 1, "Edmonton", "banner", new ArrayList<Integer>(), -1, false);
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(mockEvent);

        when(mockCollectionReference.addSnapshotListener(any())).thenAnswer(invocation -> {
            EventListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockQueryDocumentSnapshot.getString("eventName")).thenReturn("eventName");
            when(mockQueryDocumentSnapshot.getString("eventDescription")).thenReturn("eventDescription");
            when(mockQueryDocumentSnapshot.getString("eventBanner")).thenReturn("eventBanner");
            when(mockQueryDocumentSnapshot.getString("QRcode")).thenReturn("QRcode");
            listener.onEvent(mockQuerySnapshot, null);
            return null;
        });

        FirebaseUtil.getAllEventsCallback mockCallback = mock(FirebaseUtil.getAllEventsCallback.class);

        // Call method and wait
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onCallback(any(ArrayList.class));
        getAllEvents(mockFirestore, mockCallback);
        latch.await();

        // Assert
        verify(mockCallback, times(1)).onCallback(expectedEvents);
    }
    @Test
    public void testGetAllEventsGetSeveral() throws InterruptedException { //TODO

        when(mockQuerySnapshot.iterator()).thenReturn(mockQDSIterator);
        when(mockQDSIterator.hasNext()).thenReturn(true, true, false);
        when(mockQDSIterator.next()).thenReturn(mockQueryDocumentSnapshot);

        Event mockEvent1 = new Event("eventName1", "eventDescription1", "eventBanner1", "QRcode1", 0L, 1L, "Edmonton", "banner1", null, -1, false);
        Event mockEvent2 = new Event("eventName2", "eventDescription2", "eventBanner2", "QRcode2", 5L, 10L, "Vancouver", "banner2", null, 100, true);
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(mockEvent1);
        expectedEvents.add(mockEvent2);

        when(mockCollectionReference.addSnapshotListener(any())).thenAnswer(invocation -> {
            EventListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockQueryDocumentSnapshot.getString("eventName")).thenReturn("eventName1", "eventName2");
            when(mockQueryDocumentSnapshot.getString("eventDescription")).thenReturn("eventDescription1", "eventDescription2");
            when(mockQueryDocumentSnapshot.getString("eventBanner")).thenReturn("eventBanner1", "eventBanner2");
            when(mockQueryDocumentSnapshot.getString("QRcode")).thenReturn("QRcode1", "QRcode2");
            when(mockQueryDocumentSnapshot.getLong("eventCapacity")).thenReturn(0L, 5L);
            when(mockQueryDocumentSnapshot.getLong("eventRegistered")).thenReturn(1L, 10L);
            when(mockQueryDocumentSnapshot.getString("eventLocation")).thenReturn("Edmonton", "Vancouver");
            when(mockQueryDocumentSnapshot.getString("eventImage")).thenReturn("banner1", "banner2");
            when(mockQueryDocumentSnapshot.get("eventAttendees")).thenReturn(null);
            when(mockQueryDocumentSnapshot.get("eventHost")).thenReturn(-1, 100);
            when(mockQueryDocumentSnapshot.getBoolean("eventPrivate")).thenReturn(false, true);
            listener.onEvent(mockQuerySnapshot, null);
            return null;
        });


        FirebaseUtil.getAllEventsCallback mockCallback = mock(FirebaseUtil.getAllEventsCallback.class);

        // Call method and wait
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onCallback(any(ArrayList.class));
        getAllEvents(mockFirestore, mockCallback);
        latch.await();

        // Assert
        verify(mockCallback, times(1)).onCallback(expectedEvents);
    }
    @Test
    public void testGetAllEventsGetNone() throws InterruptedException { //TODO

        when(mockQuerySnapshot.iterator()).thenReturn(mockQDSIterator);
        when(mockQDSIterator.hasNext()).thenReturn(false);
        when(mockQDSIterator.next()).thenReturn(null);

        List<Event> expectedEvents = new ArrayList<>();

        when(mockCollectionReference.addSnapshotListener(any())).thenAnswer(invocation -> {
            EventListener<QuerySnapshot> listener = invocation.getArgument(0);
            when(mockQueryDocumentSnapshot.getString("eventName")).thenReturn("eventName");
            when(mockQueryDocumentSnapshot.getString("eventDescription")).thenReturn("eventDescription");
            when(mockQueryDocumentSnapshot.getString("eventBanner")).thenReturn("eventBanner");
            when(mockQueryDocumentSnapshot.getString("QRcode")).thenReturn("QRcode");
            listener.onEvent(mockQuerySnapshot, null);
            return null;
        });

        FirebaseUtil.getAllEventsCallback mockCallback = mock(FirebaseUtil.getAllEventsCallback.class);

        // Call method and wait
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onCallback(any(ArrayList.class));
        getAllEvents(mockFirestore, mockCallback);
        latch.await();

        // Assert
        verify(mockCallback, times(1)).onCallback(expectedEvents);
    }

    //TODO add a fail check, when there is one




    @Test
    public void testAddEventToDBSuccess() throws InterruptedException {

        FirebaseUtil.AddEventCallback mockCallback = mock(FirebaseUtil.AddEventCallback.class);

        //successfully completed the task but did not find the document
        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockGetTask.getResult()).thenReturn(mockDocSnapshot);
        when(mockDocSnapshot.exists()).thenReturn(false);

        //set doc listeners
        when(mockSetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockSetTask;
        });
        when(mockSetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            // Do not call listener.onFailure() as we're simulating a successful operation
            return mockSetTask;
        });

        // Use a CountDownLatch to wait for the async callback
        // only one of these should be called but define all so test does not get stuck
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventExists();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventAdded();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onError(any());

        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd", 0, 0, null, null, null, 0, false);
        addEventToDB(mockFirestore, event, mockCallback);

        // Wait for the async callback
        latch.await();

        // Verify the interactions
        verify(mockCallback, times(1)).onEventAdded();
        verify(mockCallback, never()).onEventExists();
        verify(mockCallback, never()).onError(any());
    }
    @Test
    public void testAddEventToDBFailEventExists() throws InterruptedException {

        FirebaseUtil.AddEventCallback mockCallback = mock(FirebaseUtil.AddEventCallback.class);

        //successfully completed the task and found the document exists
        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockGetTask.getResult()).thenReturn(mockDocSnapshot);
        when(mockDocSnapshot.exists()).thenReturn(true);

        //set doc listeners
        when(mockSetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockSetTask;
        });
        when(mockSetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            // Do not call listener.onFailure() as we're simulating a successful operation
            return mockSetTask;
        });

        // Use a CountDownLatch to wait for the async callback
        // only one of these should be called but define all so test does not get stuck
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventExists();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventAdded();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onError(any());

        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd", 0, 0, null, null, null, 0, false);
        addEventToDB(mockFirestore, event, mockCallback);

        // Wait for the async callback
        latch.await();

        // Verify the interactions
        verify(mockCallback, never()).onEventAdded();
        verify(mockCallback, times(1)).onEventExists();
        verify(mockCallback, never()).onError(any());
    }
    @Test
    public void testAddEventToDBFailGetTask() throws InterruptedException {

        FirebaseUtil.AddEventCallback mockCallback = mock(FirebaseUtil.AddEventCallback.class);

        //failed to complete the get document task
        when(mockGetTask.isSuccessful()).thenReturn(false);
        when(mockGetTask.getResult()).thenReturn(mockDocSnapshot);
        when(mockDocSnapshot.exists()).thenReturn(false);

        //set doc listeners
        when(mockSetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockSetTask;
        });
        when(mockSetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            // Do not call listener.onFailure() as we're simulating a successful operation
            return mockSetTask;
        });

        // Use a CountDownLatch to wait for the async callback
        // only one of these should be called but define all so test does not get stuck
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventExists();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventAdded();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onError(any());

        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd", 0, 0, null, null, null, 0, false);
        addEventToDB(mockFirestore, event, mockCallback);

        // Wait for the async callback
        latch.await();

        // Verify the interactions
        verify(mockCallback, never()).onEventAdded();
        verify(mockCallback, never()).onEventExists();
        verify(mockCallback, times(1)).onError(any());
    }
    @Test
    public void testAddEventToDBFailSetTask() throws InterruptedException {

        FirebaseUtil.AddEventCallback mockCallback = mock(FirebaseUtil.AddEventCallback.class);

        //failed to complete the get document task
        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockGetTask.getResult()).thenReturn(mockDocSnapshot);
        when(mockDocSnapshot.exists()).thenReturn(false);

        //set doc listeners
        when(mockSetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            // do not call onSuccess since we failed
            return mockSetTask;
        });
        when(mockSetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Simulated failure"));
            return mockSetTask;
        });

        // Use a CountDownLatch to wait for the async callback
        // only one of these should be called but define all so test does not get stuck
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventExists();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onEventAdded();
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(mockCallback).onError(any());

        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd", 0, 0, null, null, null, 0, false);
        addEventToDB(mockFirestore, event, mockCallback);

        // Wait for the async callback
        latch.await();

        // Verify the interactions
        verify(mockCallback, never()).onEventAdded();
        verify(mockCallback, never()).onEventExists();
        verify(mockCallback, times(1)).onError(any());
    }




}
