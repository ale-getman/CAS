package com.android.dis.cas_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.dis.cas_project.GPSTracker;
import com.android.dis.cas_project.R;
import com.android.dis.cas_project.TabOrder;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 14.12.2015.
 */
public class FragmentOrder extends AbstractTabFragment{

    private static final int LAYOUT = R.layout.fragment_order;

    public static TextView text_order;
    public static TextView text_technic;
    public static TextView text_status;
    public static TextView text_accept;
    public static TextView text_address;
    public static String log, id, who, pas, status;
    public static Button accept,ready;
    public static ProgressDialog dialog;
    public static double dol,shi;
    public static String loc_x,loc_y;
    public static String type;
    public static String res;

    public static String dolstr,shistr;
    public static GPSTracker gps;

    public ScrollView scrollTab;

    public static Context frg_context;

    public static FragmentOrder getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentOrder fragment = new FragmentOrder();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_order));
        frg_context = context;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        text_order = (TextView) view.findViewById(R.id.text_order1);
        text_technic = (TextView) view.findViewById(R.id.text_technic1);
        text_status = (TextView) view.findViewById(R.id.text_status1);
        text_accept = (TextView) view.findViewById(R.id.text_accept1);
        text_address = (TextView) view.findViewById(R.id.text_address1);
        accept = (Button) view.findViewById(R.id.accept1);
        ready = (Button) view.findViewById(R.id.ready1);

        ready.setEnabled(false);

        loc_x  = TabOrder.loc_x;
        loc_y  = TabOrder.loc_y;
        dol = TabOrder.dol;
        shi = TabOrder.shi;
        log = TabOrder.log;
        id = TabOrder.id;
        who = TabOrder.who;
        pas = TabOrder.pas;
        type = TabOrder.type;
        text_status.setText(TabOrder.text_status);
        text_order.setText(TabOrder.text_order);
        text_technic.setText(TabOrder.text_technic);
        text_address.setText(TabOrder.address);


        if(!who.contains(log))
        {
            accept.setEnabled(false);
            ready.setEnabled(false);
        }

        if(type.equals("менеджер"))
        {
            accept.setEnabled(false);
            ready.setEnabled(false);
        }

        if(TabOrder.text_status.equals("открыт"))
        {
            text_status.setTextColor(getResources().getColor(R.color.open));
            if(!type.equals("менеджер"))
            {
                accept.setEnabled(true);
                ready.setEnabled(false);
            }
            text_accept.setText(who);
        }

        if(TabOrder.text_status.equals("закрыт"))
        {
            text_status.setTextColor(getResources().getColor(R.color.close));
            accept.setEnabled(false);
            ready.setEnabled(false);
            text_accept.setText(who);
        }

        if(TabOrder.text_status.equals("выполняется"))
        {
            text_status.setTextColor(getResources().getColor(R.color.inwork));
            if(who.contains(log))
                if(!type.equals("менеджер"))
                {
                    accept.setEnabled(false);
                    ready.setEnabled(true);
                }
            text_accept.setText(who);
        }

        accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                accept.setEnabled(false);
                ready.setEnabled(true);
                status = "выполняется";
                text_accept.setText("Заказ выполняет: " + log);
                text_status.setText("" + status);
                text_status.setTextColor(getResources().getColor(R.color.inwork));
                new RequestTask().execute(getString(R.string.adress_2));
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                accept.setEnabled(false);
                ready.setEnabled(false);
                status = "закрыт";
                text_accept.setText("Заказ выполнил: " + log);
                text_status.setText("" + status);
                text_status.setTextColor(getResources().getColor(R.color.close));
                new RequestTask().execute(getString(R.string.adress_2));
            }
        });


        gps = new GPSTracker(frg_context);
        GPSsetting();

        scrollTab = (ScrollView) view.findViewById(R.id.scrollView1);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void GPSsetting() {

        if (gps.canGetLocation()) {

            dolstr = "" + gps.getLongitude();

            shistr = "" + gps.getLatitude();
        } else {
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                //передаем параметры из наших текстбоксов
                //логин
                nameValuePairs.add(new BasicNameValuePair("login", log));

                nameValuePairs.add(new BasicNameValuePair("id", id));

                nameValuePairs.add(new BasicNameValuePair("status", status.toString()));
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

            dialog.dismiss();
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
}