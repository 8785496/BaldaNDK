package es.hol.chernyshov.balda;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class RegisterActivity extends Activity {
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }
}
