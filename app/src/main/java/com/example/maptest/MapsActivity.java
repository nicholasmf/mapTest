package com.example.maptest;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
                    info_panel.OnFragmentInteractionListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private ConstraintLayout mMainLayout;
    private GoogleMap mMap;
    private FireStore db;
    private TextView mName;
    private FloatingActionButton mNewButton;
    private info_panel mInfoPanel;
    private MaterialCardView mInfoCard;
    private TextView mInfoCardTitle;
    private TextView mInfoCardPrice;

    private InfoViewModel mInfoViewModel;

    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;
    private Building selectedBuilding;
    private HashMap<String, Building> loadedBuildings = new HashMap<>();
    private ArrayList viewImages = new ArrayList();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mMainLayout = findViewById(R.id.main_layout);
        mName = findViewById(R.id.name);
        mInfoPanel = (info_panel) getSupportFragmentManager().findFragmentById(R.id.info_panel_fragment);
        getSupportFragmentManager().beginTransaction()
                .hide(mInfoPanel)
                .commit();
        mInfoCard = findViewById(R.id.info_card);
        mInfoCardTitle = findViewById(R.id.info_card__title);
        mInfoCardPrice = findViewById(R.id.info_card__price);

        db = new FireStore();
        mInfoViewModel = ViewModelProviders.of(this).get(InfoViewModel.class);

        mRecyclerView = findViewById(R.id.info_card__image_container);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(viewImages);
        mRecyclerView.setAdapter(mAdapter);

        mNewButton = findViewById(R.id.newButton);
        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, SelectionMap.class);
                mCameraPosition = mMap.getCameraPosition();
                intent.putExtra("mLocationPermissionGranted", mLocationPermissionGranted);
                intent.putExtra("mLastKnownLocation", mLastKnownLocation);
                intent.putExtra("mCameraPosition", mCameraPosition);
                startActivityForResult(intent, 1);
            }
        });

        mInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = mInfoCard.getLayoutParams();
                layoutParams.height = -1;
                layoutParams.width = -1;
                mInfoCard.setLayoutParams(layoutParams);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                getAllOffers();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState, outPersistentState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        if (selectedItem == R.id.create) {
            Intent intent = new Intent(MapsActivity.this, SelectionMap.class);
            mCameraPosition = mMap.getCameraPosition();
            intent.putExtra("mLocationPermissionGranted", mLocationPermissionGranted);
            intent.putExtra("mLastKnownLocation", mLastKnownLocation);
            intent.putExtra("mCameraPosition", mCameraPosition);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("panel click", "clicked");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = (TextView) infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = (TextView) infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                getSupportFragmentManager().beginTransaction()
//                        .hide(mInfoPanel)
//                        .commit();

                mInfoCard.setVisibility(View.GONE);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                mInfoViewModel.getSelectedMarker().setValue(marker);
//                getSupportFragmentManager().beginTransaction()
//                        .show(mInfoPanel)
//                        .commit();
                ImageView imageView = findViewById(R.id.info_card__image);

                selectedBuilding = loadedBuildings.get(marker.getSnippet());
                if (selectedBuilding.getImages() != null && selectedBuilding.getImages().size() > 0) {
                    viewImages.addAll(selectedBuilding.getImages());
                    Log.d("new images", String.format("%d", mAdapter.getItemCount()));
//                    setPic(imageView, selectedBuilding.getImages().get(0));
                }
                viewImages.clear();
                mAdapter.notifyDataSetChanged();
                mInfoCardTitle.setText(marker.getTitle());
                mInfoCardPrice.setText(String.format(Locale.UK, "R$ %.2f", selectedBuilding.getPrice()));
                mInfoCard.setVisibility(View.VISIBLE);

                return true;
            }
        });

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();

        getAllOffers();
    }

    public void onPanelClick(View v) {
        Log.d("info panel", "clicked");
    }

    private void getAllOffers() {
        db.getAllOffers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Building data = document.toObject(Building.class);
                        loadedBuildings.put(document.getId(), data);
                        mMap.addMarker(new MarkerOptions()
                                .position(data.getLatLng())
                                .title(data.getName())
                                .snippet(document.getId()));
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private class LoadData extends AsyncTask<String, Integer, Long> implements DatabaseLoaded {
        Building data;

        @Override
        protected Long doInBackground(String... strings) {
            db.getDocument("buildings", "batata", this);
            try {
                while(data == null) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.getStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            mName.setText(data.getName());

            mMap.addMarker(new MarkerOptions().position(data.getLatLng()).title(data.getName()));
        }

        @Override
        public void databaseLoaded(Building building) {
            this.data = building;
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch(requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            CircleOptions circleOptions = new CircleOptions()
                                    .center(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                                    .fillColor(Color.argb(150, 30, 30,30))
                                    .strokeColor(Color.argb(150, 30, 30,30))
                                    .radius(1000);
                            Circle circle = mMap.addCircle(circleOptions);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setPic(ImageView mImageView, String filePath) {
//        int targetW = Math.max(mImageView.getWidth(), 80);
//        int targetH = Math.max(mImageView.getHeight(), 80);
//
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, bmOptions);
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        int scaleFactor = Math.min(photoW/targetW, photoH/ targetH);
//        scaleFactor = Math.min(scaleFactor, 80);
//
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

//        try {
//            URL url = new URL("https://unsplash.it/300/300");
//            Bitmap bmp = BitmapFactory.decodeStream(url.openStream());
//            mImageView.setImageBitmap(bmp);
//        } catch (Exception e) {
//            Log.e("error on file stream", "Error on " + e.getMessage());
//            e.getStackTrace();
//        }

        AsyncTask downloadImageTask = new DownloadImageTask(mImageView).execute(filePath);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
