package com.example.mayank.smarthome.model;


public class DeviceData {

    //Private Variables
    private String DeviceName ;
    private String LastConnectionTime ;

    //Public Constructor
    public DeviceData() {

    }

    public DeviceData(String deviceName, String lastConnectionTime) {
        DeviceName = deviceName;
        LastConnectionTime = lastConnectionTime;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public String getLastConnectionTime() {
        return LastConnectionTime;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public void setLastConnectionTime(String lastConnectionTime) {
        LastConnectionTime = lastConnectionTime;
    }
}
