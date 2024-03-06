package com.example.fire_and_based;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.view.View;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String uuid = sharedPref.getString("uuid_key", "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("uuid_key", uuid);
            User hello;
            currentUser = new User(uuid, "", new ArrayList<Event>(), "");
            FirebaseUtil.addUserToDB(db, currentUser);
            editor.commit();
        }
        else {
            // Queries for document with matching uuid
            Query q = db.collection("users").whereEqualTo(FieldPath.documentId(), uuid);

            //  Whenever query successful, creates new User based on values within document, not sure if saving userEvents works like that
            q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    String userID = queryDocumentSnapshots.getDocuments().get(0).getString("device_id");
                    String username = queryDocumentSnapshots.getDocuments().get(0).getString("userName");
                    String profilePicture = queryDocumentSnapshots.getDocuments().get(0).getString("profiePicture");
                    ArrayList<Event> userEvents = (ArrayList<Event>) queryDocumentSnapshots.getDocuments().get(0).get("userEvents");
                    currentUser = new User(userID, username, userEvents, profilePicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "User not added");
                }
            });
        }

        Button button = findViewById(R.id.eventlistButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AttendeeActivity.class);
            startActivity(intent);
        });


        Button firebaseTest = findViewById(R.id.firebaseButton);

        firebaseTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Firebase.class);
                startActivity(intent);
            }
        });

        Button imageUploader = findViewById(R.id.imageUploadTest);

        imageUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageUploader.class);
                startActivity(intent);
            }
        });

        Button QRScanTest = findViewById(R.id.QRScanTest);

        QRScanTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventCheckIn.class);
                startActivity(intent);
            }
        });

        Button QRGenTest = findViewById(R.id.QRGenTest);

        QRGenTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestQRCodeGenerator.class);
                startActivity(intent);
            }
        });
    }

}