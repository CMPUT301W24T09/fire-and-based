package com.example.fire_and_based;

import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

public class AnnouncementUtil {

    /**
     * Can be used to send a message to a given user or all users subscribed to a topic
     * @param recipient a users device ID, or a topic prefixed with /topics/
     * @return whether or not the message was sent successfully
     */
    public static boolean sendToRecipient(Announcement announcement, String recipient){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "{\n    \"to\" : \"" + recipient +"\",\n    \"notification\" : {\n        \"title\" : \"" + announcement.getTitle() +"\",\n        \"body\" : \""+ announcement.getContent() + "\"\n    }\n}");
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "key=" + BuildConfig.API_KEY)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d("Announcement", String.valueOf(response.code()));
            System.out.println(response.code());
            boolean success = response.isSuccessful();
            response.close();
            return success;
        } catch (IOException e) {
            Log.e("Announcement", e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    /**
     * subscribes the current user to a given topic
     * @param topic topic to subscribe to
     */
    public static void subscribeToTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("AnnouncementUtil", "Failed to subscribe to " + topic);
                    } else {
                        Log.d("Announcement", "Subscribed to " + topic);
                    }
                });
    }

    public static void newAnnouncement(FirebaseFirestore db, String content, Event event, OnSuccessListener<Void> successListener, OnFailureListener failureListener){
        Announcement announcement = new Announcement(event.getEventName(), content, System.currentTimeMillis()/1000L, MainActivity.getDeviceID(), event.getQRcode());
        FirebaseUtil.saveAnnouncement(db, event.getQRcode(), announcement, aVoid -> {
            boolean success = sendToRecipient(announcement, "/topics/"+event.getQRcode());
            if (!success){
                failureListener.onFailure(new Exception("Failed to send notification"));
            } else {
                successListener.onSuccess(null);
            }
        }, failureListener);
    }

}
