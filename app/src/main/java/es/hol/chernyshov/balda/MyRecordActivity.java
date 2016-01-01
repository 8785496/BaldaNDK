package es.hol.chernyshov.balda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyRecordActivity extends Activity {
    private TableLayout table;
    private TableRow.LayoutParams paramsUser;
    private TableRow.LayoutParams paramsPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences myPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);
        String username = myPreferences.getString("username", "");

        if (username == "") {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }

        setContentView(R.layout.activity_my_record);

        table = (TableLayout) findViewById(R.id.tableMyRecord);

        paramsUser = new TableRow.LayoutParams();
        paramsUser.weight = 0.8f;

        paramsPoint = new TableRow.LayoutParams();
        paramsPoint.weight = 0.2f;

        new GetRecordsTask().execute(username);
    }

    private class GetRecordsTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            JSONArray jsonRecords = null;
            try {
                URL url = new URL("http://chernyshov.hol.es/record/" + params[0]);
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
                String resultJson = buffer.toString();
                jsonRecords = new JSONArray(resultJson);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return jsonRecords;
        }

        @Override
        protected void onPostExecute(JSONArray data) {
            super.onPostExecute(data);
            try {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject record = data.getJSONObject(i);
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
}
