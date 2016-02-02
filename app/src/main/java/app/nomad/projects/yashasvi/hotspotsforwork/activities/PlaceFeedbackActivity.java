package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.models.PlaceFeedback;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;

public class PlaceFeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlaceFeedbackActivity";

    int place_id;
    String placeName;

    Toolbar toolbar;

    ImageButton ibHappy, ibStraight, ibSad;
    CheckBox cbWifi, cbFood, cbAmbiance, cbService;

    TextInputLayout tilName, tilMessage;
    EditText etName, etMessage;

    Button bSendFeedback;

    int feeling = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_feedback);
        initialize();
    }

    private void initialize() {

        place_id = getIntent().getIntExtra("placeId",-1);
        placeName = getIntent().getStringExtra("placeName");

        toolbar = (Toolbar) findViewById(R.id.toolbarPlaceFeedback);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(placeName);


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
        TestAsyncTask testAsyncTask = new TestAsyncTask(jsonString);
        testAsyncTask.execute();
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
                if (!validateName()) {
                    return;
                }
                if (!validateMessage()) {
                    return;
                }
                if(feeling == -1){
                    Toast.makeText(PlaceFeedbackActivity.this, "Please tell us how are you feeling", Toast.LENGTH_LONG).show();
                    return;
                }
                sendPlaceFeedbackToServer();
                break;
        }
    }

    public int postJSON(String jsonData) {
        int status = 0;
        HttpURLConnection connection = null;
        try {
            URL u = new URL(ServerConstants.SERVER_URL + ServerConstants.PLACE_FEEDBACK_PATH);
            connection = (HttpURLConnection) u.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(jsonData);
            wr.flush();
            wr.close();
            status = connection.getResponseCode();
            Log.i(TAG, "status : " + status);
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();


        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    //disconnect error
                }
            }
        }
        return status;
    }

    public class TestAsyncTask extends AsyncTask<Void, Void, Integer> {
        private String jsonData;

        public TestAsyncTask(String jsonString) {
            jsonData = jsonString;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return postJSON(jsonData);
        }

        @Override
        protected void onPostExecute(Integer responseFromServer) {
            super.onPostExecute(responseFromServer);
            Log.i(TAG, String.valueOf(responseFromServer));
            if (responseFromServer == HttpURLConnection.HTTP_OK)
                finish();
            else {
                //TODO : show error message
            }

        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

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
