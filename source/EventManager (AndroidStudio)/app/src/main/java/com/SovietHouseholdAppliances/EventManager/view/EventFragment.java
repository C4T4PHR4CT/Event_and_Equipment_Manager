package com.SovietHouseholdAppliances.EventManager.view;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.SovietHouseholdAppliances.EventManager.model.MyLocalDateTime;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventFragment extends Fragment implements EventAdapter.ItemClickListener {

    MainActivity activity;

    TextView filter_from;
    TextView filter_until;
    MyLocalDateTime filter_selected_from;
    MyLocalDateTime filter_selected_until;

    FloatingActionButton addEvent;
    RecyclerView recyclerView;
    EventAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (filter_selected_from == null && filter_selected_until == null)
            if (savedInstanceState == null) {
                filter_selected_from = new MyLocalDateTime();
                filter_selected_from.dateTime = filter_selected_from.dateTime.withHour(0).withMinute(0).withSecond(0);
                filter_selected_until = new MyLocalDateTime();
                filter_selected_until.dateTime = filter_selected_until.dateTime.plusMonths(1).withHour(0).withMinute(0).withSecond(0);
            } else  {
                filter_selected_from = new MyLocalDateTime((LocalDateTime) savedInstanceState.getSerializable("filter_selected_from"));
                filter_selected_until = new MyLocalDateTime((LocalDateTime) savedInstanceState.getSerializable("filter_selected_until"));
            }
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = (MainActivity) getActivity();

        filter_from = activity.findViewById(R.id.event_filter_from);
        filter_until = activity.findViewById(R.id.event_filter_until);

        filter_from.setText(filter_selected_from.getDate());
        filter_until.setText(filter_selected_until.getDate());

        filter_from.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {
                filter_selected_from = new MyLocalDateTime(LocalDate.of(year, month, dayOfMonth));
                filter_from.setText(filter_selected_from.getDate());
                activity.viewModel.refreshEvents(null, filter_selected_from.getEpochMilis(), filter_selected_until.getEpochMilis());
            }, filter_selected_from.dateTime.getYear(), filter_selected_from.dateTime.getMonthValue(), filter_selected_from.dateTime.getDayOfMonth());
            dialog.show();
        });

        filter_until.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {
                filter_selected_until = new MyLocalDateTime(LocalDate.of(year, month, dayOfMonth));
                filter_until.setText(filter_selected_until.getDate());
                activity.viewModel.refreshEvents(null, filter_selected_from.getEpochMilis(), filter_selected_until.getEpochMilis());
            }, filter_selected_until.dateTime.getYear(), filter_selected_until.dateTime.getMonthValue(), filter_selected_until.dateTime.getDayOfMonth());
            dialog.show();
        });

        addEvent = activity.findViewById(R.id.event_add);
        addEvent.setOnClickListener(e -> {
            Intent temp = new Intent(activity, EventEditActivity.class);
            temp.putExtra("isEdit", false);
            activity.startActivity(temp);
        });

        recyclerView = activity.findViewById(R.id.event_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        activity.viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            adapter = new EventAdapter(getContext(), events);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        });

        activity.viewModel.refreshEvents(null, filter_selected_from.getEpochMilis(), filter_selected_until.getEpochMilis());
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.viewModel.refreshEvents(null, filter_selected_from.getEpochMilis(), filter_selected_until.getEpochMilis());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("filter_selected_from", filter_selected_from.dateTime);
        outState.putSerializable("filter_selected_until", filter_selected_until.dateTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(View view, int eventId) {
        Intent temp = new Intent(activity, EventEditActivity.class);
        temp.putExtra("isEdit", true);
        temp.putExtra("eventId", eventId);
        activity.startActivity(temp);
    }
}