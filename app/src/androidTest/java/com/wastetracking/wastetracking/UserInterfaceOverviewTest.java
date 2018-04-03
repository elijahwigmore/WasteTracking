package com.wastetracking.wastetracking;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/**
 * An overview test assertion of the UI scheme
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserInterfaceOverviewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void loginActivity_diffActivity() {


        onView(withId(R.id.login_button))
                .perform(click());

        // Check that we do see our 4 main buttons
        onView(withId(R.id.to_do_list_touch))
                .check(matches(isDisplayed()));

        onView(withId(R.id.map_overview_touch))
                .check(matches(isDisplayed()));

        onView(withId(R.id.phone_touch))
                .check(matches(isDisplayed()));

        onView(withId(R.id.sms_touch))
                .check(matches(isDisplayed()));

        onView(withId(R.id.general_view_pager))
                .check(matches(isDisplayed()));
    }

    @Test
    public void listFragment_seeList() {

        // Login first
        loginActivity_diffActivity();

        onView(withId(R.id.to_do_list_touch))
                .perform(click());

        // Check that we do see our main list and associated components
        onView(withId(R.id.listview_scan_log))
                .check(matches(isDisplayed()));

        onView(withId(R.id.left_arrow_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.right_arrow_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.general_view_pager))
                .check(matches(isDisplayed()));
    }

    @Test
    public void mapFragment_seeMap() {

        // Login first
        loginActivity_diffActivity();

        onView(withId(R.id.map_overview_touch))
                .perform(click());

        // Check that we do see our main list and associated components
        onView(withId(R.id.listview_scan_log))
                .check(matches(isDisplayed()));

        onView(withId(R.id.map_view))
                .check(matches(isDisplayed()));

        // Note: The button should be "displayed", but since it's in the ViewPager, it's not
        // actively displayed to us
        onView(withId(R.id.map_overview_touch))
                .check(matches(isDisplayed()));

        onView(withId(R.id.general_view_pager))
                .check(matches(isDisplayed()));
    }
}
