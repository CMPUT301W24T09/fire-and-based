package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    public User currentUser;



    public Uri profileImage;
    public String profileUrl;
    ImageView profilePic;
    StorageReference fireRef = FirebaseStorage.getInstance().getReference();
    ImageDownloader imageDownloader = new ImageDownloader();

    //LAUNCHES DEVICE IMAGE GALLERY (i will put this in its own class later sowwy)
    ActivityResultLauncher<Intent> customActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result)
        {//if (result.getResultCode() == RESULT_OK)
            try {
                if (result.getData() != null)
                {
                    profileImage = result.getData().getData();
                    //buttonUpload.setEnabled(true);
                    Glide.with(getApplicationContext()).load(profileImage).into(profilePic);
                }
            }
            catch(Exception e)
            {
                Toast.makeText(ProfileActivity.this, "Please Select An Image", Toast.LENGTH_LONG).show();}
        }
    });















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






        profilePic = findViewById(R.id.profile_pic);
        imageDownloader.getProfilePicBitmap(currentUser,profilePic);


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                customActivityResultLauncher.launch(imageIntent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setFirstName(firstNameEdit.getText().toString());
                currentUser.setLastName(lastNameEdit.getText().toString());
                currentUser.setEmail(emailEdit.getText().toString());
                currentUser.setPhoneNumber(phoneNumberEdit.getText().toString());

                profileUrl = "profiles/"+currentUser.getDeviceID();

                //prep image for storage
                StorageReference selectionRef = fireRef.child(profileUrl);
                //store image
                selectionRef.putFile(profileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfileActivity.this, "Image Uploaded To Cloud Successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Image Upload Error", Toast.LENGTH_LONG).show();
                    }
                });










                FirebaseUtil.updateProfileInfo(FirebaseFirestore.getInstance(), currentUser);
                Intent intent = new Intent(ProfileActivity.this, UserActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });
    }
}
