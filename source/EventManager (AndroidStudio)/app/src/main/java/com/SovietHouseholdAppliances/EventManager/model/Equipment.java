package com.SovietHouseholdAppliances.EventManager.model;

import androidx.annotation.NonNull;

public class Equipment
{
    public Integer id;
    public String name;
    public String category;
    public Event[] events;

    public Equipment(Integer id, String name, String category, Event[] events)
    {
        this.id = id;
        this.name = name;
        this.category = category;
        this.events = events;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Equipment))
            return false;
        return id.equals(((Equipment) obj).id);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}