package com.example.hellojni;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RecordActivity extends Activity {
    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        table = (TableLayout) findViewById(R.id.table);

        TextView textUser = new TextView(getApplicationContext());
//        textUser.setGravity(Gravity.CENTER_HORIZONTAL);
//        textUser.setTextColor(0xff000000);
        textUser.setText("Player");

        TextView textPoint = new TextView(getApplicationContext());
        textPoint.setText("25");

        TableRow row = new TableRow(getApplicationContext());
        row.addView(textUser);
        row.addView(textPoint);

        table.addView(row);
    }
}
