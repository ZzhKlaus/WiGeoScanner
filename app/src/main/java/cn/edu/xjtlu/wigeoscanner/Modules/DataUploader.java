package cn.edu.xjtlu.wigeoscanner.Modules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import cn.edu.xjtlu.wigeoscanner.Views.MainActivity;

import static cn.edu.xjtlu.wigeoscanner.Views.MainActivity.dataparas;

/**
 * Created by jBta0 on 2018/7/12.
 */
public class DataUploader extends AsyncTask<Void, Void, Boolean> {

    private String reg_url = "http://192.168.43.222/wifi_fingerprint/register.php";

    private Context context;

    public DataUploader(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            for(WiFiItem wi: MainActivity.global_wifiItemlist){
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("Building", "UTF-8") + "=" + URLEncoder.encode(dataparas.getStore_building(), "UTF-8") + "&" +
                        URLEncoder.encode("Floor", "UTF-8") + "=" + URLEncoder.encode(dataparas.getStore_floor(), "UTF-8") + "&" +
                        URLEncoder.encode("Location_x", "UTF-8") + "=" + URLEncoder.encode(dataparas.getStore_location_x(), "UTF-8") + "&" +
                        URLEncoder.encode("Location_y", "UTF-8") + "=" + URLEncoder.encode(dataparas.getStore_location_y(), "UTF-8") + "&" +
                        //ToDo add the geomagnetic values
                        URLEncoder.encode("SSID", "UTF-8") + "=" + URLEncoder.encode(wi.getSSID(), "UTF-8") + "&" +
                        URLEncoder.encode("BSSID", "UTF-8") + "=" + URLEncoder.encode(wi.getMAC_ADDR(), "UTF-8") + "&" +
                        URLEncoder.encode("Frequency", "UTF-8") + "=" + URLEncoder.encode(wi.getFREQUENCY(), "UTF-8") + "&" +
                        URLEncoder.encode("Level", "UTF-8") + "=" + URLEncoder.encode(wi.getRSSI(), "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.close();
                Log.i("Data Uploader ","uploading - "+wi.getSSID()+" "+wi.getMAC_ADDR()+" "+wi.getRSSI());
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }  catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean s) {
        if(s){
            Toast.makeText(this.context, "data upload SUCCESS!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this.context, "data upload FAILED!", Toast.LENGTH_SHORT).show();
        }
    }

}