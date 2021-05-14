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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    EventAdapter(Context context, Event[] events) {
        this.mInflater = LayoutInflater.from(context);
        this.events = Arrays.asList(events);;
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
        holder.from.setText(formatDate(LocalDateTime.ofEpochSecond(events.get(position).start / 1000, 0, ZoneOffset.ofHours(0))));
        holder.until.setText(formatDate(LocalDateTime.ofEpochSecond(events.get(position).end / 1000, 0, ZoneOffset.ofHours(0))));
        holder.fromDay.setText("(" + LocalDateTime.ofEpochSecond(events.get(position).start / 1000, 0, ZoneOffset.ofHours(0)).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + ")");
        holder.untilDay.setText("(" + LocalDateTime.ofEpochSecond(events.get(position).end / 1000, 0, ZoneOffset.ofHours(0)).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + ")");
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

    Event getItem(int id) {
        return events.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int eventId);
    }

    private String formatDate(LocalDateTime date) {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm").format(date);
    }
}
