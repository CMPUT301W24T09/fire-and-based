package com.example.fire_and_based;

public class Announcement {
    private String title;
    private String content;
    private Long timestamp;
    private String sender;
    private String eventID;
    private Boolean sent;

    public Announcement(String title, String content, long timestamp, String sender, String eventID, Boolean sent) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.sender = sender;
        this.eventID = eventID;
        this.sent = sent;
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

    public Boolean getSent(){
        return sent;
    }
}
