package com.example.fire_and_based;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import java.util.UUID;

/**
 * This class is the main activity. It displays the loading page for the app
 * while the app fetches the user info. It then sends you to UserActivity.
 */
public class MainActivity extends AppCompatActivity {
    private static User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        String uuid = sharedPref.getString("uuid_key", "");

        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("uuid_key", uuid);
            currentUser = new User(uuid, "", new ArrayList<Event>(), "");
            FirebaseUtil.addUserToDB(db, currentUser);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        }
        else {
            String finalUuid = uuid;
            FirebaseUtil.getUserObject(db, uuid, new FirebaseUtil.UserObjectCallback() {
                @Override
                public void onUserFetched(User user) {
                    currentUser = user;
                    Log.d(TAG, String.format("Username: %s UserID: %s", currentUser.getFirstName(), currentUser.getDeviceID()));

                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                }
                @Override
                public void onError(Exception e) {

                    Log.w(TAG, e.toString());
                    Log.d(TAG, finalUuid);
                }
            });
        }
    }

    protected static String getDeviceID() {
        return currentUser.getDeviceID();
    }
}
