package es.hol.chernyshov.balda;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.net.URLEncoder;

public class TranslateActivity extends Activity {
    String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        TextView txtYandex = (TextView) findViewById(R.id.txtYandex);
        txtYandex.setMovementMethod(LinkMovementMethod.getInstance());

        Uri uri = getIntent().getData();
        word = uri.getQueryParameter("word");
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
                String word = URLEncoder.encode(params[0], "UTF-8");
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

        protected void onPostExecute(String result) {
            //String word = "";
            String ts = "";
            String tr = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("def")) {
                    JSONArray def = jsonObject.getJSONArray("def");
                    if (def.length() > 0) {
                        JSONObject def_0 = def.getJSONObject(0);
                        word = def_0.getString("text");
                        ts = "[" + def_0.getString("ts") + "]";for (int i = 0; i < def.length(); i++) {
                            JSONArray jsonArray = def.getJSONObject(i).getJSONArray("tr");
                            JSONObject jsonTr = jsonArray.getJSONObject(0);
                            String text = jsonTr.getString("text");
                            tr += "<p>" + text + "</p>";
                        }
                    } else {
                        Resources res = getResources();
                        word = String.format(res.getString(R.string.error_word_not_found), word);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //String text = word + "<br>" + ts;

            TextView txtWord = (TextView) findViewById(R.id.txtWord);
            txtWord.setText(word);

            Log.d("BaldaNDK", tr);
            TextView txtTranscription = (TextView) findViewById(R.id.txtTranscription);
            txtTranscription.setText(ts);

            TextView txtTranslate = (TextView) findViewById(R.id.txtTranslate);
            txtTranslate.setText(Html.fromHtml(tr));
        }
    }
}

