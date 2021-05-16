package com.SovietHouseholdAppliances.EventManager.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.SovietHouseholdAppliances.EventManager.model.Equipment;
import com.SovietHouseholdAppliances.EventManager.model.Event;
import com.SovietHouseholdAppliances.EventManager.model.MyLocalDateTime;
import com.SovietHouseholdAppliances.EventManager.model.Preferences;
import com.SovietHouseholdAppliances.EventManager.model.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventEditViewModel extends ViewModel {

    private final MutableLiveData<String> alert;
    private final MutableLiveData<Event> event;
    private final MutableLiveData<Equipment[]> equipments;
    private Equipment[] allEquipments;

    public EventEditViewModel() {
        event = new MutableLiveData<>();
        equipments = new MutableLiveData<>();
        alert = new MutableLiveData<>();
        MyLocalDateTime start= new MyLocalDateTime();
        MyLocalDateTime end = new MyLocalDateTime();
        end.dateTime = end.dateTime.plusDays(1);
        event.setValue(new Event(0, "", false, start.getEpochMilis(), end.getEpochMilis(), new Equipment[0]));
        equipments.setValue(new Equipment[0]);
        alert.setValue("");
        allEquipments = new Equipment[0];
    }

    public MutableLiveData<Event> getEvent() {
        return event;
    }

    public MutableLiveData<Equipment[]> getEquipments() {
        return equipments;
    }

    public void refreshEvent(int eventId) {
        RetrofitClient.getInstance().getEventManagerApi().getEvent(Preferences.getInstance().getToken(), eventId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.body() != null) {
                    event.setValue(response.body());
                    refreshEquipments();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {}
        });
    }

    public void refreshEquipments() {
        RetrofitClient.getInstance().getEventManagerApi().getAllEquipments(Preferences.getInstance().getToken(), null).enqueue(new Callback<Equipment[]>() {
            @Override
            public void onResponse(@NonNull Call<Equipment[]> call, @NonNull Response<Equipment[]> response) {
                if (response.body() != null) {
                    allEquipments = response.body();
                    redoAvailableEquipments();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Equipment[]> call, @NonNull Throwable t) {}
        });
    }

    public void saveEvent() {
        RetrofitClient.getInstance().getEventManagerApi().updateEvent(Preferences.getInstance().getToken(), event.getValue().id, event.getValue()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    if (response.errorBody() != null)
                        alert.setValue(response.errorBody().string());
                    else
                        alert.setValue("done");
                } catch (IOException ignored) {}
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {}
        });
    }

    public void createEvent() {
        RetrofitClient.getInstance().getEventManagerApi().createEvent(Preferences.getInstance().getToken(), event.getValue()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    if (response.errorBody() != null)
                        alert.setValue(response.errorBody().string());
                    else
                        alert.setValue("done");
                } catch (IOException ignored) {}
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {}
        });
    }

    public void deleteEvent() {
        RetrofitClient.getInstance().getEventManagerApi().deleteEvent(Preferences.getInstance().getToken(), event.getValue().id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    if (response.errorBody() != null)
                        alert.setValue(response.errorBody().string());
                    else
                        alert.setValue("done");
                } catch (IOException ignored) {}
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {}
        });
    }

    public void renameEvent(String name) {
        Event temp = event.getValue();
        temp.name = name;
        event.setValue(temp);
    }

    public void setStart(long epochMilis) {
        Event temp = event.getValue();
        temp.start = epochMilis;
        event.setValue(temp);
    }

    public void setEnd(long epochMilis) {
        Event temp = event.getValue();
        temp.end = epochMilis;
        event.setValue(temp);
    }

    public void addEquipment(Equipment equipment) {
        Event temp = event.getValue();
        List<Equipment> equipments = new ArrayList<>(Arrays.asList(temp.equipments));
        equipments.add(equipment);
        temp.equipments = new Equipment[equipments.size()];
        for (int i = 0; i < equipments.size(); i++)
            temp.equipments[i] = equipments.get(i);
        event.setValue(temp);
        redoAvailableEquipments();
    }

    public void removeEquipment(Equipment equipment) {
        Event temp = event.getValue();
        List<Equipment> equipments = new ArrayList<>(Arrays.asList(temp.equipments));
        equipments.remove(equipment);
        temp.equipments = new Equipment[equipments.size()];
        for (int i = 0; i < equipments.size(); i++)
            temp.equipments[i] = equipments.get(i);
        event.setValue(temp);
        redoAvailableEquipments();
    }

    public MutableLiveData<String> getAlert() {
        return alert;
    }

    private void redoAvailableEquipments() {
        List<Equipment> temp1 = new ArrayList<>(Arrays.asList(allEquipments));
        Equipment[] assignedEquipments = event.getValue().equipments;
        for (Equipment assignedEquipment : assignedEquipments)
            temp1.remove(assignedEquipment);
        Equipment[] temp2 = new Equipment[temp1.size()];
        for (int i = 0; i < temp1.size(); i++)
            temp2[i] = temp1.get(i);
        equipments.setValue(temp2);
    }
}