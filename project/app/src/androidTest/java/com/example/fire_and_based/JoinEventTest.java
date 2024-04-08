package com.example.fire_and_based;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * This class tests the event joining functionalities in the app.
 * It includes tests for joining an event, verifying the user's attendance,
 * and checking the user's organizing capabilities through the app's UI.
 */
@RunWith(AndroidJUnit4.class)
public class JoinEventTest {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately


    @Test
    public void joinEventTest() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

//        onView(withId(R.id.browse_item)).perform(click());



        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Join Event")).check(matches(isDisplayed()));
        onView(withId(R.id.join_event_button)).perform(click());
        onView(withText("Check In")).check(matches(isDisplayed()));
        onView(withText("Info")).check(matches(isDisplayed()));
        onView(withText("Notifications")).check(matches(isDisplayed()));
        onView(withText("Map")).check(matches(isDisplayed()));
        onView(withText("View Event QR Codes")).check(matches(isDisplayed()));
        onView(withText("Leave Event")).check(matches(isDisplayed()));
    }

    @Test
    public void attendingEventTest() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

//        onView(withId(R.id.browse_item)).perform(click());

        onView(withId(R.id.attending_item)).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());


        onView(withText("Info")).check(matches(isDisplayed()));
        onView(withText("Notifications")).check(matches(isDisplayed()));
        onView(withText("Map")).check(matches(isDisplayed()));
        onView(withText("View Event QR Codes")).check(matches(isDisplayed()));
        onView(withText("Leave Event")).check(matches(isDisplayed()));
        onView(withId(R.id.additional_actions_button)).perform(click());

    }

    @Test
    public void organizingEventTest() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.organizing_item)).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());


        onView(withText("Info")).check(matches(isDisplayed()));
        onView(withText("Notifications")).check(matches(isDisplayed()));
        onView(withText("Map")).check(matches(isDisplayed()));
        onView(withText("Edit Details")).check(matches(isDisplayed()));
        onView(withText("Attendee List")).check(matches(isDisplayed()));
        onView(withText("View Event QR Codes")).check(matches(isDisplayed()));

    }



}