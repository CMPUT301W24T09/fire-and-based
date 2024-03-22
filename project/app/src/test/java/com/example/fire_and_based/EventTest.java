package com.example.fire_and_based;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class EventTest {

    Event event = new Event("Default Event Name", "Default Event Description", "Default Banner URL", "DefaultQRCode", 0L, 1L, "Edmonton", "banner", new ArrayList<Integer>(), -1L, false);

    @Test
    public void getEventNameTest() {
        assertEquals("Default Event Name", event.getEventName());
    }

    @Test
    public void getEventDescriptionTest() {
        assertEquals("Default Event Description", event.getEventDescription());
    }

    @Test
    public void getEventBannerTest() {
        assertEquals("Default Banner URL", event.getEventBanner());
    }

    @Test
    public void getQRcodeTest() {
        assertEquals("DefaultQRCode", event.getQRcode());
    }

    @Test
    public void setEventNameTest() {
        event.setEventName("New Event Name");
        assertEquals("New Event Name", event.getEventName());
    }

    @Test
    public void setEventDescriptionTest() {
        event.setEventDescription("Updated Event Description");
        assertEquals("Updated Event Description", event.getEventDescription());
    }

    @Test
    public void setEventBannerTest() {
        event.setEventBanner("Updated Banner URL");
        assertEquals("Updated Banner URL", event.getEventBanner());
    }
}
