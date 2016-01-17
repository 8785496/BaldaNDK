package es.hol.chernyshov.balda;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Pattern;

public class ResultActivity extends FragmentActivity
        implements LoginDialogFragment.LoginDialogListener,
        RegistrationDialogFragment.RegistrationDialogListener {

    private SharedPreferences myPreferences;
    private Intent intent;
    private int scorePlayer;
    private int scoreAndroid;
    boolean isHelp;
    String username;
    String password;

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
        username = myPreferences.getString("username", "Player");
        password = myPreferences.getString("password", "");

        if (isHelp || (scorePlayer <= scoreAndroid)) {
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
        new RegAnonymousTask().execute();
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
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
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
                user.put("username", params[0]);
                user.put("password", params[1]);
                data.put("score", new Integer(intent.getIntExtra("scorePlayer", 0)));
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
                JSONObject data = new JSONObject(strJson);
                if (data.has("code") && data.getInt("code") == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                    builder.setMessage(R.string.message_record_saved)
                            .setCancelable(false)
                            .setNegativeButton(R.string.label_ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RegAnonymousTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            // anonymous username and pass
            JSONObject jsonUser = registerAnonymous();
            String username = "";
            String password = "";
            try {
                if (jsonUser.has("code") && jsonUser.getInt("code") == 1) {
                    JSONObject user = jsonUser.getJSONObject("user");
                    username = user.getString("username");
                    password = user.getString("password");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = myPreferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.apply();
            // save record
            int score = intent.getIntExtra("scorePlayer", 0);
            JSONObject jsonRecord = sendRecord(score, username, password);
            return jsonRecord;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            super.onPostExecute(data);
            //Log.d("BaldaNDK", strJson);
            try {
                //JSONObject data = new JSONObject(strJson);
                if (data.has("code") && data.getInt("code") == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                    builder.setTitle("Сообщение")
                            .setMessage("Ваш рекорд успешно сохранен")
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject sendRecord(int score, String username, String password) {
        String jsonString = "";
        HttpURLConnection urlConnection = null;
        JSONObject jsonRecord = null;
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
            user.put("username", username);
            user.put("password", password);
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
            jsonString = buffer.toString();
            Log.d("BaldaNDK", jsonString);
            jsonRecord = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return jsonRecord;
    }

    private JSONObject registerAnonymous() {
        String resultJson = "";
        HttpURLConnection urlConnection = null;
        JSONObject data = null;
        try {
            URL url = new URL("http://chernyshov.hol.es/user/anonymous");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJson = buffer.toString();
            Log.d("BaldaNDK", resultJson);
            data = new JSONObject(resultJson);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    private class UserAuthTask extends AsyncTask<String, Void, String> {
        String resultJson = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://chernyshov.hol.es/user/exist");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
                JSONObject data = new JSONObject(strJson);
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                CharSequence text;
                if (data.has("code") && data.getInt("code") == 1) {
                    JSONObject user = data.getJSONObject("user");
                    SharedPreferences.Editor editor = myPreferences.edit();
                    editor.putString("username", user.getString("username"));
                    editor.putString("password", user.getString("password"));
                    editor.apply();
                    text = "Вход выполнен";
                    init();
                } else {
                    text = "Не верный логин или пароль";
                }
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RegistrationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            //JSONArray jsonRecords = null;
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
                JSONObject user = new JSONObject();
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
            if (data == "") {
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

    private void notification(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
