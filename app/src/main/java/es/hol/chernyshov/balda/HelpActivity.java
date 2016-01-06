package es.hol.chernyshov.balda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Intent intent = getIntent();
        String[] words = intent.getStringArrayExtra("wordsHelp");

        TextView txtWords = (TextView) findViewById(R.id.txtWords);
        String text = "";
        for (String word : words) {
            text += "<a href=\"balda://TranslateActivityHost?word=" + word + "\">" + word + "</a><br/>";
        }
        txtWords.setText(Html.fromHtml(text));
        txtWords.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
