package com.example.fire_and_based;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.content.Intent;
import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SendNotificationTest {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately


    @Test
    public void sendNotifTest() throws InterruptedException {
        User user = new User("testUser123");
        user.setProfilePicture("defaultProfiles/testUser123.jpg");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.floating_action_button)).perform(click()); // in event creation

        // Assuming you are now in a state where an EditText needs to be filled
        onView(withId(R.id.event_name_editable))
                .perform(typeText("Test Event Name"), closeSoftKeyboard());

        onView(withId(R.id.event_description_editable))
                .perform(typeText("Test Event Description"), closeSoftKeyboard());
//        Thread.sleep(1000);
//        onView(withId(R.id.event_date_editable))
//                .perform(click(), closeSoftKeyboard());
//        onView(withText("OK")).perform(click());
//
//        Thread.sleep(1000);

        onView(withId(R.id.event_date_editable_end))
                .perform(click());

        onView(withText("OK")).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.event_time_editable))
                .perform(click());


        onView(withText("OK")).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.event_time_editable_end))
                .perform(click());
        onView(withText("OK")).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.event_location_editable))
                .perform(typeText("Edmonton"), closeSoftKeyboard());


        onView(withId(R.id.event_maximum_attendees_editable))
                .perform(typeText("100"), closeSoftKeyboard());

        onView(withId(R.id.generateQR))
                .perform(click());

        onView(withId(R.id.view_qr_ok))
                .perform(click());

        onView(withId(R.id.create_event_button))
                .perform(click());

        onView(withText("MAKE EVENT")).perform(click());


        Thread.sleep(2000);

        onView(withText("Notifications")).perform(click());

        onView(withId(R.id.announcement_editable))
                .perform(typeText("Testing Notifs"), closeSoftKeyboard());
        Thread.sleep(2000);

//        onView(withText("Send")).perform(click());
//        Thread.sleep(2000);
        // WE CANNOT ACTUALLY SEND NOTIFS GIVEN THE FAKE USER DATA
//
        onView(withText("Testing Notifs")).perform(click());
//





    }

}