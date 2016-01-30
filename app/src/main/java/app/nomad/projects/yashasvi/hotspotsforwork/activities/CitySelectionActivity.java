package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.CitiesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;

public class CitySelectionActivity extends AppCompatActivity implements CitiesRecyclerViewAdapter.MyClickListener {

    final public static String LOG_TAG = "CitySelectionActivity";

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private CitiesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cities);
        initialize();
        GetPlacesAsyncTask getPlacesAsyncTask = new GetPlacesAsyncTask(ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + ServerConstants.CITY_PATH + "city");
        getPlacesAsyncTask.execute();
    }

    void initialize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.select_city);

        places = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCities);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CitiesRecyclerViewAdapter(places);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

    }

    public String getJSON(String url) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.connect();
            int status = connection.getResponseCode();
            Log.i(LOG_TAG, "status : " + status);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();

        } catch (ConnectException ex) {
            return ex.toString();
        } catch (Exception ex) {
            return ex.toString();
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    //disconnect error
                }
            }
        }
    }

    @Override
    public void onItemClick(int position, View v) {
        Intent i = new Intent(this,PlacesListActivity.class);
        i.putExtra("city",places.get(position).getName());
        startActivity(i);
    }


    public class GetPlacesAsyncTask extends AsyncTask<Void, Void, String> {

        private String mUrl;

        public GetPlacesAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString = getJSON(mUrl);
            return resultString;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            Log.i(LOG_TAG, "on post execute\n" + jsonString);
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            try {
                places = (ArrayList<Place>) new Gson().fromJson(jsonString, collectionType);
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
