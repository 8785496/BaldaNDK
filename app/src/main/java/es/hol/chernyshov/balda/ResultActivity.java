package es.hol.chernyshov.balda;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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

        boolean isHelp = intent.getBooleanExtra("isHelp", false);
        if (isHelp) {
            // TODO
        } else {

        }
//        TextView textView3 = (TextView) findViewById(R.id.textView3);
//        textView3.setText(myPreferences.getString("username", "Player"));
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
        //startActivity(new Intent(this, LoginActivity.class));
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

    private class RegAnonymousTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            // anonymous username and pass
            JSONObject jsonUser = registerAnonymous();
            String username = "";
            String password = "";
            try {
                if (jsonUser.has("code") && jsonUser.getInt("code") == 1) {
                    JSONObject user = jsonUser.getJSONObject("user");
                    username = user.getString("username");
                    password = user.getString("password");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = myPreferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.apply();
            // save record
            int score = intent.getIntExtra("scorePlayer", 0);
            JSONObject jsonRecord = sendRecord(score, username, password);
            return jsonRecord;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            super.onPostExecute(data);
            //Log.d("BaldaNDK", strJson);
            try {
                //JSONObject data = new JSONObject(strJson);
                if (data.has("code") && data.getInt("code") == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                    builder.setTitle("Сообщение")
                            .setMessage("Ваш рекорд успешно сохранен")
                            //.setIcon(R.drawable.ic_android_cat)
                            .setCancelable(false)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject sendRecord(int score, String username, String password) {
        String jsonString = "";
        HttpURLConnection urlConnection = null;
        JSONObject jsonRecord = null;
        try {
            URL url = new URL("http://chernyshov.hol.es/record");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            JSONObject data = new JSONObject();
            JSONObject user = new JSONObject();
            user.put("username", username);
            user.put("password", password);
            data.put("score", score);
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
            jsonString = buffer.toString();
            Log.d("BaldaNDK", jsonString);
            jsonRecord = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return jsonRecord;
    }

    private JSONObject registerAnonymous() {
        String resultJson = "";
        HttpURLConnection urlConnection = null;
        JSONObject data = null;
        try {
            URL url = new URL("http://chernyshov.hol.es/user/anonymous");
            urlConnection = (HttpURLConnection) url.openConnection();
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
            Log.d("BaldaNDK", resultJson);
            data = new JSONObject(resultJson);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }
}
