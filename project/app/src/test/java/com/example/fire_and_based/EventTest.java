package com.example.fire_and_based;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Array;
import java.util.ArrayList;
public class EventTest {

    Event event = new Event("Default Event Name", "Default Event Description", "Default Banner URL", "DefaultQRCode");

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
        event.setEventName("Updated Event Name");
        assertEquals("Updated Event Name", event.getEventName());
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

    @Test
    public void setQRcodeTest() {
        event.setQRcode("UpdatedQRCode");
        assertEquals("UpdatedQRCode", event.getQRcode());
    }
}
