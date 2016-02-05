package com.android.dis.cas_project;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 15.12.2015.
 */
public class MapsZakaz extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public String loc_x,loc_y;
    public Button backBtn;
    public double LAT = 0;
    public double LNG = 0;
    public static String lat = "0";
    public static String lng = "0";
    public static String add = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backBtn = (Button) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("LOGI", "LatLng: " + latLng);
                mMap.clear();
                LAT = latLng.latitude;
                LNG = latLng.longitude;
                lat = "" + LAT;
                lng = "" + LNG;
                Geocoder geoCoder = new Geocoder(
                        getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(
                            LAT,
                            LNG, 1);

                    if (addresses.size() > 0) {
                        for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex();
                             i++)
                            add += addresses.get(0).getAddressLine(i) + "\n";
                    }

                    Toast.makeText(getBaseContext(), add, Toast.LENGTH_SHORT).show();
                    String flag = new String(getIntent().getStringExtra("flag"));
                    if(flag.equals("0"))
                        ZakazActivity.zakaz_address.setText(add);
                    else
                        ZakazActivity.zakaz_address_2.setText(add);

                    LatLng myPosition2 = new LatLng(LAT, LNG);
                    mMap.addMarker(new MarkerOptions()
                            .position(myPosition2)
                            .title(add));
                    add = "";
                    Log.d("LOGI", "myLoc: " + mMap.getMyLocation());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setMyLocationEnabled(true);

        LatLng myPosition = new LatLng(MainActivity.LAT, MainActivity.LNG);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10));

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
