package com.SovietHouseholdAppliances.EventManager.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.model.Equipment;
import com.SovietHouseholdAppliances.EventManager.model.Event;
import com.SovietHouseholdAppliances.EventManager.model.MyLocalDateTime;
import com.SovietHouseholdAppliances.EventManager.viewmodel.EquipmentEditViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipmentEditActivity extends AppCompatActivity {

    EquipmentEditViewModel viewModel;

    boolean isEdit;
    int equipmentId;

    ImageButton cancel;
    ImageButton save;
    FloatingActionButton delete;

    EditText name;
    EditText category;

    Spinner add_event;
    RecyclerView recyclerView;
    EquipmentEditEventAdapter assignedEventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_edit);

        viewModel = new ViewModelProvider(this).get(EquipmentEditViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isEdit = extras.getBoolean("isEdit");
            if (isEdit)
                equipmentId = extras.getInt("equipmentId");
        }
        else
            isEdit = false;

        if (isEdit)
            viewModel.refreshEquipment(equipmentId);
        else
            viewModel.refreshEvents();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        cancel = findViewById(R.id.equipment_edit_cancel_button);
        save = findViewById(R.id.equipment_edit_save_button);
        delete = findViewById(R.id.equipment_edit_delete);
        name = findViewById(R.id.equipment_edit_name);
        category = findViewById(R.id.equipment_edit_category);
        add_event = findViewById(R.id.equipment_edit_add_event);
        recyclerView = findViewById(R.id.equipment_edit_event_list);

        cancel.setOnClickListener(e -> finish());

        if (isEdit)
            save.setOnClickListener(e -> viewModel.saveEquipment());
        else
            save.setOnClickListener(e -> viewModel.createEquipment());

        if (isEdit)
            delete.setOnClickListener(e -> viewModel.deleteEquipment());
        else
            delete.setVisibility(View.GONE);

        viewModel.getAlert().observe(this, alert -> {
            if (!alert.equals("done")) {
                Intent intent = new Intent();
                intent.putExtra("conflict", alert);
                setResult(RESULT_OK, intent);
            }
            if (alert != null && !alert.trim().equals(""))
                finish();
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.renameEquipment(name.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        category.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.changeCategory(category.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        viewModel.getEquipment().observe(this, equipment -> {
            if (!name.getText().toString().equals(equipment.name))
                name.setText(equipment.name);
            if (!category.getText().toString().equals(equipment.category))
                category.setText(equipment.category);

            assignedEventAdapter = new EquipmentEditEventAdapter(this, equipment.events);
            assignedEventAdapter.setClickListener((view, temp) -> viewModel.removeEvent(temp));
            recyclerView.setAdapter(assignedEventAdapter);
        });

        viewModel.getEvents().observe(this, events -> {
            List<Event> spinnerArray =  new ArrayList<>();
            spinnerArray.add(new Event(-1, "- select -", false, 0, 0, new Equipment[0]));
            if (isEdit)
                spinnerArray.add(new Event(-2, "- maintenance -", true, 0, 0, new Equipment[0]));
            spinnerArray.addAll(Arrays.asList(events));
            ArrayAdapter<Event> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            add_event.setAdapter(adapter);
        });

        Activity activity = this;
        add_event.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Event selected = (Event) parentView.getItemAtPosition(position);
                if (selected.id >= 0)
                    viewModel.addEvent(selected);
                if (selected.id == -2) {
                    MyLocalDateTime start = new MyLocalDateTime();
                    start.dateTime = start.dateTime.withSecond(0);
                    MyLocalDateTime end = new MyLocalDateTime();
                    end.dateTime = end.dateTime.plusDays(1).withSecond(0);
                    new DatePickerDialog(activity, (view1, year1, month1, dayOfMonth1) -> {
                        start.dateTime = start.dateTime.withYear(year1).withMonth(month1 + 1).withDayOfMonth(dayOfMonth1);
                        new TimePickerDialog(activity, (view2, hour2, minute2) -> {
                            start.dateTime = start.dateTime.withHour(hour2).withMinute(minute2);
                            end.dateTime = start.dateTime.plusDays(1);
                            new DatePickerDialog(activity, (view3, year3, month3, dayOfMonth3) -> {
                                end.dateTime = end.dateTime.withYear(year3).withMonth(month3 + 1).withDayOfMonth(dayOfMonth3);
                                new TimePickerDialog(activity, (view4, hour4, minute4) -> {
                                    end.dateTime = end.dateTime.withHour(hour4).withMinute(minute4);
                                    viewModel.addEvent(new Event(null, null, true, start.getEpochMilis(), end.getEpochMilis(), null));
                                }, end.dateTime.getHour(), end.dateTime.getMinute(), true).show();
                            }, end.dateTime.getYear(), end.dateTime.getMonthValue() - 1, end.dateTime.getDayOfMonth()).show();
                        }, start.dateTime.getHour(), start.dateTime.getMinute(), true).show();
                    }, start.dateTime.getYear(), start.dateTime.getMonthValue() - 1, start.dateTime.getDayOfMonth()).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    @Override
    public void onBackPressed () {
        finish();
    }
}