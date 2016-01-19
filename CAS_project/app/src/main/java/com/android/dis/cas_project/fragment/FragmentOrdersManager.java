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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dis.cas_project.GPSTracker;
import com.android.dis.cas_project.MainActivity;
import com.android.dis.cas_project.R;
import com.android.dis.cas_project.TabOrder;
import com.android.dis.cas_project.WorkspaceDriver;
import com.android.dis.cas_project.WorkspaceManager;
import com.android.dis.cas_project.ZakazActivity;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by User on 13.12.2015.
 */
public class FragmentOrdersManager extends AbstractTabFragment{

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
    public static ListView listView;
    public MySimpleAdapter adapter;
    public static Context frg_context;
    private static final int LAYOUT = R.layout.fragment_orders_manager;
    public static FloatingActionButton fab;
    private ProgressDialog dialog;
    public String response, time_mil;
    public Locale local;
    public SimpleDateFormat df;
    public Date currentDate;
    public String dol,shi,add;
    public String log,pas;
    public static double LAT,LNG;
    public GPSTracker gps;

    public static FragmentOrdersManager getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentOrdersManager fragment = new FragmentOrdersManager();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_orders_manager));
        frg_context = context;
        Log.d("LOGII", "1111111----");
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
        Log.d("LOGII", "1111111");
        listView = (ListView) view.findViewById(R.id.list);
        local = new Locale("ru","RU");
        df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",local);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(frg_context, ZakazActivity.class);
                startActivity(intent);
            }
        });
        JSONURL(WorkspaceManager.st_json);
        log = WorkspaceManager.st_log;
        pas = WorkspaceManager.st_pas;
        gps = new GPSTracker(frg_context);
        /*listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int state = 0;
            int lastFirstVisibleElement = 0;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //Log.d("LOGI", "scrollState = " + scrollState);
                state = -1;
                //Log.d("LOGIII", "lastVis_1: " + lastFirstVisibleElement);
                if(lastFirstVisibleElement == 0)
                {
                    onResume();
                    JSONURL(WorkspaceManager.st_json);
                    Log.d("LOGIII", "lastVis_2: " + lastFirstVisibleElement);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                *//*Log.d("LOGI", "scroll: firstVisibleItem = " + firstVisibleItem
                        + ", visibleItemCount" + visibleItemCount
                        + ", totalItemCount" + totalItemCount);*//*
                //lastFirstVisibleElement = firstVisibleItem;
                if (lastFirstVisibleElement > firstVisibleItem){
                    //Log.d("LOGII", "Scroll up");
                }
                else if (lastFirstVisibleElement < firstVisibleItem){
                    //Log.d("LOGII", "Scroll down");
                }
                lastFirstVisibleElement = firstVisibleItem;
                //Log.d("LOGII", "lastvisi_3: " + lastFirstVisibleElement);
            }
        });*/
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /** @param result */
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

                    if (urls.getJSONObject(i).getString("status").toString().equals("выполняется") && urls.getJSONObject(i).getString("who").toString().equals(WorkspaceManager.st_log))
                        myBooks.add(hm);

                    //--------------------------
                    if (urls.getJSONObject(i).getString("status").toString().equals("закрыт") && urls.getJSONObject(i).getString("who").toString().equals(WorkspaceManager.st_log))
                        myBooks.add(hm);
                    //--------------------------

                    adapter = new MySimpleAdapter(frg_context, myBooks, R.layout.list,
                            new String[] { NAME, TECHNIC, STATUS, STATUS}, new int[] { R.id.text1, R.id.text2, R.id.text3, R.id.image_status});
                }
                else
                {
                    myBooks.add(hm);
                    adapter = new MySimpleAdapter(frg_context, myBooks, R.layout.list_2,
                            new String[]{NAME, TECHNIC, WHO, STATUS}, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.image_status});
                }

                Collections.sort(myBooks, new MapComparator());

                //выводим в листвбю
                listView.setAdapter(adapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FFD6D6D6"));
                listView.setDivider(divcolor);
                listView.setDividerHeight(3);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(frg_context, TabOrder.class);
                        intent.putExtra("login", WorkspaceManager.st_log);
                        intent.putExtra("password", WorkspaceManager.st_pas);
                        intent.putExtra("dolgota", WorkspaceManager.st_dol);
                        intent.putExtra("shirota", WorkspaceManager.st_shi);
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
                        startActivity(intent);
                    }
                });

            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

    }

    class MapComparator implements Comparator<HashMap<String, Object>>
    {
        @Override
        public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {

            return rhs.get(ID).toString().compareTo(lhs.get(ID).toString());
        }
    }

    class MySimpleAdapter extends SimpleAdapter {

        public MySimpleAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public void setViewImage(ImageView v, String value) {
            super.setViewImage(v, value);

            if (v.getId() == R.id.image_status) {

                if(value.equals("открыт")) {
                    v.setImageResource(R.drawable.open_order);
                }
                else
                if(value.equals("закрыт")) {
                    v.setImageResource(R.drawable.close_order);
                }
                else
                if(value.equals("выполняется")) {
                    v.setImageResource(R.drawable.inwork_order);
                }
            }
        }
    }

    @Override
    public void onResume() {
        Log.d("LOGI", "OrdersManager resume");
        JSONURL(WorkspaceManager.st_json);
        super.onResume();
    }
}
