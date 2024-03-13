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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class displays the profile for a user, and allows you to edit your details.
 * It also allows you to upload a profile pic.
 */
public class ProfileActivity extends AppCompatActivity {

    public User currentUser;

    public Uri profileImage;
    public String profileUrl;
    ImageView profilePic;
    StorageReference fireRef = FirebaseStorage.getInstance().getReference();
    ImageDownloader imageDownloader = new ImageDownloader();

    //LAUNCHES DEVICE IMAGE GALLERY (i will put this in its own class later sowwy)

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





// merge conflict if u see this past march 8 just delete plz thx : )

//         profilePic = findViewById(R.id.profile_pic);
//         if (currentUser !=null )
//         {
//             /**
//              * Downloads profile pic if available
//              */
//             imageDownloader.getProfilePicBitmap(currentUser,profilePic);
//         }
        profilePic = findViewById(R.id.profile_pic);
        if (currentUser.getProfilePicture() !=null )
        {
            /**
             * Downloads profile pic if available
             */
            imageDownloader.getProfilePicBitmap(currentUser,profilePic);
        }

        Button pic_button = findViewById(R.id.pic_button);
        pic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /**
                 * links to customActivityResultLauncher to display the image in imageview
                 */
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                customActivityResultLauncher.launch(imageIntent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.format("Email%sEmail", emailEdit.getText().toString()));
                if (validEmail(emailEdit.getText().toString().replace("\n", ""))) {
                    currentUser.setEmail(emailEdit.getText().toString().replace("\n", ""));
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                }
                if (validPhone(phoneNumberEdit.getText().toString().replace("\n", ""))) {
                    currentUser.setPhoneNumber(phoneNumberEdit.getText().toString().replace("\n", ""));
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }
                if (validName(firstNameEdit.getText().toString().replace("\n", ""))) {
                    currentUser.setFirstName(firstNameEdit.getText().toString().replace("\n", ""));
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Invalid First Name", Toast.LENGTH_SHORT).show();
                }
                if (validName(lastNameEdit.getText().toString().replace("\n", ""))) {
                    currentUser.setLastName(lastNameEdit.getText().toString().replace("\n", ""));
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Invalid Last Name", Toast.LENGTH_SHORT).show();
                }

                //setup profile url
                profileUrl = "profiles/"+currentUser.getDeviceID();
                //prep image for storage

                FirebaseUtil.updateProfileInfo(FirebaseFirestore.getInstance(), currentUser);
                Intent intent = new Intent(ProfileActivity.this, UserActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });
    }

    public static boolean validEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validPhone(String phoneNumber) {
        String phoneRegex = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
    public static boolean validName(String name) {
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
}
