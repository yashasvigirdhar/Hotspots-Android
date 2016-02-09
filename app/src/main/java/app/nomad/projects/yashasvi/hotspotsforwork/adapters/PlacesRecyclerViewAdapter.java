package app.nomad.projects.yashasvi.hotspotsforwork.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.activities.PlaceViewActivity;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;


/**
 * Created by yashasvi on 1/9/16.
 */
public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.DataObjectHolder> implements View.OnClickListener {

    public final static String LOG_TAG = "PlacesRecyclerAdapter";

    Context mContext;
    private List<Place> mDataset;
    private List<Place> all_Places;
    private List<Float> distances;

    public static OnPlaceClickedListener onPlaceClickedListener;

    public PlacesRecyclerViewAdapter(List<Place> myDataset, List<Float> distances, Context mContext) {
        this.mDataset = myDataset;
        this.all_Places = myDataset;
        this.distances = distances;
        this.mContext = mContext;
    }

    public void setOnPlaceClickedListener(OnPlaceClickedListener onPlaceClickedListener) {
        this.onPlaceClickedListener = onPlaceClickedListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card, parent, false);

        return new DataObjectHolder(view);
    }


    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {
        holder.tvPlaceName.setText(mDataset.get(position).getName());
        String address = mDataset.get(position).getAddress();
        String area = address.substring(address.lastIndexOf(',') + 1) + ", " + mDataset.get(position).getCity();
        holder.tvPlaceArea.setText(area);
        holder.tvPlaceCost.setText(mContext.getString(R.string.cost_per_person) + mDataset.get(position).getCost());
        holder.tvPlaceRating.setText(mDataset.get(position).getRating());
        if (distances.size() > position)
            holder.tvPlaceDistance.setText(String.format("%.2f", distances.get(position)) + " km");
        else
            holder.tvPlaceDistance.setText("-");

        holder.tvPlaceName.setOnClickListener(this);
        holder.tvPlaceName.setTag(position);

        holder.llNavigate.setOnClickListener(this);
        holder.llNavigate.setTag(position);

        holder.llCall.setOnClickListener(this);
        holder.llCall.setTag(position);
    }

    public String getFilteredData() {
        String data = "";
        for (int i = 0; i < mDataset.size(); i++) {
            data += mDataset.get(i).getName();
            data += " , ";
        }
        return data;
    }

    public String getOriginalData() {
        String data = "";
        for (int i = 0; i < all_Places.size(); i++) {
            data += all_Places.get(i).getName();
            data += " , ";
        }
        return data;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Intent i;
        switch (v.getId()) {
            case R.id.tvPlaceName:
                i = new Intent(mContext, PlaceViewActivity.class);
                i.putExtra("place", mDataset.get(position));
                mContext.startActivity(i);
                break;
            case R.id.llPlaceCardCall:
                i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + mDataset.get(position).getPhone()));
                try {
                    mContext.startActivity(i);
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.e(LOG_TAG, ex.toString());
                    Toast.makeText(mContext, "your Activity is not founded", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.llPlaceCardNavigate:
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f", Double.parseDouble(mDataset.get(position).getLatitude()), Double.parseDouble(mDataset.get(position).getLongitude()));
                //Uri gmmIntentUri = Uri.parse("google.navigation:q=12.939074,77.612976");
                i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                i.setPackage("com.google.android.apps.maps");
                if (i.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(i);
                } else {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        mContext.startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(mContext, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvPlaceName, tvPlaceArea, tvPlaceDistance, tvPlaceCost, tvPlaceRating;
        LinearLayout llNavigate, llCall;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            tvPlaceArea = (TextView) itemView.findViewById(R.id.tvPlaceArea);
            tvPlaceDistance = (TextView) itemView.findViewById(R.id.tvPlaceDistance);
            tvPlaceCost = (TextView) itemView.findViewById(R.id.tvPlaceCost);
            tvPlaceRating = (TextView) itemView.findViewById(R.id.tvPlaceRating);
            llNavigate = (LinearLayout) itemView.findViewById(R.id.llPlaceCardNavigate);
            llCall = (LinearLayout) itemView.findViewById(R.id.llPlaceCardCall);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPlaceClickedListener.onPlaceClicked(getAdapterPosition(),v);
        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(int position, Place dataObj) {
        mDataset.add(position, dataObj);
        notifyItemInserted(position);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Place place = mDataset.remove(fromPosition);
        mDataset.add(toPosition, place);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void flushFilter() {
        mDataset = new ArrayList<>();
        mDataset.addAll(all_Places);
        notifyDataSetChanged();
    }

    public void setFilter(String constraint) {

        mDataset = new ArrayList<>();
        constraint = constraint.toLowerCase();
        for (Place item : all_Places) {
            if (item.getName().toLowerCase().contains(constraint))
                mDataset.add(item);
        }
        notifyDataSetChanged();
    }

    public interface OnPlaceClickedListener {
        void onPlaceClicked(int position, View v);
    }
}
