package com.valmiki.hotspots.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.valmiki.hotspots.MyApplication;
import com.valmiki.hotspots.R;
import com.valmiki.hotspots.adapters.PlacesRecyclerViewAdapter;
import com.valmiki.hotspots.enums.ConnectionAvailability;
import com.valmiki.hotspots.enums.InternetCheck;
import com.valmiki.hotspots.fragments.FragmentDrawer;
import com.valmiki.hotspots.models.Place;
import com.valmiki.hotspots.utils.CheckInternetAsyncTask;
import com.valmiki.hotspots.utils.Constants;
import com.valmiki.hotspots.utils.LocationHelper;
import com.valmiki.hotspots.utils.ServerHelperFunctions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PlacesListActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, LocationListener, LocationHelper.GpsListener, PlacesRecyclerViewAdapter.OnPlaceClickedListener, View.OnClickListener, FragmentDrawer.FragmentDrawerListener, SwipeRefreshLayout.OnRefreshListener {

    Tracker analyticsTracker;


    private final static String LOG_TAG = "PlacesListActivity";
    private String city = "";

    private Toast toast = null;

    DrawerLayout drawerLayout;

    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    FragmentDrawer drawerFragment;

    InternetCheck internetCheck;

    private RecyclerView mRecyclerView;
    private PlacesRecyclerViewAdapter mAdapter;

    private LocationHelper locationHelper = null;

    List<Place> places;
    private List<Float> distances;

    private ProgressDialog pd;

    AlertDialog.Builder dialogBuilder;
    AlertDialog sortDialog;

    private Boolean areDistancesCalculated;
    HashMap<Integer, Float> placeDistances;

    RadioButton rbDistance, rbWifi, rbRating;

    Snackbar internetSnackbar, gpsSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.placesActivityBackground));
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getStringExtra("city") != null)
            city = callingIntent.getStringExtra("city");

        initialize();
        if (callingIntent != null)
            internetCheck = InternetCheck.valueOf(callingIntent.getStringExtra("internetChecked"));
        Log.i(LOG_TAG, "internetChecked : " + internetCheck);
        getPlaces();
    }

    private void getPlaces() {
        Log.i(LOG_TAG, "getPlaces()");

        if (internetCheck == InternetCheck.CHECKED) {
            if (!swipeRefreshLayout.isRefreshing()) {
                pd = ProgressDialog.show(this, "Please wait", "Fetching you the list of places", true, true);
            }
            new GetPlacesAsyncTask(ServerHelperFunctions.getPlacesUrlByCity(city)).execute();
            return;
        }

        Log.i(LOG_TAG, "checking for internet connection");

        ConnectionAvailability isInternetAvailable = null;
        try {
            isInternetAvailable = new CheckInternetAsyncTask(this).execute().get(3L, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isInternetAvailable == ConnectionAvailability.INTERNET_AVAILABLE) {
            internetCheck = InternetCheck.CHECKED;
            if (!swipeRefreshLayout.isRefreshing())
                pd = ProgressDialog.show(this, "Please wait", "Fetching you the list of places", true, true);
            new GetPlacesAsyncTask(ServerHelperFunctions.getPlacesUrlByCity(city)).execute();
        } else {
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
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
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(LOG_TAG, "onNewIntent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.i(LOG_TAG, "search intent");
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(LOG_TAG, "query : " + query);
            if (query.isEmpty())
                mAdapter.flushFilter();
            mAdapter.setFilter(query);
            return;
        }
    }

    private void initialize() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarPlacesList);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_places_list);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorPlacesList);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeFreshPlaces);
        swipeRefreshLayout.setOnRefreshListener(this);

        areDistancesCalculated = false;

        TextView title = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
        title.setText(city);
        mToolbar.findViewById(R.id.ll_toolbarPlacesList).setOnClickListener(this);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout_places_list), mToolbar);
        drawerFragment.setDrawerListener(this);

        places = new ArrayList<>();
        distances = new ArrayList<>();
        placeDistances = new HashMap<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new PlacesRecyclerViewAdapter(places, distances, this);
        mAdapter.setOnPlaceClickedListener(this);
        mRecyclerView.setAdapter(mAdapter);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationHelper = new LocationHelper(this, locationManager, this, coordinatorLayout);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationHelper.registerLocationManager();
        }

        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sort_dialog, null);
        dialogBuilder.setView(dialogView);
        sortDialog = dialogBuilder.create();

        rbDistance = (RadioButton) dialogView.findViewById(R.id.radioBDistance);
        rbDistance.setOnClickListener(this);

        rbWifi = (RadioButton) dialogView.findViewById(R.id.radioBWifi);
        rbWifi.setOnClickListener(this);

        rbRating = (RadioButton) dialogView.findViewById(R.id.radioBRating);
        rbRating.setOnClickListener(this);

    }

    private void calculatePlacesDistance() {
        if (gpsSnackbar != null && gpsSnackbar.isShownOrQueued())
            gpsSnackbar.dismiss();
        Location placeLocation;
        Float distance;
        Place place;
        distances.clear();
        for (int i = 0; i < places.size(); i++) {
            place = places.get(i);
            placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(Double.parseDouble(place.getLatitude()));
            placeLocation.setLongitude(Double.parseDouble(place.getLongitude()));
            distance = locationHelper.getLocation().distanceTo(placeLocation) / 1000;
            distances.add(distance);
            placeDistances.put(place.getId(), distance);
        }
        mAdapter.notifyDataSetChanged();
        areDistancesCalculated = true;

    }

    @Override
    public void onLocationChanged(Location location) {
        locationHelper.setLocation(location);
        // Toast.makeText(this, "Calculating distance of places from here", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        locationHelper.getLocationFromSystem();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void updateGpsLocation() {
        Log.i(LOG_TAG, "updateGpsLocation");
        calculatePlacesDistance();
    }

    @Override
    public void onPlaceClicked(int position, View v) {
        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory(LOG_TAG)
                .setAction(getString(R.string.place_clicked))
                .setLabel(places.get(position).getName())
                .build());
        Intent i = new Intent(this, PlaceViewActivity.class);
        i.putExtra("place", places.get(position));
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_toolbarPlacesList:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("PlacesList")
                        .setAction("ClickToolbar")
                        .setLabel("City Change")
                        .build());
                Intent i = new Intent(this, CitySelectionActivity.class);
                i.putExtra("from", "places");
                startActivity(i);
                finish();
                break;
            case R.id.radioBDistance:
                rbRating.setChecked(false);
                rbWifi.setChecked(false);
                if (areDistancesCalculated) {
                    Collections.sort(places, new Comparator<Place>() {
                        @Override
                        public int compare(Place lhs, Place rhs) {
                            return placeDistances.get(lhs.getId()).compareTo(placeDistances.get(rhs.getId()));
                        }
                    });
                    calculatePlacesDistance();
                } else {
                    Toast.makeText(PlacesListActivity.this, "Distances not calculated yet", Toast.LENGTH_SHORT).show();
                }
                if (sortDialog.isShowing())
                    sortDialog.cancel();
                break;
            case R.id.radioBRating:
                rbDistance.setChecked(false);
                rbWifi.setChecked(false);
                Collections.sort(places, new Comparator<Place>() {
                    @Override
                    public int compare(Place lhs, Place rhs) {
                        return rhs.getRating().compareTo(lhs.getRating());
                    }
                });

                if (areDistancesCalculated)
                    calculatePlacesDistance();
                else
                    mAdapter.notifyDataSetChanged();
                if (sortDialog.isShowing())
                    sortDialog.cancel();
                break;
            case R.id.radioBWifi:
                rbRating.setChecked(false);
                rbDistance.setChecked(false);
                Collections.sort(places, new Comparator<Place>() {
                    @Override
                    public int compare(Place lhs, Place rhs) {
                        return rhs.getWifiSpeed().compareTo(lhs.getWifiSpeed());
                    }
                });

                if (areDistancesCalculated)
                    calculatePlacesDistance();
                else
                    mAdapter.notifyDataSetChanged();

                if (sortDialog.isShowing())
                    sortDialog.cancel();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsTracker = ((MyApplication) getApplication()).getDefaultTracker();
        analyticsTracker.setScreenName(LOG_TAG);
        analyticsTracker.set("cityName", city);
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mAdapter.setAnalyticsTracker(analyticsTracker);
    }

    @Override
    protected void onPause() {
        killToast();
        super.onPause();
    }

    private void killToast() {
        if (this.toast != null) {
            this.toast.cancel();
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        Intent i;
        switch (position) {
            case 0:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(getString(R.string.analytics_draweritem_clicked))
                        .setLabel(getString(R.string.title_about))
                        .build());
                i = new Intent(this, AboutMeActivity.class);
                startActivity(i);
                break;
            case 1:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(getString(R.string.analytics_draweritem_clicked))
                        .setLabel(getString(R.string.title_app_feedback))
                        .build());
                i = new Intent(this, AppFeedbackActivity.class);
                startActivity(i);
                break;
            case 2:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(getString(R.string.analytics_draweritem_clicked))
                        .setLabel(getString(R.string.title_suggest_place))
                        .build());
                i = new Intent(this, SuggestNewPlaceActivity.class);
                startActivity(i);
                break;

        }
    }

    @Override
    public void onRefresh() {
        Log.i(LOG_TAG, "onRefresh()");
        refreshPlaces();
    }

    private void refreshPlaces() {
        getPlaces();
    }

    public class GetPlacesAsyncTask extends AsyncTask<Void, Void, String> {

        private final String mUrl;

        public GetPlacesAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString;
            resultString = ServerHelperFunctions.getJSON(mUrl, analyticsTracker);
            return resultString;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            Log.i(LOG_TAG, "onPostExecute");
            Log.i(LOG_TAG, jsonString);
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
                List<Place> receivedPlaces = (ArrayList<Place>) new Gson().fromJson(jsonString, collectionType);
                Log.i(LOG_TAG, "updating adapter data");
                places = receivedPlaces;
                mAdapter.updateData(receivedPlaces);

                if (internetSnackbar != null && internetSnackbar.isShownOrQueued())
                    internetSnackbar.dismiss();
                locationHelper.checkPermissionAndgetLocation();
                for (int i = 0; i < places.size(); i++)
                    Log.i(LOG_TAG, places.get(i).toString());
            } catch (JsonSyntaxException e) {
                //not able to parse response, after requesting all places
                analyticsTracker.send(new HitBuilders.ExceptionBuilder()
                        .setDescription(e.toString())
                        .setFatal(true)
                        .build());
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constants.REQUEST_CODE_GPS_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper.getLocationFromSystem();
                    if (locationHelper.getLocation() != null)
                        calculatePlacesDistance();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(PlacesListActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        gpsSnackbar = Snackbar
                                .make(coordinatorLayout, "To show the distances of places, gps permission is required", Snackbar.LENGTH_INDEFINITE)
                                .setAction("ALLOW", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        analyticsTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory(LOG_TAG)
                                                .setAction(getString(R.string.analytics_snackbar_action))
                                                .setLabel("gps_permission_settings")
                                                .build());
                                        ActivityCompat.requestPermissions(PlacesListActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                Constants.REQUEST_CODE_GPS_PERMISSIONS);
                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                        gpsSnackbar.show();
                        break;
                    }
                    gpsSnackbar = Snackbar
                            .make(coordinatorLayout, "To show the distances of places, gps permission is required", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SETTINGS", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    analyticsTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory(LOG_TAG)
                                            .setAction(getString(R.string.analytics_snackbar_action))
                                            .setLabel("app_settings")
                                            .build());
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                    startActivityForResult(intent, Constants.REQUEST_CODE_INTENT_APP_SETTINGS);
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                    gpsSnackbar.show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        item.setVisible(false);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        item = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.clearFocus();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i(LOG_TAG, "onQueryTextSubmit : query : " + query);
                    if (query.isEmpty())
                        mAdapter.flushFilter();
                    else
                        mAdapter.setFilter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    Log.i(LOG_TAG, "onQueryTextChange : query : " + query);
                    if (query.isEmpty())
                        mAdapter.flushFilter();
                    else
                        mAdapter.setFilter(query);
                    return true;
                }
            });

            //TODO : check if it works with previous version of android

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Log.i(LOG_TAG, "Search closed");
                    mAdapter.flushFilter();
                    return false;
                }
            });

            MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    Log.i(LOG_TAG, "Search expanded");
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    Log.i(LOG_TAG, "Search closed");
                    mAdapter.flushFilter();
                    return true;
                }
            });
        }

        MenuItem sortItem = menu.findItem(R.id.action_sort);
        Drawable sortDrawable = sortItem.getIcon();
        sortDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(getString(R.string.analytics_menuitem_clicked))
                        .setLabel(getString(R.string.analytics_menu_sort))
                        .build());
                sortDialog.show();
                return true;
            case R.id.menu_refresh:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(getString(R.string.analytics_menuitem_clicked))
                        .setLabel(getString(R.string.analytics_menu_refresh))
                        .build());
                Log.i(LOG_TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                swipeRefreshLayout.setRefreshing(true);


                refreshPlaces();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_INTENT_GPS_SETTINGS:
                locationHelper.updateIsGpsEnabled();
                locationHelper.getLocationFromSystem();
                locationHelper.dissmissSnackbar();
                break;
            case Constants.REQUEST_CODE_INTENT_APP_SETTINGS:
                int hasGpsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasGpsPermission == PackageManager.PERMISSION_GRANTED) {
                    locationHelper.getLocationFromSystem();
                }
                locationHelper.dissmissSnackbar();
                break;
            case Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS:
                getPlaces();
                break;
        }
    }
}
