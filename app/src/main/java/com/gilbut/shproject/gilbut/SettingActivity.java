package com.gilbut.shproject.gilbut;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener
        , GoogleMap.OnMapClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleMap map;
    GoogleApiClient googleApiClient = null;


    LocationManager locationManager;
    LocationListener locationListener;
    LocationRequest locationRequest;
    Location location;

    Protector protector;

    PolylineOptions polylineOptions;
    PolygonOptions polygonOptions;
    Polygon polygon;
    ArrayList<LatLng> arrayPoints;

    Button button;
    ToggleButton pre_pro;

    String targetId;

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        /*
            아직 완벽하지 않은 설정 화면
            여기서 한단계 더 들어가 범위를 지정해야 하나
            우선 이 곳에서 테스트로 코딩함
        */



        init();
    }

    public void init() {
        Bundle extras = getIntent().getExtras();
        targetId = extras.getString("targetId");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String protectorId =  auth.getCurrentUser().getEmail();

        polygonOptions = new PolygonOptions();

        pre_pro = (ToggleButton)findViewById(R.id.pre_pro);
        pre_pro.setTextOn("이탈 금지");
        pre_pro.setTextOff("접근 금지");
        pre_pro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isPrevent) {
                ConnectionController connectionController = new ConnectionController();
                connectionController.setPrevent(targetId, protectorId, isPrevent);
            }
        });

        button = (Button)findViewById(R.id.end_of_poly);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.clear();

                arrayPoints.add(arrayPoints.get(0));
                polygonOptions.addAll(arrayPoints);
                polygonOptions.strokeWidth(15);
                polygonOptions.strokeColor(Color.rgb(255,203,81));
                polygon = map.addPolygon(polygonOptions);

                //레인지 추가
                RangeController range = new RangeController();
                range.updateRange(arrayPoints, targetId, protectorId, new RangeController.OnSetRangeListener() {
                    @Override
                    public void onComplete(String rangeRef) {
                        ConnectionController connectionController = new ConnectionController();
                        connectionController.updateConnectionRangeReference(targetId, protectorId, rangeRef, new ConnectionController.OnSetCompleteListener() {
                            @Override
                            public void onComplete() {
                                // 범위 갱신 성공.
                            }

                            @Override
                            public void onFailure(String err) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(String err) {

                    }
                });

                Intent result = new Intent();
                //result.putExtra("setting", range);
                result.putExtra("setting", arrayPoints);
                result.putExtra("test", "test");
                result.putExtra("pre_pro",pre_pro.isChecked());
                //1 대신 범위 정보
                setResult(1,result);
                finish();
                //startActivity(result);
            }
        });

        protector = new Protector();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API) //로케이션정보를 얻겠다
                    .build();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        arrayPoints = new ArrayList<LatLng>();
    }


    void updateMap(Location newLoc) {
        LatLng Loc = new LatLng(newLoc.getLatitude(), newLoc.getLongitude());
        map.clear();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 16));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);


        updateMap(location);
    }

    @Override
    public void onLocationChanged(Location location) {
       // updateMap(location);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        map.addMarker(markerOptions);

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.rgb(255,203,81));
        polylineOptions.width(10);
        arrayPoints.add(latLng);
        polylineOptions.addAll(arrayPoints);
        map.addPolyline(polylineOptions);


    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        map.clear();
        arrayPoints.clear();
    }
}
