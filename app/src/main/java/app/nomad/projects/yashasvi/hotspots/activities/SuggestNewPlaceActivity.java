package app.nomad.projects.yashasvi.hotspots.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import app.nomad.projects.yashasvi.hotspots.R;
import app.nomad.projects.yashasvi.hotspots.utils.ServerConstants;

public class SuggestNewPlaceActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SuggestNewPlaceActivity";

    private WebView webView;
    private final Activity activity = this;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_place);
        initialize();
        pd = ProgressDialog.show(this, "Loading..", "Please wait", true,true);
        webView.loadUrl(ServerConstants.SUGGEST_PLACE_URL);
    }

    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSuggestNewPlace);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.suggest_place);

        webView = (WebView) findViewById(R.id.webviewSuggestPlace);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);

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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(pd!=null && pd.isShowing())
                {
                    pd.dismiss();
                }
            }
        });

    }
}
