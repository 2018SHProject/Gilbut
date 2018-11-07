package com.gilbut.shproject.gilbut;

public class Target {

    private String m_Id;                                                //내 아이디 (대상)
    private String y_Id;                                                //보호자 아이디
    private int status;                                                 //연결상태
    private double latitude;                                            //위도
    private double longitude;                                           //경도
    private Boolean alarm;                                              //보호자에게가는 알람서비스 on/off

    public void setM_Id(String m_Id){this.m_Id = m_Id;}
    public void setY_Id(String y_Id){this.y_Id = y_Id;}
    public void setStatus(int status){this.status = status;}
    public void setLocation(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public void setAlarm(Boolean alarm){this.alarm = alarm;}

    public String getM_Id(){return m_Id;}
    public String getY_Id(){return y_Id;}
    public int getStatus(){return status;}
    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}
    public Boolean getAlarm(){return alarm;}
}
