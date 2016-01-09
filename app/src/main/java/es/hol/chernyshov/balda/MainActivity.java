package es.hol.chernyshov.balda;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends Activity {
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
//    int scorePlayer;
//    int scoreAndroid;

    static {
        System.loadLibrary("balda");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        lang = intent.getIntExtra("lang", 0);
        isRandom = intent.getBooleanExtra("isRandom", false);
        complexity = intent.getIntExtra("complexity", 10);

        AssetManager myAssetManager = getResources().getAssets();
        nativDicInit(myAssetManager, lang);

        String startWord = "";
        if (lang == 1) {
            chars = " abcdefghijklmnopqrstuvwxyz";
            startWord = "panda";
        } else {
            chars = " абвгдежзийклмнопрстуфхцчшщъыьэюя";
            startWord = "балда";
        }

        if (isRandom) {
            Random r = new Random();
            int coontWordLen5 = nativCountWordLen5();
            int randIndex = r.nextInt(coontWordLen5 - 1);
            startWord = hashToString(nativRandomWord(randIndex));
        } else {
            startWord = intent.getStringExtra("startWord");
        }

        wordsAll.add(startWord);

        space = new byte[/*25*/] {
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 1,
                2, 1, 12, 5, 0
        };
        for (int i = 0; i < 5; i++) {
            space[i + 10] = (byte) chars.indexOf(startWord.charAt(i));
        }

        refresh();
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
        switch (view.getId()){
            case R.id.button_0:
                action(0);
                break;
            case R.id.button_1:
                action(1);
                break;
            case R.id.button_2:
                action(2);
                break;
            case R.id.button_3:
                action(3);
                break;
            case R.id.button_4:
                action(4);
                break;
            case R.id.button_5:
                action(5);
                break;
            case R.id.button_6:
                action(6);
                break;
            case R.id.button_7:
                action(7);
                break;
            case R.id.button_8:
                action(8);
                break;
            case R.id.button_9:
                action(9);
                break;
            case R.id.button_10:
                action(10);
                break;
            case R.id.button_11:
                action(11);
                break;
            case R.id.button_12:
                action(12);
                break;
            case R.id.button_13:
                action(13);
                break;
            case R.id.button_14:
                action(14);
                break;
            case R.id.button_15:
                action(15);
                break;
            case R.id.button_16:
                action(16);
                break;
            case R.id.button_17:
                action(17);
                break;
            case R.id.button_18:
                action(18);
                break;
            case R.id.button_19:
                action(19);
                break;
            case R.id.button_20:
                action(20);
                break;
            case R.id.button_21:
                action(21);
                break;
            case R.id.button_22:
                action(22);
                break;
            case R.id.button_23:
                action(23);
                break;
            case R.id.button_24:
                action(24);
                break;
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
                int n = coordinates.size();
                char[] buffer = new char[n];
                for(int i = 0; i < n; i++)
                    buffer[i] = chars.charAt(space[coordinates.get(i)]);
                TextView txtWord = (TextView) findViewById(R.id.txtWord);
                txtWord.setText(buffer, 0, buffer.length);
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
        Button btn;

        btn = (Button) findViewById(R.id.button_0);
        btn.setText(String.valueOf(chars.charAt(space[0])));
        btn = (Button) findViewById(R.id.button_1);
        btn.setText(String.valueOf(chars.charAt(space[1])));
        btn = (Button) findViewById(R.id.button_2);
        btn.setText(String.valueOf(chars.charAt(space[2])));
        btn = (Button) findViewById(R.id.button_3);
        btn.setText(String.valueOf(chars.charAt(space[3])));
        btn = (Button) findViewById(R.id.button_4);
        btn.setText(String.valueOf(chars.charAt(space[4])));

        btn = (Button) findViewById(R.id.button_5);
        btn.setText(String.valueOf(chars.charAt(space[5])));
        btn = (Button) findViewById(R.id.button_6);
        btn.setText(String.valueOf(chars.charAt(space[6])));
        btn = (Button) findViewById(R.id.button_7);
        btn.setText(String.valueOf(chars.charAt(space[7])));
        btn = (Button) findViewById(R.id.button_8);
        btn.setText(String.valueOf(chars.charAt(space[8])));
        btn = (Button) findViewById(R.id.button_9);
        btn.setText(String.valueOf(chars.charAt(space[9])));

        btn = (Button) findViewById(R.id.button_10);
        btn.setText(String.valueOf(chars.charAt(space[10])));
        btn = (Button) findViewById(R.id.button_11);
        btn.setText(String.valueOf(chars.charAt(space[11])));
        btn = (Button) findViewById(R.id.button_12);
        btn.setText(String.valueOf(chars.charAt(space[12])));
        btn = (Button) findViewById(R.id.button_13);
        btn.setText(String.valueOf(chars.charAt(space[13])));
        btn = (Button) findViewById(R.id.button_14);
        btn.setText(String.valueOf(chars.charAt(space[14])));

        btn = (Button) findViewById(R.id.button_15);
        btn.setText(String.valueOf(chars.charAt(space[15])));
        btn = (Button) findViewById(R.id.button_16);
        btn.setText(String.valueOf(chars.charAt(space[16])));
        btn = (Button) findViewById(R.id.button_17);
        btn.setText(String.valueOf(chars.charAt(space[17])));
        btn = (Button) findViewById(R.id.button_18);
        btn.setText(String.valueOf(chars.charAt(space[18])));
        btn = (Button) findViewById(R.id.button_19);
        btn.setText(String.valueOf(chars.charAt(space[19])));

        btn = (Button) findViewById(R.id.button_20);
        btn.setText(String.valueOf(chars.charAt(space[20])));
        btn = (Button) findViewById(R.id.button_21);
        btn.setText(String.valueOf(chars.charAt(space[21])));
        btn = (Button) findViewById(R.id.button_22);
        btn.setText(String.valueOf(chars.charAt(space[22])));
        btn = (Button) findViewById(R.id.button_23);
        btn.setText(String.valueOf(chars.charAt(space[23])));
        btn = (Button) findViewById(R.id.button_24);
        btn.setText(String.valueOf(chars.charAt(space[24])));

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
        txtScorePlayer.setText("Player: " + String.valueOf(scorePlayer));

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
        txtScoreAndroid.setText("Android: " + String.valueOf(scoreAndroid));
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
                notification("Слово не содержит добавленную букву");
            } else if (wordsAll.contains(word)) {
                notification("Слово уже использовано");
            } else if (nativFindWord(bytes)) {
                boolTrack = false;
                insertCharIndex = -1;
                wordsAll.add(word);
                wordsUser.add(word);
                trackInit(complexity);
                trackIter();
                getWord();
                if (endGame()) {
                    notification("End game!");
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("scorePlayer", getScorePlayer());
                    intent.putExtra("scoreAndroid", getScoreAndroid());
                    intent.putExtra("isHelp", isHelp);
                    startActivity(intent);
                    finish();
                }
            } else {
                notification("Слово не найдено");
            }
        } else {
            notification("Выберете слово");
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
            notification("End game!");
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("scorePlayer", getScorePlayer());
            intent.putExtra("scoreAndroid", getScoreAndroid());
            intent.putExtra("isHelp", isHelp);
            startActivity(intent);
            finish();
        }
    }

    private void notification(String text) {
        Log.d("BaldaNDK", text);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(text);
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

}
