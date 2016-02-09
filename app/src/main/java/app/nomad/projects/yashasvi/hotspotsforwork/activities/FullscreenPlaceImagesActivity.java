package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.MyApplication;
import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.FullScreenPlaceImageAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.enums.ImageSize;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.Constants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerHelperFunctions;


public class FullscreenPlaceImagesActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    final static String LOG_TAG = "FullScreenImageActivity";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Toolbar toolbar;

    private ShareActionProvider mShareActionProvider;

    Bitmap currentBitmap;

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
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_fullscreen_place_images);
        initialize();
        this.onPageSelected(position);
    }

    void initialize() {

        toolbar = (Toolbar) findViewById(R.id.toolbarFullScreenImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPager = (ViewPager) findViewById(R.id.pagerPlaceImages);

        placeImageBitmaps = new ArrayList<>();

        imagesCount = getIntent().getIntExtra("images_count", 0);
        placeId = getIntent().getStringExtra("place_id");
        placeName = getIntent().getStringExtra("place_name");
        getSupportActionBar().setTitle(placeName);
        position = getIntent().getIntExtra("position", 0);

        Log.i(LOG_TAG, "images_count " + imagesCount + ", placeId " + placeId + "placeName " + placeName + ", position " + position);

        populateBitmaps();
        currentBitmap = placeImageBitmaps.get(position);
        mAdapter = new FullScreenPlaceImageAdapter(this, placeImageBitmaps);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(position);
        mPager.addOnPageChangeListener(this);
    }

    void populateBitmaps() {
        Bitmap bt;
        String key;
        for (int i = 0; i < imagesCount; i++) {
            key = ServerHelperFunctions.getImageCacheKey(placeId, i, ImageSize.FULL);
            bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
            if (bt != null) {
                placeImageBitmaps.add(bt);
                Log.i(LOG_TAG, "adding full size image from cache " + i);
            } else {
                key = ServerHelperFunctions.getImageCacheKey(placeId, i, ImageSize.THUMBNAIL);
                bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
                if (bt != null) {
                    placeImageBitmaps.add(bt);
                    Log.i(LOG_TAG, "adding image thumbnail from cache " + i);
                }

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        checkPermissionAndShareImage();
        // Return true to display menu
        return true;
    }

    void checkPermissionAndShareImage() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.i(LOG_TAG, "external storage write permission : " + permission);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return;
        }
        shareImage();
    }

    void shareImage() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("image/jpeg");
        myShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
        myShareIntent.putExtra(Intent.EXTRA_TEXT, String.format(Constants.IMAGE_SHARE_TEXT, placeName));
        setShareIntent(myShareIntent);
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int pos) {
        Log.i(LOG_TAG, "page selected " + pos);
        position = pos;
        Bitmap bt;
        String key = ServerHelperFunctions.getImageCacheKey(placeId, position, ImageSize.FULL);
        bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
        if (bt != null) {
            currentBitmap = bt;
            checkPermissionAndShareImage();
            invalidateOptionsMenu();
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
            Bitmap bt = ServerHelperFunctions.downloadBitmapFromUrl(url);
            publishProgress(bt);
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... bitmaps) {
            super.onProgressUpdate(bitmaps[0]);
            updateImageToFullSize(bitmaps[0]);
            ((MyApplication) getApplication()).putBitmapInCache(ServerHelperFunctions.getImageCacheKey(placeId, position, ImageSize.FULL), bitmaps[0]);
        }
    }

    public void updateImageToFullSize(Bitmap bitmap) {
        Log.i(LOG_TAG, "replacing bitmap");
        placeImageBitmaps.set(position, bitmap);
        mAdapter.notifyDataSetChanged();
        currentBitmap = bitmap;
        checkPermissionAndShareImage();
        invalidateOptionsMenu();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareImage();
                } else {
                    Toast.makeText(this, "You have to grant permissions in order to show distance", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
