package com.SovietHouseholdAppliances.EventManager.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TextView;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.model.Equipment;
import com.SovietHouseholdAppliances.EventManager.model.MyLocalDateTime;
import com.SovietHouseholdAppliances.EventManager.viewmodel.EventEditViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventEditActivity extends AppCompatActivity {

    EventEditViewModel viewModel;

    boolean isEdit;
    int eventId;

    ImageButton cancel;
    ImageButton save;
    FloatingActionButton delete;

    EditText name;

    TextView from_date;
    TextView from_time;
    TextView until_date;
    TextView until_time;
    MyLocalDateTime selected_from;
    MyLocalDateTime selected_until;

    Spinner add_equipment;
    RecyclerView recyclerView;
    EventEditEquipmentAdapter assignedEquipmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        viewModel = new ViewModelProvider(this).get(EventEditViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isEdit = extras.getBoolean("isEdit");
            if (isEdit)
                eventId = extras.getInt("eventId");
        }
        else
            isEdit = false;

        if (isEdit)
            viewModel.refreshEvent(eventId);
        else
            viewModel.refreshEquipments();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        cancel = findViewById(R.id.event_edit_cancel_button);
        save = findViewById(R.id.event_edit_save_button);
        delete = findViewById(R.id.event_edit_delete);
        name = findViewById(R.id.event_edit_name);
        from_date = findViewById(R.id.event_edit_from_date);
        from_time = findViewById(R.id.event_edit_from_time);
        until_date = findViewById(R.id.event_edit_until_date);
        until_time = findViewById(R.id.event_edit_until_time);
        selected_from = new MyLocalDateTime();
        selected_until = new MyLocalDateTime();
        selected_until.dateTime = selected_until.dateTime.plusDays(1);
        from_date.setText(selected_from.getDate());
        from_time.setText(selected_from.getTime());
        until_date.setText(selected_until.getDate());
        until_time.setText(selected_until.getTime());
        add_equipment = findViewById(R.id.event_edit_add_equipment);
        recyclerView = findViewById(R.id.event_edit_equipment_list);

        cancel.setOnClickListener(e -> finish());

        if (isEdit)
            save.setOnClickListener(e -> viewModel.saveEvent());
        else
            save.setOnClickListener(e -> viewModel.createEvent());

        if (isEdit)
            delete.setOnClickListener(e -> viewModel.deleteEvent());
        else
            delete.setVisibility(View.GONE);

        viewModel.getAlert().observe(this, alert -> {
            if (alert.equals("done"))
                finish();
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.renameEvent(name.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        viewModel.getEvent().observe(this, event -> {
            if (!name.getText().toString().equals(event.name))
                name.setText(event.name);
            if (selected_from.getEpochMilis() != event.start && event.start != 0)
            {
                selected_from = new MyLocalDateTime(event.start);
                from_date.setText(selected_from.getDate());
                from_time.setText(selected_from.getTime());
            }
            if (selected_until.getEpochMilis() != event.end && event.end != 0)
            {
                selected_until = new MyLocalDateTime(event.end);
                until_date.setText(selected_until.getDate());
                until_time.setText(selected_until.getTime());
            }

            assignedEquipmentAdapter = new EventEditEquipmentAdapter(this, event.equipments);
            assignedEquipmentAdapter.setClickListener((view, equipment) -> {
                viewModel.removeEquipment(equipment);
            });
            recyclerView.setAdapter(assignedEquipmentAdapter);
        });

        viewModel.getEquipments().observe(this, equipments -> {
            List<Equipment> spinnerArray =  new ArrayList<>();
            spinnerArray.add(new Equipment(-1, "- select -", "", null));
            spinnerArray.addAll(Arrays.asList(equipments));
            ArrayAdapter<Equipment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            add_equipment.setAdapter(adapter);
        });

        add_equipment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Equipment selected = (Equipment) parentView.getItemAtPosition(position);
                if (selected.id >= 0)
                    viewModel.addEquipment(selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        from_date.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selected_from.dateTime = selected_from.dateTime.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth);
                viewModel.setStart(selected_from.getEpochMilis());
                from_date.setText(selected_from.getDate());
            }, selected_from.dateTime.getYear(), selected_from.dateTime.getMonthValue(), selected_from.dateTime.getDayOfMonth());
            dialog.show();
        });

        from_time.setOnClickListener(e -> {
            TimePickerDialog dialog = new TimePickerDialog(this, (view, hour, minute) -> {
                selected_from.dateTime = selected_from.dateTime.withHour(hour).withMinute(minute);
                viewModel.setStart(selected_from.getEpochMilis());
                from_time.setText(selected_from.getTime());
            }, selected_from.dateTime.getHour(), selected_from.dateTime.getMinute(), true);
            dialog.show();
        });

        until_date.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selected_until.dateTime = selected_until.dateTime.withYear(year).withMonth(month).withDayOfMonth(dayOfMonth);
                viewModel.setEnd(selected_until.getEpochMilis());
                until_date.setText(selected_until.getDate());
            }, selected_until.dateTime.getYear(), selected_until.dateTime.getMonthValue(), selected_until.dateTime.getDayOfMonth());
            dialog.show();
        });

        until_time.setOnClickListener(e -> {
            TimePickerDialog dialog = new TimePickerDialog(this, (view, hour, minute) -> {
                selected_until.dateTime = selected_until.dateTime.withHour(hour).withMinute(minute);
                viewModel.setEnd(selected_until.getEpochMilis());
                until_time.setText(selected_until.getTime());
            }, selected_until.dateTime.getHour(), selected_until.dateTime.getMinute(), true);
            dialog.show();
        });
    }

    @Override
    public void onBackPressed () {
        finish();
    }
}