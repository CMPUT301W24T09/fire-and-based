package com.example.fire_and_based;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * This class is the main activity. It displays the loading page for the app
 * while the app fetches the user info. It then sends you to UserActivity.
 * @author Tyler, Carson, Sumayya, Ilya
 */
public class MainActivity extends AppCompatActivity {
    private static User currentUser;
    StorageReference fireRef = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Needed for notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        Log.d("MainActivity", "Getting UUID");
        String uuid = sharedPref.getString("uuid_key", "");
        Log.d("MainActivity", "UUID is: " + uuid);

        if (TextUtils.isEmpty(uuid)) {
            Log.d("MainActivity", "No UUID found, generating...");
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("uuid_key", uuid);

            // First 5 letters of device id for profile picture
            String profileImageId = uuid.substring(0, Math.min(uuid.length(), 5));

            // First time users get deterministically (on device id) generated profile picture
            Bitmap bitmap = generateBitmap(profileImageId, 100, 75);

            uploadProfilePicture(bitmap, uuid);

            Log.d(TAG, "Getting FCM Token");
            String finalUuid = uuid;
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            String token = task.getResult();
                            Log.d(TAG, "Token: " + token);
                            currentUser = new User(finalUuid, "", "", "", "", "", "", "", false, token);
                            FirebaseUtil.addUserToDB(db, currentUser,
                                    aVoid -> {
                                        addFieldstoUser(db, currentUser, new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused)
                                            {
                                                Log.d(TAG, "Extra Fields Added to User in DB");

                                            }
                                        }, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e)
                                            {
                                                Log.d(TAG, "Extra fields not added");

                                            }
                                        });

                                        Log.d(TAG, "User added successfully");
                                        AnnouncementUtil.subscribeToTopic("system");
                                        Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                        intent.putExtra("user", currentUser);  // we can switch activities now that a user has been created
                                        startActivity(intent);
                                    },
                                    e -> {
                                        Log.d("Failed to add user: ", e.getMessage());
                                    });
                            editor.commit();
                        }
                    });
        }
        else {
            Log.d("MainActivity", "Found UUID, getting user...");
            FirebaseUtil.getUserObject(db, uuid,
                    user -> {
                        currentUser = user;
                        Log.d(TAG, String.format("Username: %s UserID: %s", currentUser.getFirstName(), currentUser.getDeviceID()));

                        if (user.isAdmin()) {
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(MainActivity.this, UserActivity.class);
                            intent.putExtra("user", currentUser);
                            startActivity(intent);
                            finish();
                        }
                    },
                    e -> {
                        Log.d(TAG, e.toString());
                    });
        }



    }

    public static void addFieldstoUser(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener ){
        db.collection("users").document(user.getDeviceID())
                .update("attendeeEvents", FieldValue.arrayUnion())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

        db.collection("users").document(user.getDeviceID())
                .update("organizerEvents", FieldValue.arrayUnion())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    protected static String getDeviceID() {
        return currentUser.getDeviceID();
    }

    private Bitmap generateBitmap(String text, int width, int height) {
        // This is all subjective to change, I don't really like how they look currently so open to suggestions

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // Background colour
        canvas.drawColor(Color.LTGRAY);

        // First 5 letters drawn onto image
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(17);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, width / 2f, height / 2f, paint);

        return bitmap;
    }

    private void uploadProfilePicture(Bitmap bitmap, String uuid) {
        // Converts bitmap to URI
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, uuid, null);
        Uri profilePicUri = Uri.parse(path);

        // Profile pictures referenced by device id
        String imageUrl = "profiles/" + uuid;
        StorageReference selectionRef = fireRef.child(imageUrl);
        // Uploads profile image as URI
        selectionRef.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "superb");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "death");
           }
        });
    }
}
