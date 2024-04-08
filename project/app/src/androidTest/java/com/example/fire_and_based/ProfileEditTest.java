package com.example.fire_and_based;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileEditTest {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately


    @Test
    public void profileClickTest() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);
        Thread.sleep(3000);
        onView(withId(R.id.profile_item)).perform(click());
        Thread.sleep(3000);
        onView(withText("Username")).check(matches(isDisplayed()));
        onView(withText("Email")).check(matches(isDisplayed()));
        onView(withText("Phone")).check(matches(isDisplayed()));
        onView(withText("Homepage")).check(matches(isDisplayed()));
        onView(withText("Edit")).check(matches(isDisplayed()));
    }



    @Test
    public void editProfileClickTest() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.profile_item)).perform(click());
        onView(withId(R.id.edit_profile_button)).perform(click());


        onView(withId(R.id.editTextFirst))
                .perform(typeText("Test First"), closeSoftKeyboard());

        onView(withId(R.id.editTextLast))
                .perform(typeText("Test Last"), closeSoftKeyboard());


        onView(withId(R.id.editTextUsername))
                .perform(typeText("Test User"), closeSoftKeyboard());


        onView(withId(R.id.editTextEmail))
                .perform(typeText("TestEmail@Gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.editTextPhone))
                .perform(typeText("7801234567"), closeSoftKeyboard());

        onView(withId(R.id.editTextHomepage))
                .perform(typeText("https://www.Testing.com"), closeSoftKeyboard());

        onView(withText("Edit Profile")).check(matches(isDisplayed()));
        onView(withText("Cancel")).check(matches(isDisplayed()));
        onView(withText("Save")).check(matches(isDisplayed()));

        onView(withId(R.id.save_text_view))
                .perform(click());


        onView(withText("Test User")).check(matches(isDisplayed()));


    }



    @Test
    public void profileEditUpdateField() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        Thread.sleep(3000);

        intentsTestRule.launchActivity(startIntent);
        Thread.sleep(3000);

        onView(withId(R.id.profile_item)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.edit_profile_button)).perform(click());
        Thread.sleep(3000);



        onView(withText("Edit Profile")).check(matches(isDisplayed()));
        onView(withText("Cancel")).check(matches(isDisplayed()));
        onView(withText("Save")).check(matches(isDisplayed()));
    }


}