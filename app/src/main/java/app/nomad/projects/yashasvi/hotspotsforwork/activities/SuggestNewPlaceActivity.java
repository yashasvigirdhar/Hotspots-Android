package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.Constants;

public class SuggestNewPlaceActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SuggestNewPlaceActivity";

    String formUrl;

    WebView webView;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_place);
        initialize();
        Log.i(LOG_TAG,"oncreate after initialization" + webView.toString());
        webView.loadUrl("https://docs.google.com/forms/d/1UcSgyQvyoGvYuH_cNQh6LYhuybTEFMv-jpvx-fUR5Tk/viewform");
    }

    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSuggestNewPlace);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.suggest_place);
        webView = (WebView) findViewById(R.id.webviewSuggestPlace);
        formUrl = Constants.NEW_PLACE_GOOGLE_FORM;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(SuggestNewPlaceActivity.this, "Error" + description, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
