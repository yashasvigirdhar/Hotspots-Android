package app.nomad.projects.yashasvi.hotspotsforwork;

import android.app.Application;
import android.graphics.Bitmap;

import app.nomad.projects.yashasvi.hotspotsforwork.utils.ImageMemoryCache;

/**
 * Created by ygirdha on 1/31/16.
 */
public class MyApplication extends Application {

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
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new ImageMemoryCache(cacheSize);
    }

    public void putBitmapInCache(String key, Bitmap bt) {
        mMemoryCache.put(key, bt);
    }

    public Bitmap getBitmapFromCache(String key) {
        Bitmap bt = mMemoryCache.get(key);
        return bt;
    }

}
