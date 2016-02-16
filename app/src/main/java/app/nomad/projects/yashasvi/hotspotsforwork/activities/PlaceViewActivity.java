package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import app.nomad.projects.yashasvi.hotspotsforwork.MyApplication;
import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.PlaceMenuSmallImagesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.PlaceSmallImagesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.enums.ImageSize;
import app.nomad.projects.yashasvi.hotspotsforwork.enums.ImageType;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Timings;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.Constants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerHelperFunctions;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.UtilFunctions;

public class PlaceViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = "PlaceViewActivity";

    private ShareActionProvider mShareActionProvider;

    int imagesCount = -1;
    int menuImagesCount = -1;
    String imagesPath = "";

    public static String day;

    CollapsingToolbarLayout collapsing_container;
    Toolbar toolbar;

    ImageView ivPlaceCover;

    TextView tvTiming;
    TextView tvAmbiance;
    TextView tvAddress;
    TextView tvDescription;
    TextView tvWifiValue;
    TextView tvWifiPaid;
    TextView tvChargingPoints;
    TextView tvPhone;

    RecyclerView recyclerViewPlaceSmallImages;
    PlaceSmallImagesRecyclerViewAdapter placeSmallImagesAdapter;
    List<Bitmap> placeSmallImageBitmaps;

    RecyclerView recyclerViewPlaceMenuSmallImages;
    PlaceMenuSmallImagesRecyclerViewAdapter placeMenuSmallImagesAdapter;
    List<Bitmap> placeMenuSmallImageBitmaps;

    ImageButton ibCallPlace, ibNavigateToPlace, ibTimingExpand;
    Button bGoToFeedbackScreen;

    Place place = null;
    Timings timings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);

        initialize();

        place = getIntent().getExtras().getParcelable("place");
        if (place != null) {
            Log.i(LOG_TAG, "place received " + place.toString());
            populate();
            new downloadImageAsyncTask().execute();
            new GetTimingsAsyncTask(ServerHelperFunctions.getTimingsUriFromId(String.valueOf(place.getId()))).execute();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initialize() {

        day = Constants.days.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        toolbar = (Toolbar) findViewById(R.id.technique_three_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);

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
        ibTimingExpand = (ImageButton) findViewById(R.id.ibTimingExpand);
        ibTimingExpand.setOnClickListener(this);
        tvAddress = (TextView) findViewById(R.id.tvPlaceAddressValue);
        tvPhone = (TextView) findViewById(R.id.tvPlacePhoneValue);

        tvAmbiance = (TextView) findViewById(R.id.tvAmbianceValue);
        tvDescription = (TextView) findViewById(R.id.tvDescriptionValue);

        tvWifiPaid = (TextView) findViewById(R.id.tvWifiPaidValue);
        tvWifiValue = (TextView) findViewById(R.id.tvWifiValue);

        tvChargingPoints = (TextView) findViewById(R.id.tvChargingPointsValue);

        ibCallPlace = (ImageButton) findViewById(R.id.ibCallPlace);
        ibNavigateToPlace = (ImageButton) findViewById(R.id.ibNavigateToPlace);
        ibCallPlace.setOnClickListener(this);
        ibNavigateToPlace.setOnClickListener(this);

        bGoToFeedbackScreen = (Button) findViewById(R.id.bGoToPlaceFeedbackScreen);
        bGoToFeedbackScreen.setOnClickListener(this);

    }

    private void populate() {

        placeSmallImagesAdapter = new PlaceSmallImagesRecyclerViewAdapter(this, placeSmallImageBitmaps, String.valueOf(place.getId()), place.getName());
        recyclerViewPlaceSmallImages.setAdapter(placeSmallImagesAdapter);

        placeMenuSmallImagesAdapter = new PlaceMenuSmallImagesRecyclerViewAdapter(this, placeMenuSmallImageBitmaps, String.valueOf(place.getId()), place.getName());
        recyclerViewPlaceMenuSmallImages.setAdapter(placeMenuSmallImagesAdapter);

        collapsing_container.setTitle(place.getName());

        tvAddress.setText(place.getAddress());
        tvAmbiance.setText(String.valueOf(place.getAmbiance()));
        tvChargingPoints.setText(Constants.chargingPointsLevel.get(Integer.parseInt(place.getChargingPoints())));

        tvWifiValue.setText(Constants.wifiSpeedLevel.get(Integer.parseInt(place.getWifiSpeed())));
        tvWifiPaid.setText("( " + place.getWifiPaid() + ")");

        tvDescription.setText(place.getDescription());

        imagesCount = place.getImagesCount();
        placeSmallImagesAdapter.updateImagesCount(imagesCount);

        imagesPath = place.getImagesPath();
        placeSmallImagesAdapter.updateImagesPath(imagesPath);

        menuImagesCount = place.getMenuImagesCount();
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

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("text/plain");
        myShareIntent.putExtra(Intent.EXTRA_TEXT, String.format(Constants.PLACE_SHARE_TEXT, place.getName(), place.getWifiSpeed()));
        setShareIntent(myShareIntent);

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    public class downloadImageAsyncTask extends AsyncTask<Void, Object, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String coverImageUrl = ServerHelperFunctions.getPlaceCoverImageUrl(imagesPath, place.getName());
            Bitmap bt = ServerHelperFunctions.downloadBitmapFromUrl(coverImageUrl);
            if (bt != null)
                publishProgress(bt, ImageType.COVER);

            String path;
            for (int i = 0; i < 5; i++) {
                bt = ((MyApplication) getApplication()).getBitmapFromCache(UtilFunctions.getImageCacheKey(String.valueOf(place.getId()), ImageType.PLACE, i, ImageSize.THUMBNAIL));
                if (bt != null) {
                    publishProgress(bt, ImageType.PLACE);
                }

                path = imagesPath + ServerConstants.PLACE_PATH + ServerConstants.THUMBNAILS_PATH + place.getName() + i + ".png";
                path = path.replace(" ", "%20");
                Log.i(LOG_TAG, path);
                bt = ServerHelperFunctions.downloadBitmapFromUrl(path);

                if (bt != null) {
                    publishProgress(bt, ImageType.PLACE);
                    ((MyApplication) getApplication()).putBitmapInCache(UtilFunctions.getImageCacheKey(String.valueOf(place.getId()), ImageType.PLACE, i, ImageSize.THUMBNAIL), bt);
                }
            }

            for (int i = 0; i < menuImagesCount; i++) {
                bt = ((MyApplication) getApplication()).getBitmapFromCache(UtilFunctions.getImageCacheKey(String.valueOf(place.getId()), ImageType.MENU, i, ImageSize.THUMBNAIL));
                if (bt != null) {
                    publishProgress(bt, ImageType.MENU);
                }

                path = imagesPath + ServerConstants.MENU_PATH + ServerConstants.THUMBNAILS_PATH + place.getName() + i + ".png";
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
        private String mUrl;

        public GetTimingsAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString;
            resultString = ServerHelperFunctions.getJSON(mUrl);
            return resultString;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            Log.i(LOG_TAG, "on post execute\n" + jsonString);
            try {
                timings = new Gson().fromJson(jsonString, Timings.class);
                if (timings != null) {
                    Log.i(LOG_TAG, timings.toString());
                    tvTiming.setText(day + " : " + timings.getTuesday());
                }
            } catch (JsonSyntaxException e) {
                //not able to parse response, after requesting timings
            }

        }
    }

}
