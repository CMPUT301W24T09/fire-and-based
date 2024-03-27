package com.example.fire_and_based;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Represents an announcement (for an event).
 * @author Ilya
 */
public class Announcement {
    private String title;
    private String content;
    private Long timestamp;
    private String sender;
    private String eventID;

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

}
