package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;

public class PlaceViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PlaceViewActivity";

    TextView tvAmbiance;
    TextView tvAddress;
    TextView tvService;
    TextView tvDescription;
    TextView tvWifiSpeed;
    TextView tvWifiPaid;
    TextView tvChargingPoints;
    TextView tvPhone;

    CollapsingToolbarLayout collapsing_container;
    Toolbar toolbar;

    CardView cvFood;
    CardView cvPhotos;

    PhoneCallListener phoneListener;

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
        }
    }

    private void initialize() {

        toolbar = (Toolbar) findViewById(R.id.technique_three_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);

        cvFood = (CardView) findViewById(R.id.cv_placeFood);
        cvFood.setOnClickListener(this);
        cvPhotos = (CardView) findViewById(R.id.cv_placePhotos);
        cvPhotos.setOnClickListener(this);

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

        phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void populate() {
        collapsing_container.setTitle(place.getName());

        tvAddress.setText(place.getAddress());
        tvAmbiance.setText(String.valueOf(place.getAmbiance()));
        tvService.setText(String.valueOf(place.getService()));
        tvChargingPoints.setText(place.getChargingPoints());
        tvWifiSpeed.setText(String.valueOf(place.getWifiSpeed()));
        tvWifiPaid.setText(place.getWifiPaid());
        tvDescription.setText(place.getDescription());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Log.i(TAG, "home pressed");
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_feedback:
                Intent i = new Intent(this, AppFeedbackActivity.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        invalidateOptionsMenu();
        return true;
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
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", 12.939074, 77.612976);
                //Uri gmmIntentUri = Uri.parse("google.navigation:q=12.939074,77.612976");
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
                i.putExtra("place_id", place.getId());
                i.putExtra("place_name",place.getName());
                startActivity(i);
        }
    }

    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;


        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(TAG, "restart app");

                    // restart app
                    Intent i = new Intent(getApplicationContext(), PlaceViewActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //i.putExtra("place",place);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }
}
