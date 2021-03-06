package es.hol.chernyshov.balda;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends Activity {

    private SharedPreferences myPreferences;
    private String chars;
    private byte[] space;
    private ArrayList<String> wordsAll = new ArrayList<>();
    private ArrayList<String> wordsAndroid = new ArrayList<>();
    private ArrayList<String> wordsUser = new ArrayList<>();
    private ArrayList<Integer> coordinates = new ArrayList<>();
    private boolean boolTrack = false;
    int insertCharIndex = -1;
    int lang;
    boolean isRandom;
    boolean isHelp = false;
    int complexity;
    String username;
    Button[] buttons = new Button[25];
    ColorStateList textColor;

    static {
        System.loadLibrary("balda");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPreferences = getSharedPreferences("mySettings", Context.MODE_PRIVATE);
        username = myPreferences.getString("username", "Player");

        Intent intent = getIntent();
        lang = intent.getIntExtra("lang", 0);
        isRandom = intent.getBooleanExtra("isRandom", false);
        complexity = intent.getIntExtra("complexity", 10);

        AssetManager myAssetManager = getResources().getAssets();
        nativDicInit(myAssetManager, lang);

        String startWord;
        if (lang == 1) {
            chars = " abcdefghijklmnopqrstuvwxyz";
        } else {
            chars = " абвгдежзийклмнопрстуфхцчшщъыьэюя";
        }

        if (isRandom) {
            Random r = new Random();
            int countWordLen5 = nativCountWordLen5();
            int randIndex = r.nextInt(countWordLen5 - 1);
            startWord = hashToString(nativRandomWord(randIndex));
        } else {
            startWord = intent.getStringExtra("startWord");
        }

        wordsAll.add(startWord);

        space = new byte[25] /*{
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 0
        }*/;
        for (int i = 0; i < 5; i++) {
            space[i + 10] = (byte) chars.indexOf(startWord.charAt(i));
        }

        init();
        refresh();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (lang == 1) {
            inflater.inflate(R.menu.menu_main_en, menu);
        } else {
            inflater.inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                _cancel();
                trackInit(10);
                trackIter();
                long[] longArr = nativHelp();
                String[] strArr = new String[longArr.length];
                for (int i = 0; i < longArr.length; i++) {
                    strArr[i] = hashToString(longArr[i]);
                }
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra("wordsHelp", strArr);
                startActivity(intent);
                isHelp = true;
                return true;
            case R.id.miss:
                miss();
                item.setEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativDestruct();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        init();
        refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode > 0) {
            Log.d("BaldaNDK", "resultCode - " + String.valueOf(resultCode));
            space[requestCode] = (byte) resultCode;
            boolTrack = true;
            coordinates.clear();
            insertCharIndex = requestCode;
            refresh();
        } else {
            Log.d("BaldaNDK", "RESULT_CANCELED");
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);

        quitDialog.setTitle(R.string.title_dialog_quit)
                .setMessage(R.string.label_quit)
                .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        quitDialog.show();
    }

    private void init() {
        buttons[0] = (Button) findViewById(R.id.button_0);
        buttons[1] = (Button) findViewById(R.id.button_1);
        buttons[2] = (Button) findViewById(R.id.button_2);
        buttons[3] = (Button) findViewById(R.id.button_3);
        buttons[4] = (Button) findViewById(R.id.button_4);

        buttons[5] = (Button) findViewById(R.id.button_5);
        buttons[6] = (Button) findViewById(R.id.button_6);
        buttons[7] = (Button) findViewById(R.id.button_7);
        buttons[8] = (Button) findViewById(R.id.button_8);
        buttons[9] = (Button) findViewById(R.id.button_9);

        buttons[10] = (Button) findViewById(R.id.button_10);
        buttons[11] = (Button) findViewById(R.id.button_11);
        buttons[12] = (Button) findViewById(R.id.button_12);
        buttons[13] = (Button) findViewById(R.id.button_13);
        buttons[14] = (Button) findViewById(R.id.button_14);

        buttons[15] = (Button) findViewById(R.id.button_15);
        buttons[16] = (Button) findViewById(R.id.button_16);
        buttons[17] = (Button) findViewById(R.id.button_17);
        buttons[18] = (Button) findViewById(R.id.button_18);
        buttons[19] = (Button) findViewById(R.id.button_19);

        buttons[20] = (Button) findViewById(R.id.button_20);
        buttons[21] = (Button) findViewById(R.id.button_21);
        buttons[22] = (Button) findViewById(R.id.button_22);
        buttons[23] = (Button) findViewById(R.id.button_23);
        buttons[24] = (Button) findViewById(R.id.button_24);

        textColor = buttons[0].getTextColors();
    }

    private native void nativDestruct();

    private native void nativDicInit(Object obj, int lang);

    private native void nativTrackInit(long[] hashWords, int complexity);

    private native void nativTrackIter(byte[] space);

    private native long nativGetWord();

    private native byte nativGetCharValue();

    private native int nativGetCharIndex();

    private native boolean nativFindWord(byte[] chars);

    private native long[] nativHelp();

    private native long nativRandomWord(int index);

    private native int nativCountWordLen5();

    private String hashToString(long hash) {
        String str = "";
        do {
            int dig = (int) (hash % 33);
            str = chars.substring(dig, dig + 1) + str;
            hash = (long) (hash / 33L);
        } while (hash != 0);
        return str;
    }

    private long stringToHash(String word) {
        long id = 0L;
        int len = word.length();
        for (int i = 0; i < len; i++) {
            String sym = word.substring(len - i - 1, len - i);
            id += (chars.indexOf(sym) + 0) * Math.pow(33, i);
        }
        return id;
    }

    private byte[] stringToChars(String word) {
        int n = word.length();
        byte[] bytes = new byte[n];
        for(int i = 0; i < n; i++)
            bytes[i] = (byte) chars.indexOf(word.charAt(i));
        return bytes;
    }

    public void trackInit(int complexity) {
        int n = wordsAll.size();
        long[] words = new long[n];
        for(int i = 0; i < n; i++){
            words[i] = stringToHash(wordsAll.get(i));
        }
        nativTrackInit(words, complexity);
    }

    public void trackIter(/*View view*/) {
        byte _space[];
        _space = Arrays.copyOf(space, space.length);

        long timeout= System.currentTimeMillis();
        nativTrackIter(_space);
        //nativTrackIter(space);
        timeout = System.currentTimeMillis() - timeout;
        Log.d("BaldaNDK", "time  = " + String.valueOf(timeout));
    }

    public void getWord(/*View view*/) {
        long hash = nativGetWord();
        String word = hashToString(hash);
        Log.d("BaldaNDk", "hashNDK = " + word);

        byte charValue = nativGetCharValue();
        Log.d("BaldaNDk", "charValue = " + chars.substring(charValue, charValue + 1));

        int charIndex = nativGetCharIndex();
        Log.d("BaldaNDk", "charIndex = " + String.valueOf(charIndex));

        if(charIndex != -1){
            wordsAll.add(word);
            wordsAndroid.add(word);
            space[charIndex] = charValue;
        }
    }

    public void showDialog(View view) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getId() == view.getId()) {
                action(i);
                break;
            }
        }
    }

    private void action(int requestCode) {
        if (boolTrack) {
            if (space[requestCode] != 0 && !coordinates.contains(requestCode)) {
                if (coordinates.size() > 0) {
                    int lastI = coordinates.get(coordinates.size() - 1);
                    if ((lastI == requestCode + 5) || (lastI == requestCode - 5)
                            || (lastI == requestCode + 1) || (lastI == requestCode - 1)) {
                        coordinates.add(requestCode);
                    } else {
                        return;
                    }
                } else {
                    coordinates.add(requestCode);
                }

                refresh();
            }
        } else {
            if (space[requestCode] == 0) {
                int i = requestCode;
                if ((i < 20 && space[i + 5] != 0) ||
                        (i > 5 && space[i - 5] != 0) ||
                        (i % 5 < 4 && space[i + 1] != 0) ||
                        (i % 5 > 0 && space[i - 1] != 0)) {
                    Intent intent = new Intent(MainActivity.this, KeyboardActivity.class);
                    intent.putExtra("lang", lang);
                    startActivityForResult(intent, requestCode);
                }
            }
        }
    }

    private void refresh () {
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].setText(String.valueOf(chars.charAt(space[i])));
            if (coordinates.size() > 0 && coordinates.get(0) == i) {
                buttons[i].setTextColor(Color.RED);
            } else if (coordinates.contains(i)) {
                buttons[i].setTextColor(Color.YELLOW);
            } else {
                buttons[i].setTextColor(textColor);
            }
        }

        int n = coordinates.size();
        char[] buffer = new char[n];
        for(int i = 0; i < n; i++)
            buffer[i] = chars.charAt(space[coordinates.get(i)]);
        TextView txtWord = (TextView) findViewById(R.id.txtWord);
        txtWord.setText(buffer, 0, buffer.length);

        TextView txtPlayer = (TextView) findViewById(R.id.txtPlayer);
        TextView txtScorePlayer = (TextView) findViewById(R.id.txtScorePlayer);
        String strWordsPlayer = "";
        int scorePlayer = 0;
        for (String word : wordsUser) {
            if (lang == 1) {
                strWordsPlayer += "<a href=\"balda://TranslateActivityHost?word=" + word + "\">" + word + "</a><br/>";
            } else {
                strWordsPlayer += word + "\n";
            }
            scorePlayer += word.length();
        }
        if (lang == 1) {
            txtPlayer.setText(Html.fromHtml(strWordsPlayer));
            txtPlayer.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            txtPlayer.setText(strWordsPlayer);
        }
        txtScorePlayer.setText(String.format("%s: %d", username, scorePlayer));

        TextView txtAndroid = (TextView) findViewById(R.id.txtAndroid);
        TextView txtScoreAndroid = (TextView) findViewById(R.id.txtScoreAndroid);
        String strWordsAndroid = "";
        int scoreAndroid = 0;
        for (String word : wordsAndroid) {
            if (lang == 1) {
                strWordsAndroid += "<a href=\"balda://TranslateActivityHost?word=" + word + "\">" + word + "</a><br/>";
            } else {
                strWordsAndroid += word + "\n";
            }
            scoreAndroid += word.length();
        }
        if (lang == 1) {
            txtAndroid.setText(Html.fromHtml(strWordsAndroid));
            txtAndroid.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            txtAndroid.setText(strWordsAndroid);
        }
        txtScoreAndroid.setText(String.format("Android: %s", scoreAndroid));
    }

    public void step(View view) {
        if (boolTrack) {
            int n = coordinates.size();
            byte[] bytes = new byte[n];
            char[] wordChars = new char[n];
            for (int i = 0; i < n; i++) {
                bytes[i] = space[coordinates.get(i)];
                wordChars[i] = chars.charAt(bytes[i]);
            }
            String word = String.valueOf(wordChars);
            if (!coordinates.contains(insertCharIndex)) {
                notification(R.string.message_word_not_contain);
            } else if (wordsAll.contains(word)) {
                notification(R.string.message_word_used);
            } else if (nativFindWord(bytes)) {
                boolTrack = false;
                insertCharIndex = -1;
                wordsAll.add(word);
                wordsUser.add(word);
                trackInit(complexity);
                trackIter();
                getWord();
                coordinates.clear();
                if (endGame()) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("scorePlayer", getScorePlayer());
                    intent.putExtra("scoreAndroid", getScoreAndroid());
                    intent.putExtra("isHelp", isHelp);
                    startActivity(intent);
                    finish();
                }
                //refresh();
            } else {
                notification(R.string.message_word_not_found);
            }
        } else {
            notification(R.string.message_select_letter);
        }
        refresh();
    }

    public void cancel(View view) {
        _cancel();
    }

    private void _cancel() {
        boolTrack = false;
        coordinates.clear();
        if (insertCharIndex != -1) {
            space[insertCharIndex] = 0;
            insertCharIndex = -1;
        }
        TextView txtWord = (TextView) findViewById(R.id.txtWord);
        txtWord.setText("");
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        refresh();
    }

    private void miss() {
        _cancel();
        trackInit(complexity);
        trackIter();
        getWord();
        refresh();
        if (endGame()) {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("scorePlayer", getScorePlayer());
            intent.putExtra("scoreAndroid", getScoreAndroid());
            intent.putExtra("isHelp", isHelp);
            startActivity(intent);
            finish();
        }
    }

    private void notification(int stringId) {
        String text = getResources().getString(stringId);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean endGame(){
        for (byte c: space){
            if (c == 0)
                return false;
        }
        return true;
    }

    private int getScorePlayer(){
        int scorePlayer = 0;
        for (String word : wordsUser) {
            scorePlayer += word.length();
        }
        return scorePlayer;
    }

    private int getScoreAndroid(){
        int scoreAndroid = 0;
        for (String word : wordsAndroid) {
            scoreAndroid += word.length();
        }
        return scoreAndroid;
    }

    public void openMenu(View view) {
        openOptionsMenu();
        refresh();
    }
}
