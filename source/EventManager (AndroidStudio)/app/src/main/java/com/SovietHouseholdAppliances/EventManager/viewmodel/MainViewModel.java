package com.SovietHouseholdAppliances.EventManager.viewmodel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.SovietHouseholdAppliances.EventManager.model.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Class<? extends Fragment>> activeFragment;
    private final MutableLiveData<User> user;
    private final MutableLiveData<String> alert;
    private final MutableLiveData<Event[]> events;

    public MainViewModel() {
        activeFragment = new MutableLiveData<>();
        user = new MutableLiveData<>();
        events = new MutableLiveData<>();
        user.setValue(new User(0, "", "", "", ""));
        events.setValue(new Event[0]);
        alert = new MutableLiveData<>();
        alert.setValue("");
        if (!getKeepLoggedIn() || Preferences.getInstance().getToken().equals("")) {
            clearToken();
            alert.setValue("logout");
        }
        else
            renewToken();
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

    public MutableLiveData<Event[]> getEvents() {
        return events;
    }

    public void renewToken() {
        RetrofitClient.getInstance().getEventManagerApi().getToken(Preferences.getInstance().getToken()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful())
                    Preferences.getInstance().setToken("Bearer " + response.body());
                else
                    alert.setValue("logout");
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                alert.setValue("logout");
            }
        });
    }

    public void refreshUser() {
        RetrofitClient.getInstance().getEventManagerApi().getAuthenticatedUser(Preferences.getInstance().getToken()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.body() != null)
                    user.setValue(response.body());
                else
                    alert.setValue("logout");
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                alert.setValue("logout");
            }
        });
    }

    public void refresEvents(Integer equipmentId, Long from, Long until) {
        RetrofitClient.getInstance().getEventManagerApi().getAllEvents(Preferences.getInstance().getToken(), equipmentId, from, until).enqueue(new Callback<Event[]>() {
            @Override
            public void onResponse(@NonNull Call<Event[]> call, @NonNull Response<Event[]> response) {
                if (response.body() != null)
                    events.setValue(response.body());
                else
                    alert.setValue("logout");
            }
            @Override
            public void onFailure(@NonNull Call<Event[]> call, @NonNull Throwable t) {
                alert.setValue("logout");
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