package com.android.dis.cas_project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

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
    public String log,pas,type, technic;
    public double dol,shi;
    public Locale local;
    public SimpleDateFormat df;
    public String add;
    public NotificationManager nm;
    public PendingIntent pIntent;
    public String count_table, tech_last_row;
    public int count_table_int;
    public int count_table_local = 0;

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
        pas = intent.getStringExtra("pas");
        type = intent.getStringExtra("type");
        technic = intent.getStringExtra("technic");
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
                    KorToAdr(shi, dol);
                    new RequestTask().execute(getString(R.string.adress_4));
                    if(count_table_local == 0)
                        count_table_local = count_table_int;

                    if(type.equals("водитель"))
                        if(count_table_int > count_table_local)
                            if(technic.equals(tech_last_row))
                            {
                                count_table_local = count_table_int;
                                sendNotif();
                            }
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
                JSONURL(response.toString());

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

    public void JSONURL(String result) {

        try {
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            final JSONArray urls = json.getJSONArray("data");
            //проходим циклом по всем нашим параметрам
            tech_last_row = urls.getJSONObject(0).getString("tech_last_row").toString();
            count_table = urls.getJSONObject(0).getString("count_table").toString();
            count_table_int = Integer.valueOf(count_table);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    void sendNotif() {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String text = "Есть новый заказ.";
        Log.d("LOGI", "sendNotif: " + text);

        if(type.equals("водитель"))
        {
            Intent intent = new Intent(this, WorkspaceDriver.class);

            pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }
        else
        {
            Intent intent = new Intent(this, WorkspaceManager.class);

            pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Новый заказ")
                .setContentText(text)
                .setSmallIcon(R.drawable.logo_app)
                .setContentIntent(pIntent)
                .setTicker("Есть заказ")
                .build();

                //.setContentIntent(pIntent)

        // ставим флаг, чтобы уведомление пропало после нажатия
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        noti.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "1");

        // отправляем
        nm.notify(1, noti);
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
