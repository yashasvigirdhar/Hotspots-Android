package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.MyApplication;
import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.PlaceImagesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.enums.ImageSize;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerHelperFunctions;

public class PlaceImagesActivity extends AppCompatActivity {

    private static final String TAG = "PlaceImageActivity";

    Toolbar toolbar;

    String placeId;
    String placeName;
    String imagesPath;

    int imagesCount;

    RecyclerView placeImagesRecyclerView;
    PlaceImagesRecyclerViewAdapter placeImagesRecyclerViewAdapter;
    List<Bitmap> placeImageBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_place_images);
        initialize();
        new downloadImageAsyncTask().execute();
    }

    void initialize() {
        placeId = getIntent().getStringExtra("placeId");
        placeName = getIntent().getStringExtra("placeName");
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
        placeImagesRecyclerViewAdapter = new PlaceImagesRecyclerViewAdapter(this, placeImageBitmaps, placeId, placeName, imagesCount);
        placeImagesRecyclerView.setAdapter(placeImagesRecyclerViewAdapter);

    }

    public class downloadImageAsyncTask extends AsyncTask<Void, Object, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (imagesCount == -1) {
                return null;
            }

            String path;
            for (int i = 0; i < imagesCount; i++) {
                path = imagesPath + ServerConstants.THUMBNAILS_PATH + "/" + placeName + i + ".png";
                path = path.replace(" ", "%20");
                Log.i(TAG, path);
                Bitmap bt = ((MyApplication) getApplication()).getBitmapFromCache(ServerHelperFunctions.getImageCacheKey(placeId, i, ImageSize.THUMBNAIL));
                if (bt == null) {
                    Log.i(TAG, "bitmap not present in cache " + i);
                    bt = ServerHelperFunctions.downloadBitmapFromUrl(path);
                }
                publishProgress(bt, i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... params) {
            Bitmap bitmap = (Bitmap) params[0];
            int number = (Integer) params[1];

            super.onProgressUpdate(bitmap);

            placeImageBitmaps.add(bitmap);
            placeImagesRecyclerViewAdapter.notifyDataSetChanged();
            placeImagesRecyclerViewAdapter.updateImagesCount(imagesCount);
            ((MyApplication) getApplication()).putBitmapInCache(ServerHelperFunctions.getImageCacheKey(placeId, number, ImageSize.THUMBNAIL), bitmap);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
