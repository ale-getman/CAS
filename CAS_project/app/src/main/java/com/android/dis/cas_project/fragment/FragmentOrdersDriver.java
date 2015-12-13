package com.android.dis.cas_project.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.dis.cas_project.MainActivity;
import com.android.dis.cas_project.R;
import com.android.dis.cas_project.WorkspaceDriver;
import com.android.dis.cas_project.WorkspaceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 13.12.2015.
 */
public class FragmentOrdersDriver extends AbstractTabFragment{

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

    public static FragmentOrdersDriver getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentOrdersDriver fragment = new FragmentOrdersDriver();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_orders_manager));
        frg_context = context;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        listView = (ListView) view.findViewById(R.id.list);

        JSONURL(WorkspaceDriver.st_json);
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
                            new String[] { NAME, TECHNIC, STATUS}, new int[] { R.id.text1, R.id.text2, R.id.image_status});
                }
                else
                {
                    myBooks.add(hm);
                    adapter = new MySimpleAdapter(frg_context, myBooks, R.layout.list_2,
                            new String[]{NAME, TECHNIC, WHO, STATUS}, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.image_status});
                }

                //выводим в листвбю
                listView.setAdapter(adapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FF12212f"));
                listView.setDivider(divcolor);
                listView.setDividerHeight(1);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(frg_context, MainActivity.class);
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
}