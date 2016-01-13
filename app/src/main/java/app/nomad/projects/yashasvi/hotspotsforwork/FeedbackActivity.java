package app.nomad.projects.yashasvi.hotspotsforwork;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.nomad.projects.yashasvi.hotspotsforwork.models.AppFeedback;
import app.nomad.projects.yashasvi.hotspotsforwork.utils.ServerConstants;

public class FeedbackActivity extends AppCompatActivity {

    private static final String TAG = "FeedbackActivity";

    Button bSendAppFeedback;
    EditText etFeedbackName, etFeedbackEmail, etFeedbackMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_app);

        initialize();
    }

    private void initialize() {
        bSendAppFeedback = (Button) findViewById(R.id.bSendAppFeedback);
        etFeedbackName = (EditText) findViewById(R.id.etFeedbackName);
        etFeedbackEmail = (EditText) findViewById(R.id.etFeedbackEmail);
        etFeedbackMessage = (EditText) findViewById(R.id.etFeedbackMessage);

        bSendAppFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAppFeedbackToServer();
            }
        });
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


    public String postJSON(String jsonData) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL(ServerConstants.APP_FEEDBACK_PATH);
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
            int status = connection.getResponseCode();
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
            return response.toString();

        } catch (Exception ex) {
            return ex.toString();
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    //disconnect error
                }
            }
        }
    }


    public class TestAsyncTask extends AsyncTask<Void, Void, String> {
        private String jsonData;

        public TestAsyncTask(String jsonString) {
            jsonData = jsonString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String responseFromServer = postJSON(jsonData);
            return responseFromServer;
        }

        @Override
        protected void onPostExecute(String responseFromServer) {
            super.onPostExecute(responseFromServer);
            Log.i(TAG, responseFromServer);
        }
    }
}
