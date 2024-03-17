package com.example.fire_and_based;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ViewEvent extends AppCompatActivity {
    public Event clickedEvent;
    public ImageView imagePreview;
    ImageDownloader imageDownloader = new ImageDownloader();

//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    StorageReference fireRef = storage.getReference();


//    public void getBannerImage(String bannerUrl)
//    {
//        Toast.makeText(ViewEvent.this, "I've atually been called", Toast.LENGTH_LONG).show();
//
//        StorageReference uriRef = fireRef.child(bannerUrl);
//        uriRef.getBytes(10000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Toast.makeText(ViewEvent.this, "Image Grabbed From Cloud Successfully", Toast.LENGTH_SHORT).show();
//
//                Bitmap imageMap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                imagePreview.setImageBitmap(imageMap);
//            }
//        });}
       // System.out.println(uriRef.toString());
//        uriRef.getFile(imageUri).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>()
//        {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
//            {
//                //imageUri = Uri.fromFile(new File(imageUri.getPath()));
//
//            }
//        });
//        System.out.println("WE REUTNRING");
//        return imageUri;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clickedEvent = getIntent().getParcelableExtra("event");
        }



        imagePreview = findViewById(R.id.bannerPreview);

        imageDownloader.getBannerBitmap(clickedEvent,imagePreview);





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
                FirebaseUtil.deleteEvent(db, clickedEvent,
                        aVoid -> {
                            System.out.println("Success");
                        },
                        e -> {
                            System.err.println("Error");
                        });

                Intent intent = new Intent(ViewEvent.this, Firebase.class);
                startActivity(intent);
            }
        });
    }
}
