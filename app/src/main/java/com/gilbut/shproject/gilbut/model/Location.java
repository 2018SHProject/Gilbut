package com.gilbut.shproject.gilbut.model;

public class Location {
    double latitude;
    double longitude;

    public Location(){}

    public  Location(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
