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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * This class is the main activity. It displays the loading page for the app
 * while the app fetches the user info. It then sends you to UserActivity.
 * @author Tyler, Carson, Sumayya, Ilya, Aiden
 */
public class MainActivity extends AppCompatActivity {
    private static User currentUser;
    StorageReference fireRef = FirebaseStorage.getInstance().getReference();

    /**
     * Initializes the main activity, checks for existing user UUID,
     * generates a UUID if none exists, and proceeds to create or fetch user data accordingly.
     * @param savedInstanceState The saved instance state
     */
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
            createNewUser(db, sharedPref);
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
                        sharedPref.edit().remove("uuid_key").commit();
                        createNewUser(db, sharedPref);
                    });
        }

    }

    /**
     * Adds fields to the user document in Firebase, specifically for attendeeEvents and organizerEvents.
     * @param db The instance of FirebaseFirestore
     * @param user The user object
     * @param successListener Listener for successful update
     * @param failureListener Listener for failed update
     */
    public static void addFieldstoUser(FirebaseFirestore db, User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener ){
        db.collection("users").document(user.getDeviceID())
                .update("attendeeEvents", FieldValue.arrayUnion())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

        db.collection("users").document(user.getDeviceID())
                .update("organizerEvents", FieldValue.arrayUnion())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

        // For some reason this isn't being added to the database despite it being an attribute of the user class so I did this
        db.collection("users").document(user.getDeviceID())
                .update("profilePicture", user.getProfilePicture())
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Gets the device ID of the current user.
     *
     * @return The device ID.
     */
    protected static String getDeviceID() {
        return currentUser.getDeviceID();
    }


    /**
     * Generates a profile picture bitmap based on the given text and size.
     *
     * @param text The text used to generate the profile picture.
     * @param size The size of the profile picture (both width and height).
     * @return The generated profile picture bitmap.
     */
    private Bitmap generateProfilePic(String text, int size) {
        String hash = "";
        int noiseLevel = 50;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(text.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            hash = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Set background color based on hash
        canvas.drawColor(getColorFromHash(hash));

        Random random = new Random();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int alpha = Color.alpha(pixels[i]);
            int red = Color.red(pixels[i]);
            int green = Color.green(pixels[i]);
            int blue = Color.blue(pixels[i]);

            int noiseRed = clampColor(red + random.nextInt(noiseLevel * 2) - noiseLevel);
            int noiseGreen = clampColor(green + random.nextInt(noiseLevel * 2) - noiseLevel);
            int noiseBlue = clampColor(blue + random.nextInt(noiseLevel * 2) - noiseLevel);

            pixels[i] = Color.argb(alpha, noiseRed, noiseGreen, noiseBlue);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }


    /**
     * Clamps the given color value between 0 and 255.
     *
     * @param color The color value to clamp.
     * @return The clamped color value.
     */
    private static int clampColor(int color) {
        return Math.min(255, Math.max(0, color)); // Clamp color value between 0 and 255
    }

    /**
     * Generates a color based on the given hash string.
     *
     * @param hash The hash string used to generate the color.
     * @return The generated color.
     */
    private static int getColorFromHash(String hash) {
        // For simplicity, let's just use the first 6 characters of the hash to generate a color
        String colorHex = hash.substring(0, 6);
        return Color.parseColor("#" + colorHex);
    }


    /**
     * Uploads the user's profile picture to Firebase Storage.
     * Converts the provided bitmap image to a URI and inserts it into the MediaStore.
     * Then, uploads the profile picture to the Firebase Storage using the URI.
     * @param bitmap The bitmap representation of the profile picture.
     * @param uuid The unique identifier associated with the user.
     *             This identifier is used to reference the profile picture in the storage.
     */
    private void uploadProfilePicture(Bitmap bitmap, String uuid) {
        // Converts bitmap to URI
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] byteArray = bytes.toByteArray();

        String fileName = "image_" + uuid + ".png";

        File tempFile = new File(getCacheDir(), fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            outputStream.write(byteArray);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException e) {
            Log.w(TAG, "Error: Didn't make this thing correctly.");
        }

        // Profile pictures referenced by device id
        String defaultImageUrl = "defaultProfiles/" + uuid;
        StorageReference selectionRef = fireRef.child(defaultImageUrl);
        // Uploads profile image as URI
        selectionRef.putFile(Uri.fromFile(tempFile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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


    /**
     * Creates a new user in the Firestore database and generates a unique identifier for the user.
     * If the user is created successfully, the user is added to the database and redirected to the UserActivity.
     *
     * @param db        The instance of the Firestore database.
     * @param sharedPref The SharedPreferences object to store the generated UUID.
     */
    private void createNewUser(FirebaseFirestore db, SharedPreferences sharedPref) {
        Log.d("MainActivity", "No UUID found, generating...");
        String uuid = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("uuid_key", uuid);

        // First time users get deterministically (on device id) generated profile picture
        Bitmap bitmap = generateProfilePic(uuid, 200);
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
                        currentUser = new User(finalUuid, "", "defaultProfiles/" + finalUuid, "", "", "", "", "", false, token);
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
}