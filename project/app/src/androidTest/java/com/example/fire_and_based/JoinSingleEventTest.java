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

@RunWith(AndroidJUnit4.class)
public class JoinSingleEventTest {

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
        onView(withId(R.id.browse_item)).perform(click());


        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Join Event")).check(matches(isDisplayed()));
        onView(withId(R.id.join_event_button)).perform(click());

    }
}