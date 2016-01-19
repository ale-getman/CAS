package com.android.dis.cas_project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.dis.cas_project.adapter.TabsFragmentAdapter;

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

public class WorkspaceManager extends AppCompatActivity {

    private static final int LAYOUT = R.layout.workspace_manager;

    private Toolbar toolbar;
    private ViewPager viewPager;

    public static String JsonURL;
    public double dol,shi;
    public static double st_dol,st_shi;
    public String buf,buf_json2;
    public String dolstr,shistr;
    public static String st_dolstr,st_shistr;
    public GPSTracker gps;
    public static String log,pas,type,technic;
    public static String st_log,st_pas;
    public ProgressDialog dialog2;
    public String version,flag;
    public NotificationManager nm;
    public Locale local;
    public SimpleDateFormat df;
    public String add;
    public static String st_json;
    public static String time_mil,response;
    private ProgressDialog dialog;
    public Date currentDate;
    public static double LAT,LNG;
    public String network_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        local = new Locale("ru","RU");
        df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",local);

        /*log = new String(getIntent().getStringExtra("login"));
        pas = new String(getIntent().getStringExtra("password"));
        type = new String(getIntent().getStringExtra("type"));
        technic = new String(getIntent().getStringExtra("technic"));*/

        st_log = log;
        st_pas = pas;
        startService(new Intent(this, MyService.class).putExtra("login", log).putExtra("pas", pas).putExtra("type", type).putExtra("technic", technic));
        network_status = "online";
        //принимаем параметр который мы послылали в manActivity
        Bundle extras = getIntent().getExtras();
        //превращаем в тип стринг для парсинга

        /*String json = extras.getString(JsonURL);
        st_json = json;*/

        gps = new GPSTracker(this);
        GPSsetting();

        initToolbar();
        initTabs();

        check_version();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsFragmentAdapter adapter = new TabsFragmentAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.close_app:
                        stopService(new Intent(WorkspaceManager.this, MyService.class));
                        currentDate = new Date();
                        time_mil = df.format(currentDate);
                        GPSsetting();
                        KorToAdr(LAT, LNG);
                        network_status = "offline";
                        new RequestTask_destroy().execute(getString(R.string.adress));
                        Intent a = new Intent(WorkspaceManager.this,MainActivity.class);
                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MyService.class));
        currentDate = new Date();
        time_mil = df.format(currentDate);
        GPSsetting();
        KorToAdr(LAT, LNG);
        network_status = "offline";
        new RequestTask_destroy().execute(getString(R.string.adress));
        Log.d("LOGI", "destroy destroy");
        super.onDestroy();
    }

    public void GPSsetting(){

        if(gps.canGetLocation()) {

            dol = gps.getLongitude();
            buf = "Долгота: " + dol;
            dolstr = "" + dol;
            st_dol = dol;
            st_dolstr = dolstr;
            LNG = gps.getLongitude();

            shi = gps.getLatitude();
            buf = "Широта: " + shi;
            shistr = "" + shi;
            st_shi = shi;
            st_shistr = shistr;
            LAT = gps.getLatitude();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    public void check_version(){
        version = getString(R.string.versionCode);
        new RequestTask_version().execute(getString(R.string.adress_5));
    }

    class RequestTask_version extends AsyncTask<String, String, String> {

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
                nameValuePairs.add(new BasicNameValuePair("version", version));
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                //получаем ответ от сервера
                String response = hc.execute(postMethod, res);

                buf_json2 = response.toString();

            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog2.dismiss();
            JSONURL_version(buf_json2);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog2 = new ProgressDialog(WorkspaceManager.this);
            dialog2.setMessage("Загружаюсь...");
            dialog2.setIndeterminate(true);
            dialog2.setCancelable(true);
            dialog2.show();
            super.onPreExecute();
        }
    }

    public void JSONURL_version(String result) {

        try {
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            final JSONArray urls = json.getJSONArray("data_version");
            //проходим циклом по всем нашим параметрам
            flag = urls.getJSONObject(0).getString("flag").toString();
            Log.d("LOGI", "FLAG_version: " + flag);

            if(flag.equals("false"))
                sendNotif();

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    void sendNotif() {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String text = "Нажмите, чтобы скачать.";
        Log.d("LOGI", "sendNotif: " + text);
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.adress_8)));

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, browseIntent, 0);

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Есть обновление приложения")
                .setContentText(text)
                .setSmallIcon(R.drawable.logo_app)
                .setContentIntent(pIntent)
                .setTicker("Есть обновление")
                .build();

        // ставим флаг, чтобы уведомление пропало после нажатия
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        noti.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");

        // отправляем
        nm.notify(1, noti);
    }

    @Override
    protected void onPostResume() {
        currentDate = new Date();
        time_mil = df.format(currentDate);
        GPSsetting();
        KorToAdr(LAT, LNG);
        new RequestTask().execute(getString(R.string.adress));
        Log.d("LOGI", "PostResume");
        super.onPostResume();
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

                nameValuePairs.add(new BasicNameValuePair("loc_x", dolstr));

                nameValuePairs.add(new BasicNameValuePair("loc_y", shistr));

                nameValuePairs.add(new BasicNameValuePair("time", time_mil));

                nameValuePairs.add(new BasicNameValuePair("address", add));

                nameValuePairs.add(new BasicNameValuePair("network_status", network_status));
                //Log.d("LOGI", nameValuePairs.toString());
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                //получаем ответ от сервера
                //String
                response = hc.execute(postMethod, res);
                //Log.d("LOGI", response.toString());
                st_json = response;

            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(WorkspaceManager.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    public void KorToAdr(double lt, double lg){

        Geocoder geoCoder = new Geocoder(
                getBaseContext(), Locale.getDefault());
        add = "";
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

    class RequestTask_destroy extends AsyncTask<String, String, String> {

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

                nameValuePairs.add(new BasicNameValuePair("loc_x", dolstr));

                nameValuePairs.add(new BasicNameValuePair("loc_y", shistr));

                nameValuePairs.add(new BasicNameValuePair("time", time_mil));

                nameValuePairs.add(new BasicNameValuePair("address", add));

                nameValuePairs.add(new BasicNameValuePair("network_status", network_status));
                //Log.d("LOGI", nameValuePairs.toString());
                //собераем их вместе и посылаем на сервер
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
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}
