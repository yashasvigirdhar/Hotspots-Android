package app.nomad.projects.yashasvi.hotspotsforwork;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

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
