package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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
import app.nomad.projects.yashasvi.hotspotsforwork.fragments.FragmentDrawer;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.UtilFunctions;

public class CitySelectionActivity extends AppCompatActivity implements CitiesRecyclerViewAdapter.MyClickListener, FragmentDrawer.FragmentDrawerListener {

    final public static String LOG_TAG = "CitySelectionActivity";

    private RecyclerView mRecyclerView;
    private CitiesRecyclerViewAdapter mAdapter;

    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cities);
        initialize();
        getCities();
    }

    private void getCities() {
        new GetPlacesAsyncTask(UtilFunctions.getPlacesUrlByCity("city")).execute();
    }

    void initialize() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarCityList);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.select_city);

        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

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

    @Override
    public void onItemClick(int position, View v) {
        Intent i = new Intent(this, PlacesListActivity.class);
        i.putExtra("city", places.get(position).getName());
        startActivity(i);
    }


    public class GetPlacesAsyncTask extends AsyncTask<Void, Void, String> {

        private String mUrl;

        public GetPlacesAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            return UtilFunctions.getJSON(mUrl);
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            Log.i(LOG_TAG, "on post execute\n" + jsonString);
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            try {
                places = new Gson().fromJson(jsonString, collectionType);
                for (int i = 0; i < places.size(); i++)
                    Log.i(LOG_TAG, places.get(i).toString());
                mAdapter = new CitiesRecyclerViewAdapter(places);
                mRecyclerView.setAdapter(mAdapter);
            } catch (JsonSyntaxException e) {
                //not able to parse response, after requesting all places
            }

        }
    }
}
