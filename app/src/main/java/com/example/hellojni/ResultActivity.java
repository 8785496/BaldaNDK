package com.example.hellojni;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mSettings = getSharedPreferences("mySettings", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        TextView textView = (TextView) findViewById(R.id.txtScore);
        String text = String.format("%1$s : %2$s", intent.getIntExtra("scorePlayer", 0), intent.getIntExtra("scoreAndroid", 0));
        textView.setText(text);

        TextView textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText(mSettings.getString("username", "Player"));
    }

    public void save(View view) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("username", "Peter");
        editor.apply();
    }
}
