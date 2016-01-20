package app.nomad.projects.yashasvi.hotspotsforwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;


/**
 * Created by ygirdha on 1/9/16.
 */
public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.DataObjectHolder> {

    private List<Place> mDataset;
    private List<Place> all_Places;

    private static MyClickListener myClickListener;
    private String data;

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.tvPlaceName.setText(mDataset.get(position).getName());
        String address = mDataset.get(position).getAddress();
        String area = address.substring(address.lastIndexOf(',') + 1) + ", " + mDataset.get(position).getCity();
        holder.tvPlaceArea.setText(area);
        holder.tvPlaceCost.setText(mDataset.get(position).getCost());
        holder.tvPlaceRating.setText(mDataset.get(position).getRating());
        holder.tvPlaceDistance.setText("7.5km");
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

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvPlaceName, tvPlaceArea, tvPlaceDistance, tvPlaceCost, tvPlaceRating;

        public DataObjectHolder(View itemView) {
            super(itemView);
            tvPlaceName = (TextView) itemView.findViewById(R.id.tvPlaceName);
            tvPlaceArea = (TextView) itemView.findViewById(R.id.tvPlaceArea);
            tvPlaceDistance = (TextView) itemView.findViewById(R.id.tvPlaceDistance);
            tvPlaceCost = (TextView) itemView.findViewById(R.id.tvPlaceCost);
            tvPlaceRating = (TextView) itemView.findViewById(R.id.tvPlaceRating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public PlacesRecyclerViewAdapter(List<Place> myDataset) {
        mDataset = myDataset;
        all_Places = myDataset;
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

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public void flushFilter() {
        mDataset = new ArrayList<>();
        mDataset.addAll(all_Places);
        notifyDataSetChanged();
    }

    public void setFilter(String constraint) {

        mDataset = new ArrayList<>();
        constraint = constraint.toString().toLowerCase();
        for (Place item : all_Places) {
            if (item.getName().toLowerCase().contains(constraint))
                mDataset.add(item);
        }
        notifyDataSetChanged();
    }

}
