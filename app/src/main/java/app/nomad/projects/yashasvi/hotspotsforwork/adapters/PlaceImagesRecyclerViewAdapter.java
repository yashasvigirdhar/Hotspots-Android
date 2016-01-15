package app.nomad.projects.yashasvi.hotspotsforwork.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.R;

/**
 * Created by ygirdha on 1/15/16.
 */
public class PlaceImagesRecyclerViewAdapter extends RecyclerView.Adapter<PlaceImagesRecyclerViewAdapter.PlaceImageRecyclerViewHolder> {

    List<Bitmap> imageBitmaps;


    public PlaceImagesRecyclerViewAdapter(List<Bitmap> bitmaps) {
        this.imageBitmaps = bitmaps;
    }

    @Override
    public PlaceImageRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_image_card, null);
        PlaceImageRecyclerViewHolder holder = new PlaceImageRecyclerViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaceImageRecyclerViewHolder holder, int position) {
        holder.placeImage.setImageBitmap(imageBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    public void swap(List<Bitmap> data){
        imageBitmaps.clear();
        imageBitmaps.addAll(data);
        notifyDataSetChanged();
    }

    class PlaceImageRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView placeImage;

        public PlaceImageRecyclerViewHolder(View itemView) {
            super(itemView);
            placeImage = (ImageView) itemView.findViewById(R.id.ivPlaceImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked Images Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
