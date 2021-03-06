package com.valmiki.hotspots;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.valmiki.hotspots.utils.Constants;
import com.valmiki.hotspots.utils.ImageMemoryCache;

/**
 * Created by yashasvi on 1/31/16.
 */
public class MyApplication extends Application {

    private final String LOG_TAG = "MyApplication";

    private ImageMemoryCache mMemoryCache;
    private int cacheSize;

    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        cacheSize = maxMemory / 4;

        mMemoryCache = new ImageMemoryCache(cacheSize);

        populateConstants();

        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            mTracker.enableAutoActivityTracking(true);
            mTracker.enableExceptionReporting(true);
            mTracker.setAppName(getResources().getString(R.string.app_name));
            mTracker.setAppVersion("1.0");

            Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                    mTracker,
                    Thread.getDefaultUncaughtExceptionHandler(),
                    this);

            // Make myHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(myHandler);
        }
    }

    private void populateConstants() {
        Constants.wifiSpeedLevel.put(1.0, "Slow surfing");
        Constants.wifiSpeedLevel.put(2.0, "Good for surfing");
        Constants.wifiSpeedLevel.put(3.0, "Surfing + images/videos");
        Constants.wifiSpeedLevel.put(4.0, "Video calls + HQ videos");
        Constants.wifiSpeedLevel.put(5.0, "Blazing Fast");

        Constants.chargingPointsLevel.put(1, "Very less");
        Constants.chargingPointsLevel.put(2, "Can find, but not easy");
        Constants.chargingPointsLevel.put(3, "Plenty of them");

        Constants.days.put(1, "Sunday");
        Constants.days.put(2, "Monday");
        Constants.days.put(3, "Tuesday");
        Constants.days.put(4, "Wednesday");
        Constants.days.put(5, "Thursday");
        Constants.days.put(6, "Friday");
        Constants.days.put(7, "Saturday");


    }

    public void putBitmapInCache(String key, Bitmap bt) {
        try {
            mMemoryCache.put(key, bt);
        } catch (NullPointerException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            mTracker.enableAutoActivityTracking(true);
            mTracker.enableExceptionReporting(true);
            mTracker.setAppName(getResources().getString(R.string.app_name));
            mTracker.setAppVersion("1.0");

            Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                    mTracker,
                    Thread.getDefaultUncaughtExceptionHandler(),
                    this);

            // Make myHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(myHandler);
        }
        return mTracker;
    }

}
