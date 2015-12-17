package com.android.dis.cas_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.dis.cas_project.R;
import com.android.dis.cas_project.WorkspaceDriver;
import com.android.dis.cas_project.WorkspaceManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 13.12.2015.
 */
public class FragmentMapsDrivers extends AbstractTabFragment implements OnMapReadyCallback {

    private static final int LAYOUT = R.layout.activity_maps_driver;
    private GoogleMap mMap;
    private Toolbar toolbar;
    public double dol,shi;
    public String loc_x,loc_y;
    public Button backBtn, refreshBtn;
    private ProgressDialog dialog;
    public String response;
    public LatLng posDriver;
    public static Context frg_context;
    public SupportMapFragment mapFragment;

    public static FragmentMapsDrivers getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentMapsDrivers fragment = new FragmentMapsDrivers();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_maps_drivers));
        frg_context = context;
        Log.d("LOGII", "3333333-------");
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //view = inflater.inflate(LAYOUT, container, false);
        /*SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

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
        Log.d("LOGII", "3333333");
        initToolbar();
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

                        LatLng myPosition = new LatLng(shi, dol);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10));


                        googleMap.setMyLocationEnabled(true);

                        new RequestTask().execute(getString(R.string.adress_7));

                    }

                }
            });}
       /* mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);*/

            backBtn = (Button) view.findViewById(R.id.backBtn);
            backBtn.setVisibility(view.INVISIBLE);
            refreshBtn = (Button) view.findViewById(R.id.refreshBtn);

            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.clear();
                    new RequestTask().execute(getString(R.string.adress_7));
                }
            });

            dol = WorkspaceManager.st_dol;
            shi = WorkspaceManager.st_shi;
            loc_x = "" + dol;
            loc_y = "" + shi;
            return view;
        }

    public void setContext(Context context) {
        this.context = context;
    }

    private void initToolbar() {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.setVisibility(view.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myPosition = new LatLng(shi, dol);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10));


        mMap.setMyLocationEnabled(true);

        new RequestTask().execute(getString(R.string.adress_7));*/

    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {


            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(params[0]);
                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                //передаем параметры из наших текстбоксов
                //логин
                nameValuePairs.add(new BasicNameValuePair("metka", "ok"));

                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                //получаем ответ от сервера
                //String
                response = hc.execute(postMethod, res);

            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            JSONURL(response.toString());
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(frg_context);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    public void JSONURL(String result) {

        double x,y;
        String name,tech,number,date;
        try {
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            final JSONArray urls = json.getJSONArray("data");
            //проходим циклом по всем нашим параметрам
            for (int i = 0; i < urls.length(); i++) {
                x = Double.valueOf(urls.getJSONObject(i).getString("loc_x").toString());
                y = Double.valueOf(urls.getJSONObject(i).getString("loc_y").toString());
                name = urls.getJSONObject(i).getString("info").toString();
                tech = urls.getJSONObject(i).getString("tech").toString();
                number = urls.getJSONObject(i).getString("number").toString();
                date = urls.getJSONObject(i).getString("date").toString();
                posDriver = new LatLng(y,x);

                if(urls.getJSONObject(i).getString("status").toString().equals("online"))
                    mMap.addMarker(new MarkerOptions()
                        .position(posDriver)
                        .title(name)
                        .snippet(tech + "\n" + number + "\n" + date));
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

    }
}
