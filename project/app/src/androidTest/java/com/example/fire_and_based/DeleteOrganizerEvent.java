package com.example.fire_and_based;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.fire_and_based.R;
import com.example.fire_and_based.User;
import com.example.fire_and_based.UserActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * Tests the functionality of deleting an event by an organizer within the app.
 * This class simulates the user interactions required to delete an event
 * from the list of events they are organizing.
 */
@RunWith(AndroidJUnit4.class)
public class DeleteOrganizerEvent {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately


    @Test
    public void deleteAnEvent() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);
        Thread.sleep(2000);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        Thread.sleep(2000);


        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        Thread.sleep(2000);

        onView(withText("Edit Details")).perform(click());
        Thread.sleep(2000);
        onView(withText("Delete")).perform(click());
        Thread.sleep(2000);
        onView(withText("Organizing Events")).check(matches(isDisplayed()));

    }



}