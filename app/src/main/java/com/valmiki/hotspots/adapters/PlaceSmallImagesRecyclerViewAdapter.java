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
import com.valmiki.hotspots.activities.PlaceImagesActivity;

import java.util.List;

/**
 * Created by yashasvi on 1/15/16.
 */
public class PlaceSmallImagesRecyclerViewAdapter extends RecyclerView.Adapter<PlaceSmallImagesRecyclerViewAdapter.PlaceImageRecyclerViewHolder> implements View.OnClickListener {

    private final String LOG_TAG = "SmallImageRecyclAdapter";
    private final Activity mActivity;
    private final List<Bitmap> imageBitmaps;
    private final String place_id;
    private final String place_name;

    private String imagesPath;

    private int imagesCount = 0;

    Tracker analyticsTracker;

    public PlaceSmallImagesRecyclerViewAdapter(Activity activity, List<Bitmap> bitmaps, String place_id, String place_name) {
        this.mActivity = activity;
        this.imageBitmaps = bitmaps;
        this.place_id = place_id;
        this.place_name = place_name;
    }

    public void setAnalyticsTracker(Tracker analyticsTracker) {
        this.analyticsTracker = analyticsTracker;
    }

    public void updateImagesCount(int imagesCount) {
        this.imagesCount = imagesCount;
    }

    public void updateImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    @Override
    public PlaceImageRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_photos_row_card, parent, false);
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
        Intent i;
        switch (v.getId()) {
            case R.id.ivPlaceSmallImage:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(LOG_TAG)
                        .setAction(mActivity.getString(R.string.anaylitics_click_image))
                        .setLabel("Small Image Clicked")
                        .build());
                i = new Intent(mActivity, PlaceImagesActivity.class);
                i.putExtra("place_id", place_id);
                i.putExtra("place_name", place_name);
                i.putExtra("images_count", imagesCount);
                i.putExtra("images_path", imagesPath);
                mActivity.startActivity(i);
                break;
        }
    }

    class PlaceImageRecyclerViewHolder extends RecyclerView.ViewHolder {

        public final ImageView placeImage;

        public PlaceImageRecyclerViewHolder(View itemView) {
            super(itemView);
            Log.i(LOG_TAG, "cv id place image: " + String.valueOf(itemView.getId()));
            placeImage = (ImageView) itemView.findViewById(R.id.ivPlaceSmallImage);

        }
    }


}
