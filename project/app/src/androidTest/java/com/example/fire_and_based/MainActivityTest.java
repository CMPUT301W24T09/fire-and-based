package com.example.fire_and_based;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

/**
 * This class contains intent tests for the MainActivity.
 * It verifies that the screen switch to UserActivity occurs correctly.
 * @author Tyler, Sumayya
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
    @Test
    public void testScreenSwitchIntent() throws InterruptedException {
        // Simulate a user action that should trigger the screen switch
        // Verify that the correct intent was launched

        // We are trying to test that the app starts in MainActivity and Automatically switches to UserActivity
        Thread.sleep(2000);
        intended(hasComponent(UserActivity.class.getName()));
    }
}
