package com.example.hellojni;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultActivity extends Activity {
    private SharedPreferences myPreferences;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        myPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);

        intent = getIntent();
        TextView textView = (TextView) findViewById(R.id.txtScore);
        String text = String.format("%1$s : %2$s", intent.getIntExtra("scorePlayer", 0), intent.getIntExtra("scoreAndroid", 0));
        textView.setText(text);

        TextView textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText(myPreferences.getString("username", "Player"));
    }

    public void save(View view) {
        String username = myPreferences.getString("username", "Player");
        String password = myPreferences.getString("password", "pass");
        new SaveRecordTask().execute(username, password);
    }

    public void registrationAnonymous(View view) {
        new RegAnonymousTask().execute();
    }

    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private class SaveRecordTask extends AsyncTask<String, Void, String> {
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://chernyshov.hol.es/record");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                JSONObject data = new JSONObject();
                JSONObject user = new JSONObject();
                user.put("username", params[0]);
                user.put("password", params[1]);
                data.put("score", new Integer(intent.getIntExtra("scorePlayer", 0)));
                data.put("user", user);
                Log.d("BaldaNDK", data.toString());
                writer.write(data.toString());
                writer.flush();
                writer.close();
                outputStream.close();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d("BaldaNDK", strJson);
            try {
                JSONObject data = new JSONObject(strJson);
                if (data.has("status") && data.getInt("status") == 1) {
                    Log.d("BaldaNDK", String.valueOf(data.getInt("status")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RegAnonymousTask extends AsyncTask<String, Void, String> {
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://chernyshov.hol.es/user/anonymous");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d("BaldaNDK", strJson);
            try {
                JSONObject data = new JSONObject(strJson);
                if (data.has("status") && data.getInt("status") == 1) {
                    //Log.d("BaldaNDK", String.valueOf(data.getInt("status")));
                    JSONObject user = data.getJSONObject("user");
                    SharedPreferences.Editor editor = myPreferences.edit();
                    editor.putString("username", user.getString("username"));
                    editor.putString("password", user.getString("password"));
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
