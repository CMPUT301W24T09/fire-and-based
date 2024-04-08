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

@RunWith(AndroidJUnit4.class)
public class deleteTenOrganizerEvents {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately
    public void deleteTen() throws InterruptedException {
        for (int i = 0; i < 10; i++){
            deleteAnEvent();
        }
    }
    @Test
    public void deleteAnEvent() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteAnEvent2() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteAnEvent3() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteAnEvent4() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteAnEvent5() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteAnEvent6() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteAnEvent7() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    @Test
    public void deleteAnEvent8() throws InterruptedException {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        intentsTestRule.launchActivity(startIntent);
        onView(withId(R.id.organizing_item)).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Edit Details")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }
}