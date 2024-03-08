package com.example.fire_and_based;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.UUID;

/**
 * MainActivity of the application, serving as the entry point for the user.
 * This activity is responsible for initializing the app, managing user sessions,
 * and navigating to the UserActivity if a user session is found.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Static variable to hold the current user's session data across the application.
     */
    public static User currentUser;

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
     * to programmatically interact with widgets in the UI, initializing class-scope variables, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve or generate a unique identifier for the user and manage user session
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uuid = sharedPref.getString("uuid_key", "");
        if (TextUtils.isEmpty(uuid)) {
            // If UUID is not found, create a new user object and store it in Firestore
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("uuid_key", uuid);
            currentUser = new User(uuid, "", new ArrayList<Event>(), "");
            FirebaseUtil.addUserToDB(db, currentUser);
            editor.commit();
        } else {
            // If UUID exists, fetch the user object from Firestore and proceed to UserActivity
            FirebaseUtil.getUserObject(db, uuid, new FirebaseUtil.UserObjectCallback() {
                @Override
                public void onUserFetched(User user) {
                    currentUser = user;
                    Log.d(TAG, String.format("Username: %s UserID: %s", currentUser.getFirstName(), currentUser.getDeviceID()));
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    intent.putExtra("currentUser", currentUser);
                    startActivity(intent);
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, e.toString());
                }
            });
        }
    }

    /**
     * Returns the current user session if exists.
     *
     * @return The currently logged-in {@link User}, or null if no session is active.
     */
    protected static User getCurrentUser() {
        return currentUser;
    }
}
