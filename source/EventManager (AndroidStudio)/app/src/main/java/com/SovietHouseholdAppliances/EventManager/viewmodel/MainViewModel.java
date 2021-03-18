package com.SovietHouseholdAppliances.EventManager.viewmodel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.SovietHouseholdAppliances.EventManager.model.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Class<? extends Fragment>> activeFragment;
    private final MutableLiveData<User> user;
    private final MutableLiveData<String> alert;

    public MainViewModel() {
        activeFragment = new MutableLiveData<>();
        user = new MutableLiveData<>();
        user.setValue(new User(0, "", "", ""));
        alert = new MutableLiveData<>();
        alert.setValue("");
        if (!getKeepLoggedIn() || Preferences.getInstance().getToken().equals("")) {
            clearToken();
            alert.setValue("logout");
        }
    }

    public void setActiveFragment(Class<? extends Fragment> activeFragment) {
        this.activeFragment.setValue(activeFragment);
    }

    public MutableLiveData<Class<? extends Fragment>> getActiveFragment() {
        return activeFragment;
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void refreshUser() {
        RetrofitClient.getInstance().getEventManagerApi().getUser(Preferences.getInstance().getToken(), "@").enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() != null)
                    user.setValue(response.body());
                else
                    alert.setValue("logout");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<String> getAlert() {
        return alert;
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

    public void clearToken() {
        Preferences.getInstance().setToken("");
    }
}