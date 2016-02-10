package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.nomad.projects.yashasvi.hotspotsforwork.MyApplication;
import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.PlaceSmallImagesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.enums.ImageType;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.models.PlaceImages;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.Constants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerHelperFunctions;

public class PlaceViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PlaceViewActivity";

    private ShareActionProvider mShareActionProvider;

    int imagesCount = -1;
    String imagesPath = "";

    CollapsingToolbarLayout collapsing_container;
    Toolbar toolbar;

    ImageView ivPlaceCover;

    TextView tvAmbiance;
    TextView tvAddress;
    TextView tvService;
    TextView tvDescription;
    TextView tvWifiSpeed;
    TextView tvWifiPaid;
    TextView tvChargingPoints;
    TextView tvPhone;

    CardView cvFood;
    //CardView cvPhotos;

    RecyclerView recyclerViewsmallImages;
    PlaceSmallImagesRecyclerViewAdapter mAdapter;
    List<Bitmap> smallImageBitmaps;

    ImageButton ibCallPlace, ibNavigateToPlace;
    Button bGoToFeedbackScreen;

    Place place = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);

        initialize();

        place = getIntent().getExtras().getParcelable("place");
        if (place != null) {
            Log.i(TAG, "place received " + place.toString());
            populate();
            new downloadImageAsyncTask(ServerHelperFunctions.getPlaceImagesObjectUrl(place.getId())).execute();

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initialize() {

        toolbar = (Toolbar) findViewById(R.id.technique_three_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);

        ivPlaceCover = (ImageView) findViewById(R.id.ivPlaceCover);

        cvFood = (CardView) findViewById(R.id.cv_placeFood);
        cvFood.setOnClickListener(this);
        //cvPhotos = (CardView) findViewById(R.id.cv_placePhotos);
        //cvPhotos.setOnClickListener(this);

        smallImageBitmaps = new ArrayList<>();
        recyclerViewsmallImages = (RecyclerView) findViewById(R.id.recyclerViewPhotosPlaceViewPage);

        tvAddress = (TextView) findViewById(R.id.tvPlaceAddressValue);
        tvPhone = (TextView) findViewById(R.id.tvPlacePhoneValue);
        tvWifiSpeed = (TextView) findViewById(R.id.tvWifiSpeedValue);
        tvAmbiance = (TextView) findViewById(R.id.tvAmbianceValue);
        tvService = (TextView) findViewById(R.id.tvServiceValue);
        tvDescription = (TextView) findViewById(R.id.tvDescriptionValue);
        tvWifiPaid = (TextView) findViewById(R.id.tvWifiPaidValue);
        tvChargingPoints = (TextView) findViewById(R.id.tvChargingPointsValue);

        ibCallPlace = (ImageButton) findViewById(R.id.ibCallPlace);
        ibNavigateToPlace = (ImageButton) findViewById(R.id.ibNavigateToPlace);
        ibCallPlace.setOnClickListener(this);
        ibNavigateToPlace.setOnClickListener(this);

        bGoToFeedbackScreen = (Button) findViewById(R.id.bGoToPlaceFeedbackScreen);
        bGoToFeedbackScreen.setOnClickListener(this);

    }

    private void populate() {

        recyclerViewsmallImages = (RecyclerView) findViewById(R.id.recyclerViewPhotosPlaceViewPage);
        //recyclerViewsmallImages.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewsmallImages.setLayoutManager(mLayoutManager);
        mAdapter = new PlaceSmallImagesRecyclerViewAdapter(this, smallImageBitmaps, String.valueOf(place.getId()), place.getName());
        recyclerViewsmallImages.setAdapter(mAdapter);

        collapsing_container.setTitle(place.getName());

        tvAddress.setText(place.getAddress());
        tvAmbiance.setText(String.valueOf(place.getAmbiance()));
        tvService.setText(String.valueOf(place.getService()));
        tvChargingPoints.setText(Constants.chargingPointsLevel.get(Integer.parseInt(place.getChargingPoints())));
        tvWifiSpeed.setText(Constants.wifiSpeedLevel.get(Integer.parseInt(place.getWifiSpeed())));
        tvWifiPaid.setText(place.getWifiPaid());
        tvDescription.setText(place.getDescription());
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.cv_placeFood:
                i = new Intent(this, ZomatoWebviewActivity.class);
                i.putExtra("zomato_url", place.getZomatoUrl());
                startActivity(i);
                break;
            case R.id.cv_placePhotos:
                i = new Intent(this, PlaceImagesActivity.class);
                i.putExtra("place_id", place.getId().toString());
                i.putExtra("place_name", place.getName());
                i.putExtra("images_count", imagesCount);
                i.putExtra("images_path", imagesPath);
                startActivity(i);
                break;
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
                    Log.e(TAG, ex.toString());
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

    private PlaceImages getPlaceImages(String url) {
        PlaceImages placeImages = null;
        HttpURLConnection connection;
        try {
            //get PlaceImages object
            URL u = new URL(url);
            Log.i(TAG, "getting place images " + u.toString());
            connection = (HttpURLConnection) u.openConnection();
            connection.connect();
            int status = connection.getResponseCode();
            Log.i(TAG, "status : " + status);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

            placeImages = new Gson().fromJson(sb.toString(), PlaceImages.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (placeImages != null)
            Log.i(TAG, placeImages.toString());
        return placeImages;
    }


    public class downloadImageAsyncTask extends AsyncTask<Void, Object, Void> {

        String placeImageUrl;

        downloadImageAsyncTask(String url) {
            placeImageUrl = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            PlaceImages placeImages = getPlaceImages(placeImageUrl);
            if (placeImages == null)
                return null;
            imagesCount = placeImages.getCount();
            mAdapter.updateImagesCount(imagesCount);

            imagesPath = placeImages.getPath();
            mAdapter.updateImagesPath(imagesPath);

            String coverImageUrl = ServerHelperFunctions.getPlaceCoverImageUrl(imagesPath, place.getName());
            Bitmap bt = ServerHelperFunctions.downloadBitmapFromUrl(coverImageUrl);
            if (bt != null)
                publishProgress(bt, ImageType.COVER);

            String path;
            for (int i = 1; i < 5; i++) {
                path = imagesPath + ServerConstants.THUMBNAILS_PATH + "/" + place.getName() + i + ".png";
                path = path.replace(" ", "%20");
                Log.i(TAG, path);
                bt = ((MyApplication) getApplication()).getBitmapFromCache(ServerHelperFunctions.getImageCacheKey(String.valueOf(place.getId()), i, ImageType.THUMBNAIL));
                if (bt == null) {
                    Log.i(TAG, "bitmap not present in cache " + i);
                    bt = ServerHelperFunctions.downloadBitmapFromUrl(path);
                }
                if (bt != null) {
                    publishProgress(bt, ImageType.SMALL);
                    ((MyApplication) getApplication()).putBitmapInCache(ServerHelperFunctions.getImageCacheKey(String.valueOf(place.getId()), i, ImageType.THUMBNAIL), bt);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Log.i(TAG, "onProgressUpdate");
            ImageType type = (ImageType) values[1];
            if (type == ImageType.COVER) {
                ivPlaceCover.setImageBitmap((Bitmap) values[0]);
                ivPlaceCover.setOnClickListener(PlaceViewActivity.this);
            } else if (type == ImageType.SMALL) {
                Log.i(TAG, "adding small image");
                smallImageBitmaps.add((Bitmap) values[0]);
                //mAdapter = new PlaceSmallImagesRecyclerViewAdapter(PlaceViewActivity.this, smallImageBitmaps, String.valueOf(place.getId()), place.getName());
                //recyclerViewsmallImages.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
