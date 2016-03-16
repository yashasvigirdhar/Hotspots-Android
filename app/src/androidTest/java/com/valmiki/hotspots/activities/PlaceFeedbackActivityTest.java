package com.valmiki.hotspots.activities;

import android.support.test.rule.ActivityTestRule;

import com.valmiki.hotspots.R;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by yashasvi on 3/12/16.
 */
public class PlaceFeedbackActivityTest {

    private String name, message;

    @Rule
    public ActivityTestRule<PlaceFeedbackActivity> mActivityRole = new ActivityTestRule<>(PlaceFeedbackActivity.class);

    @Before
    public void initValidString() {
        name = "espressotestuser";
        message = "testmessage";
    }

    @Test
    public void testSubmitPlaceFeedback() {
        onView(withId(R.id.ibFeelingHappy)).perform(click());
        onView(withId(R.id.cbFoodFeeling)).perform(click());
        onView(withId(R.id.etPlaceFeedbackName)).perform(typeText(name));
        onView(withId(R.id.etPlaceFeedbackMessage)).perform(scrollTo(), typeText(message));
        onView(withId(R.id.bSendPlaceFeedback)).perform(scrollTo(), click());
        Assert.assertEquals(true, mActivityRole.getActivity().isFinishing());
    }

}
