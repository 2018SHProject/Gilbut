package com.gilbut.shproject.gilbut.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class Connection {
    public  Long status;
    public String tId;
    public String pId;
    public String date;
    public String rangeRef;
    public boolean alarm;

    public Map<String,Double> location;

    public Connection(){}

    public Connection(Long status, String tId, String pId, String rangeRef, String date, boolean alarm, double latitude, double longitude){
        this.status = status;
        this.tId = tId;
        this.pId = pId;
        this.date = date;
        this.rangeRef = rangeRef;
        this.alarm = alarm;
        HashMap<String, Double>  newLocation = new HashMap<>();
        newLocation.put("latitude",latitude);
        newLocation.put("longitude", longitude);
        this.location = newLocation;
    }

    public Connection(Long status, String tId, String pId, String rangeRef, String date, boolean alarm, LatLng location){
        this.status = status;
        this.tId = tId;
        this.pId = pId;
        this.date = date;
        this.rangeRef = rangeRef;
        this.alarm = alarm;
        HashMap<String, Double>  newLocation = new HashMap<>();
        newLocation.put("latitude",location.latitude);
        newLocation.put("longitude", location.longitude);
        this.location = newLocation;
    }

    public LatLng getLocation() {
        if(location != null && location.get("latitude") != null && location.get("longitude") != null) {
            return new LatLng(location.get("latitude"), location.get("longitude"));
        }
        else
            return null;
    }
}