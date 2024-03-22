package com.example.fire_and_based;

public class Announcement {
    private String title;
    private String content;
    private long timestamp;
    private String sender;

    public Announcement(String title, String content, long timestamp, String sender) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.sender = sender;
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
}
