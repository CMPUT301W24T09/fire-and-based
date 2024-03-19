package com.example.fire_and_based;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;

public class UserTest {

    private User testUser = new User("FakeDeviceID", "FakeUserName", "FakeProfilePicture");

    private Event testEvent = new Event("Test Event", "This is a test event", "TestBannerURL", "TestQRCode");



    @Test
    public void getDeviceIDTest() {
        assertEquals("FakeDeviceID", testUser.getDeviceID());
    }
//
    @Test
    public void getUserNameTest() {
        assertEquals("FakeUserName", testUser.getUserName());
    }

    @Test
    public void setDeviceIDTest() {
        testUser.setDeviceID("NewDeviceID");
        assertEquals("NewDeviceID", testUser.getDeviceID());
    }

    @Test
    public void setUserNameTest() {
        testUser.setUserName("NewUserName");
        assertEquals("NewUserName", testUser.getUserName());
    }

    @Test
    public void setProfilePictureTest() {
        testUser.setProfilePicture("NewProfilePictureURL");
        assertEquals("NewProfilePictureURL", testUser.getProfilePicture());
    }

    @Test
    public void getFirstNameTest() {
        testUser.setFirstName("John");
        assertEquals("John", testUser.getFirstName());
    }

    @Test
    public void setFirstNameTest() {
        testUser.setFirstName("Jane");
        assertEquals("Jane", testUser.getFirstName());
    }

    @Test
    public void getLastNameTest() {
        testUser.setLastName("Doe");
        assertEquals("Doe", testUser.getLastName());
    }

    @Test
    public void setLastNameTest() {
        testUser.setLastName("Smith");
        assertEquals("Smith", testUser.getLastName());
    }

    @Test
    public void getPhoneNumberTest() {
        testUser.setPhoneNumber("1234567890");
        assertEquals("1234567890", testUser.getPhoneNumber());
    }

    @Test
    public void setPhoneNumberTest() {
        testUser.setPhoneNumber("0987654321");
        assertEquals("0987654321", testUser.getPhoneNumber());
    }

    @Test
    public void getEmailTest() {
        testUser.setEmail("test@example.com");
        assertEquals("test@example.com", testUser.getEmail());
    }

    @Test
    public void setEmailTest() {
        testUser.setEmail("example@test.com");
        assertEquals("example@test.com", testUser.getEmail());
    }

}
