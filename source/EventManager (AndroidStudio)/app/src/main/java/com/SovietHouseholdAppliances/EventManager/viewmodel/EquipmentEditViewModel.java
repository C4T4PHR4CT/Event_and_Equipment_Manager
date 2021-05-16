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

public class EquipmentEditViewModel extends ViewModel {

    private final MutableLiveData<String> alert;
    private final MutableLiveData<Equipment> equipment;
    private final MutableLiveData<Event[]> events;
    private Event[] allEvents;
    private final ArrayList<Event> hiddenEventsRemoveQueue;
    private final ArrayList<Event> hiddenEventsAddQueue;
    private int hiddenEventsAddQueueIndexing;

    public EquipmentEditViewModel() {
        equipment = new MutableLiveData<>();
        events = new MutableLiveData<>();
        alert = new MutableLiveData<>();
        equipment.setValue(new Equipment(0, "", "", new Event[0]));
        events.setValue(new Event[0]);
        alert.setValue("");
        allEvents = new Event[0];
        hiddenEventsRemoveQueue = new ArrayList<>();
        hiddenEventsAddQueue = new ArrayList<>();
        hiddenEventsAddQueueIndexing = -1;
    }

    public MutableLiveData<Equipment> getEquipment() {
        return equipment;
    }

    public MutableLiveData<Event[]> getEvents() {
        return events;
    }

    public void refreshEquipment(int equipmentId) {
        RetrofitClient.getInstance().getEventManagerApi().getEquipment(Preferences.getInstance().getToken(), equipmentId).enqueue(new Callback<Equipment>() {
            @Override
            public void onResponse(@NonNull Call<Equipment> call, @NonNull Response<Equipment> response) {
                if (response.body() != null) {
                    equipment.setValue(response.body());
                    refreshEvents();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Equipment> call, @NonNull Throwable t) {}
        });
    }

    public void refreshEvents() {
        MyLocalDateTime today = new MyLocalDateTime();
        today.dateTime = today.dateTime.minusDays(1).withHour(23).withMinute(59).withSecond(59);
        RetrofitClient.getInstance().getEventManagerApi().getAllEvents(Preferences.getInstance().getToken(), null, today.getEpochMilis(), null).enqueue(new Callback<Event[]>() {
            @Override
            public void onResponse(@NonNull Call<Event[]> call, @NonNull Response<Event[]> response) {
                if (response.body() != null) {
                    allEvents = response.body();
                    redoAvailableEvents();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Event[]> call, @NonNull Throwable t) {}
        });
    }

    public void saveEquipment() {
        Equipment temp = this.equipment.getValue();
        List<Event> events = new ArrayList<>(Arrays.asList(temp.events));
        int count = events.size();
        for (int i = 0; i < count; i++)
            if (events.get(i).id < 0) {
                events.remove(i);
                i--;
                count--;
            }
        temp.events = new Event[events.size()];
        for (int i = 0; i < events.size(); i++)
            temp.events[i] = events.get(i);
        RetrofitClient.getInstance().getEventManagerApi().updateEquipment(Preferences.getInstance().getToken(), temp.id, temp).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                for (Event hiddenEvent : hiddenEventsRemoveQueue)
                    deleteEvent(hiddenEvent);
                for (Event hiddenEvent : hiddenEventsAddQueue)
                    createEvent(hiddenEvent);
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

    public void createEquipment() {
        Equipment temp = this.equipment.getValue();
        List<Event> events = new ArrayList<>(Arrays.asList(temp.events));
        int count = events.size();
        for (int i = 0; i < count; i++)
            if (events.get(i).id < 0) {
                events.remove(i);
                i--;
                count--;
            }
        temp.events = new Event[events.size()];
        for (int i = 0; i < events.size(); i++)
            temp.events[i] = events.get(i);
        RetrofitClient.getInstance().getEventManagerApi().createEquipment(Preferences.getInstance().getToken(), temp).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    for (Event hiddenEvent : hiddenEventsRemoveQueue)
                        deleteEvent(hiddenEvent);
                    for (Event hiddenEvent : hiddenEventsAddQueue)
                        createEvent(hiddenEvent);
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

    public void deleteEquipment() {
        RetrofitClient.getInstance().getEventManagerApi().deleteEquipment(Preferences.getInstance().getToken(), equipment.getValue().id).enqueue(new Callback<String>() {
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

    private void createEvent(Event event) {
        RetrofitClient.getInstance().getEventManagerApi().createEvent(Preferences.getInstance().getToken(), event).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {}
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {}
        });
    }

    private void deleteEvent(Event event) {
        RetrofitClient.getInstance().getEventManagerApi().deleteEvent(Preferences.getInstance().getToken(), event.id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {}
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {}
        });
    }

    public void renameEquipment(String name) {
        Equipment temp = equipment.getValue();
        temp.name = name;
        equipment.setValue(temp);
    }

    public void changeCategory(String category) {
        Equipment temp = equipment.getValue();
        temp.category = category;
        equipment.setValue(temp);
    }

    public void addEvent(Event event) {
        if (event.hidden) {
            event.id = hiddenEventsAddQueueIndexing;
            hiddenEventsAddQueueIndexing--;
            event.name = "Repair";
            event.hidden = true;
            event.equipments = new Equipment[]{new Equipment(equipment.getValue().id, null, null, null)};
            hiddenEventsAddQueue.add(event);
        }
        Equipment temp = this.equipment.getValue();
        List<Event> events = new ArrayList<>(Arrays.asList(temp.events));
        events.add(event);
        temp.events = new Event[events.size()];
        for (int i = 0; i < events.size(); i++)
            temp.events[i] = events.get(i);
        this.equipment.setValue(temp);
        redoAvailableEvents();
    }

    public void removeEvent(Event event) {
        if (event.hidden) {
            hiddenEventsRemoveQueue.add(event);
        }
        Equipment temp = this.equipment.getValue();
        List<Event> events = new ArrayList<>(Arrays.asList(temp.events));
        events.remove(event);
        temp.events = new Event[events.size()];
        for (int i = 0; i < events.size(); i++)
            temp.events[i] = events.get(i);
        this.equipment.setValue(temp);
        redoAvailableEvents();
    }

    public MutableLiveData<String> getAlert() {
        return alert;
    }

    private void redoAvailableEvents() {
        List<Event> temp1 = new ArrayList<>(Arrays.asList(allEvents));
        Event[] assignedEvents = equipment.getValue().events;
        for (Event assignedEvent : assignedEvents)
            temp1.remove(assignedEvent);
        Event[] temp2 = new Event[temp1.size()];
        for (int i = 0; i < temp1.size(); i++)
            temp2[i] = temp1.get(i);
        events.setValue(temp2);
    }
}