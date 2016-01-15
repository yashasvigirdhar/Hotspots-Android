package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.PlaceImagesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.models.PlaceImages;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;

public class PlaceImagesActivity extends AppCompatActivity {

    private static final String TAG = "PlaceImageActivity";

    String imagesUrl;

    RecyclerView placeImagesRecyclerView;
    PlaceImagesRecyclerViewAdapter placeImagesRecyclerViewAdapter;
    GridLayoutManager placeLayoutGridLayoutManager;
    List<Bitmap> placeImageBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_images);
        initialize();
        new downloadImageAsyncTask(imagesUrl).execute();
    }

    void initialize(){
        placeImagesRecyclerView = (RecyclerView) findViewById(R.id.rvPlaceImages);

        placeLayoutGridLayoutManager = new GridLayoutManager(this, 3);
        placeImagesRecyclerView.setLayoutManager(placeLayoutGridLayoutManager);

        placeImageBitmaps = new ArrayList<>();
        placeImagesRecyclerViewAdapter = new PlaceImagesRecyclerViewAdapter(placeImageBitmaps);
        placeImagesRecyclerView.setAdapter(placeImagesRecyclerViewAdapter);

        String place_id = getIntent().getStringExtra("place_id");
        imagesUrl = ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + ServerConstants.IMAGES_PATH + place_id;
    }

    private Bitmap download(URL u) {
        System.out.println("Downloading image " + u.toString());
        Bitmap imageBitmap = null;
        try {

            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoInput(true);
            c.connect();

            InputStream is = c.getInputStream();
            imageBitmap = BitmapFactory.decodeStream((InputStream) u.getContent());

            Log.i("image activity", "Download Completed Successfully");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return imageBitmap;
    }

    public class downloadImageAsyncTask extends AsyncTask<Void, Bitmap, Void> {

        String url;

        downloadImageAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            PlaceImages placeImages = null;
            HttpURLConnection connection = null;
            try {
                //get PlaceImages object
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

                placeImages = new Gson().fromJson(sb.toString(), PlaceImages.class);
                //TODO : check here if placeImages is null
                Log.i(TAG, placeImages.toString());


                String path;
                for (int i = 1; i <= placeImages.getCount(); i++) {
                    path = placeImages.getPath() + "Tea Everyday" + i + ".png";
                    path = path.replace(" ", "%20");
                    Log.i(TAG, path);
                    Bitmap bt = download(new URL(path));
                    publishProgress(bt);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... bitmaps) {
            super.onProgressUpdate(bitmaps[0]);
            placeImageBitmaps.add(bitmaps[0]);
            placeImagesRecyclerViewAdapter.notifyDataSetChanged();
        }

    }


}
