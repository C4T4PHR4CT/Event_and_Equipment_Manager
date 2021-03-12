package com.SovietHouseholdAppliances.EventManager.view;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SovietHouseholdAppliances.EventManager.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView filter_from;
    TextView filter_until;
    LocalDate filter_selected_from;
    LocalDate filter_selected_until;

    @Override
    public void onResume() {
        super.onResume();

        filter_from = getActivity().findViewById(R.id.event_filter_from);
        filter_until = getActivity().findViewById(R.id.event_filter_until);

        filter_from.setText(formatDate(filter_selected_from));
        filter_until.setText(formatDate(filter_selected_until));

        filter_from.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                filter_selected_from = LocalDate.of(year, month, dayOfMonth);
                filter_from.setText(formatDate(filter_selected_from));
            }, filter_selected_from.getYear(), filter_selected_from.getMonthValue(), filter_selected_from.getDayOfMonth());
            dialog.show();
        });

        filter_until.setOnClickListener(e -> {
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                filter_selected_until = LocalDate.of(year, month, dayOfMonth);
                filter_until.setText(formatDate(filter_selected_until));
            }, filter_selected_until.getYear(), filter_selected_until.getMonthValue(), filter_selected_until.getDayOfMonth());
            dialog.show();
        });
    }

    private String formatDate(LocalDate date)
    {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("filter_selected_from", filter_selected_from);
        outState.putSerializable("filter_selected_until", filter_selected_until);
    }
}