package com.valmiki.hotspots.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.valmiki.hotspots.utils.Constants;

import com.valmiki.hotspots.R;

public class AboutMeActivity extends AppCompatActivity {

    private static String LOG_TAG = "AboutMeAcivity";

    boolean back;

    private Toolbar toolbar;

    private ImageView ivLogo;
    private TextView tvAboutName, tvAboutVersion, tvAboutDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        initialize();
    }

    private void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbarAboutMe);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_about);
        ivLogo = (ImageView) findViewById(R.id.ivLogoAbout);


        tvAboutVersion = (TextView) findViewById(R.id.tvTextAboutVersion);
        tvAboutVersion.setGravity(Gravity.CENTER);
        tvAboutVersion.setText(Constants.VERSION_NO);
        tvAboutVersion.setTextColor(getResources().getColor(R.color.colorPrimary));

        tvAboutDescription = (TextView) findViewById(R.id.tvTextAboutDescription);
        tvAboutDescription.setText(Constants.ABOUT_DESCRIPTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

}
