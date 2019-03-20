package com.example.maptest;

import com.google.android.gms.maps.model.LatLng;

public class MyLatLng {
    private Double latitude;
    private Double longitude;

    public MyLatLng() {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public MyLatLng(Double latitutde, Double longitude) {
        this.latitude = latitutde;
        this.longitude = longitude;
    }

    public Double getLatitutde() {
        return latitude;
    }

    public void setLatitutde(Double latitutde) {
        this.latitude = latitutde;
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
