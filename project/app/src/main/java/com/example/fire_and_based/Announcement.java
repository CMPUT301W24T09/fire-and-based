package com.example.fire_and_based;

/**
 * Represents an announcement (for an event).
 * @author Ilya
 */
public class Announcement {
    private String content;
    private long timestamp;
    private String sender;

    public Announcement(String content, long timestamp, String sender) {
        this.content = content;
        this.timestamp = timestamp;
        this.sender = sender;
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
}
