package app.nomad.projects.yashasvi.hotspotsforwork;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;


/**
 * Created by ygirdha on 12/22/15.
 */
public class DisplayPlacesAdapter extends ArrayAdapter {

    final public static String TAG = "DisplayPlacesAdapter";

    Context mContext;
    List<Place> places;
    int layoutResourceId;

    public DisplayPlacesAdapter(Context context, int resource, List<Place> places) {
        super(context, resource, places);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.places = places;
    }

    @Override
    public int getCount() {
        Log.i(TAG,"getCount " + places.size());
        return places.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, null);
        }
        Log.i(TAG,"getview method" + row.toString());
        TextView tv = (TextView) row.findViewById(R.id.tvName);
        tv.setText(places.get(position).getName());
        return row;
    }
}
