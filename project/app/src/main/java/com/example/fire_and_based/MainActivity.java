package com.example.fire_and_based;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button2);
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
    }

}