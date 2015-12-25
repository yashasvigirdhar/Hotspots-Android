package app.nomad.projects.yashasvi.hotspotsforwork;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    final public static String serverUrl = "http://192.168.0.126:8080/findplace/api/places";
    final public static String TAG = "MainActivity";

    Button bGetResults;
    ListView listview;

    DisplayPlacesAdapter displayPlacesAdapter;


    List<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intialize();
    }

    private void intialize() {
        bGetResults = (Button) findViewById(R.id.bGetPlacesFromServer);
        listview = (ListView) findViewById(R.id.listPlaces);
        places = new ArrayList<Place>();
        displayPlacesAdapter = new DisplayPlacesAdapter(getBaseContext(), R.layout.places_listview_item, places);
        listview.setAdapter(displayPlacesAdapter);
        bGetResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestAsyncTask testAsyncTask = new TestAsyncTask(serverUrl);
                testAsyncTask.execute();
            }
        });

        listview.setOnItemClickListener(this);
    }

    public String getJSON(String url) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.connect();
            int status = connection.getResponseCode();
            Log.i(TAG, "status : " + status);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();

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

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PlaceActivity.class);
        intent.putExtra("place", places.get(position));
        startActivity(intent);
    }

    public class TestAsyncTask extends AsyncTask<Void, Void, String> {
        private String mUrl;

        public TestAsyncTask(String url) {
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultString = null;
            resultString = getJSON(mUrl);
            return resultString;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            places = (List<Place>) new Gson().fromJson(jsonString, collectionType);
            displayPlacesAdapter = new DisplayPlacesAdapter(getBaseContext(), R.layout.places_listview_item, places);
            listview.setAdapter(displayPlacesAdapter);
            //displayPlacesAdapter.notifyDataSetChanged();
            for (int i = 0; i < places.size(); i++)
                Log.i(TAG, places.get(i).toString());
        }
    }
}
