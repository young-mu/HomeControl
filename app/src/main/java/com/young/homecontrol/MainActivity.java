package com.young.homecontrol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {
    private Context mContext;
    private Spinner sp_raspicam_res, sp_raspicam_qua, sp_raspicam_cod, sp_raspicam_flp, sp_raspicam_dly;
    private ArrayAdapter<String> adp_raspicam_res, adp_raspicam_qua, adp_raspicam_cod, adp_raspicam_flp, adp_raspicam_dly;
    private String opt_raspicam_res, opt_raspicam_qua, opt_raspicam_cod, opt_raspicam_flp, opt_raspicam_dly, opt_raspicam_name;
    private Button btn_raspicam_capt;
    private Button btn_raspicam_save;
    private ImageView iv_raspicam_image;
    private Bitmap raspicam_image;
    private String imageName;
    private boolean raspicam_svimg_flag = false;
    private String captureUrlBase = "http://192.168.1.108/php/camera.php";
    private String imageUrlBase = "http://192.168.1.108/Camera/Images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        sp_raspicam_res = (Spinner) findViewById(R.id.sp_raspicam_res);
        sp_raspicam_qua = (Spinner) findViewById(R.id.sp_raspicam_qua);
        sp_raspicam_cod = (Spinner) findViewById(R.id.sp_raspicam_cod);
        sp_raspicam_flp = (Spinner) findViewById(R.id.sp_raspicam_flp);
        sp_raspicam_dly = (Spinner) findViewById(R.id.sp_raspicam_dly);
        btn_raspicam_capt = (Button) findViewById(R.id.btn_raspicam_capt);
        btn_raspicam_save = (Button) findViewById(R.id.btn_raspicam_save);
        btn_raspicam_capt.setOnClickListener(this);
        btn_raspicam_save.setOnClickListener(this);
        iv_raspicam_image = (ImageView) findViewById(R.id.iv_raspicam);
        init_sp_raspicam_res();
        init_sp_raspicam_qua();
        init_sp_raspicam_cod();
        init_sp_raspicam_flp();
        init_sp_raspicam_dly();
    }

    void init_sp_raspicam_res() {
        List<String> list = new ArrayList<String>();
        list.add("2400x1600");
        list.add("1600x1200");
        list.add("1024x768");
        list.add("640x480");
        list.add("320x240");
        adp_raspicam_res = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        adp_raspicam_res.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_raspicam_res.setAdapter(adp_raspicam_res);
        sp_raspicam_res.setSelection(2);
        sp_raspicam_res.setOnItemSelectedListener(this);
    }

    void init_sp_raspicam_qua() {
        List<String> list = new ArrayList<String>();
        list.add("100");
        list.add("85");
        list.add("50");
        adp_raspicam_qua = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        adp_raspicam_qua.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_raspicam_qua.setAdapter(adp_raspicam_qua);
        sp_raspicam_qua.setSelection(1);
        sp_raspicam_qua.setOnItemSelectedListener(this);
    }

    void init_sp_raspicam_cod() {
        List<String> list = new ArrayList<String>();
        list.add("JPG");
        list.add("BMP");
        list.add("PNG");
        adp_raspicam_cod = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        adp_raspicam_cod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_raspicam_cod.setAdapter(adp_raspicam_cod);
        sp_raspicam_cod.setSelection(0);
        sp_raspicam_cod.setOnItemSelectedListener(this);
    }

    void init_sp_raspicam_flp() {
        List<String> list = new ArrayList<String>();
        list.add("NO");
        list.add("vertical");
        list.add("horizontal");
        adp_raspicam_flp = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        adp_raspicam_flp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_raspicam_flp.setAdapter(adp_raspicam_flp);
        sp_raspicam_flp.setSelection(0);
        sp_raspicam_flp.setOnItemSelectedListener(this);
    }

    void init_sp_raspicam_dly() {
        List<String> list = new ArrayList<String>();
        list.add("1s");
        list.add("3s");
        list.add("5s");
        adp_raspicam_dly = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        adp_raspicam_dly.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_raspicam_dly.setAdapter(adp_raspicam_dly);
        sp_raspicam_dly.setSelection(1);
        sp_raspicam_dly.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Pattern pattern;
        String[] strs;
        switch (parent.getId()) {
            case R.id.sp_raspicam_res:
                String res = adp_raspicam_res.getItem(position);
                Log.d("HomeControl", "raspicam resolution: " + res);
                pattern = Pattern.compile("x");
                strs = pattern.split(res);
                opt_raspicam_res = "-w%20" + strs[0] + "%20-h%20" + strs[1]; // replace spacw with %20 for php
                break;
            case R.id.sp_raspicam_qua:
                String qua = adp_raspicam_qua.getItem(position);
                Log.d("HomeControl", "raspicam quality: " + qua);
                opt_raspicam_qua = "-q%20" + qua;
                break;
            case R.id.sp_raspicam_cod:
                String cod = adp_raspicam_cod.getItem(position);
                Log.d("HomeControl", "raspicam encode: " + cod);
                opt_raspicam_cod = "-e%20" + cod.toLowerCase();
                break;
            case R.id.sp_raspicam_flp:
                String flp = adp_raspicam_flp.getItem(position);
                Log.d("HomeControl", "raspicam flip: " + flp);
                switch (flp) {
                    case "NO":
                        opt_raspicam_flp = "";
                        break;
                    case "vertical":
                        opt_raspicam_flp = "-vf";
                        break;
                    case "horizontal":
                        opt_raspicam_flp = "-hf";
                        break;
                    default:
                        break;
                }
                break;
            case R.id.sp_raspicam_dly:
                String dly = adp_raspicam_dly.getItem(position);
                Log.d("HomeControl", "raspicam delay: " + dly);
                pattern = Pattern.compile("s");
                strs = pattern.split(dly);
                opt_raspicam_dly = "-t%20" + strs[0] + "000"; // ms
                break;
            default:
                Log.d("HomeControl", "no");
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // nothing to do
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_raspicam_capt:
                if (isWifiEnabled()) {
                    captureThread ct = new captureThread();
                    ct.start();
                    Log.d("HomeControl", "launch captureThread");
                } else {
                    Toast.makeText(mContext, "You need to enable Wifi first", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_raspicam_save:
                if (raspicam_svimg_flag) {
                    MediaStore.Images.Media.insertImage(getContentResolver(), raspicam_image, "raspicam", "image captured by Raspberry Pi Camera");
                    Toast.makeText(mContext, "Save image successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Fail to save since no image has been captured", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        int status = wifiManager.getWifiState();
        return (status == WifiManager.WIFI_STATE_ENABLED);
    }

    private class captureThread extends Thread {
        @Override
        public void run() {
            String dir = "/srv/http/Camera/Images/";
            imageName = getFileName() + "." + opt_raspicam_cod.substring(5, opt_raspicam_cod.length());
            opt_raspicam_name = "-o%20" + dir + imageName;
            String captureUrl = captureUrlBase;
            captureUrl += "?" + "camera=capture" +
                          "&" + "resolution=" + opt_raspicam_res +
                          "&" + "quality=" + opt_raspicam_qua +
                          "&" + "encode=" + opt_raspicam_cod +
                          "&" + "flip=" + opt_raspicam_flp +
                          "&" + "delay=" + opt_raspicam_dly +
                          "&" + "name=" + opt_raspicam_name;
            Log.v("HomeControl", "capture host URL is " + captureUrl);
            HttpURLConnection capUrlConnection = null;
            Message msg = new Message();
            try {
                URL c_url = new URL(captureUrl);
                capUrlConnection = (HttpURLConnection)c_url.openConnection();
                capUrlConnection.setRequestMethod("GET");
                capUrlConnection.setDoInput(true);
                capUrlConnection.setReadTimeout(10000);
                capUrlConnection.setConnectTimeout(10000);
                if (capUrlConnection.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(capUrlConnection.getInputStream()));
                    String ret = in.readLine();
                    int status = Integer.parseInt(ret);
                    msg.arg1 = 1;
                    msg.arg2 = status;
                    captureHandler.sendMessage(msg);
                } else {
                    int errCode = capUrlConnection.getResponseCode();
                    msg.arg1 = 2;
                    msg.arg2 = errCode;
                    captureHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.arg1 = 3;
                captureHandler.sendMessage(msg);
            } finally {
                if (capUrlConnection != null) {
                    capUrlConnection.disconnect();
                }
            }
        }
    }

    Handler captureHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                int status = msg.arg2;
                if (status == 0) {
                    Toast.makeText(mContext, "Capture successfully", Toast.LENGTH_SHORT).show();
                    imageThread it = new imageThread();
                    it.start();
                    Log.d("HomeControl", "launch imageThread");
                } else {
                    Toast.makeText(mContext, "Capture Failed", Toast.LENGTH_SHORT).show();
                }
            } else if (msg.arg1 == 2) {
                int errCode = msg.arg2;
                Toast.makeText(mContext, "Fail to connect capture host (" + errCode + ")", Toast.LENGTH_SHORT).show();
            } else if (msg.arg1 == 3) {
                Toast.makeText(mContext, "Fail to connect capture host", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String getFileName() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        String s_month = (month / 10 == 0) ? "0"+month : ""+month;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String s_day = (day / 10 == 0) ? "0"+day : ""+day;
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String s_hour = (hour / 10 == 0) ? "0"+hour : ""+hour;
        int minute = cal.get(Calendar.MINUTE);
        String s_minute = (minute / 10 == 0) ? "0"+minute : ""+minute;
        int second = cal.get(Calendar.SECOND);
        String s_second = (second / 10 == 0) ? "0"+second : ""+second;
        String filename = year + s_month + s_day + "-" + s_hour + s_minute + s_second;
        Log.d("HomeControl", "filename prefix is " + filename);
        return filename;
    }

    private class imageThread extends Thread {
        @Override
        public void run() {
            String imageUrl = imageUrlBase + imageName;
            Log.v("HomeControl", "image host URL is " + imageUrl);
            HttpURLConnection imgUrlConnection = null;
            Message msg = new Message();
            try {
                URL c_url = new URL(imageUrl);
                imgUrlConnection = (HttpURLConnection)c_url.openConnection();
                imgUrlConnection.setRequestMethod("GET");
                imgUrlConnection.setDoInput(true);
                imgUrlConnection.setReadTimeout(10000);
                imgUrlConnection.setConnectTimeout(10000);
                if (imgUrlConnection.getResponseCode() == 200) {
                    Bitmap bitmap = BitmapFactory.decodeStream(imgUrlConnection.getInputStream());
                    msg.arg1 = 1;
                    msg.obj = bitmap;
                    imageHandler.sendMessage(msg);
                } else {
                    int errCode = imgUrlConnection.getResponseCode();
                    msg.arg1 = 2;
                    msg.arg2 = errCode;
                    captureHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.arg1 = 3;
                captureHandler.sendMessage(msg);
            } finally {
                if (imgUrlConnection != null) {
                    imgUrlConnection.disconnect();
                }
            }
        }
    }

    Handler imageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                raspicam_image = (Bitmap) msg.obj;
                iv_raspicam_image.setImageBitmap(raspicam_image);
                raspicam_svimg_flag = true;
            } else if (msg.arg1 == 2) {
                int errCode = msg.arg2;
                Toast.makeText(mContext, "Fail to connect image host (" + errCode + ")", Toast.LENGTH_SHORT).show();
            } else if (msg.arg1 == 3) {
                Toast.makeText(mContext, "Fail to connect image host", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
