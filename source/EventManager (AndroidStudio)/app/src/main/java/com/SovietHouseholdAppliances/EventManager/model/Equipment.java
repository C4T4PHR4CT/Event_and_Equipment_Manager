package com.SovietHouseholdAppliances.EventManager.model;

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
}