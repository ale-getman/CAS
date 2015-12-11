package com.android.dis.cas_project;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by User on 11.12.2015.
 */
public class WorkspaceActivity extends Activity {

    public static String JsonURL;
    public static ArrayList<HashMap<String, Object>> myBooks;
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String TECHNIC = "technic";
    public static final String WHO = "who";
    public static final String LOC_X = "loc_x";
    public static final String LOC_Y = "loc_y";
    public static final String TYPE = "type";
    public static final String ADDRESS = "address";
    public static final String DATE = "date";
    public static final String IMAGE_URL = "image_url";

    public ListView listView;
    public double dol,shi;
    public String buf,buf_json,buf_json2;
    public String dolstr,shistr;
    public GPSTracker gps;
    public String log,pas;
    public ProgressDialog dialog,dialog2;
    public MySimpleAdapter adapter;
    public String time_mil;
    public Date currentDate;
    public String version,flag;
    public NotificationManager nm;
    public Locale local;
    public SimpleDateFormat df;
    public String add;


    /** @param result */
    public void JSONURL(String result) {
        listView = (ListView) findViewById(R.id.list);
        myBooks = new ArrayList<HashMap<String, Object>>();

        try {
            //создали читателя json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            //дальше находим вход в наш json им является ключевое слово data
            final JSONArray urls = json.getJSONArray("data");
            //проходим циклом по всем нашим параметрам
            for (int i = 0; i < urls.length(); i++) {
                HashMap<String, Object> hm;
                hm = new HashMap<String, Object>();
                //читаем что в себе хранит параметр firstname

                hm.put(ID, urls.getJSONObject(i).getString("id").toString());

                hm.put(NAME, urls.getJSONObject(i).getString("id").toString() + urls.getJSONObject(i).getString("name").toString() + "-" + urls.getJSONObject(i).getString("date").toString());
                //читаем что в себе хранит параметр lastname

                hm.put(TECHNIC, "Техника: " + urls.getJSONObject(i).getString("technic").toString());

                hm.put(STATUS, urls.getJSONObject(i).getString("status").toString());

                if(urls.getJSONObject(i).getString("status").toString().equals("открыт"))
                    hm.put(WHO, "Заказ не назначен");
                if(urls.getJSONObject(i).getString("status").toString().equals("выполняется"))
                    hm.put(WHO, "Заказ выполняет: " + urls.getJSONObject(i).getString("who").toString());
                if(urls.getJSONObject(i).getString("status").toString().equals("закрыт"))
                    hm.put(WHO, "Заказ выполнил: " + urls.getJSONObject(i).getString("who").toString());

                hm.put(LOC_X, urls.getJSONObject(i).getString("loc_x").toString());

                hm.put(LOC_Y, urls.getJSONObject(i).getString("loc_y").toString());

                hm.put(TYPE, urls.getJSONObject(i).getString("type").toString());

                hm.put(ADDRESS, urls.getJSONObject(i).getString("address").toString());

                hm.put(DATE, urls.getJSONObject(i).getString("date").toString());

                hm.put(IMAGE_URL, urls.getJSONObject(i).getString("image_url").toString());

                if(urls.getJSONObject(i).getString("type").toString().equals("водитель"))
                {

                    if (urls.getJSONObject(i).getString("status").toString().equals("открыт"))
                        if(urls.getJSONObject(i).getString("tech").toString().equals(urls.getJSONObject(i).getString("technic").toString()))
                            myBooks.add(hm);

                    if (urls.getJSONObject(i).getString("status").toString().equals("выполняется") && urls.getJSONObject(i).getString("who").toString().equals(log))
                        myBooks.add(hm);

                    //--------------------------
                    if (urls.getJSONObject(i).getString("status").toString().equals("закрыт") && urls.getJSONObject(i).getString("who").toString().equals(log))
                        myBooks.add(hm);
                    //--------------------------

                    adapter = new MySimpleAdapter(WorkspaceActivity.this, myBooks, R.layout.list,
                            new String[] { NAME, TECHNIC, STATUS, STATUS}, new int[] { R.id.text1, R.id.text2, R.id.text3 , R.id.image_status});
                }
                else
                {
                    myBooks.add(hm);
                    adapter = new MySimpleAdapter(WorkspaceActivity.this, myBooks, R.layout.list_2,
                            new String[]{NAME, TECHNIC, WHO, STATUS, STATUS}, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.image_status});
                }

                //выводим в листвбю
                listView.setAdapter(adapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FF12212f"));
                listView.setDivider(divcolor);
                listView.setDividerHeight(2);
                listView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*Intent intent = new Intent(WorkspaceActivity.this, TabOrder.class);
                        intent.putExtra("login", log);
                        intent.putExtra("password", pas);
                        intent.putExtra("dolgota", dol);
                        intent.putExtra("shirota", shi);
                        intent.putExtra("id", myBooks.get(position).get(ID).toString());
                        intent.putExtra("name", myBooks.get(position).get(NAME).toString());
                        intent.putExtra("technic", myBooks.get(position).get(TECHNIC).toString());
                        intent.putExtra("status", myBooks.get(position).get(STATUS).toString());
                        intent.putExtra("who", myBooks.get(position).get(WHO).toString());
                        intent.putExtra("loc_x", myBooks.get(position).get(LOC_X).toString());
                        intent.putExtra("loc_y", myBooks.get(position).get(LOC_Y).toString());
                        intent.putExtra("type", myBooks.get(position).get(TYPE).toString());
                        intent.putExtra("address", myBooks.get(position).get(ADDRESS).toString());
                        intent.putExtra("image_url", myBooks.get(position).get(IMAGE_URL).toString());
                        startActivity(intent);*/
                    }
                });
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MyService.class));
        Log.d("LOGI", "destroy destroy");
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.workspace);

        local = new Locale("ru","RU");
        df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",local);

        log = new String(getIntent().getStringExtra("login"));
        pas = new String(getIntent().getStringExtra("password"));

        startService(new Intent(this, MyService.class).putExtra("login", log));

        //принимаем параметр который мы послылали в manActivity
        Bundle extras = getIntent().getExtras();
        //превращаем в тип стринг для парсинга
        String json = extras.getString(JsonURL);

        gps = new GPSTracker(WorkspaceActivity.this);
        GPSsetting();

        //передаем в метод парсинга
        JSONURL(json);

        Button refresh = (Button) findViewById(R.id.refresh);
        Button btnmaps = (Button) findViewById(R.id.btnmaps);

        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myBooks.clear();
                GPSsetting();
                currentDate = new Date();
                time_mil = df.format(currentDate);
                add="";
                KorToAdr(shi,dol);
                Log.d("LOGI", "date: " + time_mil);
                new RequestTask().execute(getString(R.string.adress));
            }
        });

        btnmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(WorkspaceActivity.this, MapsActivity.class);
                intent.putExtra("dolgota", dol);
                intent.putExtra("shirota", shi);
                intent.putExtra("flag", "work");
                startActivity(intent);*/

            }
        });

        check_version();
    }


    public void GPSsetting(){

        if(gps.canGetLocation()) {

            dol = gps.getLongitude();
            buf = "Долгота: " + dol;
            dolstr = "" + dol;

            shi = gps.getLatitude();
            buf = "Широта: " + shi;
            shistr = "" + shi;
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                //передаем параметры из наших текстбоксов
                //логин
                nameValuePairs.add(new BasicNameValuePair("login", log));
                //пароль
                nameValuePairs.add(new BasicNameValuePair("pass", pas));

                nameValuePairs.add(new BasicNameValuePair("loc_x", dolstr));
                nameValuePairs.add(new BasicNameValuePair("loc_y", shistr));

                nameValuePairs.add(new BasicNameValuePair("time", time_mil));

                nameValuePairs.add(new BasicNameValuePair("address", add));
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                //получаем ответ от сервера
                String response = hc.execute(postMethod, res);
                Log.d("LOGI", nameValuePairs.toString());
                buf_json = response.toString();
                Log.d("LOGI", buf_json);

            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            JSONURL(buf_json);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(WorkspaceActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }


    class MySimpleAdapter extends SimpleAdapter {

        public MySimpleAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public void setViewText(TextView v, String text) {
            // метод супер-класса, который вставляет текст

            super.setViewText(v, text);
            // если нужный нам TextView, то разрисовываем
            if (v.getId() == R.id.text3) {

                if(text.equals("открыт")) {
                    v.setTextColor(getResources().getColor(R.color.open));
                }
                else
                if(text.equals("закрыт")) {
                    v.setTextColor(getResources().getColor(R.color.close));
                }
                else
                if(text.equals("выполняется")) {
                    v.setTextColor(getResources().getColor(R.color.inwork));
                }
                else
                    v.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void setViewImage(ImageView v, String value) {
            super.setViewImage(v, value);

            if (v.getId() == R.id.image_status) {

                if(value.equals("открыт")) {
                    v.setImageResource(R.drawable.ic_open);
                }
                else
                if(value.equals("закрыт")) {
                    v.setImageResource(R.drawable.ic_close);
                }
                else
                if(value.equals("выполняется")) {
                    v.setImageResource(R.drawable.ic_inwork);
                }
            }
        }
    }

    @Override
    protected void onResume() {

        myBooks.clear();
        GPSsetting();
        currentDate = new Date();
        time_mil = df.format(currentDate);
        new RequestTask().execute(getString(R.string.adress));
        Log.d("LOGI", "resume resume");

        super.onResume();
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

            dialog2 = new ProgressDialog(WorkspaceActivity.this);
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