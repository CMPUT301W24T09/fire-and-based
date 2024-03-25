package com.example.fire_and_based;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.type.DateTime;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an event with a name, description, banner image, and a QR code.
 * This class implements Parcelable to allow event objects to be passed between activities.
 */
public class Event implements Parcelable {
    private String eventName;
    private String eventDescription;
    private String eventBanner;
    private String QRcode;
    private Long startTimeStamp;
    private Long endTimeStamp;
    private String location;
    private String bannerQR;
    private ArrayList<Integer> milestones;
    private Long maxAttendees;
    private Boolean trackLocation;

    /**
     * Constructs a new Event with the specified details.
     *
     * @param eventName        The name of the event.
     * @param eventDescription A description of the event.
     * @param eventBanner      A URL or path to an image banner for the event.
     * @param QRcode           A QR code associated with the event, possibly for event entry or information.
     * @param startTimeStamp   The start time of the event.
     * @param endTimeStamp     The end time of the event.
     * @param location         The location of the event.
     * @param bannerQR         The QR code for the event banner.
     * @param milestones       The milestones of the event.
     * @param maxAttendees     The maximum number of attendees for the event.
     * @param trackLocation    Whether the event is tracking location.
     */
    Event(String eventName, String eventDescription, String eventBanner, String QRcode, Long startTimeStamp, Long endTimeStamp, String location, String bannerQR, ArrayList<Integer> milestones, Long maxAttendees, Boolean trackLocation) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventBanner = eventBanner;
        this.QRcode = QRcode;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.location = location;
        this.bannerQR = bannerQR;
        this.milestones = milestones;
        this.maxAttendees = maxAttendees;
        this.trackLocation = trackLocation;
    }

    /**
     * Constructs an Event from a Parcel, allowing for the class to be parcelable.
     *
     * @param in The Parcel containing the event data.
     */
    /**
     * Constructs an Event from a Parcel, allowing for the class to be parcelable.
     *
     * @param in The Parcel containing the event data.
     */
    protected Event(Parcel in) {
        eventName = in.readString();
        eventDescription = in.readString();
        eventBanner = in.readString();
        QRcode = in.readString();
        startTimeStamp = in.readLong();
        endTimeStamp = in.readLong();
        location = in.readString();
        bannerQR = in.readString();
        milestones = in.readArrayList(Integer.class.getClassLoader());
        maxAttendees = in.readLong();
        trackLocation = in.readByte() != 0;  // trackLocation == true if byte != 0
    }



    /**
     * Creator to facilitate the parceling of Event objects.
     */
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    Event() {
    }


    // GETTERS AND SETTERS

    /**
     * Returns the start time of the event.
     *
     * @return the start time of the event.
     */
    public Long getEventStart() {
        return this.startTimeStamp;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTimeStamp the start time of the event.
     */
    public void setEventStart(Long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    /**
     * Returns the end time of the event.
     *
     * @return the end time of the event.
     */
    public Long getEventEnd() {
        return this.endTimeStamp;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTimeStamp the end time of the event.
     */
    public void setEventEnd(Long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    /**
     * Returns the location of the event.
     *
     * @return the location of the event.
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the location of the event.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the QR code for the event banner.
     *
     * @return the QR code for the event banner.
     */
    public String getBannerQR() {
        return this.bannerQR;
    }

    /**
     * Sets the QR code for the event banner.
     *
     * @param bannerQR the QR code for the event banner.
     */
    public void setBannerQR(String bannerQR) {
        this.bannerQR = bannerQR;
    }

    /**
     * Returns the milestones of the event.
     *
     * @return the milestones of the event.
     */
    public ArrayList<Integer> getMilestones() {
        return this.milestones;
    }

    /**
     * Sets the milestones of the event.
     *
     * @param milestones the milestones of the event.
     */
    public void setMilestones(ArrayList<Integer> milestones) {
        this.milestones = milestones;
    }

    /**
     * Returns the maximum number of attendees for the event.
     *
     * @return the maximum number of attendees for the event.
     */
    public Long getMaxAttendees() {
        return this.maxAttendees;
    }

    /**
     * Sets the maximum number of attendees for the event.
     *
     * @param maxAttendees the maximum number of attendees for the event.
     */
    public void setMaxAttendees(Long maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    /**
     * Returns whether the event is tracking location.
     *
     * @return true if the event is tracking location, false otherwise.
     */
    public Boolean isTrackLocation() {
        return this.trackLocation;
    }

    /**
     * Sets whether the event should track location.
     *
     * @param trackLocation true if the event should track location, false otherwise.
     */
    public void setTrackLocation(Boolean trackLocation) {
        this.trackLocation = trackLocation;
    }


    /**
     * Returns the name of the event.
     *
     * @return The name of the event.
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     * Returns the description of the event.
     *
     * @return The description of the event.
     */
    public String getEventDescription() {
        return this.eventDescription;
    }

    /**
     * Returns the banner image of the event.
     *
     * @return The banner image of the event.
     */
    String getEventBanner() {
        return this.eventBanner;
    }

    /**
     * Returns the QR code of the event.
     *
     * @return The QR code of the event.
     */
    String getQRcode() {
        return this.QRcode;
    }

    /**
     * Updates the event banner with the specified banner.
     *
     * @param banner The new banner image for the event.
     */
    void setEventBanner(String banner) {
        this.eventBanner = banner;
    }

    /**
     * Updates the event name with the specified name.
     *
     * @param updatedEventName The new name for the event.
     */
    public void setEventName(String updatedEventName) {
        this.eventName = updatedEventName;
    }

    /**
     * Updates the event description with the specified description.
     *
     * @param eventDescription The new description for the event.
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

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
        dest.writeString(eventName);
        dest.writeString(eventDescription);
        dest.writeString(eventBanner);
        dest.writeString(QRcode);
        dest.writeLong(startTimeStamp);
        dest.writeLong(endTimeStamp);
        dest.writeString(location);
        dest.writeString(bannerQR);
        dest.writeList(milestones);
        dest.writeLong(maxAttendees);
        dest.writeInt(trackLocation ? 1 : 0);  // write boolean as int
    }


    @Override
    public boolean equals(Object o){
        if (o instanceof Event){
            return (((Event) o).getQRcode().equals(this.getQRcode()));
        }
        return false;
    }
}
