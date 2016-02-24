package app.nomad.projects.yashasvi.hotspots.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.HttpURLConnection;

import app.nomad.projects.yashasvi.hotspots.R;
import app.nomad.projects.yashasvi.hotspots.enums.FeedbackType;
import app.nomad.projects.yashasvi.hotspots.models.AppFeedback;
import app.nomad.projects.yashasvi.hotspots.utils.ServerHelperFunctions;

public class AppFeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AppFeedbackActivity";

    private final Context mContext = this;

    private int feeling = -1;

    private Toolbar mToolbar;

    private ImageButton ibHappy;
    private ImageButton ibStraight;
    private ImageButton ibSad;

    private Button bSendAppFeedback;
    private EditText etFeedbackName;
    private EditText etFeedbackEmail;
    private EditText etFeedbackMessage;
    private TextInputLayout tilFeedbackName;
    private TextInputLayout tilFeedbackEmail;
    private TextInputLayout tilFeedbackMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_app);

        initialize();

    }

    private void initialize() {
        mToolbar = (Toolbar) findViewById(R.id.toolbarAppFeedback);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_feedback_activity_title);

        bSendAppFeedback = (Button) findViewById(R.id.bSendAppFeedback);

        ibHappy = (ImageButton) findViewById(R.id.ibAppFeelingHappy);
        ibStraight = (ImageButton) findViewById(R.id.ibAppFeelingStraight);
        ibSad = (ImageButton) findViewById(R.id.ibAppFeelingSad);
        ibHappy.setOnClickListener(this);
        ibStraight.setOnClickListener(this);
        ibSad.setOnClickListener(this);

        tilFeedbackName = (TextInputLayout) findViewById(R.id.tilAppFeedbackName);
        tilFeedbackEmail = (TextInputLayout) findViewById(R.id.tilAppFeedbackEmail);
        tilFeedbackMessage = (TextInputLayout) findViewById(R.id.tilAppFeedbackMessage);

        etFeedbackName = (EditText) findViewById(R.id.etFeedbackName);
        etFeedbackEmail = (EditText) findViewById(R.id.etFeedbackEmail);
        etFeedbackMessage = (EditText) findViewById(R.id.etFeedbackMessage);
        etFeedbackName.addTextChangedListener(new MyTextWatcher(etFeedbackName));
        etFeedbackEmail.addTextChangedListener(new MyTextWatcher(etFeedbackEmail));
        etFeedbackMessage.addTextChangedListener(new MyTextWatcher(etFeedbackMessage));

        bSendAppFeedback.setOnClickListener(this);
    }

    private boolean validateName() {
        if (etFeedbackName.getText().toString().trim().isEmpty()) {
            tilFeedbackName.setError(getString(R.string.err_msg_name));
            requestFocus(etFeedbackName);
            return false;
        } else {
            tilFeedbackName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = etFeedbackEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            tilFeedbackEmail.setError(getString(R.string.err_msg_email));
            requestFocus(etFeedbackEmail);
            return false;
        } else {
            tilFeedbackEmail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateMessage() {
        if (etFeedbackMessage.getText().toString().trim().isEmpty()) {
            tilFeedbackMessage.setError(getString(R.string.err_msg_message));
            requestFocus(etFeedbackMessage);
            return false;
        } else {
            tilFeedbackMessage.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void sendAppFeedbackToServer() {
        String feedbackName = etFeedbackName.getText().toString();
        String feedbackEmail = etFeedbackEmail.getText().toString();
        String feedbackMessage = etFeedbackMessage.getText().toString();
        AppFeedback appFeedback = new AppFeedback(feedbackName, feedbackEmail, feedbackMessage, String.valueOf(feeling));
        String jsonString = new Gson().toJson(appFeedback);
        SendFeedbackAsyncTask sendFeedbackAsyncTask = new SendFeedbackAsyncTask(jsonString);
        sendFeedbackAsyncTask.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibAppFeelingHappy:
                ibHappy.setBackgroundResource(R.drawable.happy_face_pressed);
                ibStraight.setBackgroundResource(R.drawable.straight_face_normal);
                ibSad.setBackgroundResource(R.drawable.sad_face_normal);
                feeling = 1;
                break;
            case R.id.ibAppFeelingStraight:
                ibHappy.setBackgroundResource(R.drawable.happy_face_normal);
                ibStraight.setBackgroundResource(R.drawable.straight_face_pressed);
                ibSad.setBackgroundResource(R.drawable.sad_face_normal);
                feeling = 2;
                break;
            case R.id.ibAppFeelingSad:
                ibHappy.setBackgroundResource(R.drawable.happy_face_normal);
                ibStraight.setBackgroundResource(R.drawable.straight_face_normal);
                ibSad.setBackgroundResource(R.drawable.sad_face_pressed);
                feeling = 3;
                break;
            case R.id.bSendAppFeedback:
                if (!validateName()) {
                    return;
                }

                if (!validateEmail()) {
                    return;
                }

                if (!validateMessage()) {
                    return;
                }
                if (feeling == -1) {
                    Toast.makeText(AppFeedbackActivity.this, "Please tell us how are you feeling", Toast.LENGTH_LONG).show();
                    return;
                }
                sendAppFeedbackToServer();
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
            return ServerHelperFunctions.postJSON(jsonData, FeedbackType.APP);
        }

        @Override
        protected void onPostExecute(String responseFromServer) {
            super.onPostExecute(responseFromServer);
            Log.i(TAG, String.valueOf(responseFromServer));
            if (Integer.parseInt(responseFromServer) == HttpURLConnection.HTTP_OK) {
                Toast.makeText(mContext, "Thanks! Your feedback has been submitted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(mContext, responseFromServer, Toast.LENGTH_SHORT).show();
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
                case R.id.etFeedbackName:
                    validateName();
                    break;
                case R.id.etFeedbackEmail:
                    validateEmail();
                    break;
                case R.id.etFeedbackMessage:
                    validateMessage();
                    break;
            }
        }
    }

}
