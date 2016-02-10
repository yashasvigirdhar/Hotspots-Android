package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.CitiesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.Constants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerHelperFunctions;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.UtilFunctions;

public class CitySelectionActivity extends AppCompatActivity implements CitiesRecyclerViewAdapter.MyClickListener {

    final public static String LOG_TAG = "CitySelectionActivity";

    final int REQUEST_CODE = 1;

    private RecyclerView mRecyclerView;
    private CitiesRecyclerViewAdapter mAdapter;

    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cities);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);


        switch (UtilFunctions.checkAppStart(this, sharedPreferences)) {
            case NORMAL:
                // find out which city has been selected previously and proceed to places activity
                String city = sharedPreferences.getString(
                        Constants.SELECTED_CITY, "city");
                goToPlacesActivity(city);
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
        new GetPlacesAsyncTask(ServerHelperFunctions.getPlacesUrlByCity("city")).execute();
    }

    void initialize() {
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
                .putString(Constants.SELECTED_CITY, city).commit();
        goToPlacesActivity(city);
    }

    public void goToPlacesActivity(String city) {
        Intent i = new Intent(this, PlacesListActivity.class);
        i.putExtra("city", city);
        startActivity(i);
    }

    public class GetPlacesAsyncTask extends AsyncTask<Void, Void, String> {

        private String mUrl;

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
            Log.i(LOG_TAG, "on post execute\n" + jsonString);
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            try {
                List<Place> tempPlaces = new Gson().fromJson(jsonString, collectionType);
                for (int i = 0; i < tempPlaces.size(); i++) {
                    Log.i(LOG_TAG, tempPlaces.get(i).toString());
                    places.add(tempPlaces.get(i));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JsonSyntaxException e) {
                //not able to parse response, after requesting all places
            }

        }
    }
}
