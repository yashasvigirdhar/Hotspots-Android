package app.nomad.projects.yashasvi.hotspotsforwork;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.adapters.MyRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    final public static String LOG_TAG = "MainActivity";


    private Toolbar mToolbar;
    private Button bGetResults;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bGetResults = (Button) findViewById(R.id.bGetPlacesFromServer);
        places = new ArrayList<Place>();

        bGetResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestAsyncTask testAsyncTask = new TestAsyncTask(ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + "places");
                testAsyncTask.execute();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(places);
        mRecyclerView.setAdapter(mAdapter);
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
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
            Log.i(LOG_TAG,"on post execute\n" + jsonString);
            Type collectionType = new TypeToken<List<Place>>() {
            }.getType();
            try {
                places = (ArrayList<Place>) new Gson().fromJson(jsonString, collectionType);
                mAdapter = new MyRecyclerViewAdapter(places);
                mRecyclerView.setAdapter(mAdapter);
                //displayPlacesAdapter.notifyDataSetChanged();
                for (int i = 0; i < places.size(); i++)
                    Log.i(LOG_TAG, places.get(i).toString());
            }
            catch (JsonSyntaxException e){
                //not able to parse response, after requesting all places
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
