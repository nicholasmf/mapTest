package com.example.maptest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.maps.model.Marker;

public class InfoViewModel extends ViewModel {
    private MutableLiveData<Marker> selectedMarker;

    public MutableLiveData<Marker> getSelectedMarker() {
        if (selectedMarker == null) {
            selectedMarker = new MutableLiveData<>();
        }
        return selectedMarker;
    }
}
