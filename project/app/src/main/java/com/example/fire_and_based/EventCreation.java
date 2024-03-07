package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * GUI logic for Creating a new event
 * Current issues: no db access somwhere (idk what that comment is about), no error checking rn
 * @author   Tyler Beach, Ilya Nalivaiko
 */
public class EventCreation extends AppCompatActivity {
    private Button createEventSubmit;

    private Button reuseQRCode;
    private Button generateQRCode;
    private TextView showQRString;
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
                    showQRString.setText(getString(R.string.qr_code_display).replace("%s", "ERROR: No QR Code"));
                } else {
                    // TODO remove debug confirmation maybe
                    Toast.makeText(EventCreation.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    qrCode = result.getContents();
                    showQRString.setText(getString(R.string.qr_code_display).replace("%s", qrCode));
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
        showQRString = findViewById(R.id.create_event_show_qr_string);

        if (qrCode == null){
            showQRString.setText(getString(R.string.qr_code_display).replace("%s", "ERROR: No QR Code"));
        }

        createEventSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitleString = eventTitle.getText().toString();
                String eventDescriptionString = eventDescription.getText().toString();

                if (false){ //TODO add checks for invalid entries, code not generated, etc
                    //TODO error popup
                    return;
                }

                Event newEvent = new Event(eventTitleString, eventDescriptionString, null, qrCode);
                addNewEvent(newEvent);

                displayQR(qrCode, eventTitleString);

            }

        });

        reuseQRCode = findViewById(R.id.create_event_reuseQR_button);
        reuseQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });

        generateQRCode = findViewById(R.id.create_event_generateQR_button);
        generateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] array = new byte[7]; // length is bounded by 7
                new Random().nextBytes(array);
                qrCode = "fire_and_based_event:" + new String(array, StandardCharsets.UTF_8);
                showQRString.setText(getString(R.string.qr_code_display).replace("%s", qrCode));
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
