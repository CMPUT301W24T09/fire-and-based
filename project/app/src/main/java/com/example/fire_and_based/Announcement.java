package com.example.fire_and_based;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Announcement {
    private String title;
    private String content;
    private Long timestamp;
    private String sender;
    private String eventID;
    public static String SERVER_KEY; //temporary workaround

    public Announcement(String title, String content, long timestamp, String sender, String eventID) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.sender = sender;
        this.eventID = eventID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getEventID(){
        return eventID;
    }

    /**
     * Can be used to send a message to a given user or all users subscribed to a topic
     * @param recipient a users device ID, or a topic prefixed with /topics/
     * @return whether or not the message was sent successfully
     */
    public boolean sendToRecipient(String recipient){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"to\" : \"" + recipient +"\",\n    \"notification\" : {\n        \"title\" : \"Title of Your Notification\",\n        \"body\" : \"Body of Your Notification\"\n    }\n}");
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "key=" + SERVER_KEY)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d("Announcement", String.valueOf(response.code()));
            boolean success = response.isSuccessful();
            response.close();
            return success;
        } catch (IOException e) {
            Log.e("Announcement", e.getMessage());
            return false;
        }
    }
}
