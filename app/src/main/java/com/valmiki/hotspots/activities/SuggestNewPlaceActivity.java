package com.valmiki.hotspots.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.valmiki.hotspots.MyApplication;
import com.valmiki.hotspots.R;
import com.valmiki.hotspots.enums.ConnectionAvailability;
import com.valmiki.hotspots.utils.CheckInternetAsyncTask;
import com.valmiki.hotspots.utils.Constants;
import com.valmiki.hotspots.utils.ServerConstants;

import java.util.concurrent.TimeUnit;

public class SuggestNewPlaceActivity extends AppCompatActivity {

    Tracker analyticsTracker;

    private static final String LOG_TAG = "SuggestNewPlaceActivity";

    private WebView webView;
    private final Activity activity = this;
    private ProgressDialog pd;
    LinearLayout llParent;

    SwipeRefreshLayout swipeRefreshLayout;
    Snackbar internetSnackbar;

    Boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_place);
        initialize();
        pd = ProgressDialog.show(this, "Please wait..", "", true, true);
        webView.loadUrl(ServerConstants.SUGGEST_PLACE_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsTracker = ((MyApplication) getApplication()).getDefaultTracker();
        analyticsTracker.setScreenName(LOG_TAG);
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initialize() {

        success = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSuggestNewPlace);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.suggest_place);

        llParent = (LinearLayout) findViewById(R.id.ll_suggestPlaceActivity);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshSuggestPage);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "OnRefresh");
                success = true;
                webView.loadUrl(ServerConstants.SUGGEST_PLACE_URL);
            }
        });
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
                Log.i(LOG_TAG, "OnReceivedError : " + description);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                success = false;
                internetSnackbar = Snackbar
                        .make(llParent, "Check Internet Connection and swipe down to refresh.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETTINGS", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS);
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                internetSnackbar.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(LOG_TAG, "OnPageFinish : " + url);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                if (!success && !url.equals("about:blank"))
                    webView.loadUrl("about:blank");
                if (swipeRefreshLayout.isShown())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "OnActivityResult");
        switch (requestCode) {
            case Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS:
                ConnectionAvailability isInternetActive = ConnectionAvailability.INTERNET_NOT_AVAILABLE;
                try {
                    Log.i(LOG_TAG, "checking internet");
                    isInternetActive = new CheckInternetAsyncTask(this).execute().get(2L, TimeUnit.SECONDS);
                } catch (Exception e) {
                    Log.i(LOG_TAG, e.toString());
                }
                if (isInternetActive == ConnectionAvailability.INTERNET_AVAILABLE) {
                    Log.i(LOG_TAG, "Internet active");
                    if (internetSnackbar.isShown())
                        internetSnackbar.dismiss();
                    pd = ProgressDialog.show(this, "Please wait..", "", true, true);
                    success = true;
                    webView.loadUrl(ServerConstants.SUGGEST_PLACE_URL);
                } else {
                    internetSnackbar = Snackbar
                            .make(llParent, "Check Internet Connection and swipe down to refresh.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("SETTINGS", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS);
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                    internetSnackbar.show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void dismissProgressDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
}
