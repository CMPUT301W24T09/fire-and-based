package com.example.fire_and_based;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
public class Event implements Parcelable {
    private String eventName;
    private String eventDescription;
    private String eventBanner;
    private String QRcode;

    // constructor
    Event(String eventName, String eventDescription, String eventBanner, String QRcode){
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventBanner = eventBanner;
        this.QRcode = QRcode;
    }


    // for parcelable ( to pass objects into new activities)
    protected Event(Parcel in) {
        eventName = in.readString();
        eventDescription = in.readString();
        eventBanner = in.readString();
    }
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
    public String getEventName() {
        return this.eventName;
    }
    public String getEventDescription() {
        return this.eventDescription;
    }
    public String getEventBanner(){return this.eventBanner; }
    public String getQRcode(){return this.QRcode;}
    public void setEventBanner(String banner){this.eventBanner = banner;}




    // for parcelable ( to pass objects into new activities)
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventDescription);
        dest.writeString(eventBanner);
    }
}
