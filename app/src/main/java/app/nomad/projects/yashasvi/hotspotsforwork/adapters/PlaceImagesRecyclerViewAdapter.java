package app.nomad.projects.yashasvi.hotspotsforwork.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.activities.FullscreenPlaceImagesActivity;

/**
 * Created by yashasvi on 1/15/16.
 */
public class PlaceImagesRecyclerViewAdapter extends RecyclerView.Adapter<PlaceImagesRecyclerViewAdapter.PlaceImageRecyclerViewHolder> implements View.OnClickListener {

    private final String LOG_TAG = "ImagesRecyclerAdapter";
    Activity mActivity;
    List<Bitmap> imageBitmaps;
    String place_id;
    String place_name;
    int imagesCount;


    public PlaceImagesRecyclerViewAdapter(Activity activity, List<Bitmap> bitmaps, String place_id, String place_name, int imagesCount) {
        this.mActivity = activity;
        this.imageBitmaps = bitmaps;
        this.place_id = place_id;
        this.place_name = place_name;
        this.imagesCount = imagesCount;
    }

    public void updateImagesCount(int imagesCount) {
        this.imagesCount = imagesCount;
    }

    @Override
    public PlaceImageRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_image_card, null);
        return new PlaceImageRecyclerViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(PlaceImageRecyclerViewHolder holder, int position) {
        holder.placeImage.setImageBitmap(imageBitmaps.get(position));
        holder.placeImage.setTag(position);
        holder.placeImage.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Intent i;
        switch (v.getId()) {
            case R.id.ivPlaceImage:
                i = new Intent(mActivity, FullscreenPlaceImagesActivity.class);
                Log.i(LOG_TAG, "starting full screen activity " + place_id + " " + imagesCount + " " + position);
                i.putExtra("place_id", place_id);
                i.putExtra("place_name", place_name);
                i.putExtra("images_count", imagesCount);
                i.putExtra("position", position);
                mActivity.startActivity(i);
        }
    }

    class PlaceImageRecyclerViewHolder extends RecyclerView.ViewHolder {

        public ImageView placeImage;

        public PlaceImageRecyclerViewHolder(View itemView) {
            super(itemView);
            placeImage = (ImageView) itemView.findViewById(R.id.ivPlaceImage);
        }
    }
}
