package com.example.maptest;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Building {
    private String name;
    private MyLatLng location;
    private String description;
    private float price;
    private String type;
    private ArrayList<String> images;

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

    public Building(String name, LatLng location, String description, float price, String type, ArrayList images) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.images = images;
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

    public LatLng getLatLng() {
        return location.getLatLng();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
