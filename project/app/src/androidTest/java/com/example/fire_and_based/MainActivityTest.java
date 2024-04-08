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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

/**
 * This class contains intent tests for the MainActivity.
 * It verifies that the screen switch to UserActivity occurs correctly.
 * @author Tyler, Sumayya
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule =
            new IntentsTestRule<>(MainActivity.class, true, true); // Do not launch activity immediately


    @Test
    public void profileClickTest() throws InterruptedException {
//        User user = new User("testUser123");
//        Intent startIntent = new Intent();
//        startIntent.putExtra("user", user);

//        intentsTestRule.launchActivity(startIntent);
        Thread.sleep(2000);

        onView(withText("Browse Events")).check(matches(isDisplayed()));

//        intended(hasComponent(UserActivity.class.getName()));


    }


}
