package com.SovietHouseholdAppliances.EventManager.view;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.model.Equipment;
import com.SovietHouseholdAppliances.EventManager.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventFragment extends Fragment implements EventAdapter.ItemClickListener {

    MainActivity activity;

    TextView filter_from;
    TextView filter_until;
    LocalDate filter_selected_from;
    LocalDate filter_selected_until;

    FloatingActionButton addEvent;
    RecyclerView recyclerView;
    EventAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (filter_selected_from == null && filter_selected_until == null)
            if (savedInstanceState == null) {
                filter_selected_from = LocalDateTime.now().toLocalDate();
                filter_selected_until = filter_selected_from.plusMonths(1);
            } else  {
                filter_selected_from = (LocalDate) savedInstanceState.getSerializable("filter_selected_from");
                filter_selected_until = (LocalDate) savedInstanceState.getSerializable("filter_selected_until");
            }
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = (MainActivity) getActivity();

        filter_from = activity.findViewById(R.id.event_filter_from);
        filter_until = activity.findViewById(R.id.event_filter_until);

        filter_from.setText(formatDate(filter_selected_from));
        filter_until.setText(formatDate(filter_selected_until));

        filter_from.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {
                filter_selected_from = LocalDate.of(year, month, dayOfMonth);
                filter_from.setText(formatDate(filter_selected_from));
                activity.viewModel.refresEvents(null, filter_selected_from.toEpochDay() * 24 * 60 * 60 * 1000, filter_selected_until.toEpochDay() * 24 * 60 * 60 * 1000);
            }, filter_selected_from.getYear(), filter_selected_from.getMonthValue(), filter_selected_from.getDayOfMonth());
            dialog.show();
        });

        filter_until.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {
                filter_selected_until = LocalDate.of(year, month, dayOfMonth);
                filter_until.setText(formatDate(filter_selected_until));
                activity.viewModel.refresEvents(null, filter_selected_from.toEpochDay() * 24 * 60 * 60 * 1000, filter_selected_until.toEpochDay() * 24 * 60 * 60 * 1000);
            }, filter_selected_until.getYear(), filter_selected_until.getMonthValue(), filter_selected_until.getDayOfMonth());
            dialog.show();
        });

        addEvent = activity.findViewById(R.id.event_add);
        addEvent.setOnClickListener(e -> {
            MainActivity.print("adding event");
        });

        recyclerView = activity.findViewById(R.id.event_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        activity.viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            adapter = new EventAdapter(getContext(), events);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        });

        activity.viewModel.refresEvents(null, filter_selected_from.toEpochDay() * 24 * 60 * 60 * 1000, filter_selected_until.toEpochDay() * 24 * 60 * 60 * 1000);
    }

    private String formatDate(LocalDate date) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("filter_selected_from", filter_selected_from);
        outState.putSerializable("filter_selected_until", filter_selected_until);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(View view, int eventId) {
        MainActivity.print(eventId+ "");
    }
}