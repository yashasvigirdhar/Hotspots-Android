package app.nomad.projects.yashasvi.hotspotsforwork.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yashasvi on 1/29/16.
 */
public class LocationHelper {

    String LOG_TAG = "Locationhelper Activity";
    Activity parentActivity;
    GpsListener listener;

    double latitude = -1, longitude = -1;

    // flag for GPS status
    boolean isGPSEnabled = false;

    LocationManager locationManager;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        listener.updateGpsLocation();
    }

    Location location;

    public LocationHelper(Activity activity, LocationManager locationManager, GpsListener listener) {
        this.parentActivity = activity;
        this.listener = listener;
        this.locationManager = locationManager;
        updateIsGpsEnabled();
    }

    public void updateIsGpsEnabled(){
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void initiateLocationProcess() {
        try {
            // getting GPS status

            int hasGpsPermission = ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasGpsPermission != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(parentActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    showMessageOKCancel("You need to allow access to Contacts",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(parentActivity,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            Constants.REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            });
                    return;
                }
                ActivityCompat.requestPermissions(parentActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setLocation();
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(parentActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS not enabled");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                parentActivity.startActivityForResult(intent, Constants.REQUEST_CODE_INTENT_GPS_SETTINGS);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void setLocation() {
        if (isGPSEnabled) {
            Log.d(LOG_TAG, "GPS Enabled");
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_TIME_BW_UPDATES,
                            Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) parentActivity);
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        setLocation(location);
                    }

                } else {
                    Toast.makeText(parentActivity, "You need to enable gps to show distances", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            showSettingsAlert();
        }
    }

    public interface GpsListener {
        void updateGpsLocation();
    }

}
