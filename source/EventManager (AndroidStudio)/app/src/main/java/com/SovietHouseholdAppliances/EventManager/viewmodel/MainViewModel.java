package com.SovietHouseholdAppliances.EventManager.viewmodel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.SovietHouseholdAppliances.EventManager.model.*;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Class<? extends Fragment>> activeFragment;
    private final MutableLiveData<User> user;

    public MainViewModel() {
        activeFragment = new MutableLiveData<>();
        user = new MutableLiveData<>();
    }

    public void setActiveFragment(Class<? extends Fragment> activeFragment) {
        this.activeFragment.setValue(activeFragment);
    }

    public MutableLiveData<Class<? extends Fragment>> getActiveFragment() {
        return activeFragment;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void refreshUser() {
        setUser(new User(1, "Levente", "employee", "Event-Audio limited company"));
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
}