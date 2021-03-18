package com.SovietHouseholdAppliances.EventManager.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.SovietHouseholdAppliances.EventManager.model.Preferences;
import com.SovietHouseholdAppliances.EventManager.model.RetrofitClient;

import java.io.IOException;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> loginState;

    public LoginViewModel() {
        loginState = new MutableLiveData<>();
    }

    public MutableLiveData<String> getLoginState() {
        return loginState;
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

    public void clearToken() {
        Preferences.getInstance().setToken("");
    }

    public void login(String username, String password) {
        if (getRememberCredentials()) {
            setUsername(username);
            setPassword(password);
        }
        String basic = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        RetrofitClient.getInstance().getEventManagerApi().getToken(basic).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Preferences.getInstance().setToken("Bearer " + response.body());
                    loginState.setValue("ok");
                } else if (response.errorBody() != null) {
                    try {
                        loginState.setValue(response.errorBody().string());
                    } catch (IOException ignored) {
                        loginState.setValue(" ");
                    }
                }
                else
                    loginState.setValue(" ");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                loginState.setValue(t.getMessage());
            }
        });
    }
}
