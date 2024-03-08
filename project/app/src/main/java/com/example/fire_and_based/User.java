package com.example.fire_and_based;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.ArrayList;

/**
 * Represents a user with personal information, device details, and events they are registered to.
 * This class implements Parcelable to facilitate passing User objects between activities.
 */
public class User implements Parcelable {
    private String deviceID;
    private String userName;
    private String profilePicture;
    private ArrayList<Event> userEvents;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    /**
     * Constructs a new User with specified device ID, user name, profile picture, and a several parameters.
     *
     * @param deviceID The unique identifier for the user's device.
     * @param userName The user's chosen name.
     * @param userRegisteredEvents A list of events the user has registered for.
     * @param profilePicture The URL or resource identifier for the user's profile picture.
     * @param firstName The first name of the User.
     * @param lastName The last name of the User.
     * @param email The User's email.
     * @param phoneNumber The User's phone number.
     */
    }
    User(String deviceID, String userName, ArrayList<Event> userRegisteredEvents, String profilePicture, String firstName, String lastName,
         String email, String phoneNumber) {
        this.deviceID = deviceID;
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.userEvents = userRegisteredEvents;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Internal constructor to create a User from a Parcel.
     * This is used for parceling/unparceling.
     *
     * @param in The Parcel from which to read the user's data.
     */
    protected User(Parcel in) {
        deviceID = in.readString();
        userName = in.readString();
        profilePicture = in.readString();
        userEvents = in.createTypedArrayList(Event.CREATOR);
        firstName = in.readString();
        lastName = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
    }

    /**
     * Creator required for classes that implement Parcelable.
     */
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

    // Accessor and mutator methods

    public String getDeviceID() {
        return this.deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    public ArrayList<Event> getUserEvents(){
        return this.userEvents;
    }

    public ArrayList<Event> getUserEvents() {
        return this.userRegisteredEvents;
    }

    public void setUserRegisteredEvents(ArrayList<Event> eventList) {
        this.userRegisteredEvents = eventList;
    }


    public String getProfilePicture() {
        return this.profilePicture;
    public void addEvent(Event event){
        this.userEvents.add(event);
    }

    public ArrayList<Event> getEvents(){
        return this.userEvents;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    public void setUserRegisteredEvents(ArrayList<Event> eventList){
        this.userEvents = eventList;
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

    // Parcelable implementation methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(deviceID);
        dest.writeString(userName);
        dest.writeString(profilePicture);
        dest.writeTypedList(userEvents);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNumber);
        dest.writeString(email);
    }
}
