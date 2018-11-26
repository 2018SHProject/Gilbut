package com.gilbut.shproject.gilbut.model;

import java.util.List;

public class Connection {
    public  Long status;
    public String tId;
    public String pId;
    public String date;
    public String range;
    public boolean alarm;
    public double latitude;
    public double longitude;
    public List<Location> rangeList;

    public Connection(){}

    public Connection(Long status, String tId, String pId, String date, String range, boolean alarm, double latitude, double longitude){
        this.status = status;
        this.tId = tId;
        this.pId = pId;
        this.date = date;
        this.range = range;
        this.alarm = alarm;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}