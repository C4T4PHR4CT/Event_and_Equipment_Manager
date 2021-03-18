package com.SovietHouseholdAppliances.EventManager.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.SovietHouseholdAppliances.EventManager.view.MainActivity;

public class Preferences {

    private static Preferences instance;
    private final SharedPreferences preferences;

    private Preferences() {
        Context mainActivity = MainActivity.mainActivity;
        preferences = mainActivity.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public static Preferences getInstance() {
        if (instance == null)
            instance = new Preferences();
        return instance;
    }

    public void setKeepLoggedIn(boolean keepLoggedIn) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("keepLoggedIn", keepLoggedIn);
        if (!keepLoggedIn) {
            //setToken("");
            setRememberCredentials(false);
        }
        editor.apply();
    }

    public boolean getKeepLoggedIn() {
        return preferences.getBoolean("keepLoggedIn", false);
    }

    public void setToken(String token) {
        //if (!getKeepLoggedIn()) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public String getToken() {
        return preferences.getString("token", "");
    }

    public void setRememberCredentials(boolean rememberCredentials) {
        if (!getKeepLoggedIn()) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rememberCredentials", rememberCredentials);
        if (!rememberCredentials) {
            setUsername("");
            setPassword("");
        }
        editor.apply();
    }

    public boolean getRememberCredentials() {
        return preferences.getBoolean("rememberCredentials", false);
    }

    public void setUsername(String username) {
        if (!getRememberCredentials()) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public String getUsername() {
        return preferences.getString("username", "");
    }

    public void setPassword(String password) {
        if (!getRememberCredentials()) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", password);
        editor.apply();
    }

    public String getPassword() {
        return preferences.getString("password", "");
    }
}
