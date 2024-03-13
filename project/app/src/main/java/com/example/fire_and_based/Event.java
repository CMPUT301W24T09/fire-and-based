package com.example.fire_and_based;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.io.Serializable;

/**
 * Represents an event with a name, description, banner image, and a QR code.
 * This class implements Parcelable to allow event objects to be passed between activities.
 */
public class Event implements Parcelable {
    public String eventName;
    public String eventDescription;
    public String eventBanner;
    public String QRcode;

    /**
     * Constructs a new Event with the specified name, description, banner image, and QR code.
     *
     * @param eventName        The name of the event.
     * @param eventDescription A description of the event.
     * @param eventBanner      A URL or path to an image banner for the event.
     * @param QRcode           A QR code associated with the event, possibly for event entry or information.
     */
    Event(String eventName, String eventDescription, String eventBanner, String QRcode) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventBanner = eventBanner;
        this.QRcode = QRcode;
    }

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


    // GETTERS AND SETTERS

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
    }
}
