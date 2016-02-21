package app.nomad.projects.yashasvi.hotspots.activities;

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

import app.nomad.projects.yashasvi.hotspots.MyApplication;
import app.nomad.projects.yashasvi.hotspots.R;
import app.nomad.projects.yashasvi.hotspots.adapters.PlaceImagesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspots.enums.ImageSize;
import app.nomad.projects.yashasvi.hotspots.enums.ImageType;
import app.nomad.projects.yashasvi.hotspots.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspots.utils.ServerHelperFunctions;
import app.nomad.projects.yashasvi.hotspots.utils.UtilFunctions;

public class PlaceImagesActivity extends AppCompatActivity {

    private static final String TAG = "PlaceImageActivity";

    private String placeId;
    private String placeName;
    private String imagesPath;

    private int imagesCount;

    private RecyclerView placeImagesRecyclerView;
    private PlaceImagesRecyclerViewAdapter placeImagesRecyclerViewAdapter;
    private List<Bitmap> placeImageBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
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
                Log.i(TAG, path);
                Bitmap bt = ((MyApplication) getApplication()).getBitmapFromCache(UtilFunctions.getImageCacheKey(placeId, ImageType.PLACE, i, ImageSize.THUMBNAIL));
                if (bt == null) {
                    Log.i(TAG, "bitmap not present in cache " + i);
                    bt = ServerHelperFunctions.downloadBitmapFromUrl(path);
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
