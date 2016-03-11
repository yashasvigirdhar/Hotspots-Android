package com.valmiki.hotspots.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.valmiki.hotspots.MyApplication;
import com.valmiki.hotspots.R;
import com.valmiki.hotspots.adapters.PlaceMenuSmallImagesRecyclerViewAdapter;
import com.valmiki.hotspots.adapters.PlaceSmallImagesRecyclerViewAdapter;
import com.valmiki.hotspots.enums.ConnectionAvailability;
import com.valmiki.hotspots.enums.ImageSize;
import com.valmiki.hotspots.enums.ImageType;
import com.valmiki.hotspots.models.Place;
import com.valmiki.hotspots.models.Timings;
import com.valmiki.hotspots.utils.CheckInternetAsyncTask;
import com.valmiki.hotspots.utils.Constants;
import com.valmiki.hotspots.utils.ServerConstants;
import com.valmiki.hotspots.utils.ServerHelperFunctions;
import com.valmiki.hotspots.utils.UtilFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlaceViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "PlaceViewActivity";

    private ShareActionProvider mShareActionProvider;

    CoordinatorLayout coordinatorLayout;

    private int imagesCount = -1;
    private int menuImagesCount = -1;
    private String imagesPath = "";

    private static String day;

    private CollapsingToolbarLayout collapsing_container;
    private Toolbar toolbar;

    private ImageView ivPlaceCover;

    private TextView tvTiming;
    private TextView tvAmbiance;
    private TextView tvAddress;
    private TextView tvDescription;
    private TextView tvWifiValue;
    private TextView tvWifiPaid;
    private TextView tvChargingPoints;
    private TextView tvCostValue;
    private TextView tvPhone;
    private TextView tvMoreImages;
    private TextView tvFoodRating;
    private TextView tvStaffValue;

    private TextView tvFoodMenuFallback;
    private TextView tvPhotosFallback;

    private RecyclerView recyclerViewPlaceSmallImages;
    private PlaceSmallImagesRecyclerViewAdapter placeSmallImagesAdapter;
    private List<Bitmap> placeSmallImageBitmaps;

    private RecyclerView recyclerViewPlaceMenuSmallImages;
    private PlaceMenuSmallImagesRecyclerViewAdapter placeMenuSmallImagesAdapter;
    private List<Bitmap> placeMenuSmallImageBitmaps;

    private ImageButton ibCallPlace;
    private ImageButton ibNavigateToPlace;
    private ImageButton ibTimingExpand;
    private Button bGoToFeedbackScreen;

    private Place place = null;
    private Timings timings = null;

    Snackbar internetSnackbar;

    GetTimingsAsyncTask getTimingsAsyncTask;
    DownloadImageAsyncTask downloadImageAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.placesActivityBackground));
        initialize();

        place = getIntent().getExtras().getParcelable("place");
        if (place != null) {
            Log.i(LOG_TAG, "place received " + place.toString());
            populate();
            checkInternetAndDownloadThings();
        }
    }

    private void checkInternetAndDownloadThings() {
        ConnectionAvailability isInternetAvailable = null;
        try {
            Log.i(LOG_TAG, "checking for internet");
            isInternetAvailable = new CheckInternetAsyncTask(this).execute().get(3L, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isInternetAvailable == ConnectionAvailability.INTERNET_AVAILABLE) {
            Log.i(LOG_TAG, "internet available");
            getTimingsAsyncTask = new GetTimingsAsyncTask(ServerHelperFunctions.getTimingsUriFromId(String.valueOf(place.getId())));
            getTimingsAsyncTask.execute();
            downloadImageAsyncTask = new DownloadImageAsyncTask();
            downloadImageAsyncTask.execute();
        } else {
            internetSnackbar = Snackbar
                    .make(coordinatorLayout, "Check Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS);
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary));
            internetSnackbar.show();
        }
    }

    private void initialize() {

        day = Constants.days.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        toolbar = (Toolbar) findViewById(R.id.technique_three_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorPlaceView);

        ivPlaceCover = (ImageView) findViewById(R.id.ivPlaceCover);

        placeSmallImageBitmaps = new ArrayList<>();
        recyclerViewPlaceSmallImages = (RecyclerView) findViewById(R.id.recyclerViewPhotosPlaceViewPage);
        recyclerViewPlaceSmallImages.setHasFixedSize(true);
        recyclerViewPlaceSmallImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        placeMenuSmallImageBitmaps = new ArrayList<>();
        recyclerViewPlaceMenuSmallImages = (RecyclerView) findViewById(R.id.recyclerViewMenuPlaceViewPage);
        recyclerViewPlaceMenuSmallImages.setHasFixedSize(true);
        recyclerViewPlaceMenuSmallImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        tvTiming = (TextView) findViewById(R.id.tvPlaceTimingsValue);
        tvTiming.setText("getting timings . . .");

        ibTimingExpand = (ImageButton) findViewById(R.id.ibTimingExpand);
        ibTimingExpand.setVisibility(View.INVISIBLE);
        ibTimingExpand.setOnClickListener(this);

        tvAddress = (TextView) findViewById(R.id.tvPlaceAddressValue);
        tvPhone = (TextView) findViewById(R.id.tvPlacePhoneValue);

        tvAmbiance = (TextView) findViewById(R.id.tvAmbianceValue);
        tvDescription = (TextView) findViewById(R.id.tvDescriptionValue);

        tvStaffValue = (TextView) findViewById(R.id.tvStaffValue);

        tvCostValue = (TextView) findViewById(R.id.tvPlaceCostValue);
        tvFoodRating = (TextView) findViewById(R.id.tvPlaceFoodRating);

        tvWifiPaid = (TextView) findViewById(R.id.tvWifiPaidValue);
        tvWifiValue = (TextView) findViewById(R.id.tvWifiValue);

        tvMoreImages = (TextView) findViewById(R.id.tvMoreImages);
        tvMoreImages.setVisibility(View.INVISIBLE);
        tvMoreImages.setOnClickListener(this);

        tvChargingPoints = (TextView) findViewById(R.id.tvChargingPointsValue);

        ibCallPlace = (ImageButton) findViewById(R.id.ibCallPlace);
        ibNavigateToPlace = (ImageButton) findViewById(R.id.ibNavigateToPlace);
        ibCallPlace.setOnClickListener(this);
        ibNavigateToPlace.setOnClickListener(this);

        bGoToFeedbackScreen = (Button) findViewById(R.id.bGoToPlaceFeedbackScreen);
        bGoToFeedbackScreen.setOnClickListener(this);

        tvFoodMenuFallback = (TextView) findViewById(R.id.tvFoodMenuFallback);
        tvFoodMenuFallback.setVisibility(View.INVISIBLE);

        tvPhotosFallback = (TextView) findViewById(R.id.tvPhotosFallback);
        tvPhotosFallback.setVisibility(View.INVISIBLE);
    }

    private void populate() {

        placeSmallImagesAdapter = new PlaceSmallImagesRecyclerViewAdapter(this, placeSmallImageBitmaps, String.valueOf(place.getId()), place.getName());
        recyclerViewPlaceSmallImages.setAdapter(placeSmallImagesAdapter);

        placeMenuSmallImagesAdapter = new PlaceMenuSmallImagesRecyclerViewAdapter(this, placeMenuSmallImageBitmaps, String.valueOf(place.getId()), place.getName());
        recyclerViewPlaceMenuSmallImages.setAdapter(placeMenuSmallImagesAdapter);

        collapsing_container.setTitle(place.getName());

        tvPhone.setText(place.getPhone());

        tvAddress.setText(place.getAddress());
        tvAmbiance.setText(String.valueOf(place.getAmbiance()));
        tvStaffValue.setText(place.getService() + "/5");
        tvChargingPoints.setText(Constants.chargingPointsLevel.get(Integer.parseInt(place.getChargingPoints())));

        tvWifiValue.setText(Constants.wifiSpeedLevel.get(Double.parseDouble(place.getWifiSpeed())));
        tvWifiPaid.setText("(" + place.getWifiPaid() + ")");

        tvCostValue.setText("Rs. " + place.getCost());
        tvFoodRating.setText(place.getFood());

        tvDescription.setText(place.getDescription());

        imagesCount = place.getImagesCount();
        placeSmallImagesAdapter.updateImagesCount(imagesCount);
        if (imagesCount == 0) {
            tvMoreImages.setVisibility(View.INVISIBLE);
            tvPhotosFallback.setVisibility(View.VISIBLE);
        } else {
            ViewGroup.LayoutParams params = tvMoreImages.getLayoutParams();
            float scale = getResources().getDisplayMetrics().density;
            int pixels = (int) (61 * scale + 0.5f);
            if (imagesCount > 5) {
                params.height = pixels;
                tvMoreImages.setText("+" + String.valueOf(imagesCount - 5));
            } else if (imagesCount > 3) {
                params.height = pixels;
                tvMoreImages.setText("+0");
            }
        }
        imagesPath = place.getImagesPath();
        placeSmallImagesAdapter.updateImagesPath(imagesPath);

        menuImagesCount = place.getMenuImagesCount();

        if (menuImagesCount == 0)
            tvFoodMenuFallback.setVisibility(View.VISIBLE);

        placeMenuSmallImagesAdapter.updateImagesCount(menuImagesCount);
        placeMenuSmallImagesAdapter.updateImagesPath(imagesPath);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.ivPlaceCover:
                i = new Intent(this, PlaceImagesActivity.class);
                i.putExtra("place_id", place.getId().toString());
                i.putExtra("place_name", place.getName());
                i.putExtra("images_count", imagesCount);
                i.putExtra("images_path", imagesPath);
                startActivity(i);
                break;
            case R.id.tvMoreImages:
                i = new Intent(this, PlaceImagesActivity.class);
                i.putExtra("place_id", place.getId().toString());
                i.putExtra("place_name", place.getName());
                i.putExtra("images_count", imagesCount);
                i.putExtra("images_path", imagesPath);
                startActivity(i);
                break;
            case R.id.ibCallPlace:
                i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
                try {
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.e(LOG_TAG, ex.toString());
                    Toast.makeText(getApplicationContext(), "your Activity is not founded", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ibNavigateToPlace:
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));
                i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                i.setPackage("com.google.android.apps.maps");
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                } else {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.bGoToPlaceFeedbackScreen:
                i = new Intent(this, PlaceFeedbackActivity.class);
                i.putExtra("placeId", place.getId());
                i.putExtra("placeName", place.getName());
                startActivity(i);
                break;
            case R.id.ibTimingExpand:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(UtilFunctions.getTimingsDialogString(timings))
                        .setTitle("Timings");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

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
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        myShareIntent.putExtra(Intent.EXTRA_TEXT, String.format(Constants.PLACE_SHARE_TEXT, place.getName(), place.getWifiSpeed()));
        setShareIntent(myShareIntent);

        MenuItem sortItem = menu.findItem(R.id.action_sort);
        sortItem.setVisible(false);

        // Return true to display menu
        return true;
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "activity paused");
        getTimingsAsyncTask.cancel(true);
        downloadImageAsyncTask.cancel(true);
        super.onPause();
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private class DownloadImageAsyncTask extends AsyncTask<Void, Object, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (isCancelled())
                return null;
            if (imagesCount == 0 && menuImagesCount == 0)
                return null;
            String coverImageUrl = ServerHelperFunctions.getPlaceCoverImageUrl(imagesPath, place.getName());
            Bitmap bt = ServerHelperFunctions.downloadBitmapFromUrl(coverImageUrl);
            if (bt != null)
                publishProgress(bt, ImageType.COVER);

            String path;
            int imagesToDownload;
            imagesToDownload = (imagesCount > 5) ? 5 : imagesCount;
            for (int i = 0; i < imagesToDownload; i++) {
                if (isCancelled())
                    return null;
                bt = ((MyApplication) getApplication()).getBitmapFromCache(UtilFunctions.getImageCacheKey(String.valueOf(place.getId()), ImageType.PLACE, i, ImageSize.THUMBNAIL));
                if (bt != null) {
                    publishProgress(bt, ImageType.PLACE);
                    continue;
                }

                path = ServerConstants.SERVER_URL + imagesPath + ServerConstants.PLACE_PATH + ServerConstants.THUMBNAILS_PATH + place.getName() + i + ".png";
                path = path.replace(" ", "%20");
                bt = ServerHelperFunctions.downloadBitmapFromUrl(path);

                if (bt != null) {
                    publishProgress(bt, ImageType.PLACE);
                    ((MyApplication) getApplication()).putBitmapInCache(UtilFunctions.getImageCacheKey(String.valueOf(place.getId()), ImageType.PLACE, i, ImageSize.THUMBNAIL), bt);
                }
            }
            if (menuImagesCount == 0)
                return null;
            for (int i = 0; i < menuImagesCount; i++) {
                if (isCancelled())
                    return null;
                bt = ((MyApplication) getApplication()).getBitmapFromCache(UtilFunctions.getImageCacheKey(String.valueOf(place.getId()), ImageType.MENU, i, ImageSize.THUMBNAIL));
                if (bt != null) {
                    publishProgress(bt, ImageType.MENU);
                }

                path = ServerConstants.SERVER_URL + imagesPath + ServerConstants.MENU_PATH + ServerConstants.THUMBNAILS_PATH + place.getName() + i + ".png";
                path = path.replace(" ", "%20");
                Log.i(LOG_TAG, path);
                bt = ServerHelperFunctions.downloadBitmapFromUrl(path);

                if (bt != null) {
                    publishProgress(bt, ImageType.MENU);
                    ((MyApplication) getApplication()).putBitmapInCache(UtilFunctions.getImageCacheKey(String.valueOf(place.getId()), ImageType.MENU, i, ImageSize.THUMBNAIL), bt);
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.i(LOG_TAG, "onProgressUpdate");
            Bitmap bt = (Bitmap) values[0];
            ImageType type = (ImageType) values[1];
            if (type == ImageType.COVER) {
                ivPlaceCover.setImageBitmap(bt);
                ivPlaceCover.setOnClickListener(PlaceViewActivity.this);
            } else if (type == ImageType.PLACE) {
                if (!tvMoreImages.isShown())
                    tvMoreImages.setVisibility(View.VISIBLE);
                Log.i(LOG_TAG, "adding small image");
                placeSmallImageBitmaps.add(bt);
                placeSmallImagesAdapter.notifyDataSetChanged();
            } else if (type == ImageType.MENU) {
                placeMenuSmallImageBitmaps.add(bt);
                placeMenuSmallImagesAdapter.notifyDataSetChanged();
            }
        }
    }

    public class GetTimingsAsyncTask extends AsyncTask<Void, Void, String> {


        private static final String LOG_TAG = "GetTimingsAsyncTask";
        private final String mUrl;

        public GetTimingsAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString;
            if (isCancelled())
                return null;
            resultString = ServerHelperFunctions.getJSON(mUrl);
            return resultString;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            Log.i(LOG_TAG, "on post execute\n" + jsonString);
            if (jsonString == "not present") {
                tvTiming.setText("Timings not available");
            }
            try {
                timings = new Gson().fromJson(jsonString, Timings.class);
                if (timings != null) {
                    ibTimingExpand.setVisibility(View.VISIBLE);
                    Log.i(LOG_TAG, timings.toString());
                    tvTiming.setText(day + " : " + timings.getTuesday());
                }
            } catch (Exception e) {
                Log.i(LOG_TAG, e.toString());
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS:
                new GetTimingsAsyncTask(ServerHelperFunctions.getTimingsUriFromId(String.valueOf(place.getId()))).execute();
                new DownloadImageAsyncTask().execute();
                break;
        }
    }
}
