package com.SovietHouseholdAppliances.EventManager;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    CheckBox remember;
    CheckBox cache;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        remember = findViewById(R.id.login_remember);
        cache = findViewById(R.id.login_cache);
        login = findViewById(R.id.login_button);

        cache.setPaintFlags(cache.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        remember.setOnClickListener(e -> {
            if (remember.isChecked())
            {
                cache.setClickable(true);
                cache.setPaintFlags(cache.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            else
            {
                cache.setClickable(false);
                cache.setChecked(false);
                cache.setPaintFlags(cache.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });
    }
}