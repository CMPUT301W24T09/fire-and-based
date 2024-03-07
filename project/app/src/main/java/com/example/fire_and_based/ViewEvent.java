package com.example.fire_and_based;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewEvent extends AppCompatActivity {
    public Event clickedEvent;
    private ImageView imagePreview;


    FirebaseFirestore db = FirebaseFirestore.getInstance();


    StorageReference fireRef = FirebaseStorage.getInstance().getReference();
    public Uri imageUri;
    public Uri getBannerUri(String bannerUrl)
    {
        StorageReference uriRef = fireRef.child("events/"+bannerUrl);
        uriRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                imageUri = uri;
            }
        });

        return imageUri;
    }









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedEvent = getIntent().getParcelableExtra("event");
        }

//        TextView titleText = findViewById(R.id.event_title);
//        titleText.setText(clickedEvent.getEventName());


        imagePreview = findViewById(R.id.imagePreview);
        FirebaseUtil.getEventBannerUrl(db, clickedEvent, new FirebaseUtil.EventBannerCallback()
        {
            public void onBannerUrlFetched(String bannerUrl)
            {
                imageUri = getBannerUri(bannerUrl);
                Glide.with(getApplicationContext()).load(imageUri).into(imagePreview);

            }
            public void onError(Exception e)
            {

            }
        });






        TextView titleText2 = findViewById(R.id.event_title2);
        titleText2.setText(clickedEvent.getEventName());

        TextView eventDescription = findViewById(R.id.event_description);
        eventDescription.setText(clickedEvent.getEventDescription());

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEvent.this, EventInfoActivity.class);
                startActivity(intent);
            }
        });

        Button deleteEventButton = findViewById(R.id.deleteEventButton);


        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUtil.deleteEvent(db, clickedEvent);

                Intent intent = new Intent(ViewEvent.this, Firebase.class);
                startActivity(intent);
            }
        });
    }
}
