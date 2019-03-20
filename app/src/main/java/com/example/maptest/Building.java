package com.example.maptest;

import com.google.android.gms.maps.model.LatLng;

public class Building {
    private String name;
    private MyLatLng location;
    private String description;
    private float price;

    public Building() {
        this.name = "";
        this.location = null;
        this.description = "";
        this.price = 0;
    }

    public Building(String name, MyLatLng location, String description, float price) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyLatLng getLocation() {
        return location;
    }

    public void setLocation(MyLatLng location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
