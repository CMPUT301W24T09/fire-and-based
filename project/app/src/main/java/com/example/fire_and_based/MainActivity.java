package com.example.fire_and_based;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.view.View;


import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String uuid = sharedPref.getString("uuid_key", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("uuid_key", uuid);
            editor.commit();
        }

        Button button = findViewById(R.id.eventlistButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
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