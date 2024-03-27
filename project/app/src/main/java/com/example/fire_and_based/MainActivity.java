package com.example.fire_and_based;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import java.util.UUID;

/**
 * This class is the main activity. It displays the loading page for the app
 * while the app fetches the user info. It then sends you to UserActivity.
 * @author Tyler, Carson, Sumayya, Ilya
 */
public class MainActivity extends AppCompatActivity {
    private static User currentUser;
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

            String finalUuid = uuid;

            Log.d(TAG, "Getting FCM Token");
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
                        } else {
                            Intent intent = new Intent(MainActivity.this, UserActivity.class);
                            intent.putExtra("user", currentUser);
                            startActivity(intent);
                        }
                    },
                    e -> {
                        Log.d(TAG, e.toString());
                    });
        }



    }

    protected static String getDeviceID() {
        return currentUser.getDeviceID();
    }
}
