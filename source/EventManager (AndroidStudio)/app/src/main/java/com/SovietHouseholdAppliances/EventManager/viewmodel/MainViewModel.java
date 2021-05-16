package com.SovietHouseholdAppliances.EventManager.viewmodel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.SovietHouseholdAppliances.EventManager.model.*;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> alert;
    private final MutableLiveData<Class<? extends Fragment>> activeFragment;
    private final MutableLiveData<User> user;
    private final MutableLiveData<Event[]> events;
    private final MutableLiveData<Equipment[]> equipments;
    private final MutableLiveData<String[]> categories;

    public MainViewModel() {
        activeFragment = new MutableLiveData<>();
        user = new MutableLiveData<>();
        events = new MutableLiveData<>();
        equipments = new MutableLiveData<>();
        categories = new MutableLiveData<>();
        user.setValue(new User(0, "", "", "", ""));
        events.setValue(new Event[0]);
        equipments.setValue(new Equipment[0]);
        categories.setValue(new String[0]);
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

    public MutableLiveData<Equipment[]> getEquipments() {
        return equipments;
    }

    public MutableLiveData<String[]> getCategories() {
        return categories;
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

    public void refreshEvents(Integer equipmentId, Long from, Long until) {
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

    public void refreshEquipments(Integer eventId) {
        RetrofitClient.getInstance().getEventManagerApi().getAllEquipments(Preferences.getInstance().getToken(), eventId).enqueue(new Callback<Equipment[]>() {
            @Override
            public void onResponse(@NonNull Call<Equipment[]> call, @NonNull Response<Equipment[]> response) {
                if (response.body() != null) {
                    Equipment[] temp1 = response.body();
                    equipments.setValue(temp1);
                    ArrayList<String> temp2 = new ArrayList<>();
                    for (Equipment equipment : temp1)
                        if (!temp2.contains(equipment.category))
                            temp2.add(equipment.category);
                    String[] temp3 = new String[temp2.size()];
                    for (int i = 0; i < temp2.size(); i++)
                        temp3[i] = temp2.get(i);
                    categories.setValue(temp3);
                }
                else
                    alert.setValue("logout");
            }
            @Override
            public void onFailure(@NonNull Call<Equipment[]> call, @NonNull Throwable t) {
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