package com.SovietHouseholdAppliances.EventManager.model;

import androidx.annotation.NonNull;

public class User
{
    public Integer id;
    public String name;
    public String password;
    public String permission;
    public String organization;

    public User(Integer id, String name, String password, String permission, String organization)
    {
        this.id = id;
        this.name = name;
        this.password = password;
        this.permission = permission;
        this.organization = organization;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;
        if (id == null || ((User) obj).id == null)
            return false;
        return id.equals(((User) obj).id);
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}