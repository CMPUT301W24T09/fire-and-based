package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.qrcode.encoder.QRCode;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
/**
 * GUI logic for Creating a new event
 * Current issues: no db access?
 * @author   Tyler Beach, Ilya Nalivaiko
 */
public class EventCreation extends AppCompatActivity {
    private Button createEventSubmit;

    private Button reuseQRCode;
    private EditText eventTitle;
    private EditText eventDescription;

    private String qrCode = null;

    /**
     * Launches the QR Code scanner and sets the QR code to be used for the next event
     */
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(EventCreation.this, "Cancelled scan", Toast.LENGTH_LONG).show();
                    qrCode = null;
                } else {
                    // TODO remove debug confirmation maybe
                    Toast.makeText(EventCreation.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    qrCode = result.getContents();
                }
            }
    );

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_creation);

        createEventSubmit = findViewById(R.id.create_new_event_submit);
        eventTitle = findViewById(R.id.event_title_input);
        eventDescription = findViewById(R.id.event_description_input);

        createEventSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitleString = eventTitle.getText().toString();
                String eventDescriptionString = eventDescription.getText().toString();

                if (qrCode == null) {
                    byte[] array = new byte[7]; // length is bounded by 7
                    new Random().nextBytes(array);
                    qrCode = "fire_and_based_event:" + new String(array, StandardCharsets.UTF_8);
                }

                Event newEvent = new Event(eventTitleString, eventDescriptionString, null, qrCode);

                addNewEvent(newEvent);

                displayQR(qrCode, eventTitleString);
                qrCode = null;

            }

        });

        reuseQRCode = findViewById(R.id.craete_evet_reuseQR_button);
        reuseQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });


    }
    /**
     * Brings up a screen with the event name and a scannable QR code to join it
     *
     * @param  content the QR Code ID string of an event
     * @param  name the name of the event
     */
    private void displayQR(String content, String name){
        Intent intent = new Intent(this, QRCodeViewer.class);
        Bundle extras = new Bundle();
        extras.putString("name", name);
        extras.putString("code", content);
        intent.putExtras(extras);
        startActivity(intent);
    }

    // ADD EVENT TO DATABASE HERE
    // WE WILL NEED TO UPDATE THIS PARTICULAR ONE WHEN THE QR CODE IS GENERATED IN THE NEXT SCREEN


    private void addNewEvent(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtil.addEventToDB(db, event);
    }
}
