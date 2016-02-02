package app.nomad.projects.yashasvi.hotspotsforwork.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import app.nomad.projects.yashasvi.hotspotsforwork.R;

/**
 * Created by yashasvi on 1/30/16.
 */
public class FullScreenPlaceImageAdapter extends PagerAdapter {

    private Activity mActivity;
    private List<Bitmap> imageBitmaps;

    public FullScreenPlaceImageAdapter(Activity activity, List<Bitmap> bitmaps) {
        this.mActivity = activity;
        this.imageBitmaps = bitmaps;
    }

    @Override
    public int getCount() {
        return imageBitmaps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;

        LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.place_image_fullscreen, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.ivPlaceFullscreen);

        imgDisplay.setImageBitmap(imageBitmaps.get(position));

        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
