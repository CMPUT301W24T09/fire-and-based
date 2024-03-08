package com.example.fire_and_based;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ScanContract;

import java.util.ArrayList;

public class EventCheckIn extends AppCompatActivity {

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

    // Method to launch the barcode scanner
    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);

        // Launch the barcode scanner
        qrLauncher.launch(options);
    }

    private void offerEvent(String qrCode){

        //TODO add database call and user id
        Event event = new Event("testName", "test description", null, "testQRC");
        ArrayList<Event> testUserRegisteredEvents = new ArrayList<Event>();
        User user = new User("testID", "testUserName", testUserRegisteredEvents, "");

        //end fake test

        if (!user.getUserEvents().contains(event)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Would you like to join the event " + event.getEventName() + "?")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(EventCheckIn.this, "Joined event " + event.getEventName(), Toast.LENGTH_LONG).show();
                            user.addEvent(event);
                            //TODO add user to event as well
                            goToEvent(event);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(EventCheckIn.this, "Declined event invitation", Toast.LENGTH_LONG).show();
                            //TODO direct user back to wherever (without adding them)
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(EventCheckIn.this, "You are already in event " + event.getEventName(), Toast.LENGTH_LONG).show();
            goToEvent(event);
        }
    }

    public void goToEvent(Event event){
        Intent intent = new Intent(this, EventInfoActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }
}