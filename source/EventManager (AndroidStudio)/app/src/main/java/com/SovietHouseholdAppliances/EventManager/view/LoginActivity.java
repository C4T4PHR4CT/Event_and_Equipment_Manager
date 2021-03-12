package com.SovietHouseholdAppliances.EventManager.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    LoginViewModel viewModel;

    EditText username;
    EditText password;
    CheckBox keep;
    CheckBox remember;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        keep = findViewById(R.id.login_keep);
        remember = findViewById(R.id.login_remember);
        login = findViewById(R.id.login_button);

        username.setText(viewModel.getUsername());
        password.setText(viewModel.getPassword());
        keep.setChecked(viewModel.getKeepLoggedIn());
        remember.setChecked(viewModel.getRememberCredentials());

        if (!keep.isChecked())
            remember.setPaintFlags(remember.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        keep.setOnClickListener(e -> {
            viewModel.setKeepLoggedIn(keep.isChecked());
            if (keep.isChecked()) {
                remember.setClickable(true);
                remember.setPaintFlags(remember.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                remember.setClickable(false);
                remember.setChecked(false);
                remember.setPaintFlags(remember.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        remember.setOnClickListener(e -> {
            viewModel.setRememberCredentials(remember.isChecked());
        });

        login.setOnClickListener(e -> {
            if (viewModel.getRememberCredentials()) {
                viewModel.setUsername(username.getText().toString());
                viewModel.setPassword(password.getText().toString());
            }
            //auth
            finish();
        });
    }

    @Override
    public void onBackPressed () {
        if (!getIntent().getExtras().getBoolean("auth")) {
            viewModel.setRememberCredentials(false);
            finish();
        }
    }
}