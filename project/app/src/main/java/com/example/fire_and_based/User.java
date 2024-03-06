package com.example.fire_and_based;

import java.util.ArrayList;

public class User {
    private String deviceID;
    private String userName;
    private ArrayList<Event> userRegisteredEvents;

    User(String deviceID, String userName, ArrayList<Event> userRegisteredEvents){
        this.deviceID = deviceID;
        this.userName = userName;
        this.userRegisteredEvents = userRegisteredEvents;
    }

    public String getDeviceID(){
        return this.deviceID;
    }

    public String getUserName() {
        return this.userName;
    }

    public ArrayList<Event> getUserEvents(){
        return this.userRegisteredEvents;
    }

    public void setDeviceID(String deviceID){
        this.deviceID = deviceID;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void addEvent(Event event){
        this.userRegisteredEvents.add(event);
    }

}
