package com.SovietHouseholdAppliances.EventManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    CheckBox remember;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        remember = findViewById(R.id.login_remember);
        login = findViewById(R.id.login_button);

        login.setOnClickListener(e -> {
            //auth
            finish();
        });
    }

    @Override
    public void onBackPressed () {}
}