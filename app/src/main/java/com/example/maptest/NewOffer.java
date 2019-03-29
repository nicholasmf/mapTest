package com.example.maptest;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class NewOffer extends AppCompatActivity {

    private LatLng selectedLocation;

    private EditText mFormName;
    private EditText mFormPrice;

    private FireStore mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFormName = findViewById(R.id.offer_name);
        mFormPrice = findViewById(R.id.offer_price);

        mDb = new FireStore();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFormName.getText().toString().matches("") || mFormPrice.getText().toString().matches("")) {
                    Snackbar.make(view, "Info missing", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    String name = mFormName.getText().toString();
                    float price = Float.parseFloat(mFormPrice.getText().toString());

                    Building offer = new Building(name, selectedLocation, null, price);
                    mDb.insertOffer(offer);

                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        selectedLocation = getIntent().getParcelableExtra("selectedLocation");
    }

}
