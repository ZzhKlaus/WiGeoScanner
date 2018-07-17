package cn.edu.xjtlu.wigeoscanner.Views;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.xjtlu.wigeoscanner.Modules.DataParas;
import cn.edu.xjtlu.wigeoscanner.Modules.DataUploader;
import cn.edu.xjtlu.wigeoscanner.Modules.GeoItem;
import cn.edu.xjtlu.wigeoscanner.Modules.WiFiItem;
import cn.edu.xjtlu.wigeoscanner.Modules.WiFiItemAdapter;
import cn.edu.xjtlu.wigeoscanner.R;
/**
 * Created by jBta0 on 2018/7/12.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener{

    public static List<WiFiItem> global_wifiItemlist = new ArrayList<WiFiItem>();
    public static DataParas dataparas;

    //ToDo extend to List of GeoItem, so that a list of GeoItem can be sent to server along with a list of WiFiItem
    public static GeoItem   geoitem;

    private List<WiFiItem> wifiItemlist = new ArrayList<WiFiItem>();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private WiFiItemAdapter adapter;
    private TextView tv_geoX;
    private TextView tv_geoY;
    private TextView tv_geoZ;
    private TextView tv_wifi_aps;
    private Button   button_autoscan;
    private Button   button_upload;
    private Button   button_geoscan;
    private Button   button_geostop;
    private Button   button_wifiscan;
    private Button   button_wififilter;
    private EditText input_building;
    private EditText input_floor;
    private EditText input_location_x;
    private EditText input_location_y;
    private List<String> ALLOW_SSID = new ArrayList<String>();
    private boolean ENABLE_ALLOW_SSID = false;
    private boolean WIFI_FIRST_SCAN = false;

    private WifiManager mWifi;
    private List<ScanResult> wifiList;

    private boolean GEO_SCAN_ENABLE = false;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private boolean mHasPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        initWiFi();
        initWifiAllowList();
        initGeoSensor();
        setListAdapter();
        initView();
        initDataParas();

    }
    private void initDataParas(){
        dataparas = new DataParas("","","","");
    }
    private void initPermission(){
        mHasPermission = checkPermission();
        if (!mHasPermission) {
            requestPermission();
        }
    }
    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllPermission = true;
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;   //check whether the user allowed all the permissionst
                    break;
                }
            }

            if (hasAllPermission) {
                mHasPermission = true;
                Toast.makeText(MainActivity.this,"ALL required permissions OK!",Toast.LENGTH_SHORT).show();
                //execute the first time
                mWifi.startScan();
            } else {  //user does not allow
                mHasPermission = false;
                Toast.makeText(MainActivity.this,"error,failed to get localization permission, please enable it manually!",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void initWiFi(){
        mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (mWifi.isWifiEnabled() == false){
            Toast.makeText(getApplicationContext(), "error, WIFI disabled, enabling now...", Toast.LENGTH_LONG).show();
            mWifi.setWifiEnabled(true);
        }
        if (mWifi.is5GHzBandSupported() == false){
            Toast.makeText(getApplicationContext(), "attention, this device does not support WIFI 5G band!", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(),"initializing WIFI, please wait...", Toast.LENGTH_SHORT).show();

        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context c, Intent intent)
            {
                wifiList = mWifi.getScanResults();
                if(!WIFI_FIRST_SCAN){
                    Toast.makeText(getApplicationContext(),"WIFI scan ready, enjoy!", Toast.LENGTH_SHORT).show();
                    WIFI_FIRST_SCAN = true;
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //execute the first time
        mWifi.startScan();
    }
    private void initWifiAllowList(){
        ALLOW_SSID.add("XJTLU");
        ALLOW_SSID.add("eduroam");
        ALLOW_SSID.add("CMCC-EDU");
        ALLOW_SSID.add("iOffice");
    }
    private boolean isInAllowWifiList(String in){
        for(String x:ALLOW_SSID){
            if(in.equals(x)){
                return true;
            }
        }
        return false;
    }
    private void initGeoSensor(){
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        geoitem = new GeoItem("","","");
    }
    private void setListAdapter(){
        adapter = new WiFiItemAdapter(MainActivity.this, R.layout.wifi_item, wifiItemlist);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }
    private void initView(){

        button_autoscan = (Button) findViewById(R.id.button_auto_scan);
        button_upload   = (Button) findViewById(R.id.button_upload_to_server);
        button_geoscan  = (Button) findViewById(R.id.button_geo_start);
        button_geostop  = (Button) findViewById(R.id.button_geo_stop);
        button_wifiscan  = (Button) findViewById(R.id.button_wifi_start);
        button_wififilter  = (Button) findViewById(R.id.button_wifi_filter);

        input_building = (EditText) findViewById(R.id.input_building);
        input_building.clearFocus();
        input_floor = (EditText) findViewById(R.id.input_floor);
        input_location_x = (EditText) findViewById(R.id.input_loc_x);
        input_location_y = (EditText) findViewById(R.id.input_loc_y);

        button_autoscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    scanWifiAps();
                    GEO_SCAN_ENABLE = true;
            }
        });
        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputs() && GEO_SCAN_ENABLE && global_wifiItemlist!=null){
                    DataUploader datauploader = new DataUploader(getApplicationContext());
                    datauploader.execute();
                    Toast.makeText(getApplicationContext(),"uploading...",Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_geoscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GEO_SCAN_ENABLE = true;
            }
        });
        button_geostop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GEO_SCAN_ENABLE = false;
            }
        });
        button_wifiscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifiAps();
            }
        });
        button_wififilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ENABLE_ALLOW_SSID){
                    ENABLE_ALLOW_SSID = true;
                    Toast.makeText(getApplicationContext(), "WiFi_Filter ENABLED!", Toast.LENGTH_SHORT).show();
                }else{
                    ENABLE_ALLOW_SSID = false;
                    Toast.makeText(getApplicationContext(), "WiFi_Filter DISABLED!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_geoX = (TextView) findViewById(R.id.geoX);
        tv_geoY = (TextView) findViewById(R.id.geoY);
        tv_geoZ = (TextView) findViewById(R.id.geoZ);
        tv_wifi_aps = (TextView) findViewById(R.id.wifi_total_aps);
        tv_wifi_aps.setText(""+wifiItemlist.size());
    }

    private boolean checkInputs(){
        dataparas.setStore_building(input_building.getText().toString());
        dataparas.setStore_floor(input_floor.getText().toString());
        dataparas.setStore_location_x(input_location_x.getText().toString());
        dataparas.setStore_location_y(input_location_y.getText().toString());
        if (dataparas.getStore_building().length() == 0
                || dataparas.getStore_floor().length() == 0
                || dataparas.getStore_location_x().length() == 0
                || dataparas.getStore_location_y().length() == 0) {
            Toast.makeText(getApplicationContext(), "error, please recheck the inputs!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        this.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context c, Intent intent)
            {
                wifiList = mWifi.getScanResults();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(GEO_SCAN_ENABLE) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            String geoX = String.format("%8.4f", x);
            String geoY = String.format("%8.4f", y);
            String geoZ = String.format("%8.4f", z);

            geoitem.setStore_geo_x(geoX);
            geoitem.setStore_geo_y(geoY);
            geoitem.setStore_geo_z(geoZ);

            tv_geoX.setText("GeoX:"+geoitem.getStore_geo_x());
            tv_geoY.setText("GeoY:"+geoitem.getStore_geo_y());
            tv_geoZ.setText("GeoZ:"+geoitem.getStore_geo_z());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        //Toast.makeText(getApplicationContext(), "GeomagneticSensor accuracy changed!", Toast.LENGTH_SHORT).show();
    }

    public void scanWifiAps(){
        //Toast.makeText(getApplicationContext(), "Scanning WiFi APs...", Toast.LENGTH_SHORT).show();
        if(WIFI_FIRST_SCAN){
            mWifi.startScan();
            processScannedWiFi();
        }
    }

    public void processScannedWiFi(){
        wifiItemlist.clear();
        for (int i = 0; i < wifiList.size(); i++) {
            if(ENABLE_ALLOW_SSID){
                if(isInAllowWifiList(wifiList.get(i).SSID)){
                    WiFiItem newAP = new WiFiItem(R.drawable.item_wifi,wifiList.get(i).SSID,wifiList.get(i).BSSID,""+wifiList.get(i).level,""+wifiList.get(i).frequency);
                    wifiItemlist.add(newAP);
                    adapter.notifyDataSetChanged();
                    tv_wifi_aps.setText(""+wifiItemlist.size());
                }
            }else {
                WiFiItem newAP = new WiFiItem(R.drawable.item_wifi,wifiList.get(i).SSID,wifiList.get(i).BSSID,""+wifiList.get(i).level,""+wifiList.get(i).frequency);
                wifiItemlist.add(newAP);
                adapter.notifyDataSetChanged();
                tv_wifi_aps.setText(""+wifiItemlist.size());
            }
        }
        global_wifiItemlist = wifiItemlist;
    }

}