package es.hol.chernyshov.balda;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;


import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

public class StartActivity extends FragmentActivity
        implements NoticeDialogFragment.NoticeDialogListener {
    private int[] complexity = {3, 5, 10};
    private Spinner spinnerComplexity;
    private Spinner spinnerLang;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Complexity
        ArrayAdapter<CharSequence> adapterComplexity = ArrayAdapter.createFromResource(this,
                R.array.complexity, android.R.layout.simple_spinner_item);
        adapterComplexity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerComplexity = (Spinner) findViewById(R.id.spinner);
        spinnerComplexity.setAdapter(adapterComplexity);
        spinnerComplexity.setSelection(1);

        // Lang
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
        //startActivity(new Intent(this, LoginActivity.class));
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialogFragment();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

    }

    public void logout(View view) {
    }

    public void setWord(View view) {
        Log.d("BaldaNDK", "set word");
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
//                // Add action buttons
//                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // sign in the user ...
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        LoginDialogFragment.this.getDialog().cancel();
//                    }
//                });
//        return builder.create();
//    }


}