package com.example.hellojni;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class StartActivity extends Activity {


    public static String LOG_TAG = "my_log";

    String[] data = {"2", "3", "4", "5", "6", "7", "8", "9", "10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(3);
    }

    public void getTranslate(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        new ParseTask().execute(editText.getText().toString());
    }

    public void startRussian(View view) {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("lang", 0);
        startActivity(intent);
    }

    public void startEnglish(View view) {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("lang", 1);
        startActivity(intent);
    }

    private class ParseTask extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            // получаем данные с внешнего ресурса
            try {
//                String key = "dict.1.1.20151211T161559Z.76ec6129fecd755b.447faf7495f92ff3f651ec3928e36d8cbacefb23";
//                String word = URLEncoder.encode(params[0], "UTF-8"); //"time";
//                URL url = new URL("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + key + "&lang=ru-ru&text=" + word);
                URL url = new URL("http://chernyshov.hol.es/record");

                urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.setRequestMethod("POST");
                //urlConnection.getOutputStream()
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
//            return "hello";
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку

            TextView textView = (TextView) findViewById(R.id.txtTranslate);
            textView.setText(strJson);

//            Log.d(LOG_TAG, strJson);
//
//            JSONObject dataJsonObj = null;
//            String secondName = "";

//            try {
//                dataJsonObj = new JSONObject(strJson);
//                JSONArray friends = dataJsonObj.getJSONArray("friends");
//
//                // 1. достаем инфо о втором друге - индекс 1
//                JSONObject secondFriend = friends.getJSONObject(1);
//                secondName = secondFriend.getString("name");
//                Log.d(LOG_TAG, "Второе имя: " + secondName);
//
//                // 2. перебираем и выводим контакты каждого друга
//                for (int i = 0; i < friends.length(); i++) {
//                    JSONObject friend = friends.getJSONObject(i);
//
//                    JSONObject contacts = friend.getJSONObject("contacts");
//
//                    String phone = contacts.getString("mobile");
//                    String email = contacts.getString("email");
//                    String skype = contacts.getString("skype");
//
//                    Log.d(LOG_TAG, "phone: " + phone);
//                    Log.d(LOG_TAG, "email: " + email);
//                    Log.d(LOG_TAG, "skype: " + skype);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

}