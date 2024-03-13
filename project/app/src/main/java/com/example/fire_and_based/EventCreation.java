package com.example.fire_and_based;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private Button previewQRImage;
    private Button uploadLink;
    private EditText eventTitle;
    private EditText eventDescription;
    private ImageView previewBanner;

    private Uri bannerImage;
    private String bannerUrl = null;
    //ImageUploader imageUploader = new ImageUploader();
    StorageReference fireRef = FirebaseStorage.getInstance().getReference();

    /**
     * Used to launch device photo gallery and display the preview of the image in an imageview
     */
    ActivityResultLauncher<Intent> customActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result)
        {//if (result.getResultCode() == RESULT_OK)
            try {
                if (result.getData() != null)
                {
                    bannerImage = result.getData().getData();
                    //buttonUpload.setEnabled(true);
                    Glide.with(getApplicationContext()).load(bannerImage).into(previewBanner);
                }
            }
            catch(Exception e)
            {Toast.makeText(EventCreation.this, "Please Select An Image", Toast.LENGTH_LONG).show();}
        }
    });




    private String qrCode = null;

    /**
     * Launches the QR Code scanner and sets the QR code to be used for the next event
     */
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    // TODO remove debug confirmation maybe
                    toast("Scanned: " + result.getContents());
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
        uploadLink = findViewById(R.id.uploadLinkButton);
        previewBanner = findViewById(R.id.bannerPreview);






        uploadLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Display image in imageview
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                customActivityResultLauncher.launch(imageIntent);
                //bannerImage = imageUploader.imageSelection(previewBanner);
            }
        });



        showQRString = findViewById(R.id.create_event_show_qr_string);

        //Should always be the case on view creation
        if (qrCode == null){
            showQRString.setText(getString(R.string.qr_code_display).replace("%s", "ERROR: No QR Code"));
        }

        createEventSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitleString = eventTitle.getText().toString();
                String eventDescriptionString = eventDescription.getText().toString();

                if (eventTitleString.isEmpty()){
                    toast("ERROR: Must enter an event name");
                    return;
                }
                if (eventDescriptionString.isEmpty()){
                    toast("ERROR: Must enter an event description");
                    return;
                }
                if (qrCode == null){
                    toast("ERROR: Please generate or scan a QR Code to use");
                    return;
                }


                //prep image for storage
                if (bannerImage != null){
                    bannerUrl = "events/"+eventTitleString;
                    StorageReference selectionRef = fireRef.child(bannerUrl);
                    //store image
                    selectionRef.putFile(bannerImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EventCreation.this, "Image Uploaded To Cloud Successfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EventCreation.this, "Image Upload Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }


                Event event = new Event(eventTitleString, eventDescriptionString, bannerUrl, qrCode);

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                toast("Adding your event, please wait...");
                FirebaseUtil.addEventToDB(db, event, new FirebaseUtil.AddEventCallback() {
                    @Override
                    public void onEventAdded() {
                        toast("Event successfully added!");
                        Log.println(Log.DEBUG, "EventCreation", "New event with id: " + qrCode + " added");
                        //TODO exit out maybe?
                    }

                    @Override
                    public void onEventExists() {
                        toast("ERROR: Event with the same ID already exists in the database.");
                        Log.println(Log.DEBUG, "EventCreation", "Event with id: " + qrCode + " found a duplicate");
                    }

                    @Override
                    public void onError(Exception e) {
                        toast("An internal error occurred, please try again later");
                        Log.println(Log.ERROR, "EventCreation", e.toString());
                    }
                });



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

        previewQRImage = findViewById(R.id.create_event_preview_qr_image);
        previewQRImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrCode != null){
                    displayQR(qrCode, eventTitle.getText().toString());
                } else {
                    toast("ERROR: QR Code not set");
                }
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

    /**
     * Makes a toast popup
     * @param text the text in the toast
     */
    private void toast(String text){
        Toast.makeText(EventCreation.this, text, Toast.LENGTH_LONG).show();
    }
}
