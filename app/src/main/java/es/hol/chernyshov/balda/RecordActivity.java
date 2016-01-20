package es.hol.chernyshov.balda;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class RecordActivity extends Activity {
    private TableLayout table;
    private TableRow.LayoutParams paramsUser;
    private TableRow.LayoutParams paramsPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        table = (TableLayout) findViewById(R.id.table);

        paramsUser = new TableRow.LayoutParams();
        paramsUser.weight = 0.8f;

        paramsPoint = new TableRow.LayoutParams();
        paramsPoint.weight = 0.2f;

        new GetRecordsTask().execute();
    }

    private class GetRecordsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            String resultJson = "";
            try {
                URL url = new URL("http://chernyshov.hol.es/record");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            if (strJson.equals("")) {
                notification(R.string.error_server);
                return;
            }
            try {
                JSONArray jsonRecords = new JSONArray(strJson);
                for (int i = 0; i < jsonRecords.length(); i++) {
                    JSONObject record = jsonRecords.getJSONObject(i);
                    Log.d("BaldaNDK", record.toString());

                    TextView textUser = new TextView(getApplicationContext());
                    textUser.setLayoutParams(paramsUser);
                    textUser.setText(record.getString("username"));

                    TextView textPoint = new TextView(getApplicationContext());
                    textPoint.setLayoutParams(paramsPoint);
                    textPoint.setText(record.getString("score"));

                    TableRow row = new TableRow(getApplicationContext());
                    row.addView(textUser);
                    row.addView(textPoint);

                    table.addView(row);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void notification(int stringId) {
        String text = getResources().getString(stringId);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
