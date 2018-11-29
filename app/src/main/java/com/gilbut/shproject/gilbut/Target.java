package com.gilbut.shproject.gilbut;

public class Target {

    private String id;                                                //내 아이디 (대상)
    private int status;                                                 //연결상태
    private double latitude;                                            //위도
    private double longitude;                                           //경도
    private Boolean alarm;                                              //보호자에게가는 알람서비스 on/off
    private Boolean emergency;                                          //긴급알람
    //private long key;


    public Target(){
        id = null;
        status = 10;
        latitude = -1;
        longitude = -1;
        alarm = true;
        emergency = false;
    }

    public void set_Id(String id){this.id=id;}
    public void setStatus(int status){this.status = status;}
    public void setLocation(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public void setAlarm(Boolean alarm){this.alarm = alarm;}
    public void setEmergency(Boolean emergency){this.emergency = emergency;}

    public String get_Id(){return id;}
    public int getStatus(){return status;}
    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}
    public Boolean getAlarm(){return alarm;}
    public Boolean getEmergency(){return emergency;}
}
