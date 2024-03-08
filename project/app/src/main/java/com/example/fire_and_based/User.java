package com.example.fire_and_based;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class User implements Parcelable {
    private String deviceID;
    private String userName;
    private String profilePicture;
    private ArrayList<Event> userRegisteredEvents;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    public User(String deviceID, String userName, ArrayList<Event> userRegisteredEvents, String profilePicture){
        this.deviceID = deviceID;
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.userRegisteredEvents = userRegisteredEvents;

    }


    protected User(Parcel in) {
        deviceID = in.readString();
        userName = in.readString();
        profilePicture = in.readString();
        userRegisteredEvents = in.readArrayList(null);
        firstName = in.readString();
        lastName = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(deviceID);
        dest.writeString(userName);
        dest.writeString(profilePicture);
        dest.writeList(userRegisteredEvents);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNumber);
        dest.writeString(email);
    }

    String getProfilePicture(){return this.profilePicture; }



}
