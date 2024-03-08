package com.example.fire_and_based;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

/**
 * Represents an event with a name, description, banner, and QR code.
 * This class implements Parcelable to allow event objects to be passed between activities.
 */
public class Event implements Parcelable {
    public String eventName;
    public String eventDescription;
    public String eventBanner;
    public String QRcode;

    /**
     * Constructs a new Event with specified details.
     *
     * @param eventName Name of the event.
     * @param eventDescription Description of the event.
     * @param eventBanner URL or resource identifier for the event banner image.
     * @param QRcode QR code value associated with the event.
     */
    public Event(String eventName, String eventDescription, String eventBanner, String QRcode){
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventBanner = eventBanner;
        this.QRcode = QRcode;
    }

    /**
     * Internal constructor to create an Event from a Parcel.
     * This is used for parceling/unparceling.
     *
     * @param in The Parcel from which to read the event's data.
     */
    protected Event(Parcel in) {
        eventName = in.readString();
        eventDescription = in.readString();
        eventBanner = in.readString();
        QRcode = in.readString(); // Ensure QR code is also parcelled if needed.
    }

    /**
     * Creator required for classes that implement Parcelable.
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

    // Accessor methods (getters and setters)

    /**
     * Gets the event name.
     *
     * @return The event name.
     */
    String getEventName() {
        return this.eventName;
    }

    /**
     * Gets the event description.
     *
     * @return The event description.
     */
    String getEventDescription() {
        return this.eventDescription;
    }

    /**
     * Gets the event banner identifier or URL.
     *
     * @return The event banner.
     */
    String getEventBanner() {
        return this.eventBanner;
    }

    /**
     * Gets the QR code associated with the event.
     *
     * @return The QR code value.
     */
    String getQRcode() {
        return this.QRcode;
    }

    /**
     * Sets the event name.
     *
     * @param eventName The new name of the event.
     */
    void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Sets the event description.
     *
     * @param eventDescription The new description of the event.
     */
    void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * Sets the event banner.
     *
     * @param banner The new banner identifier or URL for the event.
     */
    void setEventBanner(String banner) {
        this.eventBanner = banner;
    }

    /**
     * Sets the QR code for the event.
     *
     * @param QRcode The new QR code value.
     */
    void setQRcode(String QRcode) {
        this.QRcode = QRcode;
    }

    // Parcelable implementation methods

    /**
     * Describe the kinds of special objects contained in this Parcelable's marshalled representation.
     *
     * @return A bitmask indicating the set of special object types marshalled by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this event into a Parcel.
     *
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written (not used here).
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventDescription);
        dest.writeString(eventBanner);
        dest.writeString(QRcode); // Ensure QR code is also parcelled.
    }
}
