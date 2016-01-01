package es.hol.chernyshov.balda;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Uri uri = getIntent().getData();

        String word = uri.getQueryParameter("word");
        Log.d("BaldaNDK", word);

        new TranslateTask().execute(word);
    }

    private class TranslateTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String resultJson = "";
            String key = "dict.1.1.20151211T161559Z.76ec6129fecd755b.447faf7495f92ff3f651ec3928e36d8cbacefb23";
            try {
                String word = URLEncoder.encode(params[0], "UTF-8"); //"time";
                URL url = new URL("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + key + "&lang=en-ru&text=" + word);
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            Log.d("BaldaNDK", resultJson);
            return resultJson;
        }
    }
}

