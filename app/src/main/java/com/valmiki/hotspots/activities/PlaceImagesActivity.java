package com.valmiki.hotspots.activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.valmiki.hotspots.MyApplication;
import com.valmiki.hotspots.R;
import com.valmiki.hotspots.adapters.PlaceImagesRecyclerViewAdapter;
import com.valmiki.hotspots.enums.ImageSize;
import com.valmiki.hotspots.enums.ImageType;
import com.valmiki.hotspots.utils.ServerConstants;
import com.valmiki.hotspots.utils.ServerHelperFunctions;
import com.valmiki.hotspots.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.List;

public class PlaceImagesActivity extends AppCompatActivity {

    Tracker analyticsTracker;

    private static final String LOG_TAG = "PlaceImageActivity";

    private String placeId;
    private String placeName;
    private String imagesPath;

    private int imagesCount;

    private RecyclerView placeImagesRecyclerView;
    private PlaceImagesRecyclerViewAdapter placeImagesRecyclerViewAdapter;
    List<Bitmap> placeImageBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_place_images);
        initialize();
        new downloadImageAsyncTask().execute();
    }

    private void initialize() {
        placeId = getIntent().getStringExtra("place_id");
        placeName = getIntent().getStringExtra("place_name");
        imagesCount = getIntent().getIntExtra("images_count", -1);
        imagesPath = getIntent().getStringExtra("images_path");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPlaceImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(placeName);

        placeImagesRecyclerView = (RecyclerView) findViewById(R.id.rvPlaceImages);

        GridLayoutManager placeLayoutGridLayoutManager = new GridLayoutManager(this, 3);
        placeImagesRecyclerView.setLayoutManager(placeLayoutGridLayoutManager);

        placeImageBitmaps = new ArrayList<>();
        placeImagesRecyclerViewAdapter = new PlaceImagesRecyclerViewAdapter(this, placeImageBitmaps, placeId, placeName, imagesPath, imagesCount);
        placeImagesRecyclerView.setAdapter(placeImagesRecyclerViewAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsTracker = ((MyApplication) getApplication()).getDefaultTracker();
        analyticsTracker.setScreenName(LOG_TAG);
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        placeImagesRecyclerViewAdapter.setAnalyticsTracker(analyticsTracker);
    }

    private class downloadImageAsyncTask extends AsyncTask<Void, Object, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (imagesCount == -1) {
                return null;
            }

            String path;
            for (int i = 0; i < imagesCount; i++) {
                path = ServerConstants.SERVER_URL + imagesPath + ServerConstants.PLACE_PATH + ServerConstants.THUMBNAILS_PATH + placeName + i + ".png";
                path = path.replace(" ", "%20");
                Log.i(LOG_TAG, path);
                Bitmap bt = ((MyApplication) getApplication()).getBitmapFromCache(UtilFunctions.getImageCacheKey(placeId, ImageType.PLACE, i, ImageSize.THUMBNAIL));
                if (bt == null) {
                    Log.i(LOG_TAG, "bitmap not present in cache " + i);
                    bt = ServerHelperFunctions.downloadBitmapFromUrl(path, analyticsTracker);
                }
                publishProgress(bt, ImageType.PLACE, i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... params) {
            Bitmap bitmap = (Bitmap) params[0];
            ImageType imageType = (ImageType) params[1];
            int number = (Integer) params[2];
            super.onProgressUpdate(bitmap);
            if (imageType == ImageType.PLACE) {
                placeImageBitmaps.add(bitmap);
                placeImagesRecyclerViewAdapter.notifyDataSetChanged();
            } else if (imageType == ImageType.MENU) {

            }
            ((MyApplication) getApplication()).putBitmapInCache(UtilFunctions.getImageCacheKey(placeId, imageType, number, ImageSize.THUMBNAIL), bitmap);
        }

    }

}
