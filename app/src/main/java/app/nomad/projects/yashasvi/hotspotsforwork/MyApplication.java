package app.nomad.projects.yashasvi.hotspotsforwork;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import app.nomad.projects.yashasvi.hotspotsforwork.utils.Constants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ImageMemoryCache;

/**
 * Created by yashasvi on 1/31/16.
 */
public class MyApplication extends Application {

    String LOG_TAG = "MyApplication";

    ImageMemoryCache mMemoryCache;
    int cacheSize;

    @Override
    public void onCreate() {
        super.onCreate();
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        cacheSize = maxMemory / 8;

        mMemoryCache = new ImageMemoryCache(cacheSize);

        populateConstants();
    }

    private void populateConstants() {
        Constants.wifiSpeedLevel.put(1, "Slow surfing");
        Constants.wifiSpeedLevel.put(2, "Good for surfing");
        Constants.wifiSpeedLevel.put(3, "Surfing + images/videos");
        Constants.wifiSpeedLevel.put(4, "Video calls  + HQ videos");
        Constants.wifiSpeedLevel.put(5, "No need to worry");

        Constants.chargingPointsLevel.put(1, "Very less");
        Constants.chargingPointsLevel.put(2, "Can find, but not easy");
        Constants.chargingPointsLevel.put(3, "No need to worry");

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

}
