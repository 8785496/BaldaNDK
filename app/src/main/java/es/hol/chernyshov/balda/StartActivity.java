package es.hol.chernyshov.balda;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class StartActivity extends FragmentActivity
        implements LoginDialogFragment.LoginDialogListener,
                    WordDialogFragment.WordDialogListener,
                    RegistrationDialogFragment.RegistrationDialogListener {

    private int[] complexity = {3, 5, 10};
    private Spinner spinnerComplexity;
    private Spinner spinnerLang;
    private RadioButton radioButtonRandomWord;
    private SharedPreferences myPreferences;
    private String username;
    private String password;
    private String startWord;
    private boolean isAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);

        init();
    }

    private void init() {
        isAuth = myPreferences.contains("username");
        username = myPreferences.getString("username", "Player");
        password = myPreferences.getString("password", "");


        if (isAuth) {
            setContentView(R.layout.activity_start_auth);
        } else {
            setContentView(R.layout.activity_start);
        }

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
        radioButtonRandomWord = (RadioButton) findViewById(R.id.radioButtonRandomWord);
    }

    public void startGame(View view) {
        boolean isRandom = radioButtonRandomWord.isChecked();
        int lang = spinnerLang.getSelectedItemPosition();
        int _complexity = complexity[spinnerComplexity.getSelectedItemPosition()];

        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.putExtra("lang", lang);
        intent.putExtra("complexity", _complexity);
        intent.putExtra("isRandom", isRandom);
        if (!isRandom) {
            intent.putExtra("startWord", startWord);
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
        DialogFragment dialog = new RegistrationDialogFragment();
        dialog.show(getSupportFragmentManager(), "RegistrationDialogFragment");
    }

    public void login(View view) {
        DialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }

    @Override
    public void onLoginDialogPositiveClick(DialogFragment dialog, String username, String password) {
        new UserAuthTask().execute(username, password);
    }

    @Override
    public void onRegistrationDialogPositiveClick(DialogFragment dialog, String username, String password, String repeatPassword) {
        new RegistrationTask().execute(username, password);
    }

    @Override
    public void onWordDialogPositiveClick(DialogFragment dialog, String _word) {
        int lang = spinnerLang.getSelectedItemPosition();
        Pattern p;
        String word = _word.toLowerCase();
        word = word.replace("ё", "е");

        if (lang == 1) {
            p = Pattern.compile("^[a-z]{5}$");
        } else {
            p = Pattern.compile("^[а-я]{5}$");
        }

        if (p.matcher(word).matches()) {
            spinnerLang.setEnabled(false);
            startWord = word;
            RadioButton rbtnChooseWord = (RadioButton) findViewById(R.id.radioButtonChooseWord);
            rbtnChooseWord.setText(startWord);
        } else {
            radioButtonRandomWord.setChecked(true);
            spinnerLang.setEnabled(true);
            RadioButton rbtnChooseWord = (RadioButton) findViewById(R.id.radioButtonChooseWord);
            rbtnChooseWord.setText(getResources().getString(R.string.label_choose_word));
            notification(R.string.message_incorrect_word);
        }
    }

    @Override
    public void onWordDialogNegativeClick(DialogFragment dialog) {
        radioButtonRandomWord.setChecked(true);
        spinnerLang.setEnabled(true);
        RadioButton rbtnChooseWord = (RadioButton) findViewById(R.id.radioButtonChooseWord);
        rbtnChooseWord.setText(getResources().getString(R.string.label_choose_word));
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("score");
        editor.apply();

        init();
        notification(R.string.message_you_logged_out);
    }

    public void setWord(View view) {
        DialogFragment dialog = new WordDialogFragment();
        dialog.show(getSupportFragmentManager(), "WordDialogFragment");
        RadioButton rbtnChooseWord = (RadioButton) findViewById(R.id.radioButtonChooseWord);
        rbtnChooseWord.setText(getResources().getString(R.string.label_choose_word));
    }

    public void setRandomWord(View view) {
        spinnerLang.setEnabled(true);
    }

    private class UserAuthTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String resultJson = "";
            try {
                URL url = new URL("http://chernyshov.hol.es/user/exist");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                JSONObject data = new JSONObject();
                data.put("username", params[0]);
                data.put("password", params[1]);
                Log.d("BaldaNDK", data.toString());
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            if (strJson.equals("")) {
                notification(R.string.error_server);
                return;
            }
            Log.d("BaldaNDK", strJson);
            try {
                JSONObject data = new JSONObject(strJson);
                if (data.has("code") && data.getInt("code") == 1) {
                    JSONObject user = data.getJSONObject("user");
                    SharedPreferences.Editor editor = myPreferences.edit();
                    editor.putString("username", user.getString("username"));
                    editor.putString("password", user.getString("password"));
                    editor.apply();
                    notification(R.string.message_you_are_logged);
                    init();
                } else {
                    notification(R.string.message_invalid_login);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RegistrationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String resultJson = "";
            try {
                URL url = new URL("http://chernyshov.hol.es/user");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                JSONObject data = new JSONObject();
                data.put("username", params[0]);
                data.put("password", params[1]);
                Log.d("BaldaNDK", data.toString());
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            if (data.equals("")) {
                notification(R.string.error_server);
                return;
            }
            Log.d("BaldaNDK", data);
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.has("code")) {
                    if (jsonObject.getInt("code") == 1) {
                        notification(R.string.message_you_are_registered);
                    } else if (jsonObject.getInt("code") == 0) {
                        notification(R.string.message_user_exist);
                    }
                } else {
                    notification(R.string.error_server);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void notification(int stringId) {
        String text = getResources().getString(stringId);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}