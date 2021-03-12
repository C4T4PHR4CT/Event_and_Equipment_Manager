package com.SovietHouseholdAppliances.EventManager.model;

public class User
{
    public Integer id;
    public String name;
    public String permission;
    public String organization;

    public User(Integer id, String name, String permission, String organization)
    {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.organization = organization;
    }
}