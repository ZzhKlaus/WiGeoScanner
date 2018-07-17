package cn.edu.xjtlu.wigeoscanner.Modules;

/**
 * Created by jBta0 on 2018/7/12.
 */
public class WiFiItem {

    private int     imageId;
    private String  SSID;
    private String  MAC_ADDR;
    private String  RSSI;
    private String  FREQUENCY;

    public WiFiItem(String ssid,String mac_addr,String rssi) {
        this.SSID = ssid;
        this.MAC_ADDR = mac_addr;
        this.RSSI = rssi;
    }
    public WiFiItem(int imageId,String ssid,String mac_addr,String rssi) {
        this.imageId = imageId;
        this.SSID = ssid;
        this.MAC_ADDR = mac_addr;
        this.RSSI = rssi;
    }
    public WiFiItem(int imageId,String ssid,String mac_addr,String rssi,String frequency) {
        this.imageId = imageId;
        this.SSID = ssid;
        this.MAC_ADDR = mac_addr;
        this.RSSI = rssi;
        this.FREQUENCY = frequency;
    }

    public int getImageId() {
        return imageId;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getMAC_ADDR() {
        return MAC_ADDR;
    }

    public void setMAC_ADDR(String MAC_ADDR) {
        this.MAC_ADDR = MAC_ADDR;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String getFREQUENCY() {
        return FREQUENCY;
    }

    public void setFREQUENCY(String FREQUENCY) {
        this.FREQUENCY = FREQUENCY;
    }
}
