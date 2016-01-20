package app.nomad.projects.yashasvi.hotspotsforwork.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.nomad.projects.yashasvi.hotspotsforwork.R;
import app.nomad.projects.yashasvi.hotspotsforwork.models.AppFeedback;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;

public class FeedbackActivity extends AppCompatActivity {

    private static final String TAG = "AppFeedbackActivity";

    Button bSendAppFeedback;
    EditText etFeedbackName, etFeedbackEmail, etFeedbackMessage;
    TextInputLayout tilFeedbackName, tilFeedbackEmail, tilFeedbackMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_app);

        initialize();
    }

    private void initialize() {
        bSendAppFeedback = (Button) findViewById(R.id.bSendAppFeedback);

        tilFeedbackName = (TextInputLayout) findViewById(R.id.tilAppFeedbackName);
        tilFeedbackEmail = (TextInputLayout) findViewById(R.id.tilAppFeedbackEmail);
        tilFeedbackMessage = (TextInputLayout) findViewById(R.id.tilAppFeedbackMessage);

        etFeedbackName = (EditText) findViewById(R.id.etFeedbackName);
        etFeedbackEmail = (EditText) findViewById(R.id.etFeedbackEmail);
        etFeedbackMessage = (EditText) findViewById(R.id.etFeedbackMessage);
        etFeedbackName.addTextChangedListener(new MyTextWatcher(etFeedbackName));
        etFeedbackEmail.addTextChangedListener(new MyTextWatcher(etFeedbackEmail));
        etFeedbackMessage.addTextChangedListener(new MyTextWatcher(etFeedbackMessage));

        bSendAppFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName()) {
                    return;
                }

                if (!validateEmail()) {
                    return;
                }

                if (!validateMessage()) {
                    return;
                }
                sendAppFeedbackToServer();
            }
        });
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
        AppFeedback appFeedback = new AppFeedback(feedbackName, feedbackEmail, feedbackMessage, "1");
        String jsonString = new Gson().toJson(appFeedback);
        TestAsyncTask testAsyncTask = new TestAsyncTask(jsonString);
        testAsyncTask.execute();
    }


    public int postJSON(String jsonData) {
        int status = 0;
        HttpURLConnection connection = null;
        try {
            URL u = new URL(ServerConstants.SERVER_URL + ServerConstants.APP_FEEDBACK_PATH);
            connection = (HttpURLConnection) u.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(jsonData.toString());
            wr.flush();
            wr.close();
            status = connection.getResponseCode();
            Log.i(TAG, "status : " + status);
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int responseFromServer = postJSON(jsonData);
            return responseFromServer;
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
