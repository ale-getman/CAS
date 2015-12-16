package com.android.dis.cas_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.dis.cas_project.Base64;
import com.android.dis.cas_project.GPSTracker;
import com.android.dis.cas_project.R;
import com.android.dis.cas_project.TabOrder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.android.gms.internal.zzid.runOnUiThread;

/**
 * Created by User on 14.12.2015.
 */
public class FragmentPhoto extends AbstractTabFragment{

    private static final int LAYOUT = R.layout.fragment_order_photo;

    public static String log, id, who, pas, status;
    public static Button photobtn,send,getimg;
    public static ProgressDialog dialog, dialog2;
    public static double dol,shi;
    public static String loc_x,loc_y;
    public static String selectedPath1;
    public static String selectedPath2;
    public static int SELECT_FILE1;
    public static int SELECT_FILE2;
    public static InputStream inputStream;
    public static String the_string_response;
    public static int contentLength;
    public static String res;
    public static String image_url;

    public ImageLoader imageLoader;

    public static String dolstr,shistr;
    public static GPSTracker gps;
    public ImageView image;

    public LinearLayout tabLyaout;
    public float fromPosition,toPosition;
    public PhotoTask photoTask;
    public Bitmap imgbtmp;
    public static Context frg_context;


    public static FragmentPhoto getInstance(Context context) {
        Bundle args = new Bundle();
        FragmentPhoto fragment = new FragmentPhoto();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.tab_photo));
        frg_context = context;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        photobtn = (Button) view.findViewById(R.id.photo2);
        send = (Button) view.findViewById(R.id.send2);
        getimg = (Button) view.findViewById(R.id.getimg2);
        image = (ImageView) view.findViewById(R.id.imageView);

        photobtn.setEnabled(false);
        send.setEnabled(false);
        getimg.setEnabled(false);

        loc_x  = TabOrder.loc_x;
        loc_y  = TabOrder.loc_y;
        dol = TabOrder.dol;
        shi = TabOrder.shi;
        log = TabOrder.log;
        id = TabOrder.id;
        who = TabOrder.who;
        pas = TabOrder.pas;
        image_url = TabOrder.image_url;

        if(!(image_url.equals("")))
        {
            photoTask = new PhotoTask();
            photoTask.execute();
        }

        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                new RequestTask_2().execute(getString(R.string.adress_3));

            }
        });

        getimg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openGallery(SELECT_FILE1);

            }
        });

        photobtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CAMERA_BUTTON);
                intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_CAMERA));
                frg_context.sendOrderedBroadcast(intent, null);
                getimg.setEnabled(true);
            }
        });

        gps = new GPSTracker(frg_context);
        GPSsetting();
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void GPSsetting(){

        if(gps.canGetLocation()) {

            dolstr = "" + gps.getLongitude();;

            shistr = "" + gps.getLatitude();;
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }


    public void openGallery(int req_code){

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select file to upload "), req_code);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap img = null;

        if (resultCode == -1) {
            Uri selectedImageUri = data.getData();

            if (requestCode == SELECT_FILE1)

            {

                selectedPath1 = getPath(selectedImageUri);

                System.out.println("selectedPath1 : " + selectedPath1);

            }

            if (requestCode == SELECT_FILE2)

            {

                selectedPath2 = getPath(selectedImageUri);

                System.out.println("selectedPath2 : " + selectedPath2);

            }

            try {
                img = MediaStore.Images.Media.getBitmap(frg_context.getContentResolver(), selectedImageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(img);

            send.setEnabled(true);
            Toast.makeText(frg_context, "Выбранное фото: " + selectedPath1, Toast.LENGTH_SHORT).show();
        }

    }

    public String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };

        //Cursor cursor = managedQuery(uri, projection, null, null, null);
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

    class RequestTask_2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                upload2();

            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            Toast.makeText(frg_context, "Фото отправлено.", Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(frg_context);
            dialog.setMessage("Отправляю...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }
    }

    public void upload2(){
        Bitmap bitmap = BitmapFactory.decodeFile(selectedPath1);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str = Base64.encodeBytes(byte_arr);
        final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>(2);

        nameValuePairs.add(new BasicNameValuePair("image", image_str));
        nameValuePairs.add(new BasicNameValuePair("id", id));

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(getString(R.string.adress_3));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    the_string_response = convertResponseToString(response);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Toast.makeText(OrderActivity.this, "Response " + the_string_response, Toast.LENGTH_SHORT).show();
                        }
                    });

                }catch(final Exception e){
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(frg_context, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    System.out.println("Error in http connection "+e.toString());
                }
            }
        });
        t.start();
    }

    public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException {

        res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();
        contentLength = (int) response.getEntity().getContentLength(); //getting content length…..
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(OrderActivity.this, "contentLength : " + contentLength, Toast.LENGTH_SHORT).show();
            }
        });

        if (contentLength < 0){
        }
        else{
            byte[] data = new byte[512];
            int len = 0;
            try
            {
                while (-1 != (len = inputStream.read(data)) )
                {
                    buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                inputStream.close(); // closing the stream…..
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            res = buffer.toString();     // converting stringbuffer to string…..

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //Toast.makeText(OrderActivity.this, "Result : " + res, Toast.LENGTH_SHORT).show();
                }
            });
            //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
        }
        return res;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(FragmentOrder.text_status.equals("закрыт"))
        {
            //if(TabOrder1.text_accept.getText().toString().contains(TabOrder1.log))
            if(!(TabOrder.type.equals("менеджер")))
            {
                photobtn.setEnabled(true);
                getimg.setEnabled(true);
            }
        }
    }

    class PhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            dialog2 = new ProgressDialog(frg_context);
            dialog2.setMessage("Загружаю фото...");
            dialog2.setIndeterminate(true);
            dialog2.setCancelable(true);
            dialog2.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                imageLoader = ImageLoader.getInstance(); // Получили экземпляр
                imageLoader.init(ImageLoaderConfiguration.createDefault(frg_context)); // Проинициализировали конфигом по умолчанию
                imgbtmp = imageLoader.loadImageSync(image_url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            image.setImageBitmap(imgbtmp);
            dialog2.dismiss();
            super.onPostExecute(result);
        }
    }
}
