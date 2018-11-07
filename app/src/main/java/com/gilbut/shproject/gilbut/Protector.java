package com.gilbut.shproject.gilbut;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Tazo on 2018-11-07.
 */

public class Protector {
    String mId;
    String yId;
    int status;
    boolean alarm;
    List<LatLng> latlngs;
    ;

    Protector(){};
    Protector(String mId){this.mId = mId;};

    void PsetmId(String mId){this.mId = mId;};
    void PsetyId(String yid){this.yId = yid;}
    void PsetisConnected(int isConnected){this.status = isConnected;};
    void PsetAlarmcheck(boolean alarm){this.alarm = alarm;};
    void PsetList(List<LatLng> latLngs){this.latlngs = latLngs;};
    void PsetPolyline(){};

    String PgetmId(){return mId;};
    String PgetyId(){return yId;};
    int PgetisConnected(){return status;};
    boolean PgetAlarmcheck(){return alarm;};
    // PgetPolyline(){}
    List<LatLng> PgetList(){return latlngs;};
}
