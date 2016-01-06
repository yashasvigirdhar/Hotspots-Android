package app.nomad.projects.yashasvi.hotspotsforwork;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;
import app.nomad.projects.yashasvi.hotspotsforwork.models.PlaceImages;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;

public class PlaceActivity extends AppCompatActivity {

    public static final String TAG = "PlaceActivity";
    public int imagesCount = 11;

    TextView tvName;
    TextView tvAmbiance;
    TextView tvFood;
    TextView tvService;
    TextView tvDescription;
    TextView tvWifiSpeed;
    TextView tvWifiPaid;
    TextView tvChargingPoints;


    LinearLayout llGallery;

    Place place = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_detailed_view);
        place = (Place) getIntent().getExtras().getParcelable("place");
        initialize();

        if (place != null) {
            populate();
        }
    }

    private void DownloadImageBitmaps() {
        new downloadImageAsyncTask().execute();
    }

    private void populate() {
        tvName.setText(place.getName());

        tvWifiSpeed.append(String.valueOf(place.getWifiSpeed()));
        tvAmbiance.append(String.valueOf(place.getAmbiance()));
        tvFood.append(String.valueOf(place.getFood()));
        tvService.append(String.valueOf(place.getService()));
        tvDescription.setText(place.getDescription());
        tvWifiPaid.append(place.getWifiPaid());
        tvChargingPoints.append(place.getChargingPoints());

        DownloadImageBitmaps();
    }

    private void initialize() {
        tvName = (TextView) findViewById(R.id.tvName);

        tvWifiSpeed = (TextView) findViewById(R.id.tvWifiSpeed);
        tvAmbiance = (TextView) findViewById(R.id.tvAmbiance);
        tvFood = (TextView) findViewById(R.id.tvFood);
        tvService = (TextView) findViewById(R.id.tvService);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvWifiPaid = (TextView) findViewById(R.id.tvWifiPaid);
        tvChargingPoints = (TextView) findViewById(R.id.tvChargingPoints);

        llGallery = (LinearLayout) findViewById(R.id.llGallery);

    }


    private Bitmap download(URL u) {
        System.out.println("Inside Download");
        Bitmap imageBitmap = null;
        try {

            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoInput(true);
            c.connect();

            InputStream is = c.getInputStream();
            Log.i("log_tag", " InputStream consist of : " + is.read());
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

        List<URL> imageUrls;

        @Override
        protected Void doInBackground(Void... params) {
            PlaceImages placeImages = null;
            HttpURLConnection connection = null;
            try {
                //get PlaceImages object
                URL u = new URL(ServerConstants.SERVER_GET_IMAGES_URL + place.getId());
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
                Log.i(TAG, placeImages.toString());

                //form urls
                imageUrls = new ArrayList<>();
                String path;
                for (int i = 1; i <= placeImages.getCount(); i++) {
                    path = placeImages.getPath() + "/" + place.getName() + i + ".png";
                    path = path.replace(" ", "%20");
                    Log.i(TAG, path);
                    imageUrls.add(new URL(path));
                }

                //download images
                for (int i = 0; i < imageUrls.size(); i++) {
                    Bitmap bt = download(imageUrls.get(i));
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
            llGallery.addView(getImageViewFromBitmap(bitmaps[0]));
        }

    }

    private View getImageViewFromBitmap(Bitmap bitmap) {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(5, 5, 5, 5);
        layout.setLayoutParams(params);
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);
        layout.addView(imageView);
        return layout;

    }

}
