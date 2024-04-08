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

import com.google.android.material.button.MaterialButton;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * This class tests the check-in functionality of the UserActivity within the app.
 * It simulates user interactions for joining, checking in, and checking out of an event.
 */
@RunWith(AndroidJUnit4.class)
public class CheckInEventTest {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately

    /**
     * Tests the complete flow from launching the activity, joining an event,
     * checking in, and finally checking out and leaving the event.
     * Verifies that each action is performed as expected by checking the presence
     * of specific UI elements.
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    @Test
    public void checkInTest() throws InterruptedException {
        User user = new User("testUser123");
        user.setProfilePicture("defaultProfiles/testUser123.jpg");

        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.browse_item)).perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Join Event")).check(matches(isDisplayed()));
        Thread.sleep(500);
        onView(withText("Join Event")).perform(click());
        Thread.sleep(500);
        onView(withText("Check In")).perform(click());
        Thread.sleep(500);

        onView(withText("NO")).perform(click());  // sometimes this will fail because events dont have location permissions on
        Thread.sleep(500);
        Thread.sleep(500);
        onView(withText("Check Out")).check(matches(isDisplayed()));
        onView(withText("Leave Event")).perform(click());
    }
}
