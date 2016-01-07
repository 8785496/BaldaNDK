package es.hol.chernyshov.balda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

public class StartActivity extends Activity {
    //String[] data = {"2", "3", "4", "5", "6", "7", "8", "9", "10"};
//    private String[] data = {"Минимальная", "Средняя", "Максимальная"};
    private int[] complexity = {3, 5, 10};
//    private String[] lang = {"Русский", "Английский"};
    private Spinner spinnerComplexity;
    private Spinner spinnerLang;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Complexity
        //ArrayAdapter<String> adapterComplexity = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        ArrayAdapter<CharSequence> adapterComplexity = ArrayAdapter.createFromResource(this,
                R.array.complexity, android.R.layout.simple_spinner_item);
        adapterComplexity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerComplexity = (Spinner) findViewById(R.id.spinner);
        spinnerComplexity.setAdapter(adapterComplexity);
        spinnerComplexity.setSelection(1);

        // Lang
        //ArrayAdapter<String> adapterLang = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lang);
        ArrayAdapter<CharSequence> adapterLang = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapterLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLang = (Spinner) findViewById(R.id.spinnerLang);
        spinnerLang.setAdapter(adapterLang);
        spinnerLang.setSelection(0);

        // isRandom
        radioButton = (RadioButton) findViewById(R.id.radioButtonRandomWord);
    }

    public void startGame(View view) {
        boolean isRandom = radioButton.isChecked();
        int lang = spinnerLang.getSelectedItemPosition();
        int _complexity = complexity[spinnerComplexity.getSelectedItemPosition()];

        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("lang", lang);
        intent.putExtra("complexity", _complexity);
        intent.putExtra("isRandom", isRandom);
        if (!isRandom) {
            // TODO
            if (lang == 1) {
                intent.putExtra("startWord", "panda");
            } else {
                intent.putExtra("startWord", "балда");
            }
        }
        startActivity(intent);
    }

    public void records(View view) {
        startActivity(new Intent(this, RecordActivity.class));
    }

    public void myRecords(View view) {
        startActivity(new Intent(this, MyRecordActivity.class));
    }

    public void registration(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void logout(View view) {
    }

    public void setWord(View view) {
        Log.d("BaldaNDK", "set word");
    }




}