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
import android.opengl.Matrix;
import android.renderscript.Matrix3f;
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
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

import cn.edu.xjtlu.wigeoscanner.Modules.DataParas;
import cn.edu.xjtlu.wigeoscanner.Modules.DataUploader;
import cn.edu.xjtlu.wigeoscanner.Modules.GeoItem;
import cn.edu.xjtlu.wigeoscanner.Modules.WiFiItem;
import cn.edu.xjtlu.wigeoscanner.Modules.WiFiItemAdapter;
import cn.edu.xjtlu.wigeoscanner.Modules.MathMethod;
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

    //added
    //private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mGravmeterReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private final double[] mFinalOrientationAngles = new double[3];

    private float[][] MagRot = new float[1][3];
    private float[][] MagRotNew = new float[3][1];
    //private float[] MagRot = new float[3];
    float azimuth;
    float pitch;
    float roll;
    private static final int TEST_GRAV = Sensor.TYPE_ACCELEROMETER;
    private static final int TEST_MAG = Sensor.TYPE_MAGNETIC_FIELD;
    private final float alpha = (float) 0.8;
    private float gravity[] = new float[3];
    private float magnetic[] = new float[3];
    //

    MathMethod mathMethod = new MathMethod();
    private WifiManager mWifi;
    private List<ScanResult> wifiList;

    private boolean GEO_SCAN_ENABLE = false;
    //private boolean ACC_SCAN_ENABLE = true;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private boolean mHasPermission;

    private Sensor mSensorGeo;
    //private Sensor mSensorACC;
    private Sensor mSensorGrav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        initWiFi();
        //initACC();
        initGrav();
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
        mSensorGeo = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //geoitem = new GeoItem("","","");
    }

    //
    /*private void initACC(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorACC = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorACC = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    }*/

    private void initGrav(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorGrav = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
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
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.

        //SENSOR_DELAY_GAME : 38Hz
        //SENSOR_DELAY_NORMAL: 38Hz
        //SENSOR_DELAY_UI : 15Hz
        //SENSOR_DELAY_FASTEST : 4Hz
        super.onResume();

        //Edited mSensor to mSensorGeo/ACC
        //mSensorManager.registerListener(this, mSensorACC, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorGeo, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
        this.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context c, Intent intent)
            {
                wifiList = mWifi.getScanResults();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    //暂停后停止地磁和加速度计register，减少电池损耗
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    //added 一种基于地磁强度特征的室内定位方法
    public void setRotMatrix(){
        float sinX = Float.parseFloat(String.valueOf(Math.sin(mFinalOrientationAngles[0])));
        float cosX = Float.parseFloat(String.valueOf(Math.cos(mFinalOrientationAngles[0])));
        float sinY = Float.parseFloat(String.valueOf(Math.sin(mFinalOrientationAngles[1])));
        float cosY = Float.parseFloat(String.valueOf(Math.cos(mFinalOrientationAngles[1])));
        float sinZ = Float.parseFloat(String.valueOf(Math.sin(mFinalOrientationAngles[2])));
        float cosZ = Float.parseFloat(String.valueOf(Math.cos(mFinalOrientationAngles[2])));

        float[][] mRotationZ = {{cosX, -sinX, 0},{sinX, cosX, 0}, {0, 0, 1}};
        float[][] mRotationX = {{1, 0, 0}, {0, cosY,-sinY}, {0, sinY, cosY}};
        float[][] mRotationY = {{cosZ, 0, -sinZ},{0, 1, 0}, {sinZ, 0, cosZ}};
        //added
        //float[] mInvRotMatrix = new float[9];
        //android.opengl.Matrix.invertM(mInvRotMatrix,0,mRotationMatrix,0);

        float[][] mRotation = mathMethod.multip3x(mathMethod.multip3x(mRotationY,mRotationX),mRotationZ);
        float[][] ReverseMatrix = mathMethod.getReverseMartrix(mRotation);

        float[][] InitMag = {{mMagnetometerReading[0]},{mMagnetometerReading[1]},{mMagnetometerReading[2]}};

//        float[][] mRotationMatrixNew = {{mRotationMatrix[0],mRotationMatrix[1],mRotationMatrix[2]},{mRotationMatrix[3],mRotationMatrix[4],mRotationMatrix[5],},{mRotationMatrix[6],mRotationMatrix[7],mRotationMatrix[8]}};
 //       MagRot = mathMethod.multip3x(mRotationMatrixNew, InitMag);
        MagRot = mathMethod.multip3x(ReverseMatrix, InitMag);
        //float[] InitMag = {mMagnetometerReading[0],mMagnetometerReading[1],mMagnetometerReading[2]};
       // android.opengl.Matrix.multiplyMV(MagRot,0,mInvRotMatrix,0,InitMag,0);
    }
    //end

    @Override
    public void onSensorChanged(SensorEvent event) {
        //added
        if (GEO_SCAN_ENABLE) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
                mathMethod.lowPass(event.values.clone(), mMagnetometerReading);
            } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                System.arraycopy(event.values, 0, mGravmeterReading,
                        0, mGravmeterReading.length);
                mathMethod.lowPass(event.values.clone(), mGravmeterReading);
            }

            if (mGravmeterReading != null && mMagnetometerReading != null) {
                float I[] = new float[9];
                mSensorManager.getRotationMatrix(mRotationMatrix, I,
                        mGravmeterReading, mMagnetometerReading);
            }
            updateOrientationAngles();
            //获取raw角度数据
            double value = 180 / Math.PI;
            mFinalOrientationAngles[1] = value * mOrientationAngles[1] ; //pitch 角度在-180-180
            if(mGravmeterReading[2] < 0){
                if(mFinalOrientationAngles[1] > 0){
                    mFinalOrientationAngles[1] = 180 - mFinalOrientationAngles[1];
                }
                else if(mFinalOrientationAngles[1] < 0){
                    mFinalOrientationAngles[1] = -180 - mFinalOrientationAngles[1];
                }
            }
            mFinalOrientationAngles[2] = value * -mOrientationAngles[2] / 2; //roll 角度在-90-90
            mFinalOrientationAngles[0] = value * mOrientationAngles[0]; //azimuth 角度在0-360
            if(mFinalOrientationAngles[0] < 0){
                mFinalOrientationAngles[0] = 360 + mFinalOrientationAngles[0];
            }
            setRotMatrix();

            float[][] mRotationMatrixNew = {{mRotationMatrix[0],mRotationMatrix[1],mRotationMatrix[2]},{mRotationMatrix[3],mRotationMatrix[4],mRotationMatrix[5],},{mRotationMatrix[6],mRotationMatrix[7],mRotationMatrix[8]}};
            float[][] mReverseMatrix = mathMethod.getReverseMartrix(mRotationMatrixNew);
            float[][] InitMag = {{mMagnetometerReading[0]},{mMagnetometerReading[1]},{mMagnetometerReading[2]}};
            MagRotNew = mathMethod.multip3x(mReverseMatrix, InitMag);

            double total = Math.sqrt(MagRotNew[0][0]*MagRotNew[0][0]+MagRotNew[1][0]*MagRotNew[1][0]+MagRotNew[2][0]*MagRotNew[2][0]);
            String resultX = String.format("%8.3f",MagRotNew[0][0]);
            String resultY = String.format("%8.3f",MagRotNew[1][0]);
            String resultZ = String.format("%8.3f",MagRotNew[2][0]);
            /*tv_geoX.setText("GeoX:"+mMagnetometerReading[0]);
            tv_geoY.setText("GeoY:"+mMagnetometerReading[1]);
            tv_geoZ.setText("GeoZ:"+mMagnetometerReading[2]);*/
            tv_geoX.setText("GeoX:"+ resultX);
            tv_geoY.setText("GeoY:"+ resultY);
            tv_geoZ.setText("GeoZ:"+ resultZ);
        }
        }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.

        //mSensorManager.getRotationMatrix(mRotationMatrix, null,
               // mAccelerometerReading, mMagnetometerReading);
        float I[] = new float[9];
        mSensorManager.getRotationMatrix(mRotationMatrix, I,
           mGravmeterReading, mMagnetometerReading);
        // "mRotationMatrix" now has up-to-date information.
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        // "mOrientationAngles" now has up-to-date information.
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