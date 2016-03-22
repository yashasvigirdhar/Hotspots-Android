package com.valmiki.hotspots.activities;

import android.content.Intent;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.valmiki.hotspots.R;
import com.valmiki.hotspots.enums.InternetCheck;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by yashasvi on 3/12/16.
 */
@RunWith(AndroidJUnit4.class)
public class PlacesListActivityTest {

    PlacesListActivity mActivity;

    @Rule
    public ActivityTestRule<PlacesListActivity> mActivityRule = new ActivityTestRule<>(
            PlacesListActivity.class, true, false);


    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        intent.putExtra("internetChecked", InternetCheck.CHECKED.name());
        intent.putExtra("city", "Bangalore");
        mActivity = mActivityRule.launchActivity(intent);
    }

    @Test
    public void testPlacesListBasic() {
        int numPlaces = mActivity.places.size();
        Assert.assertTrue(numPlaces > 0);
        numPlaces = (numPlaces < 3) ? numPlaces : 3;
        for (int i = 0; i < numPlaces; i++) {
            onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.scrollToPosition(i));
            onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
            pressBack();
        }
    }

    @Test
    public void testNavigationDrawerItems() {
        onView(withId(R.id.drawer_layout_places_list)).perform(DrawerActions.open());
        onView(withId(R.id.drawerList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.ivLogoAbout)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.ll_toolbarPlacesList)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout_places_list)).perform(DrawerActions.open());
        onView(withId(R.id.drawerList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.ibAppFeelingHappy)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.ll_toolbarPlacesList)).check(matches(isDisplayed()));
        onView(withId(R.id.drawer_layout_places_list)).perform(DrawerActions.open());
        onView(withId(R.id.drawerList)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        pressBack();
    }

    @Test
    public void testRefreshPlacesList() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Refresh")).perform(click());
        Assert.assertTrue(mActivity.places.size() > 0);
    }
}
