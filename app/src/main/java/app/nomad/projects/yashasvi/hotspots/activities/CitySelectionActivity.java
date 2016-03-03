package app.nomad.projects.yashasvi.hotspots.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.nomad.projects.yashasvi.hotspots.R;
import app.nomad.projects.yashasvi.hotspots.adapters.CitiesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspots.enums.AppStart;
import app.nomad.projects.yashasvi.hotspots.enums.ConnectionAvailability;
import app.nomad.projects.yashasvi.hotspots.enums.InternetCheck;
import app.nomad.projects.yashasvi.hotspots.models.Place;
import app.nomad.projects.yashasvi.hotspots.utils.CheckInternetAsyncTask;
import app.nomad.projects.yashasvi.hotspots.utils.Constants;
import app.nomad.projects.yashasvi.hotspots.utils.ServerHelperFunctions;
import app.nomad.projects.yashasvi.hotspots.utils.UtilFunctions;

public class CitySelectionActivity extends AppCompatActivity implements CitiesRecyclerViewAdapter.MyClickListener {

    private final static String LOG_TAG = "CitySelectionActivity";

    final int REQUEST_CODE = 1;

    CoordinatorLayout coordinatorLayout;
    private RecyclerView mRecyclerView;
    private CitiesRecyclerViewAdapter mAdapter;

    private ArrayList<Place> places;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cities);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.placesActivityBackground));
        Log.i(LOG_TAG, "OnCreate");
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        AppStart appStart;
        String from = getIntent().getStringExtra("from");
        if (from != null && from.equals("places")) {
            appStart = AppStart.FIRST_TIME;
        } else {
            appStart = UtilFunctions.checkAppStart(this, sharedPreferences);
        }

        switch (appStart) {
            case NORMAL:
                // find out which city has been selected previously and proceed to places activity
                String city = sharedPreferences.getString(
                        Constants.SELECTED_CITY, "city");
                goToPlacesActivity(city, InternetCheck.NOT_CHECKED);
                break;
            case FIRST_TIME_VERSION:
                // TODO show what's new
                break;
            case FIRST_TIME:
                initialize();
                getCities();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "onResume");
        super.onResume();
        if (places == null) {
            initialize();
            getCities();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(LOG_TAG, "onNewIntent");
        super.onNewIntent(intent);
        if (places == null) {
            initialize();
            getCities();
        }

    }

    private void getCities() {
        CheckInternetAsyncTask checkInternetAsyncTask = new CheckInternetAsyncTask(this);
        ConnectionAvailability isInternetAvailable = null;
        try {
            isInternetAvailable = checkInternetAsyncTask.execute().get(2L, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isInternetAvailable == ConnectionAvailability.INTERNET_AVAILABLE) {
            pd = ProgressDialog.show(this, "Please wait", "Fetching you the list of cities", true, true);
            new GetPlacesAsyncTask(ServerHelperFunctions.getPlacesUrlByCity("city")).execute();
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Check Internet Connection and swipe down to refresh.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS);
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();
        }
    }

    private void initialize() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorCityList);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarCityList);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.select_city);

        places = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCities);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CitiesRecyclerViewAdapter(places);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(int position, View v) {
        String city = places.get(position).getName();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit()
                .putString(Constants.SELECTED_CITY, city).apply();
        goToPlacesActivity(city, InternetCheck.CHECKED);
    }

    private void goToPlacesActivity(String city, InternetCheck internetCheck) {
        super.onResume();
        Intent i = new Intent(this, PlacesListActivity.class);
        i.putExtra("city", city);
        i.putExtra("internetChecked", internetCheck.name());
        startActivity(i);
        finish();
    }

    public class GetPlacesAsyncTask extends AsyncTask<Void, Void, String> {

        private final String mUrl;

        public GetPlacesAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            return ServerHelperFunctions.getJSON(mUrl);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            Log.i(LOG_TAG, "on post execute\n" + jsonString);
            if (jsonString.contains("Exception")) {
                Toast.makeText(CitySelectionActivity.this, "Unable to get cities. Swipe down to try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            try {
                List<Place> tempPlaces = new Gson().fromJson(jsonString, collectionType);
                for (int i = 0; i < tempPlaces.size(); i++) {
                    Log.i(LOG_TAG, tempPlaces.get(i).toString());
                    places.add(tempPlaces.get(i));
                }
                mAdapter.notifyDataSetChanged();
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            } catch (JsonSyntaxException e) {
                //not able to parse response, after requesting all places
            }

        }
    }

}
