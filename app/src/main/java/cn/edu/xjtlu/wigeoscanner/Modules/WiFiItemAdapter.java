package cn.edu.xjtlu.wigeoscanner.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.xjtlu.wigeoscanner.R;

/**
 * Created by jBta0 on 2018/7/12.
 */

public class WiFiItemAdapter extends ArrayAdapter{
    private final int resourceId;

    public WiFiItemAdapter(Context context, int textViewResourceId, List<WiFiItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WiFiItem wiFiItem = (WiFiItem) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView wifiImage = (ImageView) view.findViewById(R.id.wifi_image);
        TextView wifiSSID = (TextView) view.findViewById(R.id.wifi_ssid);
        TextView wifiMAC = (TextView) view.findViewById(R.id.wifi_mac);
        TextView wifiRSSI = (TextView) view.findViewById(R.id.wifi_rssi);
        wifiImage.setImageResource(wiFiItem.getImageId());
        wifiSSID.setText(wiFiItem.getSSID());
        wifiMAC.setText(wiFiItem.getMAC_ADDR());
        wifiRSSI.setText(wiFiItem.getRSSI());
        return view;
    }
}
