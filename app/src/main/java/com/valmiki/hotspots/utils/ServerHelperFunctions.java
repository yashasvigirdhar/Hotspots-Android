package com.valmiki.hotspots.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.valmiki.hotspots.enums.FeedbackType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yashasvi on 1/31/16.
 */
public class ServerHelperFunctions {

    private static final String LOG_TAG = "ServerHelperFunctions";

    public static String getPlacesUrlByCity(String city) {
        return ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + ServerConstants.CITY_PATH + city;
    }

    public static String getTimingsUriFromId(String id) {
        return ServerConstants.SERVER_URL + ServerConstants.REST_API_PATH + ServerConstants.TIMINGS_BY_ID + id;
    }

    public static String getPlaceCoverImageUrl(String imagesPath, String placeName) {
        String url = ServerConstants.SERVER_URL + imagesPath + ServerConstants.PLACE_PATH + ServerConstants.COVER_PATH +
                placeName + ".png";
        url = url.replace(" ", "%20");
        return url;
    }

    public static Bitmap downloadBitmapFromUrl(String url, Tracker analyticsTracker) {
        Bitmap imageBitmap = null;
        try {
            URL u = new URL(url);
            Log.i(LOG_TAG, "Downloading image " + u.toString());
            imageBitmap = BitmapFactory.decodeStream((InputStream) u.getContent());
            Log.i("image activity", "Download Completed Successfully");

        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            analyticsTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(e.toString())
                    .setFatal(true)
                    .build());
        }
        return imageBitmap;
    }

    public static String getJSON(String url, Tracker analyticsTracker) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.connect();
            int status = connection.getResponseCode();
            Log.i(LOG_TAG, String.format("status for %s : %s", url, status));
            if (status == HttpURLConnection.HTTP_NO_CONTENT) {
                return String.valueOf(HttpURLConnection.HTTP_NO_CONTENT);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            if (connection != null)
                connection.disconnect();
            return sb.toString();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            analyticsTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(e.toString())
                    .setFatal(true)
                    .build());
            return e.toString();
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

    public static String postJSON(String jsonData, FeedbackType feedbackType, Tracker analyticsTracker) {
        int status = 0;
        HttpURLConnection connection = null;
        try {
            String url = null;
            if (feedbackType == FeedbackType.APP) {
                url = ServerConstants.SERVER_URL + ServerConstants.APP_FEEDBACK_PATH;
            } else if (feedbackType == FeedbackType.PLACE) {
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
            Log.i(LOG_TAG, String.format("status for %s : %s", url, status));
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
            if (connection != null)
                connection.disconnect();
            return response.toString();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            analyticsTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(e.toString())
                    .setFatal(true)
                    .build());
            return e.toString();
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
