package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.PlacesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.contentProviders.PlaceSearchSuggestionProvider;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;


public class PlacesListActivity extends AppCompatActivity implements PlacesRecyclerViewAdapter.MyClickListener {

    final public static String LOG_TAG = "PlacesListActivity";


    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private PlacesRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_places);
        handleIntent(getIntent());
        initialize();
        GetPlacesAsyncTask getPlacesAsyncTask = new GetPlacesAsyncTask(ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + "places");
        getPlacesAsyncTask.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(LOG_TAG, "onNewIntent");
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.i(LOG_TAG, "handleIntent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.i(LOG_TAG, "search intent");
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(LOG_TAG, "query : " + query);
            Log.i(LOG_TAG, "adding to search suggestions");
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    PlaceSearchSuggestionProvider.AUTHORITY, PlaceSearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);


            if (query.isEmpty())
                mAdapter.flushFilter();

            mAdapter.setFilter(query);
            Log.i(LOG_TAG, "filtered data : " + mAdapter.getFilteredData());
            Log.i(LOG_TAG, "original data : " + mAdapter.getOriginalData());
        }
    }

    private void initialize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        places = new ArrayList<>();


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PlacesRecyclerViewAdapter(places);
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

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onItemClick(int position, View v) {
        Intent intent = new Intent(this, PlaceViewActivity.class);
        intent.putExtra("place", places.get(position));
        startActivity(intent);
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
            String resultString = null;
            resultString = getJSON(mUrl);
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
                mAdapter = new PlacesRecyclerViewAdapter(places);
                mRecyclerView.setAdapter(mAdapter);
                //displayPlacesAdapter.notifyDataSetChanged();
                for (int i = 0; i < places.size(); i++)
                    Log.i(LOG_TAG, places.get(i).toString());
            } catch (JsonSyntaxException e) {
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
        if (id == R.id.action_feedback) {
            Intent i = new Intent(this, AppFeedbackActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
                    Log.i(LOG_TAG,"filtered data : " + mAdapter.getFilteredData());
                    Log.i(LOG_TAG, "original data : " + mAdapter.getOriginalData());
                    Log.i(LOG_TAG, "query : " + query);
                    Log.i(LOG_TAG, "adding to search suggestions");
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getBaseContext(),
                            PlaceSearchSuggestionProvider.AUTHORITY, PlaceSearchSuggestionProvider.MODE);
                    suggestions.saveRecentQuery(query, null);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    Log.i(LOG_TAG, "onQueryTextChange : query : " + query);

                    if (query.isEmpty())
                        mAdapter.flushFilter();
                    mAdapter.setFilter(query);
                    Log.i(LOG_TAG,"filtered data : " + mAdapter.getFilteredData());
                    Log.i(LOG_TAG, "original data : " + mAdapter.getOriginalData());
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

}
