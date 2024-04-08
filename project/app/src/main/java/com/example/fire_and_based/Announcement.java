package com.example.fire_and_based;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

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


    /**
     * Constructs an Announcement with detailed information, including title, content, timestamp,
     * sender, and the ID of the event.
     *
     * @param title        The announcement title.
     * @param content      The announcement's content.
     * @param timestamp    The time that the announcement was posted.
     * @param sender       The individual who sent the announcement.
     * @param eventID      The eventID of the event that the announcement is for.
     */
    public Announcement(String title, String content, long timestamp, String sender, String eventID) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.sender = sender;
        this.eventID = eventID;
    }

    // GETTERS AND SETTERS

    /**
     * Gets the title of the announcement.
     *
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the content of the announcement.
     *
     * @return The content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the time stamp of the announcement.
     *
     * @return The time stamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the sender of the announcement.
     *
     * @return The sender.
     */
    public String getSender() {
        return sender;
    }


    /**
     * Gets the event ID of the announcement.
     *
     * @return The event ID.
     */
    public String getEventID(){
        return eventID;
    }


    public String dateFromLong(long timeStamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String monthName = months[month];

        return (monthName + " " + day);
    }
}
