package com.android.dis.cas_project;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.dis.cas_project.adapter.TabsFragmentAdapter;
import com.android.dis.cas_project.adapter.TabsFragmentAdapter_2;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 11.12.2015.
 */
public class WorkspaceDriver extends AppCompatActivity {

    private static final int LAYOUT = R.layout.workspace_driver;

    private Toolbar toolbar;
    private ViewPager viewPager;

    public static String JsonURL;
    public double dol, shi;
    public static double st_dol, st_shi;
    public String buf, buf_json2;
    public String dolstr, shistr;
    public static String st_dolstr,st_shistr;
    public GPSTracker gps;
    public String log, pas;
    public static String st_log, st_pas;
    public ProgressDialog dialog2;
    public String version, flag;
    public NotificationManager nm;
    public Locale local;
    public SimpleDateFormat df;
    public String add;
    public static String st_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        local = new Locale("ru", "RU");
        df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", local);

        log = new String(getIntent().getStringExtra("login"));
        pas = new String(getIntent().getStringExtra("password"));
        st_log = log;
        st_pas = pas;
        startService(new Intent(this, MyService.class).putExtra("login", log));

        //принимаем параметр который мы послылали в manActivity
        Bundle extras = getIntent().getExtras();
        //превращаем в тип стринг для парсинга
        String json = extras.getString(JsonURL);
        st_json = json;

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
        TabsFragmentAdapter_2 adapter = new TabsFragmentAdapter_2(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MyService.class));
        Log.d("LOGI", "destroy destroy");
        super.onDestroy();
    }

    public void GPSsetting() {

        if (gps.canGetLocation()) {

            dol = gps.getLongitude();
            buf = "Долгота: " + dol;
            dolstr = "" + dol;
            st_dolstr = dolstr;
            st_dol = dol;

            shi = gps.getLatitude();
            buf = "Широта: " + shi;
            shistr = "" + shi;
            st_shistr = shistr;
            st_shi = shi;
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    public void check_version() {
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

            dialog2 = new ProgressDialog(WorkspaceDriver.this);
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

            if (flag.equals("false"))
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
}
