package com.example.fire_and_based;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.ArrayList;

/**
 * Represents a user in the application, including their personal information and events they are associated with.
 * Implements Parcelable to enable passing User objects between activities.
 */
public class User implements Parcelable {
    private String deviceID;
    private String userName;
    private String profilePicture;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String homepage;
    private boolean isAdmin = false;

    /**
     * Constructs a User with device ID, user name, a list of registered events, and a profile picture.
     *
     * @param deviceID        The unique identifier for the user's device.
     * @param userName        The user's chosen username.
     * @param profilePicture  The URL or path to the user's profile picture.
     */
    User(String deviceID, String userName, String profilePicture) {
        this.deviceID = deviceID;
        this.userName = userName;
        this.profilePicture = profilePicture;
    }

    /**
     * Constructs a User with detailed personal information, including device ID, user name, registered events, profile picture,
     * first name, last name, email, and phone number.
     *
     * @param deviceID        The unique identifier for the user's device.
     * @param userName        The user's chosen username.
     * @param profilePicture  The URL or path to the user's profile picture.
     * @param firstName       The user's first name.
     * @param lastName        The user's last name.
     * @param phoneNumber     The user's phone number.
     * @param email           The user's email address.
     * @param homepage        The user's homepage
     * @param isAdmin         Whether or not the user is an admin of the app
     */
    User(String deviceID, String userName, String profilePicture, String firstName, String lastName,
         String phoneNumber, String email, String homepage, boolean isAdmin) {
        this.deviceID = deviceID;
        this.userName = userName;
        this.profilePicture = profilePicture;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.homepage = homepage;
        this.isAdmin = isAdmin;
    }

    /**
     * Constructs a User instance from a Parcel, enabling the class to be parcelable.
     *
     * @param in The Parcel containing the User data.
     */
    protected User(Parcel in) {
        deviceID = in.readString();
        userName = in.readString();
        profilePicture = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        homepage = in.readString();
        isAdmin = (in.readInt() == 1);
    }

    /**
     * Creator to facilitate the parceling of User objects.
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

    // GETTERS AND SETTERS

    /**
     * Gets the device ID of the user.
     *
     * @return The device ID.
     */
    public String getDeviceID() {
        return this.deviceID;
    }

    /**
     * Gets the user name.
     *
     * @return The user name.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets the device ID of the user.
     *
     * @param deviceID The new device ID.
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Sets the user name.
     *
     * @param userName The new user name.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Sets the profile picture of the user.
     *
     * @param profilePicture The new profile picture URL or path.
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Gets the first name of the user.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Gets the homepage of the user.
     *
     * @return The homepage.
     */
    public String getHomepage(){
        return homepage;
    }

    /**
     * Sets the homepage of the user.
     * @param homepage The homepage.
     */
    public void setHomepage(String homepage){
        this.homepage = homepage;
    }

    /**
     * Checks if the user is an Administrator of the app
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(){
        return isAdmin;
    }

    /**
     * Sets the first name of the user.
     *
     * @param firstName The new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     *
     * @param lastName The new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber The new phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The new email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
     *
     * @return A bitmask indicating the set of special object types marshaled by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(deviceID);
        dest.writeString(userName);
        dest.writeString(profilePicture);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(homepage);
        dest.writeInt(isAdmin ? 1 : 0);  // write boolean as int
    }


    /**
     * Gets the profile picture of the user.
     *
     * @return The profile picture URL or path.
     */
    String getProfilePicture() {
        return this.profilePicture;
    }
}
