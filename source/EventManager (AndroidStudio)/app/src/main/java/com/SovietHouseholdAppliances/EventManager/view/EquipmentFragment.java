package com.SovietHouseholdAppliances.EventManager.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.model.Equipment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EquipmentFragment extends Fragment implements EquipmentAdapter.ItemClickListener {

    MainActivity activity;

    Spinner categoryFilter;
    FloatingActionButton addEquipment;
    RecyclerView recyclerView;
    EquipmentAdapter adapter;

    TextView error;

    String currentFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equipment, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = (MainActivity) getActivity();

        addEquipment = activity.findViewById(R.id.equipment_add);
        addEquipment.setOnClickListener(e -> {
            Intent temp = new Intent(activity, EquipmentEditActivity.class);
            temp.putExtra("isEdit", false);
            activity.startActivity(temp);
        });

        recyclerView = activity.findViewById(R.id.equipment_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        activity.viewModel.getEquipments().observe(getViewLifecycleOwner(), equipments -> setEquipments());

        activity.viewModel.refreshEquipments(null);

        categoryFilter = activity.findViewById(R.id.equipment_category_filter);

        activity.viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            List<String> spinnerArray =  new ArrayList<>();
            spinnerArray.add("- all -");
            spinnerArray.addAll(Arrays.asList(categories));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryFilter.setAdapter(adapter);
        });

        categoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = (String) parentView.getItemAtPosition(position);
                if (selected.equals("- all -"))
                    currentFilter = null;
                else
                    currentFilter = selected;
                setEquipments();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        error = activity.findViewById(R.id.equipment_error);
        error.setVisibility(View.GONE);
    }

    private void setEquipments() {
        Equipment[] equipments = activity.viewModel.getEquipments().getValue();
        if (currentFilter != null) {
            List<Equipment> temp = new ArrayList<>(Arrays.asList(equipments));
            int count = temp.size();
            for (int i = 0; i < count; i++)
                if (!temp.get(i).category.equals(currentFilter)) {
                    temp.remove(i);
                    i--;
                    count--;
                }
            equipments = new Equipment[temp.size()];
            for (int i = 0; i < temp.size(); i++)
                equipments[i] = temp.get(i);
        }
        adapter = new EquipmentAdapter(getContext(), equipments);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.viewModel.refreshEquipments(null);
    }

    @Override
    public void onItemClick(View view, int equipmentId) {
        Intent temp = new Intent(activity, EquipmentEditActivity.class);
        temp.putExtra("isEdit", true);
        temp.putExtra("equipmentId", equipmentId);
        startActivityForResult(temp, 6);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6) {
            if (resultCode == RESULT_OK) {
                String conflict = data.getStringExtra("conflict");
                if (conflict != null && !conflict.trim().equals("")) {
                    error.setVisibility(View.VISIBLE);
                    error.setText(conflict);
                }
                else {
                    error.setVisibility(View.GONE);
                    error.setText("");
                }
            }
        }
    }
}