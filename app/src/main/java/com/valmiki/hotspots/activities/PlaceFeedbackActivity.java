package com.valmiki.hotspots.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.valmiki.hotspots.MyApplication;
import com.valmiki.hotspots.R;
import com.valmiki.hotspots.enums.FeedbackType;
import com.valmiki.hotspots.models.PlaceFeedback;
import com.valmiki.hotspots.utils.Constants;
import com.valmiki.hotspots.utils.ServerHelperFunctions;

import java.net.HttpURLConnection;

public class PlaceFeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    Tracker analyticsTracker;

    private static final String LOG_TAG = "PlaceFeedbackActivity";

    private final Context mContext = this;

    private int place_id;
    private String placeName;

    CoordinatorLayout coordinatorLayout;

    private Toolbar toolbar;

    private ImageButton ibHappy;
    private ImageButton ibStraight;
    private ImageButton ibSad;
    private CheckBox cbWifi;
    private CheckBox cbFood;
    private CheckBox cbAmbiance;
    private CheckBox cbService;

    private TextInputLayout tilName;
    private TextInputLayout tilMessage;
    private EditText etName;
    private EditText etMessage;

    private Button bSendFeedback;

    private int feeling = -1;

    Snackbar internetSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_feedback);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsTracker = ((MyApplication) getApplication()).getDefaultTracker();
        analyticsTracker.setScreenName(LOG_TAG);
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initialize() {

        place_id = getIntent().getIntExtra("placeId", -1);
        placeName = getIntent().getStringExtra("placeName");

        toolbar = (Toolbar) findViewById(R.id.toolbarPlaceFeedback);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(placeName);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorPlaceFeedback);

        ibHappy = (ImageButton) findViewById(R.id.ibFeelingHappy);
        ibStraight = (ImageButton) findViewById(R.id.ibFeelingStraight);
        ibSad = (ImageButton) findViewById(R.id.ibFeelingSad);
        ibHappy.setOnClickListener(this);
        ibStraight.setOnClickListener(this);
        ibSad.setOnClickListener(this);

        cbWifi = (CheckBox) findViewById(R.id.cbWifiFeeling);
        cbFood = (CheckBox) findViewById(R.id.cbFoodFeeling);
        cbAmbiance = (CheckBox) findViewById(R.id.cbAmbianceFeeling);
        cbService = (CheckBox) findViewById(R.id.cbServiceFeeling);

        tilName = (TextInputLayout) findViewById(R.id.tilPlaceFeedbackName);
        tilMessage = (TextInputLayout) findViewById(R.id.tilPlaceFeedbackMessage);

        etName = (EditText) findViewById(R.id.etPlaceFeedbackName);
        etMessage = (EditText) findViewById(R.id.etPlaceFeedbackMessage);
        etName.addTextChangedListener(new MyTextWatcher(etName));
        etMessage.addTextChangedListener(new MyTextWatcher(etMessage));

        bSendFeedback = (Button) findViewById(R.id.bSendPlaceFeedback);
        bSendFeedback.setOnClickListener(this);
    }

    private boolean validateName() {
        if (etName.getText().toString().trim().isEmpty()) {
            tilName.setError(getString(R.string.err_msg_name));
            requestFocus(etName);
            return false;
        } else {
            tilName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMessage() {
        if (etMessage.getText().toString().trim().isEmpty()) {
            tilMessage.setError(getString(R.string.err_msg_message));
            requestFocus(etMessage);
            return false;
        } else {
            tilMessage.setErrorEnabled(false);
        }

        return true;
    }

    private void sendPlaceFeedbackToServer() {
        String feedbackName = etName.getText().toString();
        String feedbackMessage = etMessage.getText().toString();
        int food = cbFood.isChecked() ? 1 : 0;
        int wifi = cbWifi.isChecked() ? 1 : 0;
        int ambiance = cbAmbiance.isChecked() ? 1 : 0;
        int service = cbService.isChecked() ? 1 : 0;
        PlaceFeedback placeFeedback = new PlaceFeedback(place_id, feeling, wifi, food, ambiance, service, feedbackName, feedbackMessage);
        String jsonString = new Gson().toJson(placeFeedback);
        SendFeedbackAsyncTask sendFeedbackAsyncTask = new SendFeedbackAsyncTask(jsonString);
        sendFeedbackAsyncTask.execute();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibFeelingHappy:
                ibHappy.setBackgroundResource(R.drawable.happy_face_pressed);
                ibStraight.setBackgroundResource(R.drawable.straight_face_normal);
                ibSad.setBackgroundResource(R.drawable.sad_face_normal);
                feeling = 1;
                break;
            case R.id.ibFeelingStraight:
                ibHappy.setBackgroundResource(R.drawable.happy_face_normal);
                ibStraight.setBackgroundResource(R.drawable.straight_face_pressed);
                ibSad.setBackgroundResource(R.drawable.sad_face_normal);
                feeling = 2;
                break;
            case R.id.ibFeelingSad:
                ibHappy.setBackgroundResource(R.drawable.happy_face_normal);
                ibStraight.setBackgroundResource(R.drawable.straight_face_normal);
                ibSad.setBackgroundResource(R.drawable.sad_face_pressed);
                feeling = 3;
                break;
            case R.id.bSendPlaceFeedback:
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("PlaceFeedback")
                        .setAction("SubmitClick")
                        .setLabel("BeforeValidation, place : " + place_id)
                        .build());
                if (!validateName()) {
                    return;
                }
                if (!validateMessage()) {
                    return;
                }
                if (feeling == -1) {
                    Toast.makeText(PlaceFeedbackActivity.this, "Please tell us how are you feeling", Toast.LENGTH_LONG).show();
                    return;
                }
                analyticsTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("PlaceFeedback")
                        .setAction("SubmitClick")
                        .setLabel("AfterValidation, place : " + place_id)
                        .build());
                sendPlaceFeedbackToServer();
                break;
        }
    }


    public class SendFeedbackAsyncTask extends AsyncTask<Void, Void, String> {
        private final String jsonData;

        public SendFeedbackAsyncTask(String jsonString) {
            jsonData = jsonString;
        }

        @Override
        protected String doInBackground(Void... params) {
            return ServerHelperFunctions.postJSON(jsonData, FeedbackType.PLACE, analyticsTracker);
        }

        @Override
        protected void onPostExecute(String responseFromServer) {
            super.onPostExecute(responseFromServer);
            Log.i(LOG_TAG, "onPostExecute");
            Log.i(LOG_TAG, "responsefromserver : " + responseFromServer);
            if (responseFromServer.contains("Exception")) {
                internetSnackbar = Snackbar
                        .make(coordinatorLayout, "Please check your Internet Connection and try again.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETTINGS", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), Constants.REQUEST_CODE_INTENT_NETWORK_SETTINGS);
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                internetSnackbar.show();
                return;
            }
            if (responseFromServer.equals(String.valueOf(HttpURLConnection.HTTP_OK))) {
                Toast.makeText(mContext, R.string.submit_feedback_success, Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    private class MyTextWatcher implements TextWatcher {

        private final View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etPlaceFeedbackName:
                    validateName();
                    break;
                case R.id.etPlaceFeedbackMessage:
                    validateMessage();
                    break;
            }
        }
    }
}
