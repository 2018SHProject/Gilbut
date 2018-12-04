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

    public Connection(){}

    public Connection(Long status, String tId, String pId, String rangeRef, String date){
        this.status = status;
        this.tId = tId;
        this.pId = pId;
        this.date = date;
        this.rangeRef = rangeRef;
    }

    public Connection(Long status, String tId, String pId, String rangeRef, String date, boolean alarm, LatLng location){
        this.status = status;
        this.tId = tId;
        this.pId = pId;
        this.date = date;
        this.rangeRef = rangeRef;
    }
}