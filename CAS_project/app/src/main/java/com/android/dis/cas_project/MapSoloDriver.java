package com.android.dis.cas_project;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by User on 14.12.2015.
 */
public class MapSoloDriver extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar toolbar;
    public Button backBtn, refreshBtn;
    public String response;
    public LatLng posDriver;
    public double x,y;
    public String name,tech,number,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_driver);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backBtn = (Button) findViewById(R.id.backBtn);
        refreshBtn = (Button) findViewById(R.id.refreshBtn);
        refreshBtn.setVisibility(View.INVISIBLE);

        x = Double.valueOf(getIntent().getStringExtra("loc_x"));
        y = Double.valueOf(getIntent().getStringExtra("loc_y"));
        name = getIntent().getStringExtra("name");
        tech = getIntent().getStringExtra("tech");
        number = getIntent().getStringExtra("number");
        date = getIntent().getStringExtra("date");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initToolbar();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
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

        // Add a marker in Sydney and move the camera
        posDriver = new LatLng(y,x);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(posDriver));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posDriver, 10));

        mMap.addMarker(new MarkerOptions()
                .position(posDriver)
                .title(name)
                .snippet(tech + "\n" + number + "\n" + date));

    }
}
