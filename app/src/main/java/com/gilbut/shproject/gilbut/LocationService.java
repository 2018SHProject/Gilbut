package com.gilbut.shproject.gilbut;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service {
    String myId;

    // 타겟 아이디 받아야되서 이거 쓰면안됨.
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        myId = firebaseAuth.getCurrentUser().getEmail();
        setLocation();
    }

    public void setLocation(){
        String[] permission_list={
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        };
        LocationManager locationManager;
        ContextCompat.checkSelfPermission(this, String.valueOf(permission_list));                                                   //권한확인하고
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,5000,10, locationListener );                        //위치받을세팅 완료
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,5000,10, locationListener );
        //1000 : 1초, 180 : 3분
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            final double latitude = location.getLatitude();                                  //위도 받아오기
            final double longitude = location.getLongitude();                                //경도 받아오기
            //db에도 저장
            Member member = new Member();
            member.updateLocation(myId, new LatLng(latitude,longitude), new Member.OnSetCompleteListener(){

                @Override
                public void onComplete() {
                    Toast.makeText(getApplicationContext(),"위도 " +latitude+" 경도 "+ longitude,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String err) {

                }
            });

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            if(status==0)
//                Toast.makeText(getApplicationContext(),provider+"로 변경,이용불가",Toast.LENGTH_SHORT).show();
//            else if(status==1)
//                Toast.makeText(getApplicationContext(),provider+"로 변경, 일시로 정지",Toast.LENGTH_SHORT).show();
//            else if(status==2)
//                Toast.makeText(getApplicationContext(),provider+"로 변경, 이용가능",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
