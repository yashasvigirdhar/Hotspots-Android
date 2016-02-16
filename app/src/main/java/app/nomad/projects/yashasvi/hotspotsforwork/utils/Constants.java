package app.nomad.projects.yashasvi.hotspotsforwork.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yashasvi on 1/29/16.
 */
public class Constants {

    final public static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    final public static int REQUEST_CODE_INTENT_GPS_SETTINGS = 2;

    // The minimum distance to change updates in metters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // The minimum time beetwen updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 0;

    public static final String NEW_PLACE_GOOGLE_FORM = "https://docs.google.com/forms/d/1UcSgyQvyoGvYuH_cNQh6LYhuybTEFMv-jpvx-fUR5Tk/viewform";

    public static Map<Integer, String> wifiSpeedLevel = new HashMap<>();

    public static Map<Integer, String> chargingPointsLevel = new HashMap<>();

    public static Map<Integer, String> days = new HashMap<>();

    /**
     * The app version code (not the version name!) that was used on the last
     * start of the app.
     */
    public static final String LAST_APP_VERSION = "last_app_version";

    public static final String SELECTED_CITY = "1";

    public static final String PLACE_SHARE_TEXT = "Hey ! I came across this place called %s. It has wifi rating of %s. If you want " +
            "other information about the place, download this app from play store : " + "play store link";

    public static final String IMAGE_SHARE_TEXT = "Hey ! Have a look at this awesome picture of a place called %s. If you want " +
            "other information about the place download this app from play store : " + "play store link";

}
