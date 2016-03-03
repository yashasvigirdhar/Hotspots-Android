package app.nomad.projects.yashasvi.hotspots.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspots.MyApplication;
import app.nomad.projects.yashasvi.hotspots.R;
import app.nomad.projects.yashasvi.hotspots.adapters.FullScreenImageAdapter;
import app.nomad.projects.yashasvi.hotspots.enums.ImageSize;
import app.nomad.projects.yashasvi.hotspots.enums.ImageType;
import app.nomad.projects.yashasvi.hotspots.utils.Constants;
import app.nomad.projects.yashasvi.hotspots.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspots.utils.ServerHelperFunctions;
import app.nomad.projects.yashasvi.hotspots.utils.UtilFunctions;


public class FullscreenPlaceImagesActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private final static String LOG_TAG = "FullScreenImageActivity";

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Toolbar toolbar;

    private ImageType imageType;

    private ShareActionProvider mShareActionProvider;

    private Bitmap currentBitmap;

    private FullScreenImageAdapter mAdapter;
    private List<Bitmap> imageBitmaps;

    private ViewPager mPager;

    private String imageUrl;
    private String placeId;
    private String placeName;
    private String imagesPath;
    private int position;
    private int imagesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_fullscreen_place_images);
        initialize();
        this.onPageSelected(position);
        checkAndRequestStorageWritePermissionForSharingImage();
        //checkAndRequestStorageReadPermissionForSharingImage();
    }

    private void initialize() {

        toolbar = (Toolbar) findViewById(R.id.toolbarFullScreenImage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPager = (ViewPager) findViewById(R.id.pagerPlaceImages);

        imageBitmaps = new ArrayList<>();

        imagesCount = getIntent().getIntExtra("images_count", 0);
        placeId = getIntent().getStringExtra("place_id");
        placeName = getIntent().getStringExtra("place_name");
        getSupportActionBar().setTitle(placeName);
        position = getIntent().getIntExtra("position", 0);
        imagesPath = getIntent().getStringExtra("images_path");
        int type = getIntent().getIntExtra("image_type", -1);

        if (type == 1)
            imageType = ImageType.MENU;
        else if (type == 0)
            imageType = ImageType.PLACE;

        Log.i(LOG_TAG, "images_count " + imagesCount + ", placeId " + placeId + "placeName " + placeName + ", position " + position + "type " + imageType);

        populateBitmaps();
        try {
            currentBitmap = imageBitmaps.get(position);
            mAdapter = new FullScreenImageAdapter(this, imageBitmaps);
            mPager.setAdapter(mAdapter);
            mPager.setCurrentItem(position);
            mPager.addOnPageChangeListener(this);
        } catch (IndexOutOfBoundsException e) {
            Log.i(LOG_TAG, e.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    private void populateBitmaps() {
        Bitmap bt;
        String key;
        for (int i = 0; i < imagesCount; i++) {
            key = UtilFunctions.getImageCacheKey(placeId, imageType, i, ImageSize.FULL);
            Log.i(LOG_TAG, "i,key : " + i + ", " + key);
            bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
            if (bt != null) {
                imageBitmaps.add(bt);
                Log.i(LOG_TAG, "adding full size image from cache " + i);
            } else {
                key = UtilFunctions.getImageCacheKey(placeId, imageType, i, ImageSize.THUMBNAIL);
                Log.i(LOG_TAG, "i,key : " + i + ", " + key);
                bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
                if (bt != null) {
                    imageBitmaps.add(bt);
                    Log.i(LOG_TAG, "adding image thumbnail from cache " + i);
                } else {
                    String path;
                    if (imageType == ImageType.MENU) {
                        path = ServerConstants.SERVER_URL + imagesPath + ServerConstants.MENU_PATH + ServerConstants.THUMBNAILS_PATH + placeName + i + ".png";
                    } else {
                        path = ServerConstants.SERVER_URL + imagesPath + ServerConstants.PLACE_PATH + ServerConstants.THUMBNAILS_PATH + placeName + i + ".png";
                    }
                    path = path.replace(" ", "%20");
                    //bt = ServerHelperFunctions.downloadBitmapFromUrl(path);
//                    if (bt != null) {
//                        imageBitmaps.add(bt);
//                        Log.i(LOG_TAG, "adding image thumbnail from server " + i);
//                    }
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

        MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
        refreshItem.setVisible(false);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        MenuItem sortItem = menu.findItem(R.id.action_sort);
        sortItem.setVisible(false);
        // Return true to display menu
        return true;
    }

    private void checkAndRequestStorageWritePermissionForSharingImage() {
        int permission1 = ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[0]);
        int permission2 = ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[1]);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG_TAG, "external storage write permission not granted");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    Constants.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION
            );
        }
    }

    private void checkAndRequestStorageReadPermissionForSharingImage() {
        int permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[1]);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG_TAG, "external storage write permission not granted");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION
            );
        }
    }

    private void setShareImageReady() {

        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED)
            return;

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            if (f.exists()) {
                if (f.delete())
                    Log.i(LOG_TAG, "deleting previous file");
            }
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
        String key = UtilFunctions.getImageCacheKey(placeId, imageType, position, ImageSize.FULL);
        bt = ((MyApplication) getApplication()).getBitmapFromCache(key);
        if (bt != null) {
            currentBitmap = bt;
            //checkAndRequestStorageWritePermissionForSharingImage();
            setShareImageReady();
            invalidateOptionsMenu();
            Log.i(LOG_TAG, "cache hit \\m/\nalready contains full size image");
            return;
        }
        Log.i(LOG_TAG, "cache miss /m\\");
        if (imageType == ImageType.MENU) {
            imageUrl = ServerConstants.SERVER_URL + imagesPath + ServerConstants.MENU_PATH + placeName + String.valueOf(pos) + ".png";
        } else if (imageType == ImageType.PLACE) {
            imageUrl = ServerConstants.SERVER_URL + imagesPath + ServerConstants.PLACE_PATH + placeName + String.valueOf(pos) + ".png";
        }
        imageUrl = imageUrl.replace(" ", "%20");
        new downloadImageAsyncTask(imageUrl).execute();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class downloadImageAsyncTask extends AsyncTask<Void, Bitmap, Void> {

        final String url;

        downloadImageAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap bt = ServerHelperFunctions.downloadBitmapFromUrl(url);
            if (bt != null)
                publishProgress(bt);
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... bitmaps) {
            super.onProgressUpdate(bitmaps[0]);
            updateImageToFullSize(bitmaps[0]);
            ((MyApplication) getApplication()).putBitmapInCache(UtilFunctions.getImageCacheKey(placeId, imageType, position, ImageSize.FULL), bitmaps[0]);
        }

    }

    private void updateImageToFullSize(Bitmap bitmap) {
        Log.i(LOG_TAG, "replacing bitmap");
        imageBitmaps.set(position, bitmap);
        mAdapter.notifyDataSetChanged();
        currentBitmap = bitmap;
        //checkAndRequestStorageWritePermissionForSharingImage();
        setShareImageReady();
        invalidateOptionsMenu();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constants.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.llFullScreenActivity), "Storage read and write permission is required for sharing the image", Snackbar.LENGTH_INDEFINITE)
                                .setAction("ALLOW", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(FullscreenPlaceImagesActivity.this,
                                                PERMISSIONS_STORAGE, Constants.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION);
                                    }

                                })
                                .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                        snackbar.show();
                        break;
                    }
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.llFullScreenActivity), "Storage read and write permission is required for sharing the image", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SETTINGS", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                    startActivityForResult(intent, Constants.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION);
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                    snackbar.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION:
                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}
