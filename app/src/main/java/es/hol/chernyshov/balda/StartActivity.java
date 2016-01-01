package es.hol.chernyshov.balda;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class StartActivity extends Activity {
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

        TextView textView = (TextView) findViewById(R.id.txtTranslate);
        textView.setText(Html.fromHtml(
                        "<a href=\"balda://TranslateActivityHost?word=hello\">hello</a><br/>" +
                        "<a href=\"balda://TranslateActivityHost?word=bay\">bay</a><br/>" +
                        "<a href=\"balda://TranslateActivityHost?word=space\">space</a><br/>"
        ));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.record:
                startActivity(new Intent(this, RecordActivity.class));
                return true;
            case R.id.myRecord:
                startActivity(new Intent(this, MyRecordActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
//                String key = "dict.1.1.20151211T161559Z.76ec6129fecd755b.447faf7495f92ff3f651ec3928e36d8cbacefb23";
//                String word = URLEncoder.encode(params[0], "UTF-8"); //"time";
//                URL url = new URL("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + key + "&lang=ru-ru&text=" + word);
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
                user.put("username", "Ivan1111");
                user.put("password", "pass");
                data.put("score", new Integer(312));
                data.put("user", user);
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
                TextView textView = (TextView) findViewById(R.id.txtTranslate);
                JSONObject data = new JSONObject(strJson);
                int score = data.getInt("status");
                textView.setText(String.valueOf(score));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}