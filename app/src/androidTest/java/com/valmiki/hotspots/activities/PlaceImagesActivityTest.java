package com.valmiki.hotspots.activities;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.valmiki.hotspots.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by yashasvi on 3/12/16.
 */
public class PlaceImagesActivityTest {


    PlaceImagesActivity mActivity;

    @Rule
    public ActivityTestRule<PlaceImagesActivity> mActivityRule = new ActivityTestRule<>(
            PlaceImagesActivity.class, true, false);


    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        intent.putExtra("place_id", 18);
        intent.putExtra("place_name", "Cafe Medley");
        intent.putExtra("images_count", 16);
        intent.putExtra("images_path", "images/Cafe%20Medley/");
        mActivity = mActivityRule.launchActivity(intent);
    }

    @Test
    public void testPlaceImagesBasic() {
        //Assert.assertTrue(mActivity.placeImageBitmaps.size() != 0);
        onView(withId(R.id.rvPlaceImages)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.pagerPlaceImages)).perform(swipeLeft());
        onView(withId(R.id.pagerPlaceImages)).perform(swipeRight());
        pressBack();
    }


}
