package com.SovietHouseholdAppliances.EventManager.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.SovietHouseholdAppliances.EventManager.R;
import com.SovietHouseholdAppliances.EventManager.model.Event;
import com.SovietHouseholdAppliances.EventManager.model.MyLocalDateTime;

import java.util.Arrays;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final List<Event> events;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    EventAdapter(Context context, Event[] events) {
        this.mInflater = LayoutInflater.from(context);
        this.events = Arrays.asList(events);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_event_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(events.get(position).name);
        MyLocalDateTime start = new MyLocalDateTime(events.get(position).start);
        MyLocalDateTime end = new MyLocalDateTime(events.get(position).end);
        holder.from.setText(start.getDateTime());
        holder.until.setText(end.getDateTime());
        holder.fromDay.setText("(" + start.getDay() + ")");
        holder.untilDay.setText("(" + end.getDay() + ")");
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView from;
        TextView fromDay;
        TextView until;
        TextView untilDay;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.event_row_name);
            from = itemView.findViewById(R.id.event_row_from);
            fromDay = itemView.findViewById(R.id.event_row_from_day);
            until = itemView.findViewById(R.id.event_row_until);
            untilDay = itemView.findViewById(R.id.event_row_until_day);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getItem(getAdapterPosition()).id);
        }
    }

    Event getItem(int pos) {
        return events.get(pos);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int eventId);
    }
}