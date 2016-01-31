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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.MyApplication;
import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.adapters.PlaceImagesRecyclerViewAdapter;
import app.nomad.projects.yashasvi.hotspotsforwork.models.PlaceImages;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ImageSize;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.UtilFunctions;

public class PlaceImagesActivity extends AppCompatActivity {

    private static final String TAG = "PlaceImageActivity";

    String imagesUrl;
    String place_id;
    String place_name;

    int imagesCount;

    RecyclerView placeImagesRecyclerView;
    PlaceImagesRecyclerViewAdapter placeImagesRecyclerViewAdapter;
    GridLayoutManager placeLayoutGridLayoutManager;
    List<Bitmap> placeImageBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_place_images);
        initialize();
        new downloadImageAsyncTask(imagesUrl).execute();
    }

    void initialize() {
        place_id = getIntent().getStringExtra("place_id");
        place_name = getIntent().getStringExtra("place_name");

        placeImagesRecyclerView = (RecyclerView) findViewById(R.id.rvPlaceImages);

        placeLayoutGridLayoutManager = new GridLayoutManager(this, 3);
        placeImagesRecyclerView.setLayoutManager(placeLayoutGridLayoutManager);

        placeImageBitmaps = new ArrayList<>();
        placeImagesRecyclerViewAdapter = new PlaceImagesRecyclerViewAdapter(this, placeImageBitmaps, place_id, place_name, imagesCount);
        placeImagesRecyclerView.setAdapter(placeImagesRecyclerViewAdapter);

        imagesUrl = ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + ServerConstants.IMAGES_PATH + place_id;
    }

    private Bitmap download(String url) {
        Bitmap imageBitmap = null;
        try {
            URL u = new URL(url);
            System.out.println("Downloading image " + u.toString());
            imageBitmap = BitmapFactory.decodeStream((InputStream) u.getContent());
            Log.i("image activity", "Download Completed Successfully");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return imageBitmap;
    }

    private PlaceImages getPlaceImages(String url) {
        PlaceImages placeImages = null;
        HttpURLConnection connection;
        try {
            //get PlaceImages object
            URL u = new URL(url);
            Log.i(TAG, "getting place images " + u.toString());
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (placeImages != null)
            Log.i(TAG, placeImages.toString());
        return placeImages;
    }

    public class downloadImageAsyncTask extends AsyncTask<Void, Object, Void> {

        String url;

        downloadImageAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            PlaceImages placeImages = getPlaceImages(url);
            imagesCount = placeImages.getCount();
            String path;
            for (int i = 0; i < imagesCount; i++) {
                path = placeImages.getPath() + ServerConstants.THUMBNAILS_PATH + "/" + place_name + i + ".png";
                path = path.replace(" ", "%20");
                Log.i(TAG, path);
                Bitmap bt = ((MyApplication) getApplication()).getBitmapFromCache(UtilFunctions.getImageCacheKey(place_id, i, ImageSize.THUMBNAIL));
                if (bt == null) {
                    Log.i(TAG, "bitmap not present in cache " + i);
                    bt = download(path);
                }
                publishProgress(bt, i);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... params) {
            Bitmap bitmap = (Bitmap) params[0];
            int number = (Integer) params[1];

            super.onProgressUpdate(bitmap);

            placeImageBitmaps.add(bitmap);
            placeImagesRecyclerViewAdapter.notifyDataSetChanged();
            placeImagesRecyclerViewAdapter.updateImagesCount(imagesCount);
            ((MyApplication) getApplication()).putBitmapInCache(UtilFunctions.getImageCacheKey(place_id, number, ImageSize.THUMBNAIL), bitmap);
        }

    }


}
