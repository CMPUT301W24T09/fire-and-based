package com.example.fire_and_based;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for verifying the functionality of the UserActivity screen.
 * This class performs tests to ensure that different UI components such as
 * bottom navigation, browsing list, attendee list, organizing list, and event creation button
 * are displayed correctly within the UserActivity.
 * @author Tyler, Sumayya
 */
@RunWith(AndroidJUnit4.class)
public class UserActivityTest {

    @Rule
    public IntentsTestRule<UserActivity> intentsTestRule =
            new IntentsTestRule<>(UserActivity.class, true, false); // Do not launch activity immediately

    /**
     * Tests whether the bottom navigation bar is displayed in the UserActivity.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @Test
    public void testBottomNavDisplayed() throws InterruptedException {
        User user = new User("123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.bottom_nav)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the Browse List is displayed and selectable in the UserActivity.
     */
    @Test
    public void testBrowseListDisplayed() {
        User user = new User("123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.browse_item)).perform(click());
        onView(withId(R.id.browse_item)).check(matches(isSelected()));
        onView(withText("Browse Events")).check(matches(isDisplayed()));
    }

    /**
     * Tests the display and selection functionality of the Attendee List in UserActivity.
     */
    @Test
    public void testAttendeeListDisplayed() {
        User user = new User("123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.attending_item)).perform(click());
        onView(withId(R.id.attending_item)).check(matches(isSelected()));
        onView(withText("Attending Events")).check(matches(isDisplayed()));
    }

    /**
     * Ensures that the Organizing List is properly displayed and selectable in the UserActivity.
     */
    @Test
    public void testOrganizingListDisplayed() throws InterruptedException {
        User user = new User("123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);
        Thread.sleep(2000);
        intentsTestRule.launchActivity(startIntent);
        Thread.sleep(2000);

        onView(withId(R.id.organizing_item)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.organizing_item)).check(matches(isSelected()));
        Thread.sleep(2000);

        onView(withText("Organizing Events")).check(matches(isDisplayed()));
    }

    /**
     * Placeholder test to verify the appearance of the event creation button.
     * Currently, does not implement specific checks.
     */
    @Test
    public void testEventCreationButton() {
        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);
        // Placeholder for future checks
    }

    /**
     * Tests clicking an item in the ListView displayed in UserActivity.
     * Assumes that the list is populated and checks for the display of "Join Event".
     */
    @Test
    public void clickItemInListView() {


        User user = new User("testUser123");
        Intent startIntent = new Intent();
        startIntent.putExtra("user", user);

        intentsTestRule.launchActivity(startIntent);

        onData(anything())
                .inAdapterView(withId(R.id.event_list))
                .atPosition(0)
                .perform(click());
        onView(withText("Join Event")).check(matches(isDisplayed()));
    }
}
