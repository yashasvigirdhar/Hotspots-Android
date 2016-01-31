package app.nomad.projects.yashasvi.hotspotsforwork.utils;

/**
 * Created by ygirdha on 1/31/16.
 */
public class UtilFunctions {

    public static String getImageCacheKey(String place_id, int number, ImageSize size) {
        String key = "";
        key += place_id;
        key += "&";
        key += String.valueOf(number);
        key += "&";
        key += String.valueOf(size);
        return key;
    }
}
