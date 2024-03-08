package com.example.fire_and_based;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ScanContract;

import java.util.ArrayList;

/**
 * GUI logic for checking in to an event by scanning a QR Code
 * Current issues: Cant access real events or users
 * @author   Ilya Nalivaiko
 */
public class EventCheckIn extends AppCompatActivity {
    /**
     * Launches the QR Code scanner
     */
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(EventCheckIn.this, "Cancelled scan", Toast.LENGTH_LONG).show();
                } else {
                    // TODO remove debug confirmation maybe
                    Toast.makeText(EventCheckIn.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    offerEvent(result.getContents());
                }
            }
    );

    /**
     * Creates activity object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_check_in); // Assuming you have an XML layout file named "activity_my"

        // Set an OnClickListener for the "scan_qr_button"
        findViewById(R.id.scan_qr_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner(); // Call the method to launch the barcode scanner
            }
        });
    }

    /**
     * Prepares the QR Code scanner
     */
    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(false);

        // Launch the barcode scanner
        qrLauncher.launch(options);
    }

    /**
     * Logic for after a QR Code is scanned successfully
     *
     * <p>If the user is registered for the event already, direct them to the event screen.
     * If the user is not registered, ask for confirmation (Can decline joining)</p>
     *
     * @param  eventQRCode the QR Code ID string of an event
     */

    private void offerEvent(String eventQRCode){

        //FOR TESTING ONLY
        eventQRCode = "froggy";
        String userID = "321";
        //END TESTING ONLY


        Log.println(Log.DEBUG, "EventCheckIn", "Checking if user is already in event...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.checkUserInEventByQRCode(db, userID, eventQRCode, new FirebaseUtil.UserEventQRCodeCallback() {
            @Override
            public void onEventFound(Event event) {
                Log.println(Log.DEBUG, "EventCheckIn", "Found user is already in that event");
                Toast.makeText(EventCheckIn.this, "You are already in event " + event.getEventName(), Toast.LENGTH_LONG).show();
                goToEvent(event);
            }

            @Override
            public void onEventNotFound(Event event) {
                Log.println(Log.DEBUG, "EventCheckIn", "User is not in the event, offering to join");
                AlertDialog.Builder builder = new AlertDialog.Builder(EventCheckIn.this);
                builder.setMessage("Would you like to join the event " + event.getEventName() + "?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.println(Log.DEBUG, "EventCheckIn", "User accepted event");
                                Toast.makeText(EventCheckIn.this, "Joined event " + event.getEventName(), Toast.LENGTH_LONG).show();
                                //TODO add user to event and event to user, save both to DB
                                goToEvent(event);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.println(Log.DEBUG, "EventCheckIn", "User declined event");
                                Toast.makeText(EventCheckIn.this, "Declined event invitation", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            @Override
            public void onError(Exception e) {
                Log.println(Log.ERROR, "EventCheckIn", "An error occurred trying to join the event: " + e.getMessage());
            }
        });

    }

    /**
     * Directs the user to the homepage of a given event
     *
     * @param  event the event the user should be directed to
     */
    public void goToEvent(Event event){
        Intent intent = new Intent(this, EventInfoActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }
}