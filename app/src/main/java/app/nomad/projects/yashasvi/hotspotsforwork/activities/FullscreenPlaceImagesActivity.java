package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.MyApplication;
import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.FullScreenPlaceImageAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ImageSize;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.UtilFunctions;


public class FullscreenPlaceImagesActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    final static String LOG_TAG = "FullScreenImageActivity";

    FullScreenPlaceImageAdapter mAdapter;
    List<Bitmap> placeImageBitmaps;

    ViewPager mPager;

    String imageUrl;
    String placeId;
    String placeName;
    int position;
    int imagesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_fullscreen_place_images);
        initialize();
        this.onPageSelected(position);
    }

    void initialize() {
        mPager = (ViewPager) findViewById(R.id.pagerPlaceImages);

        placeImageBitmaps = new ArrayList<>();

        imagesCount = getIntent().getIntExtra("images_count", 0);
        placeId = getIntent().getStringExtra("place_id");
        placeName = getIntent().getStringExtra("place_name");
        position = getIntent().getIntExtra("position", 0);
        Log.i(LOG_TAG, "images_count " + imagesCount + ", placeId " + placeId + "placeName " + placeName + ", position " + position);

        populateAdapter();

        mAdapter = new FullScreenPlaceImageAdapter(this, placeImageBitmaps);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(position);
        mPager.addOnPageChangeListener(this);
    }

    void populateAdapter() {
        Bitmap bt;
        String key;
        for (int i = 0; i < imagesCount; i++) {
            key = UtilFunctions.getImageCacheKey(placeId, i, ImageSize.FULL);
            bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
            if (bt != null) {
                placeImageBitmaps.add(bt);
                Log.i(LOG_TAG, "adding full size image from cache " + i);
            } else {
                key = UtilFunctions.getImageCacheKey(placeId, i, ImageSize.THUMBNAIL);
                bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
                if (bt != null) {
                    placeImageBitmaps.add(bt);
                    Log.i(LOG_TAG, "adding image thumbnail from cache " + i);
                }

            }

        }
    }


    private Bitmap download(String url) {

        Bitmap imageBitmap = null;
        try {
            URL u = new URL(url);
            System.out.println("Downloading image " + u.toString());
            imageBitmap = BitmapFactory.decodeStream((InputStream) u.getContent());
            Log.i("image activity", "Download Completed Successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBitmap;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int pos) {
        Log.i(LOG_TAG, "page selected " + pos);
        position = pos;
        Bitmap bt;
        String key = UtilFunctions.getImageCacheKey(placeId, position, ImageSize.FULL);
        bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
        if (bt != null) {
            Log.i(LOG_TAG, "cache hit \\m/\nalready contains full size image");
            return;
        }
        Log.i(LOG_TAG, "cache miss /m\\");
        imageUrl = ServerConstants.SERVER_URL + ServerConstants.IMAGES_PATH +
                placeName + "/" + placeName + String.valueOf(pos) + ".png";
        imageUrl = imageUrl.replace(" ", "%20");
        new downloadImageAsyncTask(imageUrl).execute();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class downloadImageAsyncTask extends AsyncTask<Void, Bitmap, Void> {

        String url;

        downloadImageAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap bt = download(url);
            publishProgress(bt);
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... bitmaps) {
            super.onProgressUpdate(bitmaps[0]);
            updateImageToFullSize(bitmaps[0]);

            ((MyApplication) getApplication()).putBitmapInCache(UtilFunctions.getImageCacheKey(placeId, position, ImageSize.FULL), bitmaps[0]);
        }

    }

    public void updateImageToFullSize(Bitmap bitmap) {
        Log.i(LOG_TAG, "replacing bitmap");
        placeImageBitmaps.set(position, bitmap);
        mAdapter.notifyDataSetChanged();
    }

}
