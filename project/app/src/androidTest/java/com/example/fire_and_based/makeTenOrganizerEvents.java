package com.example.fire_and_based;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class makeTenOrganizerEvents {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately


    public void makeTenEventsCall() throws InterruptedException {
        for (int i = 0; i < 10; i++){
            makeTenEvents();
        }
    }

    /**
     * A test class designed to validate the functionality of creating ten organizer events
     * within the application. It ensures that the process for an organizer to create an event,
     * including filling out event details and generating a QR code, works as expected.
     */
    @Test
    public void makeTenEvents() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.floating_action_button)).perform(click()); // in event creation

        // Assuming you are now in a state where an EditText needs to be filled
        onView(withId(R.id.event_name_editable))
                .perform(typeText("Test Event Name"), closeSoftKeyboard());

        onView(withId(R.id.event_description_editable))
                .perform(typeText("Test Event Description"), closeSoftKeyboard());
        onView(withId(R.id.event_date_editable_end))
                .perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.event_time_editable))
                .perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.event_time_editable_end))
                .perform(click());
        onView(withText("OK")).perform(click());
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
        onView(withText("Test Event Name")).check(matches(isDisplayed()));
    }
}