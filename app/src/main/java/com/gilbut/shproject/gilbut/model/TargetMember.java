package com.gilbut.shproject.gilbut.model;

import com.gilbut.shproject.gilbut.ConnectionController;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class TargetMember{
    public String mId;
    public Map<String,Double> location;
    public boolean alarm;

    public TargetMember(){}

    public TargetMember(String tId, LatLng location, boolean alarm){
        this.mId = tId;
        HashMap<String, Double>  newLocation = new HashMap<>();
        newLocation.put("latitude",location.latitude);
        newLocation.put("longitude", location.longitude);
        this.location = newLocation;
        this.alarm = alarm;
    }

    public LatLng getLocation() {
        if(location != null && location.get("latitude") != null && location.get("longitude") != null) {
            return new LatLng(location.get("latitude"), location.get("longitude"));
        }
        else
            return null;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }
}