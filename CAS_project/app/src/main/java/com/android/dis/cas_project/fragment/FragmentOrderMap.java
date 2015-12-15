package com.android.dis.cas_project.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.dis.cas_project.R;
import com.android.dis.cas_project.TabOrder;
import com.android.dis.cas_project.WorkspaceManager;
import com.android.dis.cas_project.maps_path.HttpConnection;
import com.android.dis.cas_project.maps_path.PathJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 14.12.2015.
 */
public class FragmentOrderMap extends AbstractTabFragment{

    private static final int LAYOUT = R.layout.activity_path_google_map;
    public static double dol,shi,lx,ly;
    public static String loc_x,loc_y;
    public static LatLng im, order;
    public static String name_order;
    public static Button route;
    public static Context frg_context;
    public SupportMapFragment mapFragment;
    GoogleMap googleMap;
    private GoogleMap mMap;
    final String TAG = "PathGoogleMapActivity";

    public static FragmentOrderMap getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentOrderMap fragment = new FragmentOrderMap();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_map));
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

        route = (Button) view.findViewById(R.id.route2);

        name_order = TabOrder.text_order;
        dol = TabOrder.dol;
        shi = TabOrder.shi;
        loc_x = TabOrder.loc_x;
        loc_y = TabOrder.loc_y;
        lx = Double.parseDouble(loc_x);
        ly = Double.parseDouble(loc_y);

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
                        mMap = googleMap;
                        googleMap.getUiSettings().setAllGesturesEnabled(true);

                        //CameraPosition cameraPosition = new CameraPosition.Builder().target(marker_latlng).zoom(15.0f).build();
                        //CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        //googleMap.moveCamera(cameraUpdate);

                        im = new LatLng(shi,dol);
                        order = new LatLng(lx,ly);

                        MarkerOptions options = new MarkerOptions();
                        options.position(im);
                        options.position(order);
                        googleMap.addMarker(options);
                        googleMap.setMyLocationEnabled(true);
                        String url = getMapsApiDirectionsUrl();
                        ReadTask downloadTask = new ReadTask();
                        downloadTask.execute(url);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(im, 10));
                        addMarkers();

                        route.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + loc_x + "," + loc_y);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        });


                    }

                }
            });}
       /* mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);*/

        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private String getMapsApiDirectionsUrl() {

        String waypoints = "waypoints=optimize:true|"
                + im.latitude + "," + im.longitude
                + "|" + "|" + order.latitude + ","
                + order.longitude;

        String sensor = "sensor=false";
        String origin = "origin=" + im.latitude + "," + im.longitude;
        String destination = "destination=" + order.latitude + "," + order.longitude;
        String params = origin + "&" + destination + "&%20" + waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;

        return url;
    }

    private void addMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(im)
                    .title("Я здесь"));
            mMap.addMarker(new MarkerOptions().position(order)
                    .title(name_order));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes

            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }
}
