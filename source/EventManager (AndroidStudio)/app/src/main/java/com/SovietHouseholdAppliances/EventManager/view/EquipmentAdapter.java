package com.SovietHouseholdAppliances.EventManager.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.model.Equipment;

import java.util.Arrays;
import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {

    private final List<Equipment> equipments;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    EquipmentAdapter(Context context, Equipment[] equipments) {
        this.mInflater = LayoutInflater.from(context);
        this.equipments = Arrays.asList(equipments);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_equipment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(equipments.get(position).name);
        holder.category.setText(equipments.get(position).category);
    }

    @Override
    public int getItemCount() {
        return equipments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView category;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.equipment_row_name);
            category = itemView.findViewById(R.id.equipment_row_category);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getItem(getAdapterPosition()).id);
        }
    }

    Equipment getItem(int pos) {
        return equipments.get(pos);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int equipmentId);
    }
}