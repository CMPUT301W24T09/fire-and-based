package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    public User currentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = getIntent().getParcelableExtra("currentUser");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EditText firstNameEdit = findViewById(R.id.first_name_text);
        EditText lastNameEdit = findViewById(R.id.last_name_text);
        EditText emailEdit = findViewById(R.id.email_text);
        EditText phoneNumberEdit = findViewById(R.id.phone_number_text);

        Log.d(TAG, String.format("First name: %s", currentUser.getFirstName()));

        firstNameEdit.setText(currentUser.getFirstName());
        lastNameEdit.setText(currentUser.getLastName());
        emailEdit.setText(currentUser.getEmail());
        phoneNumberEdit.setText(currentUser.getPhoneNumber());

        Button saveButton = findViewById(R.id.create_profile);

        ImageView profilePic = findViewById(R.id.profile_pic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ImageUploader.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setFirstName(firstNameEdit.getText().toString());
                currentUser.setLastName(lastNameEdit.getText().toString());
                currentUser.setEmail(emailEdit.getText().toString());
                currentUser.setPhoneNumber(phoneNumberEdit.getText().toString());
                FirebaseUtil.updateProfileInfo(FirebaseFirestore.getInstance(), currentUser);
                Intent intent = new Intent(ProfileActivity.this, UserActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });
    }
}
