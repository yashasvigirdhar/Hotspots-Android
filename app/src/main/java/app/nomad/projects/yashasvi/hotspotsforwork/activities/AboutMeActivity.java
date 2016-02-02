package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import app.nomad.projects.yashasvi.hotspotsforwork.R;

public class AboutMeActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        initialize();

    }

    void initialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbarAboutMe);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_about);
    }
}
