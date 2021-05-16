package com.SovietHouseholdAppliances.EventManager.model;

import androidx.annotation.NonNull;

public class Event
{
    public Integer id;
    public String name;
    public boolean hidden;
    public long start;
    public long end;
    public Equipment[] equipments;

    public Event(Integer id, String name, boolean hidden, long start, long end, Equipment[] equipments)
    {
        this.id = id;
        this.name = name;
        this.hidden = hidden;
        this.start = start;
        this.end = end;
        this.equipments = equipments;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event))
            return false;
        if (id == null || ((Event) obj).id == null)
            return false;
        return id.equals(((Event) obj).id);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}