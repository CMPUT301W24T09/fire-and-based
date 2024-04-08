package com.example.fire_and_based;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
 * This class is designed to test the functionality of leaving an event through the app's user interface.
 * It simulates user interactions for joining an event and then leaving it, ensuring the app behaves as expected.
 */
@RunWith(AndroidJUnit4.class)
public class LeaveEventTest {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately


    @Test
    public void joinEventTest() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.browse_item)).perform(closeSoftKeyboard());
        Thread.sleep(1000);
        onView(withId(R.id.attending_item)).perform(click());

        Thread.sleep(2000);
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        Thread.sleep(2000);
        onView(withText("Leave Event")).check(matches(isDisplayed()));
        Thread.sleep(2000);
        onView(withId(R.id.additional_actions_button)).perform(click());
        Thread.sleep(2000);
        onView(withText("Attending Events")).check(matches(isDisplayed()));
    }
}