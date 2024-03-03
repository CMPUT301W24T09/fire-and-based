package com.example.fire_and_based;

public class Event {
    public String eventName;
    public String eventDescription;
    public String eventBanner;



    Event(String eventName, String eventDescription){
        this.eventName = eventName;
        this.eventDescription = eventDescription;
    }


    String getEventName() {
        return this.eventName;
    }

    String getEventDescription() {
        return this.eventDescription;
    }
    String getEventBanner(){return this.eventBanner; }
    void setEventBanner(String banner){this.eventBanner = banner;}
}
