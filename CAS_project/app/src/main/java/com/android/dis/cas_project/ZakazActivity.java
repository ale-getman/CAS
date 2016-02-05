package com.android.dis.cas_project;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    public static EditText zakaz_address_2;
    public Button zakaz_send, zakaz_maps;
    public ImageButton maps_zakaz,add_adr;
    public LinearLayout linear_address;
    public int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    public int fillParent = LinearLayout.LayoutParams.FILL_PARENT;
    public int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
    public String nameZ, technicZ, addressZ,dataZ;
    public String response;
    private ProgressDialog dialog;
    public Date d;
    public SimpleDateFormat format1;
    private Toolbar toolbar;
    public TextView t1;
    public Spinner spinner;
    public String tech_spin;
    public String[] spisok_tech = {"Автобетононасос",
            "Автовышка",
            "Автогрейдер",
            "Автокран",
            "Ассенизаторская машина",
            "Асфальтоукладчик",
            "Бульдозер",
            "Генератор",
            "Гидромолот",
            "Грунторез",
            "Длинномер",
            "Дорожная фреза",
            "Илосос",
            "Каток",
            "Коммунальная машина",
            "Компрессор",
            "Манипулятор",
            "Мини-погрузчик",
            "Мини-экскаватор",
            "Низкорамная платформа",
            "Поливомоечная машина ",
            "Самосвал",
            "Фронтальный погрузчик",
            "Экскаватор гусеничный",
            "Экскаватор колёсный",
            "Экскаватор погрузчик",
            "Ямобур"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zakaz_layout);

        linear_address = (LinearLayout) findViewById(R.id.linear_address);

        d = new Date();
        format1 = new SimpleDateFormat("ddMMyy");

        zakaz_name = (EditText) findViewById(R.id.zakaz_name);
        zakaz_technic = (EditText) findViewById(R.id.zakaz_tech);
        zakaz_address = (EditText) findViewById(R.id.zakaz_address);
        zakaz_send = (Button) findViewById(R.id.zakaz_send);
        //zakaz_maps = (Button) findViewById(R.id.zakaz_maps);
        maps_zakaz = (ImageButton) findViewById(R.id.maps_zakaz);
        add_adr = (ImageButton) findViewById(R.id.add_adr);

        zakaz_technic.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spisok_tech);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Список техники");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                tech_spin = spinner.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        zakaz_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //if(zakaz_name.getText().toString().equals("") || zakaz_technic.getText().toString().equals("") || zakaz_address.getText().toString().equals(""))
                if (zakaz_name.getText().toString().equals("") || zakaz_address.getText().toString().equals(""))
                    Toast.makeText(ZakazActivity.this, "Заполните все поля.", Toast.LENGTH_SHORT).show();
                else {
                    nameZ = new String(zakaz_name.getText().toString());
                    technicZ = new String(zakaz_technic.getText().toString());
                    technicZ = new String(tech_spin);
                    addressZ = new String(zakaz_address.getText().toString());
                    dataZ = new String(format1.format(d));
                    Log.d("LOGI", "data: " + dataZ);
                    new RequestTask().execute(getString(R.string.adress_6));
                    onBackPressed();
                }
            }
        });

        /*zakaz_maps.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZakazActivity.this, MapsZakaz.class);
                startActivity(intent);
            }
        });*/

        maps_zakaz.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZakazActivity.this, MapsZakaz.class);
                intent.putExtra("flag", "0");
                startActivity(intent);
            }
        });

        add_adr.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                        fillParent, wrapContent);
                LinearLayout.LayoutParams lParams_2 = new LinearLayout.LayoutParams(
                        fillParent, wrapContent, 1f);
                LinearLayout.LayoutParams lParams_3 = new LinearLayout.LayoutParams(
                        60, 60);

                LinearLayout lin_adr_2 = new LinearLayout(ZakazActivity.this);
                lin_adr_2.setOrientation(LinearLayout.HORIZONTAL);
                lin_adr_2.setWeightSum(1);
                linear_address.addView(lin_adr_2, lParams);

                zakaz_address_2 = new EditText(ZakazActivity.this);
                zakaz_address_2.setBackgroundResource(R.drawable.edit_text_style);
                lParams_2.gravity = Gravity.LEFT;
                lParams_2.setMargins(15, 0, 0, 0);
                zakaz_address_2.setInputType(InputType.TYPE_CLASS_TEXT);
                zakaz_address_2.setPadding(10, 15, 0, 15);
                zakaz_address_2.setHint("Введите адрес");
                lin_adr_2.addView(zakaz_address_2, lParams_2);

                ImageButton maps_zakaz_2 = new ImageButton((ZakazActivity.this));
                maps_zakaz_2.setImageResource(Resources.getSystem().getIdentifier("ic_dialog_map", "drawable", "android"));
                maps_zakaz_2.setBackgroundResource(R.drawable.edit_text_style);
                lParams_3.gravity = Gravity.CENTER;
                lParams_3.setMargins(0, 0, 15, 0);
                lin_adr_2.addView(maps_zakaz_2, lParams_3);

                maps_zakaz_2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ZakazActivity.this, MapsZakaz.class);
                        intent.putExtra("flag", "1");
                        startActivity(intent);
                    }
                });

                add_adr.setVisibility(View.INVISIBLE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}