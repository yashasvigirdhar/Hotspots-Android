package app.nomad.projects.yashasvi.hotspots.utils;

import android.content.Context;
import android.os.AsyncTask;

import app.nomad.projects.yashasvi.hotspots.enums.ConnectionAvailability;

/**
 * Created by yashasvi on 3/1/16.
 */
public class CheckInternetAsyncTask extends AsyncTask<Void, Void, ConnectionAvailability> {

    Context mContext;

    public CheckInternetAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected ConnectionAvailability doInBackground(Void... params) {
        return UtilFunctions.hasActiveInternetConnection(mContext);
    }

}
