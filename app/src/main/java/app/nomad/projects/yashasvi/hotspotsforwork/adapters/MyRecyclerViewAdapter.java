package app.nomad.projects.yashasvi.hotspotsforwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.models.Place;


/**
 * Created by ygirdha on 1/9/16.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder> {

    private ArrayList<Place> mDataset;
    private static MyClickListener myClickListener;

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.tvPlaceName.setText(mDataset.get(position).getName());
        String address = mDataset.get(position).getAddress();
        String area = address.substring(address.lastIndexOf(',') + 1) + ", " + mDataset.get(position).getCity();
        holder.tvPlaceArea.setText(area);
        holder.tvPlaceCost.append(mDataset.get(position).getCost());
        holder.tvPlaceRating.setText(mDataset.get(position).getRating());
        holder.tvPlaceDistance.setText("7.5km");
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

    public MyRecyclerViewAdapter(ArrayList<Place> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addItem(Place dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
