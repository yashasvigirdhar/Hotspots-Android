package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
        initialize();
        setContentView(webView);
        webView.loadUrl(formUrl);
    }

    private void initialize() {
        webView = new WebView(this);
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
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
