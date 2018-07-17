package cn.edu.xjtlu.wigeoscanner.Modules;

public class DataParas {
    private String store_building;
    private String store_floor;
    private String store_location_x;
    private String store_location_y;

    public DataParas(String building,String floor,String location_x,String location_y){
        this.store_building = building;
        this.store_floor = floor;
        this.store_location_x = location_x;
        this.store_location_y = location_y;
    }

    public String getStore_building() {
        return store_building;
    }

    public void setStore_building(String store_building) {
        this.store_building = store_building;
    }

    public String getStore_floor() {
        return store_floor;
    }

    public void setStore_floor(String store_floor) {
        this.store_floor = store_floor;
    }

    public String getStore_location_x() {
        return store_location_x;
    }

    public void setStore_location_x(String store_location_x) {
        this.store_location_x = store_location_x;
    }

    public String getStore_location_y() {
        return store_location_y;
    }

    public void setStore_location_y(String store_location_y) {
        this.store_location_y = store_location_y;
    }
}
