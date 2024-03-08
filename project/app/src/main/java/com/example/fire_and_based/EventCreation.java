package com.example.fire_and_based;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class EventCreation extends AppCompatActivity {
    private Button createEventSubmit;
    private Button uploadLink;
    private EditText eventTitle;
    private EditText eventDescription;
    private ImageView previewBanner;

    private Uri bannerImage;
    private String bannerUrl = null;
    ImageUploader imageUploader = new ImageUploader();


    StorageReference fireRef = FirebaseStorage.getInstance().getReference();


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
                imageUploader.imageSelection(previewBanner);
                bannerUrl = "events/"+eventTitle;
            }
        });



        createEventSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventTitleString = eventTitle.getText().toString();
                String eventDescriptionString = eventDescription.getText().toString();

                byte[] array = new byte[7]; // length is bounded by 7
                new Random().nextBytes(array);
                String qrCode = new String(array, StandardCharsets.UTF_8);

                Event newEvent = new Event(eventTitleString, eventDescriptionString, bannerUrl, qrCode);

                //prep image for storage
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


                addNewEvent(newEvent);
                displayQR(qrCode, eventTitleString);

            }

        });

    }

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
