package app.nomad.projects.yashasvi.hotspots.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import app.nomad.projects.yashasvi.hotspots.R;

/**
 * Created by yashasvi on 1/29/16.
 */
public class LocationHelper {

    private final String LOG_TAG = "Locationhelper Activity";
    private final Activity parentActivity;
    private final CoordinatorLayout coordinatorLayout;

    private final GpsListener listener;

    private Location location;

    private double latitude = -1;
    private double longitude = -1;


    // flag for GPS status
    private boolean isGPSEnabled = false;

    private final LocationManager locationManager;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        listener.updateGpsLocation();
    }

    public LocationHelper(Activity activity, LocationManager locationManager, GpsListener listener, CoordinatorLayout coordinatorLayout) {
        this.parentActivity = activity;
        this.listener = listener;
        this.locationManager = locationManager;
        this.coordinatorLayout = coordinatorLayout;
        updateIsGpsEnabled();
    }

    public void updateIsGpsEnabled() {
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void checkPermissionAndgetLocation() {
        int hasGpsPermission = ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasGpsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(parentActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_CODE_GPS_PERMISSIONS);
        }
        getLocationFromSystem();

    }

    public void registerLocationManager(){
        if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_TIME_BW_UPDATES,
                    Constants.MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) parentActivity);
        }
    }
    public void getLocationFromSystem() {
        if (isGPSEnabled) {
            Log.d(LOG_TAG, "GPS Enabled");
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    registerLocationManager();
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        setLocation(location);
                    }
                }
            }
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "You need to enable gps to show distances", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            parentActivity.startActivityForResult(intent, Constants.REQUEST_CODE_INTENT_GPS_SETTINGS);
                        }
                    })
                    .setActionTextColor(parentActivity.getResources().getColor(R.color.colorPrimary));
            snackbar.show();
        }
    }

    public interface GpsListener {
        void updateGpsLocation();
    }

}
