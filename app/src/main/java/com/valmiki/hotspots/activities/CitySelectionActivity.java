package com.valmiki.hotspots.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.valmiki.hotspots.MyApplication;
import com.valmiki.hotspots.R;
import com.valmiki.hotspots.adapters.CitiesRecyclerViewAdapter;
import com.valmiki.hotspots.enums.AppStart;
import com.valmiki.hotspots.enums.ConnectionAvailability;
import com.valmiki.hotspots.enums.InternetCheck;
import com.valmiki.hotspots.models.Place;
import com.valmiki.hotspots.utils.CheckInternetAsyncTask;
import com.valmiki.hotspots.utils.Constants;
import com.valmiki.hotspots.utils.ServerHelperFunctions;
import com.valmiki.hotspots.utils.UtilFunctions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CitySelectionActivity extends AppCompatActivity implements CitiesRecyclerViewAdapter.MyClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final static String LOG_TAG = "CitySelectionActivity";

    CoordinatorLayout coordinatorLayout;

    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private CitiesRecyclerViewAdapter mAdapter;

    List<Place> places;

    private ProgressDialog pd;

    Snackbar internetSnackbar;

    Tracker analyticsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cities);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.placesActivityBackground));
        Log.i(LOG_TAG, "OnCreate");
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        AppStart appStart;
        Intent callingIntent = getIntent();
        String from = null;
        if (callingIntent != null)
            from = getIntent().getStringExtra("from");
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
        analyticsTracker = ((MyApplication) getApplication()).getDefaultTracker();
        analyticsTracker.setScreenName(LOG_TAG);
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
            if (!swipeRefreshLayout.isRefreshing()) {
                pd = ProgressDialog.show(this, "Please wait", "Fetching you the list of cities", true, true);
            }
            new GetPlacesAsyncTask(ServerHelperFunctions.getPlacesUrlByCity("city")).execute();
            return;
        } else {
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            internetSnackbar = Snackbar
                    .make(coordinatorLayout, "Check Internet Connection and swipe down to refresh.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            analyticsTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory(LOG_TAG)
                                    .setAction(getString(R.string.analytics_snackbar_action))
                                    .setLabel("network_settings")
                                    .build());
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS);
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary));
            internetSnackbar.show();
        }
    }

    private void initialize() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorCityList);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeFreshCities);
        swipeRefreshLayout.setOnRefreshListener(this);

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
        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory(LOG_TAG)
                .setAction(getString(R.string.city_clicked))
                .setLabel(city)
                .build());

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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        item.setVisible(false);

        item = menu.findItem(R.id.action_search);
        item.setVisible(false);


        item = menu.findItem(R.id.action_sort);
        item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_refresh:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(getString(R.string.analytics_menuitem_clicked))
                        .setLabel(getString(R.string.analytics_menu_refresh))
                        .build());
                Log.i(LOG_TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                swipeRefreshLayout.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                refreshCities();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        Log.i(LOG_TAG, "onRefresh()");
        refreshCities();
    }

    private void refreshCities() {
        getCities();
    }

    public class GetPlacesAsyncTask extends AsyncTask<Void, Void, String> {

        private final String mUrl;

        public GetPlacesAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            return ServerHelperFunctions.getJSON(mUrl, analyticsTracker);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            Log.i(LOG_TAG, "on post execute\n" + jsonString);
            if (jsonString.contains("Exception")) {
                internetSnackbar = Snackbar
                        .make(coordinatorLayout, "Check Internet Connection and swipe down to refresh.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETTINGS", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS);
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                internetSnackbar.show();
                return;
            }
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            try {
                List<Place> tempPlaces = new Gson().fromJson(jsonString, collectionType);
                places = tempPlaces;
                mAdapter.updateData(places);

                if (internetSnackbar != null && internetSnackbar.isShownOrQueued())
                    internetSnackbar.dismiss();
            } catch (JsonSyntaxException e) {
                //not able to parse response, after requesting all places
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS:
                getCities();
                break;
        }
    }

}
