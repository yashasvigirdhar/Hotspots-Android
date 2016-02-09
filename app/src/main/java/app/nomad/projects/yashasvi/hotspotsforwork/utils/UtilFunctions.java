package app.nomad.projects.yashasvi.hotspotsforwork.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import app.nomad.projects.yashasvi.hotspotsforwork.enums.AppStart;

/**
 * Created by yashasvi on 2/4/16.
 */
public class UtilFunctions {

    final static String LOG_TAG = "UtilFunctions";

    public static AppStart checkAppStart(Context context, SharedPreferences sharedPreferences) {
        PackageInfo pInfo;
        AppStart appStart = AppStart.NORMAL;
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            int lastVersionCode = sharedPreferences.getInt(
                    Constants.LAST_APP_VERSION, -1);
            // String versionName = pInfo.versionName;
            int currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);

            // Update version in preferences
            sharedPreferences.edit()
                    .putInt(Constants.LAST_APP_VERSION, currentVersionCode).commit(); // must use commit here or app may not update prefs in time and app will loop into walkthrough
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(LOG_TAG,
                    "Unable to determine current app version from package manager. Defensively assuming normal app start.");
        }
        return appStart;
    }

    public static AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            Log.w(LOG_TAG, "Current version code (" + currentVersionCode
                    + ") is less then the one recognized on last startup ("
                    + lastVersionCode
                    + "). Defensively assuming normal app start.");
            return AppStart.NORMAL;
        } else {
            return AppStart.NORMAL;
        }
    }

}
