package com.example.maptest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

public class NewOffer extends AppCompatActivity {

    private LatLng selectedLocation;

    private EditText mFormName;
    private EditText mFormPrice;
    private ImageView mImageView;
    private Button mImageButton;

    private FireStore mDb;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFormName = findViewById(R.id.offer_name);
        mFormPrice = findViewById(R.id.offer_price);
        mImageView = findViewById(R.id.offer_image_preview);
        mImageButton = findViewById(R.id.offer_image);

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

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        selectedLocation = getIntent().getParcelableExtra("selectedLocation");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }
}
