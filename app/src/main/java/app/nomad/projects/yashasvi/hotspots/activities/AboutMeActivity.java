package app.nomad.projects.yashasvi.hotspots.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import app.nomad.projects.yashasvi.hotspots.R;

public class AboutMeActivity extends AppCompatActivity {

    private static String LOG_TAG = "AboutMeAcivity";

    boolean back;

    private Toolbar toolbar;

    private ImageView ivLogo;
    private TextView tvAbout;

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
        tvAbout = (TextView) findViewById(R.id.tvTextAbout);
        tvAbout.setText("HotSpots");
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
