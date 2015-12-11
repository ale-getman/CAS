package com.android.dis.cas_project;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by User on 23.10.2015.
 */
public class MyService extends Service {

    final String LOG_TAG = "myLogs";
    public GPSTracker gps;
    public String dolstr,shistr;
    public String time_mil;
    public Date currentDate;
    public Timer mTimer;
    public MyTimerTask mMyTimerTask;
    public String log;
    public double dol,shi;
    public Locale local;
    public SimpleDateFormat df;
    public String add;

    public void onCreate() {
        super.onCreate();
        gps = new GPSTracker(MyService.this);
        GPSsetting();
        local = new Locale("ru","RU");
        //df = DateFormat.getDateTimeInstance (DateFormat.DEFAULT, DateFormat.DEFAULT,local);
        df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",local);
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        someTask();
        log = intent.getStringExtra("login");
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 500, 5000);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    void someTask() {
    }

    public void GPSsetting(){
        currentDate = new Date();

        if(gps.canGetLocation()) {

            dol = gps.getLongitude();
            dolstr = "" + dol;

            shi = gps.getLatitude();
            shistr = "" + shi;
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
                    GPSsetting();
                    currentDate = new Date();
                    time_mil = df.format(currentDate);
                    add="";
                    KorToAdr(shi,dol);
                    new RequestTask().execute(getString(R.string.adress_4));
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                //передаем параметры из наших текстбоксов
                //логин
                nameValuePairs.add(new BasicNameValuePair("login", log));

                nameValuePairs.add(new BasicNameValuePair("loc_x", dolstr));
                nameValuePairs.add(new BasicNameValuePair("loc_y", shistr));

                nameValuePairs.add(new BasicNameValuePair("time", time_mil));

                nameValuePairs.add(new BasicNameValuePair("address", add));
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                //получаем ответ от сервера
                String response = hc.execute(postMethod, res);


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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
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
