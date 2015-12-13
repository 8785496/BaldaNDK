package com.example.hellojni;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class KeyboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int lang = intent.getIntExtra("lang", 0);

        if (lang == 1) {
            setContentView(R.layout.activity_keyboard_en);
        } else {
            setContentView(R.layout.activity_keyboard);
        }

    }

    public void sendResult (View view) {
        switch (view.getId()){
            case R.id.button_1:
                setResult(1);
                break;
            case R.id.button_2:
                setResult(2);
                break;
            case R.id.button_3:
                setResult(3);
                break;
            case R.id.button_4:
                setResult(4);
                break;
            case R.id.button_5:
                setResult(5);
                break;
            case R.id.button_6:
                setResult(6);
                break;
            case R.id.button_7:
                setResult(7);
                break;
            case R.id.button_8:
                setResult(8);
                break;
            case R.id.button_9:
                setResult(9);
                break;
            case R.id.button_10:
                setResult(10);
                break;
            case R.id.button_11:
                setResult(11);
                break;
            case R.id.button_12:
                setResult(12);
                break;
            case R.id.button_13:
                setResult(13);
                break;
            case R.id.button_14:
                setResult(14);
                break;
            case R.id.button_15:
                setResult(15);
                break;
            case R.id.button_16:
                setResult(16);
                break;
            case R.id.button_17:
                setResult(17);
                break;
            case R.id.button_18:
                setResult(18);
                break;
            case R.id.button_19:
                setResult(19);
                break;
            case R.id.button_20:
                setResult(20);
                break;
            case R.id.button_21:
                setResult(21);
                break;
            case R.id.button_22:
                setResult(22);
                break;
            case R.id.button_23:
                setResult(23);
                break;
            case R.id.button_24:
                setResult(24);
                break;
            case R.id.button_25:
                setResult(25);
                break;
            case R.id.button_26:
                setResult(26);
                break;
            case R.id.button_27:
                setResult(27);
                break;
            case R.id.button_28:
                setResult(28);
                break;
            case R.id.button_29:
                setResult(29);
                break;
            case R.id.button_30:
                setResult(30);
                break;
            case R.id.button_31:
                setResult(31);
                break;
            case R.id.button_32:
                setResult(32);
                break;
            default:
                setResult(RESULT_CANCELED);
        }
        finish();
    }

    public void cancel(View view) {

    }
}
