package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;

public class PlaceViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "PlaceViewActivity";

    TextView tvAmbiance;
    TextView tvService;
    TextView tvDescription;
    TextView tvWifiSpeed;
    TextView tvWifiPaid;
    TextView tvChargingPoints;

    CollapsingToolbarLayout collapsing_container;
    Toolbar toolbar;

    CardView cv_Food;
    CardView cv_photos;

    Place place = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);

        initialize();

        place = getIntent().getExtras().getParcelable("place");
        if (place != null) {
            populate();
        }
    }

    private void initialize() {

        toolbar = (Toolbar) findViewById(R.id.technique_three_toolbar);
        setSupportActionBar(toolbar);
        collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);

        cv_Food = (CardView) findViewById(R.id.cv_placeFood);
        cv_Food.setOnClickListener(this);
        cv_photos = (CardView) findViewById(R.id.cv_placePhotos);
        cv_photos.setOnClickListener(this);

        tvWifiSpeed = (TextView) findViewById(R.id.tvWifiSpeedValue);
        tvAmbiance = (TextView) findViewById(R.id.tvAmbianceValue);
        tvService = (TextView) findViewById(R.id.tvServiceValue);
        tvDescription = (TextView) findViewById(R.id.tvDescriptionValue);
        tvWifiPaid = (TextView) findViewById(R.id.tvWifiPaidValue);
        tvChargingPoints = (TextView) findViewById(R.id.tvChargingPointsValue);
    }

    private void populate() {
        collapsing_container.setTitle(place.getName());
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_feedback) {
            Intent i = new Intent(this,FeedbackActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.cv_placeFood:
                i = new Intent(this,ZomatoWebviewActivity.class);
                i.putExtra("zomato_url", place.getZomatoUrl());
                startActivity(i);
                break;
            case R.id.cv_placePhotos:
                i = new Intent(this,PlaceImagesActivity.class);
                i.putExtra("place_id", place.getId().toString());
                startActivity(i);
                break;
        }
    }
}
