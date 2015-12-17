package com.android.dis.cas_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private Toolbar toolbar;
    public EditText login;
    public EditText pass;
    private ProgressDialog dialog;
    public String log,pas;
    public GPSTracker gps;
    public static double LAT,LNG;
    public String dol,shi,time_mil;
    public CheckBox chkbx;
    public SharedPreferences sPref;
    final String SAVED_LOGIN = "LOGIN";
    final String SAVED_PAS = "PAS";
    final String SAVED_STS = "STATUS";
    public String FLAG = "middle";
    public String response, TYPE;
    public Locale local;
    public SimpleDateFormat df;
    public Date currentDate;
    public String lat, lng, add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.button);
        login = (EditText) findViewById(R.id.editText);
        pass = (EditText) findViewById(R.id.editText2);
        chkbx = (CheckBox) findViewById(R.id.checkBox);
        add="";
        loadText();
        currentDate = new Date();
        local = new Locale("ru","RU");
        df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",local);
        time_mil = df.format(currentDate);
        gps = new GPSTracker(MainActivity.this);
        GPSsetting();

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                log = new String(login.getText().toString());
                pas = new String(pass.getText().toString());
                currentDate = new Date();
                time_mil = df.format(currentDate);
                GPSsetting();
                KorToAdr(LAT, LNG);
                Log.d("log", "adr: " + add);
                new RequestTask().execute(getString(R.string.adress));
                if(chkbx.isChecked())
                    saveText();
                else
                    clearSaveText();
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

    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_LOGIN, login.getText().toString());
        ed.putString(SAVED_PAS, pass.getText().toString());
        ed.putString(SAVED_STS, "CHECK");
        ed.commit();
    }

    void clearSaveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_LOGIN, "");
        ed.putString(SAVED_PAS, "");
        ed.putString(SAVED_STS, "");
        ed.commit();
    }

    void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_LOGIN, "");
        String savedText_2 = sPref.getString(SAVED_PAS, "");
        String savedText_3 = sPref.getString(SAVED_STS, "");
        login.setText(savedText);
        pass.setText(savedText_2);

        if(savedText_3.equals("CHECK"))
            chkbx.setChecked(true);
        else
            chkbx.setChecked(false);
    }

    public void GPSsetting(){

        if(gps.canGetLocation()) {
            dol = "" + gps.getLongitude();
            shi = "" + gps.getLatitude();
            LAT = gps.getLatitude();
            LNG = gps.getLongitude();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                //передаем параметры из наших текстбоксов
                //логин
                nameValuePairs.add(new BasicNameValuePair("login", log));
                //пароль
                nameValuePairs.add(new BasicNameValuePair("pass", pas));

                nameValuePairs.add(new BasicNameValuePair("loc_x", dol));

                nameValuePairs.add(new BasicNameValuePair("loc_y", shi));

                nameValuePairs.add(new BasicNameValuePair("time", time_mil));

                nameValuePairs.add(new BasicNameValuePair("address", add));

                nameValuePairs.add(new BasicNameValuePair("network_status", "online"));
                Log.d("LOGI", nameValuePairs.toString());
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                //получаем ответ от сервера
                //String
                response = hc.execute(postMethod, res);
                Log.d("LOGI", response.toString());
                JSONURL(response.toString());




            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            if(FLAG.equals("true"))
                if(TYPE.equals("водитель"))
                {
                    //посылаем на вторую активность полученные параметры
                    Intent intent = new Intent(MainActivity.this, WorkspaceDriver.class);
                    //то что куда мы будем передавать и что, putExtra(куда, что);
                    intent.putExtra(WorkspaceDriver.JsonURL, response.toString());

                    intent.putExtra("login", log);
                    intent.putExtra("password", pas);

                    startActivity(intent);
                }
                else
                {
                    //посылаем на вторую активность полученные параметры
                    Intent intent = new Intent(MainActivity.this, WorkspaceManager.class);
                    //то что куда мы будем передавать и что, putExtra(куда, что);
                    intent.putExtra(WorkspaceManager.JsonURL, response.toString());

                    intent.putExtra("login", log);
                    intent.putExtra("password", pas);

                    startActivity(intent);
                }
            if(FLAG.equals("false"))
                Toast.makeText(MainActivity.this, "Неправильно введенные данные", Toast.LENGTH_SHORT).show();
            if(FLAG.equals("middle"))
                Toast.makeText(MainActivity.this, "Ошибка связи с сервером", Toast.LENGTH_SHORT).show();

            /*
            if(TYPE.equals("водитель"))
                if(FLAG.equals("true"))
                {
                    //посылаем на вторую активность полученные параметры
                    Intent intent = new Intent(MainActivity.this, WorkspaceActivity.class);
                    //то что куда мы будем передавать и что, putExtra(куда, что);
                    intent.putExtra(WorkspaceActivity.JsonURL, response.toString());

                    intent.putExtra("login", log);
                    intent.putExtra("password", pas);

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Неправильно введенные данные", Toast.LENGTH_SHORT).show();
                }
            else
                if(FLAG.equals("true"))
                {
                    //посылаем на вторую активность полученные параметры
                    Intent intent = new Intent(MainActivity.this, WorkspaceManagerActivity.class);
                    //то что куда мы будем передавать и что, putExtra(куда, что);
                    intent.putExtra(WorkspaceManagerActivity.JsonURL, response.toString());

                    intent.putExtra("login", log);
                    intent.putExtra("password", pas);

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Неправильно введенные данные", Toast.LENGTH_SHORT).show();
                }*/
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }


    public void JSONURL(String result) {

        try {
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            final JSONArray urls = json.getJSONArray("data");
            //проходим циклом по всем нашим параметрам
            FLAG = urls.getJSONObject(0).getString("flag").toString();
            TYPE = urls.getJSONObject(0).getString("type").toString();
            Log.d("LOGI", "FLAG: " + FLAG);
            Log.d("LOGI", "TYPE: " + TYPE);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    @Override
    protected void onResume() {
        add="";
        super.onResume();
    }

    public void KorToAdr(double lt, double lg){

        Geocoder geoCoder = new Geocoder(
                getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    lt,
                    lg, 1);

            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex();
                     i++)
                    add += addresses.get(0).getAddressLine(i) + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
