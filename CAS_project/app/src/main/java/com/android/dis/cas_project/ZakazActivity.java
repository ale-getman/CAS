package com.android.dis.cas_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZakazActivity extends Activity {

    public EditText zakaz_name, zakaz_technic;
    public static EditText zakaz_address;
    public Button zakaz_send, zakaz_maps;
    public String nameZ, technicZ, addressZ,dataZ;
    public String response;
    private ProgressDialog dialog;
    public Date d;
    public SimpleDateFormat format1;
    private Toolbar toolbar;
    public TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zakaz_layout);

        d = new Date();
        format1 = new SimpleDateFormat("ddMMyy");

        zakaz_name = (EditText) findViewById(R.id.zakaz_name);
        zakaz_technic = (EditText) findViewById(R.id.zakaz_tech);
        zakaz_address = (EditText) findViewById(R.id.zakaz_address);
        zakaz_send = (Button) findViewById(R.id.zakaz_send);
        zakaz_maps = (Button) findViewById(R.id.zakaz_maps);

        zakaz_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(zakaz_name.getText().toString().equals("") || zakaz_technic.getText().toString().equals("") || zakaz_address.getText().toString().equals(""))
                    Toast.makeText(ZakazActivity.this, "Заполните все поля.", Toast.LENGTH_SHORT).show();
                else
                {
                    nameZ = new String(zakaz_name.getText().toString());
                    technicZ = new String(zakaz_technic.getText().toString());
                    addressZ = new String(zakaz_address.getText().toString());
                    dataZ = new String(format1.format(d));
                    Log.d("LOGI", "data: " + dataZ);
                    new RequestTask().execute(getString(R.string.adress_6));
                }
            }
        });

        zakaz_maps.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZakazActivity.this, MapsZakaz.class);
                startActivity(intent);
            }
        });
        initToolbar();
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

                nameValuePairs.add(new BasicNameValuePair("name", nameZ));

                nameValuePairs.add(new BasicNameValuePair("tech", technicZ));

                nameValuePairs.add(new BasicNameValuePair("addr", addressZ));

                nameValuePairs.add(new BasicNameValuePair("data", dataZ));

                nameValuePairs.add(new BasicNameValuePair("status", "открыт"));

                //nameValuePairs.add(new BasicNameValuePair("lat", MapsZakaz.lat));

                //nameValuePairs.add(new BasicNameValuePair("lng", MapsZakaz.lng));

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

            dialog.dismiss();
            Toast.makeText(ZakazActivity.this, "Заказ добавлен", Toast.LENGTH_SHORT).show();
            zakaz_name.setText("");
            zakaz_technic.setText("");
            zakaz_address.setText("");
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(ZakazActivity.this);
            dialog.setMessage("Загружаюсь...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    @Override
    protected void onResume() {
        //zakaz_address.setText(MapsZakaz.add);
        super.onResume();
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
}