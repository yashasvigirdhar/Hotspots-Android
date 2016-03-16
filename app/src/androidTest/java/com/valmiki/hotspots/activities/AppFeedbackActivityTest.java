package com.valmiki.hotspots.activities;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.valmiki.hotspots.R;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.valmiki.hotspots.activities.EspressoTextInputLayoututils.onErrorViewWithinTilWithId;

/**
 * Created by yashasvi on 3/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class AppFeedbackActivityTest {

    private String name, email, message;

    @Rule
    public ActivityTestRule<AppFeedbackActivity> mActivityRule = new ActivityTestRule<>(
            AppFeedbackActivity.class);

    @Before
    public void initValidString() {
        name = "espressotestuser";
        email = "testemail";
        message = "testmessage";
    }

    @Test
    public void testSubmitAppFeedbackWithWrongEmail() {

        onView(withId(R.id.ibAppFeelingHappy)).perform(click());

        onView(withId(R.id.etFeedbackName))
                .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.etFeedbackEmail))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.etFeedbackMessage))
                .perform(typeText(message), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.etFeedbackName))
                .check(matches(withText(name)));
        onView(withId(R.id.etFeedbackEmail))
                .check(matches(withText(email)));
        onView(withId(R.id.etFeedbackMessage))
                .check(matches(withText(message)));

        onView(withId(R.id.bSendAppFeedback)).perform(scrollTo(), click());

        onErrorViewWithinTilWithId(R.id.tilAppFeedbackEmail).check(matches(withText(mActivityRule.getActivity().getString(R.string.err_msg_email))));
        Assert.assertEquals(false, mActivityRule.getActivity().isFinishing());

    }

    @Test
    public void testSubmitAppFeedbackWithCorrectEmail() {

        email = "yash.girdhar@gmail.com";

        onView(withId(R.id.ibAppFeelingHappy)).perform(click());

        onView(withId(R.id.etFeedbackName))
                .perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.etFeedbackEmail))
                .perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.etFeedbackMessage))
                .perform(typeText(message), closeSoftKeyboard());


        // Check that the text was changed.
        onView(withId(R.id.etFeedbackName))
                .check(matches(withText(name)));
        onView(withId(R.id.etFeedbackEmail))
                .check(matches(withText(email)));
        onView(withId(R.id.etFeedbackMessage))
                .check(matches(withText(message)));

        onView(withId(R.id.bSendAppFeedback)).perform(scrollTo(), click());
        Assert.assertEquals(true, mActivityRule.getActivity().isFinishing());

    }

}
