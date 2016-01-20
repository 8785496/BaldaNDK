package es.hol.chernyshov.balda;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ResultActivity extends FragmentActivity
        implements LoginDialogFragment.LoginDialogListener,
        RegistrationDialogFragment.RegistrationDialogListener {

    private SharedPreferences myPreferences;
    private Intent intent;
    private int scorePlayer;
    private int scoreAndroid;
    private boolean isHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);

        intent = getIntent();
        scorePlayer = intent.getIntExtra("scorePlayer", 0);
        scoreAndroid = intent.getIntExtra("scoreAndroid", 0);
        isHelp = intent.getBooleanExtra("isHelp", false);

        init();
    }

    private void init() {
        boolean isAuth = myPreferences.contains("username");
        String username = myPreferences.getString("username", "Player");
        //password = myPreferences.getString("password", "");
        int score = myPreferences.getInt("score", 0);

        if (isHelp || (scorePlayer <= scoreAndroid) || (scorePlayer <= score)) {
            setContentView(R.layout.activity_result_not_save);
        } else if (isAuth) {
            setContentView(R.layout.activity_result_auth);
        } else {
            setContentView(R.layout.activity_result);
        }


        TextView txtScore = (TextView) findViewById(R.id.txtScore);
        String text = String.format("%d : %d", scorePlayer, scoreAndroid);
        txtScore.setText(text);

        TextView txtPlayers = (TextView) findViewById(R.id.txtPlayers);
        String text1 = String.format("%s : Android", username);
        txtPlayers.setText(text1);
    }

    public void save(View view) {
        String username = myPreferences.getString("username", "Player");
        String password = myPreferences.getString("password", "pass");
        new SaveRecordTask().execute(username, password);
    }

    public void registrationAnonymous(View view) {
        new SaveAnonymousTask().execute();
    }

    public void login(View view) {
        DialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }

    public void registration(View view) {
        DialogFragment dialog = new RegistrationDialogFragment();
        dialog.show(getSupportFragmentManager(), "RegistrationDialogFragment");
    }

    @Override
    public void onLoginDialogPositiveClick(DialogFragment dialog, String username, String password) {
        new UserAuthTask().execute(username, password);
    }

    @Override
    public void onRegistrationDialogPositiveClick(DialogFragment dialog, String username, String password, String repeatPassword) {
        Pattern p = Pattern.compile("^[A-Za-z0-9]{3,10}$");

        if (!p.matcher(username).matches()) {
            notification(R.string.message_invalid_name);
        } else if (password.length() < 3) {
            notification(R.string.message_invalid_password);
        } else if (!password.equals(repeatPassword)) {
            notification(R.string.message_passwords_equivalent);
        } else {
            new RegistrationTask().execute(username, password);
        }
    }

    private class SaveRecordTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String resultJson = "";
            HttpURLConnection urlConnection = null;
            int score = intent.getIntExtra("scorePlayer", 0);
            try {
                URL url = new URL("http://chernyshov.hol.es/record");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                JSONObject data = new JSONObject();
                JSONObject user = new JSONObject();
                user.put("username", params[0]);
                user.put("password", params[1]);
                data.put("score", score);
                data.put("user", user);
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
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d("BaldaNDK", strJson);
            if (strJson.equals("")) {
                notification(R.string.error_server);
                return;
            }
            try {
                JSONObject data = new JSONObject(strJson);
                SharedPreferences.Editor editor = myPreferences.edit();
                JSONObject record;
                String message = "";
                int score;
                if (data.has("code")) {
                    switch (data.getInt("code")) {
                        case 1:
                            record = data.getJSONObject("record");
                            score = record.getInt("score");
                            message = getResources().getString(R.string.message_record_saved);
                            break;
                        case 2:
                            score = data.getInt("score");
                            String format = getResources().getString(R.string.message_record_less);
                            message = String.format(format, score);
                            break;
                        default:
                            notification(R.string.error_server);
                            return;
                    }

                    editor.putInt("score", score);
                    editor.apply();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                    builder.setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.label_ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    notification(R.string.error_server);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class SaveAnonymousTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String jsonString = "";
            HttpURLConnection urlConnection = null;
            int score = intent.getIntExtra("scorePlayer", 0);
            try {
                URL url = new URL("http://chernyshov.hol.es/record/anonymous");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                JSONObject data = new JSONObject();
                data.put("score", score);
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
                jsonString = buffer.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d("BaldaNDK", strJson);
            if (strJson.equals("")) {
                notification(R.string.error_server);
                return;
            }
            try {
                JSONObject data = new JSONObject(strJson);
                if (data.has("code") && data.getInt("code") == 1) {
                    JSONObject record = data.getJSONObject("record");
                    int score = record.getInt("score");
                    JSONObject user = data.getJSONObject("user");
                    String username = user.getString("username");
                    String password = user.getString("password");

                    SharedPreferences.Editor editor = myPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putInt("score", score);
                    editor.apply();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                    builder.setMessage(R.string.message_record_saved)
                            .setCancelable(false)
                            .setNegativeButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    notification(R.string.error_server);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
            Log.d("BaldaNDK", strJson);
            if (strJson.equals("")) {
                notification(R.string.error_server);
                return;
            }
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
