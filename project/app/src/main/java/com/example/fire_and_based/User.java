package com.example.fire_and_based;

import java.util.ArrayList;

public class User {
    private String deviceID;
    private String userName;
    private String profilePicture;
    private ArrayList<Event> userRegisteredEvents;

    User(String deviceID, String userName, ArrayList<Event> userRegisteredEvents, String profilePicture){
        this.deviceID = deviceID;
        this.profilePicture = profilePicture;
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

    public ArrayList<Event> getEvents(){
        return this.userRegisteredEvents;
    }

    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }
    public void setUserRegisteredEvents(ArrayList<Event> eventList){
        this.userRegisteredEvents = eventList;
    }

    String getProfilePicture(){return this.profilePicture; }



}
