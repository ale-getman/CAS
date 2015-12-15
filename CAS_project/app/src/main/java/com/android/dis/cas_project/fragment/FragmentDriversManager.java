package com.android.dis.cas_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.dis.cas_project.GPSTracker;
import com.android.dis.cas_project.MapSoloDriver;
import com.android.dis.cas_project.R;
import com.android.dis.cas_project.WorkspaceManager;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by User on 13.12.2015.
 */
public class FragmentDriversManager extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.list_driver;
    private ProgressDialog dialog;
    public String response;
    public ListView list;
    public MySimpleAdapter adapter;
    public static final String x = "x";
    public static final String y = "y";
    public static final String name = "name";
    public static final String tech = "tech";
    public static final String number = "number";
    public static final String date = "date";
    public static final String adr = "address";
    public static ArrayList<HashMap<String, Object>> myBooks;
    public static Context frg_context;
    public String time_mil;
    public Locale local;
    public SimpleDateFormat df;
    public Date currentDate;
    public String dol,shi,add;
    public static double LAT,LNG;
    public GPSTracker gps;


    public static FragmentDriversManager getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentDriversManager fragment = new FragmentDriversManager();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_drivers_manager));
        frg_context = context;
        Log.d("LOGII", "222222---");
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        Log.d("LOGII", "2222222");
        list = (ListView) view.findViewById(R.id.list_driver);
        local = new Locale("ru","RU");
        df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",local);
        gps = new GPSTracker(frg_context);
        new RequestTask().execute(getString(R.string.adress_7));
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
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

                hm.put(x,urls.getJSONObject(i).getString("loc_x").toString());
                hm.put(y,urls.getJSONObject(i).getString("loc_y").toString());
                hm.put(name, urls.getJSONObject(i).getString("info").toString());
                hm.put(tech, urls.getJSONObject(i).getString("tech").toString());
                hm.put(number, urls.getJSONObject(i).getString("number").toString());
                hm.put(date, urls.getJSONObject(i).getString("date").toString());
                hm.put(adr, urls.getJSONObject(i).getString("address").toString());

                myBooks.add(hm);

                adapter = new MySimpleAdapter(frg_context, myBooks, R.layout.list_driver_adapter,
                        new String[] { name, tech, number, date, adr}, new int[] { R.id.text1, R.id.text2, R.id.text3 , R.id.text4 , R.id.text5});
                list.setAdapter(adapter);
                list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FFD6D6D6"));
                list.setDivider(divcolor);
                list.setDividerHeight(3);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(frg_context, MapSoloDriver.class);
                        intent.putExtra("loc_x", myBooks.get(position).get(x).toString());
                        intent.putExtra("loc_y", myBooks.get(position).get(y).toString());
                        intent.putExtra("name", myBooks.get(position).get(name).toString());
                        intent.putExtra("tech", myBooks.get(position).get(tech).toString());
                        intent.putExtra("number", myBooks.get(position).get(number).toString());
                        intent.putExtra("date", myBooks.get(position).get(date).toString());
                        startActivity(intent);
                    }
                });
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
    }

    /*@Override
    public void onResume() {
        currentDate = new Date();
        time_mil = df.format(currentDate);
        GPSsetting();
        KorToAdr(LAT, LNG);
        new RequestTask().execute(getString(R.string.adress_7));
        super.onResume();
    }*/

    public void KorToAdr(double lt, double lg){

        Geocoder geoCoder = new Geocoder(
                frg_context, Locale.getDefault());
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
}
