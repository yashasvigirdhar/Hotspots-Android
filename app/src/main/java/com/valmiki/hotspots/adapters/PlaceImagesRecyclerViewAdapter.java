package com.valmiki.hotspots.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.valmiki.hotspots.R;
import com.valmiki.hotspots.activities.FullscreenPlaceImagesActivity;

import java.util.List;

/**
 * Created by yashasvi on 1/15/16.
 */
public class PlaceImagesRecyclerViewAdapter extends RecyclerView.Adapter<PlaceImagesRecyclerViewAdapter.PlaceImageRecyclerViewHolder> implements View.OnClickListener {

    private final String LOG_TAG = "ImagesRecyclerAdapter";
    private final Activity mActivity;
    private final List<Bitmap> imageBitmaps;
    private final String place_id;
    private final String place_name;
    private final String imagesPath;
    private int imagesCount;

    Tracker analyticsTracker;

    public PlaceImagesRecyclerViewAdapter(Activity activity, List<Bitmap> bitmaps, String place_id, String place_name, String imagesPath, int imagesCount) {
        this.mActivity = activity;
        this.imageBitmaps = bitmaps;
        this.place_id = place_id;
        this.place_name = place_name;
        this.imagesCount = imagesCount;
        this.imagesPath = imagesPath;
    }

    public void setAnalyticsTracker(Tracker analyticsTracker) {
        this.analyticsTracker = analyticsTracker;
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
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(mActivity.getString(R.string.anaylitics_click_image))
                        .setLabel("Image Clicked")
                        .build());
                i = new Intent(mActivity, FullscreenPlaceImagesActivity.class);
                Log.i(LOG_TAG, "starting full screen activity " + place_id + " " + imagesCount + " " + position);
                i.putExtra("place_id", place_id);
                i.putExtra("place_name", place_name);
                i.putExtra("images_path", imagesPath);
                i.putExtra("images_count", imagesCount);
                i.putExtra("position", position);
                i.putExtra("image_type", 0);
                mActivity.startActivity(i);
        }
    }

    class PlaceImageRecyclerViewHolder extends RecyclerView.ViewHolder {

        public final ImageView placeImage;

        public PlaceImageRecyclerViewHolder(View itemView) {
            super(itemView);
            placeImage = (ImageView) itemView.findViewById(R.id.ivPlaceImage);
        }
    }
}
