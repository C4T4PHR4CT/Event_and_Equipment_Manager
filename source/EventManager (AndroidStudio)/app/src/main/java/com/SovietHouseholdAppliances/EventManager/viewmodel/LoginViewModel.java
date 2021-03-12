package com.SovietHouseholdAppliances.EventManager.viewmodel;

import androidx.lifecycle.ViewModel;

import com.SovietHouseholdAppliances.EventManager.model.Preferences;

public class LoginViewModel extends ViewModel {

    public LoginViewModel() {

    }

    public void setKeepLoggedIn(boolean keepLoggedIn) {
        Preferences.getInstance().setKeepLoggedIn(keepLoggedIn);
    }

    public boolean getKeepLoggedIn() {
        return Preferences.getInstance().getKeepLoggedIn();
    }

    public void setRememberCredentials(boolean rememberCredentials) {
        Preferences.getInstance().setRememberCredentials(rememberCredentials);
    }

    public boolean getRememberCredentials() {
        return Preferences.getInstance().getRememberCredentials();
    }

    public void setUsername(String username) {
        Preferences.getInstance().setUsername(username);
    }

    public String getUsername() {
        return Preferences.getInstance().getUsername();
    }

    public void setPassword(String password) {
        Preferences.getInstance().setPassword(password);
    }

    public String getPassword() {
        return Preferences.getInstance().getPassword();
    }
}
