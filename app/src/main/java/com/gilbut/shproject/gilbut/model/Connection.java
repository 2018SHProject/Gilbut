package com.gilbut.shproject.gilbut.model;

import com.gilbut.shproject.gilbut.Range;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connection {
    public  Long status;
    public String tId;
    public String pId;
    public String date;
    public String rangeRef;
    public boolean alarm;
    public double latitude;
    public double longitude;

    public Connection(){}

    public Connection(Long status, String tId, String pId, String rangeRef, String date, boolean alarm, double latitude, double longitude){
        this.status = status;
        this.tId = tId;
        this.pId = pId;
        this.date = date;
        this.rangeRef = rangeRef;
        this.alarm = alarm;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}