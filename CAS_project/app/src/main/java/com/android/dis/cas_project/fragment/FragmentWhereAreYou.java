package com.android.dis.cas_project.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.dis.cas_project.R;
import com.android.dis.cas_project.WorkspaceDriver;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 13.12.2015.
 */
public class FragmentWhereAreYou extends AbstractTabFragment{

    private static final int LAYOUT = R.layout.activity_maps;
    public static Context frg_context;
    public Button backBtn;
    public SupportMapFragment mapFragment;

    public static FragmentWhereAreYou getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentWhereAreYou fragment = new FragmentWhereAreYou();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_where_are_you));
        frg_context = context;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //view = inflater.inflate(LAYOUT, container, false);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(LAYOUT, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        googleMap.getUiSettings().setAllGesturesEnabled(true);

                        //CameraPosition cameraPosition = new CameraPosition.Builder().target(marker_latlng).zoom(15.0f).build();
                        //CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        //googleMap.moveCamera(cameraUpdate);

                        LatLng myPosition = new LatLng(WorkspaceDriver.st_shi, WorkspaceDriver.st_dol);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10));


                        googleMap.setMyLocationEnabled(true);
                    }

                }
            });}
        backBtn = (Button) view.findViewById(R.id.backBtn);
        backBtn.setVisibility(view.INVISIBLE);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}