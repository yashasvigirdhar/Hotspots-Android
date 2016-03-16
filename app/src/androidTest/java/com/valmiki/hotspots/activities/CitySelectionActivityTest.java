package com.valmiki.hotspots.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.valmiki.hotspots.R;
import com.valmiki.hotspots.utils.Constants;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by yashasvi on 3/12/16.
 */
public class CitySelectionActivityTest {

    String dummyCity = "Bangalore";

    @Rule
    public ActivityTestRule<CitySelectionActivity> mActivityRule = new ActivityTestRule<>(
            CitySelectionActivity.class, true, false);

    @Test
    public void testComingBackFromPlaces() {
        Intent intent = new Intent();
        intent.putExtra("from", "places");
        CitySelectionActivity mActivity = mActivityRule.launchActivity(intent);
        int numCities = mActivity.places.size();
        for (int i = 0; i < numCities; i++) {
            onView(withId(R.id.recyclerViewCities)).perform(RecyclerViewActions.scrollToPosition(i));
            onView(withId(R.id.recyclerViewCities)).perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
            onView(withId(R.id.ll_toolbarPlacesList)).perform(click());
        }
    }

    @Test
    public void testWithCityStored() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        sharedPreferences.edit()
                .putString(Constants.SELECTED_CITY, dummyCity).commit();
        CitySelectionActivity mActivity = mActivityRule.launchActivity(null);
    }
}
