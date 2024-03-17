package com.example.fire_and_based;

import static com.example.fire_and_based.FirebaseUtil.addEventToDB;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class FirebaseUtilTest {
    FirebaseFirestore mockFirestore = mock(FirebaseFirestore.class);
    CollectionReference mockCollectionReference = mock(CollectionReference.class);
    DocumentReference mockDocRef = mock(DocumentReference.class);
    DocumentSnapshot mockDocSnapshot = mock(DocumentSnapshot.class);
    Task<DocumentSnapshot> mockGetTask = mock(Task.class);
    Task<Void> mockSetTask = mock(Task.class);

    @Before
    public void defineMockBehavior(){
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);
        when(mockFirestore.collection("users")).thenReturn(mockCollectionReference);
        //TODO: other strings return error

        //does not mean the document always *exists*, just have a reference to a potential
        //and execute the task. Can define result of mockTask as needed
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocRef);
        when(mockDocRef.get()).thenReturn(mockGetTask);

        when(mockGetTask.addOnCompleteListener(any())).thenAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockGetTask);
            return mockGetTask;
        });

        when(mockDocRef.set(any(Event.class))).thenReturn(mockSetTask);
        when(mockDocRef.set(any(User.class))).thenReturn(mockSetTask);
        //TODO error on others?

    }

    @Test
    public void testAddEventToDBSuccess() throws InterruptedException {
        System.out.println("Testing success");

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

        System.out.println("Calling test method");
        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd");
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
        System.out.println("Testing event exists");

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

        System.out.println("Calling test method");
        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd");
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
        System.out.println("Testing fail get");

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

        System.out.println("Calling test method");
        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd");
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
        System.out.println("Testing fail set");

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

        System.out.println("Calling test method");
        // Call the method to test
        Event event = new Event("name", "desc", "no", "abcd");
        addEventToDB(mockFirestore, event, mockCallback);

        // Wait for the async callback
        latch.await();

        // Verify the interactions
        verify(mockCallback, never()).onEventAdded();
        verify(mockCallback, never()).onEventExists();
        verify(mockCallback, times(1)).onError(any());
    }




}
