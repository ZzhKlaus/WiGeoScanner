package cn.edu.xjtlu.wigeoscanner.Modules;

public class GeoItem {
    private String store_geo_x;
    private String store_geo_y;
    private String store_geo_z;

    public GeoItem(String geox,String geoy,String geoz){
        this.store_geo_x = geox;
        this.store_geo_y = geoy;
        this.store_geo_z = geoz;
    }

    public String getStore_geo_x() {
        return store_geo_x;
    }

    public void setStore_geo_x(String store_geo_x) {
        this.store_geo_x = store_geo_x;
    }

    public String getStore_geo_y() {
        return store_geo_y;
    }

    public void setStore_geo_y(String store_geo_y) {
        this.store_geo_y = store_geo_y;
    }

    public String getStore_geo_z() {
        return store_geo_z;
    }

    public void setStore_geo_z(String store_geo_z) {
        this.store_geo_z = store_geo_z;
    }
}
