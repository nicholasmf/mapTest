package com.example.maptest;

import com.google.android.gms.maps.model.LatLng;

public class MyLatLng {
    private Double latitude;
    private Double longitude;

    public MyLatLng() {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public MyLatLng(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }
}
