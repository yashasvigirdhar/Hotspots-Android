package app.nomad.projects.yashasvi.hotspotsforwork.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.nomad.projects.yashasvi.hotspotsforwork.enums.Feedback;
import app.nomad.projects.yashasvi.hotspotsforwork.enums.ImageType;

/**
 * Created by yashasvi on 1/31/16.
 */
public class ServerHelperFunctions {

    private static final String LOG_TAG = "ServerHelperFunctions";

    public static String getImageCacheKey(String place_id, int number, ImageType size) {
        String key = "";
        key += place_id;
        key += "&";
        key += String.valueOf(number);
        key += "&";
        key += String.valueOf(size);
        return key;
    }

    public static String getPlaceImagesObjectUrl(int placeId) {
        return ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + ServerConstants.IMAGES_PATH + placeId;
    }

    public static String getPlacesUrlByCity(String city) {
        return ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + ServerConstants.CITY_PATH + city;
    }


    public static String getPlaceCoverImageUrl(String imagesPath, String placeName) {
        String url = imagesPath + "/" + ServerConstants.COVER_PATH + "/" +
                placeName + ".png";
        url = url.replace(" ", "%20");
        return url;
    }

    public static Bitmap downloadBitmapFromUrl(String url) {
        Bitmap imageBitmap = null;
        try {
            URL u = new URL(url);
            System.out.println("Downloading image " + u.toString());
            imageBitmap = BitmapFactory.decodeStream((InputStream) u.getContent());
            Log.i("image activity", "Download Completed Successfully");

        } catch (MalformedURLException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBitmap;
    }

    public static String getJSON(String url) {
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
                sb.append(line).append("\n");
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

    public static String postJSON(String jsonData, Feedback feedback) {
        int status = 0;
        HttpURLConnection connection = null;
        try {
            String url = null;
            if (feedback == Feedback.APP) {
                url = ServerConstants.SERVER_URL + ServerConstants.APP_FEEDBACK_PATH;
            } else if (feedback == Feedback.PLACE) {
                url = ServerConstants.SERVER_URL + ServerConstants.PLACE_FEEDBACK_PATH;
            }
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(jsonData);
            wr.flush();
            wr.close();

            status = connection.getResponseCode();
            Log.i(LOG_TAG, "status : " + status);
            if (status == HttpURLConnection.HTTP_OK) {
                return String.valueOf(status);
            }

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            Log.e(LOG_TAG, response.toString());
            return response.toString();

        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.toString());
            return String.valueOf(status);
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
}
