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
    String[] lang = {"Русский", "Английский"};
    private Spinner spinnerComplexity;
    private Spinner spinnerLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Complexity
        ArrayAdapter<String> adapterComplexity = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapterComplexity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerComplexity = (Spinner) findViewById(R.id.spinner);
        spinnerComplexity.setAdapter(adapterComplexity);
        spinnerComplexity.setSelection(3);

        // Lang
        ArrayAdapter<String> adapterLang = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lang);
        adapterLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLang = (Spinner) findViewById(R.id.spinnerLang);
        spinnerLang.setAdapter(adapterLang);
        spinnerLang.setSelection(0);
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

    public void startGame(View view) {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("lang", spinnerLang.getSelectedItemPosition());
        intent.putExtra("complexity", spinnerLang.getSelectedItemPosition());
        startActivity(intent);
    }
}