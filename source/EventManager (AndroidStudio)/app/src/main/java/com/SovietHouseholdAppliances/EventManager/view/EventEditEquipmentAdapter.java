package com.SovietHouseholdAppliances.EventManager.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.model.Equipment;

import java.util.Arrays;
import java.util.List;

public class EventEditEquipmentAdapter extends RecyclerView.Adapter<EventEditEquipmentAdapter.ViewHolder> {

    private List<Equipment> equipments;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    EventEditEquipmentAdapter(Context context, Equipment[] equipments) {
        this.mInflater = LayoutInflater.from(context);
        this.equipments = Arrays.asList(equipments);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_event_edit_equipment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(equipments.get(position).name);
    }

    @Override
    public int getItemCount() {
        return equipments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.event_edit_equipment_row_name);
            delete = itemView.findViewById(R.id.event_edit_equipment_row_delete);
            delete.setOnClickListener(e -> {
                if (mClickListener != null) mClickListener.onItemClick(itemView, getItem(getAdapterPosition()));
            });
        }
    }

    Equipment getItem(int pos) {
        return equipments.get(pos);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, Equipment equipment);
    }
}