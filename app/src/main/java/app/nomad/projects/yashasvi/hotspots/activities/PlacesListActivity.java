package app.nomad.projects.yashasvi.hotspots.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspots.R;
import app.nomad.projects.yashasvi.hotspots.adapters.PlacesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspots.fragments.FragmentDrawer;
import app.nomad.projects.yashasvi.hotspots.models.Place;
import app.nomad.projects.yashasvi.hotspots.utils.Constants;
import app.nomad.projects.yashasvi.hotspots.utils.LocationHelper;
import app.nomad.projects.yashasvi.hotspots.utils.MyLinearLayoutManager;
import app.nomad.projects.yashasvi.hotspots.utils.ServerHelperFunctions;


public class PlacesListActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, LocationListener, LocationHelper.GpsListener, PlacesRecyclerViewAdapter.OnPlaceClickedListener, View.OnClickListener, FragmentDrawer.FragmentDrawerListener {

    private final static String LOG_TAG = "PlacesListActivity";
    private String city = "";

    boolean doubleBackToExitPressedOnce = false;
    private Toast toast = null;


    private RecyclerView mRecyclerView;
    private PlacesRecyclerViewAdapter mAdapter;

    private LocationManager locationManager = null;
    private LocationHelper locationHelper = null;

    private ArrayList<Place> places;
    private List<Float> distances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.placesActivityBackground));
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getStringExtra("city") != null)
            city = callingIntent.getStringExtra("city");
        initialize();
        getPlaces();
    }

    private void getPlaces() {
        new GetPlacesAsyncTask(ServerHelperFunctions.getPlacesUrlByCity(city)).execute();
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
        if (places == null)
            getPlaces();
    }

    private void initialize() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarPlacesList);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //LinearLayout toolbarTitle = (LinearLayout) mToolbar.findViewById(R.id.ll_toolbar_title);
        TextView title = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
        title.setText(city);
        title.setOnClickListener(this);

        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        places = new ArrayList<>();
        distances = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new PlacesRecyclerViewAdapter(places, distances, this);
        mAdapter.setOnPlaceClickedListener(this);
        mRecyclerView.setAdapter(mAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationHelper = new LocationHelper(this, locationManager, this);
    }

    private void calculatePlacesDistance() {
        Location placeLocation;
        for (int i = 0; i < places.size(); i++) {
            placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(Double.parseDouble(places.get(i).getLatitude()));
            placeLocation.setLongitude(Double.parseDouble(places.get(i).getLongitude()));
            distances.add(locationHelper.getLocation().distanceTo(placeLocation) / 1000);
        }
        mAdapter.notifyDataSetChanged();

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

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
        Intent i = new Intent(this, PlaceViewActivity.class);
        i.putExtra("place", places.get(position));
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_toolbar_title:
                Intent i = new Intent(this, CitySelectionActivity.class);
                i.putExtra("from", "places");
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        showToast("Press again to exit");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
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
                i = new Intent(this, AboutMeActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(this, AppFeedbackActivity.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(this, SuggestNewPlaceActivity.class);
                startActivity(i);
                break;

        }
    }

    public class GetPlacesAsyncTask extends AsyncTask<Void, Void, String> {

        private final String mUrl;

        public GetPlacesAsyncTask(String url) {
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
            if (jsonString.contains("Exception")) {
                Toast.makeText(PlacesListActivity.this, "Unable to fetch places, Please try after some time", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(jsonString);
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            try {
                List<Place> receivedPlaces = (ArrayList<Place>) new Gson().fromJson(jsonString, collectionType);
                for (int i = 0; i < receivedPlaces.size(); i++)
                    places.add(receivedPlaces.get(i));
                mAdapter.notifyDataSetChanged();
                locationHelper.initiateLocationProcess();
                for (int i = 0; i < places.size(); i++)
                    Log.i(LOG_TAG, places.get(i).toString());
            } catch (JsonSyntaxException e) {
                //not able to parse response, after requesting all places
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constants.REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationHelper.initiateLocationProcess();
                    if (locationHelper.getLocation() != null)
                        calculatePlacesDistance();
                } else {
                    Toast.makeText(this, "You have to grant permissions in order to show distance", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareItem.setVisible(false);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final MenuItem item = menu.findItem(R.id.action_search);

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
                    mAdapter.setFilter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    Log.i(LOG_TAG, "onQueryTextChange : query : " + query);
                    if (query.isEmpty())
                        mAdapter.flushFilter();
                    mAdapter.setFilter(query);
                    return true;
                }
            });

            //TODO : check if it works with previous version of android

            MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_INTENT_GPS_SETTINGS) {
            locationHelper.updateIsGpsEnabled();
            locationHelper.initiateLocationProcess();

        }
    }
}
