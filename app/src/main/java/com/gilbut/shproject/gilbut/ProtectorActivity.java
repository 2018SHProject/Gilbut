package com.gilbut.shproject.gilbut;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.gilbut.shproject.gilbut.R.id;
import static com.gilbut.shproject.gilbut.R.layout;

public class ProtectorActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    // Firebase DB 임시 설정

    Protector protector;

    SupportMapFragment mapFragment;
    GoogleMap map;
    GoogleApiClient googleApiClient = null;

    double latitude;
    double longitude;

    ToggleButton PTB;

    View toastview;
    LinearLayout dialview;
    Toast toast;
    TextView textView;

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
        setContentView(layout.activity_protector);
        Toolbar toolbar = (Toolbar) findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            checkPer();
            // 퍼미션 허용
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            init();
            // 화면 초기화
        }



        // switch 안의 것이 나중에는 Connection의 변수 중 하나가 되어야 겟디요
        // 우선 보기 쉽게 한 곳에 몰아 넣었음
        switch (protector.status){

            case 0 :
                // 요청 들어 온 상태
                new AlertDialog.Builder(this)
                        .setTitle("<연결 요청 알림>")
                        .setView(dialview)
                        .setPositiveButton("연결", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                protector.status = 1;
                                textView.setText("연결 승인");
                                toast.setView(toastview);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.show();
                            }
                        })
                        .setNegativeButton("거절", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                protector.status = 2;
                                textView.setText("연결 거부");
                                toast.setView(toastview);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }).show();
                //break;
            case 1 :
                // 요청 승인 확인


                initFire();
                break;
            case 2 :

                // 거절 알림 후 -1로 변경
                break;

            case -1 :
                // 미연결 상태


                break;

        }


        PTB = (ToggleButton)findViewById(id.PTB);
        PTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                }
                else{

                }
            }
        });


    }

    public void checkPer(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                0
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    void openSetting(){

        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            openSetting();

        return true;
    }

    public void init(){

        protector = new Protector();

        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API) //로케이션정보를 얻겠다
                    .build();
        }

        // 연결 되어있는 지 확인
        toast = new Toast(ProtectorActivity.this);


        dialview = (LinearLayout)View.inflate(this,layout.connect_dialog,null);


        toastview  = getLayoutInflater().inflate(layout.connection_toast, (ViewGroup) findViewById(id.toast_costum));
        textView = (TextView)toastview.findViewById(id.connection_toast);




    }

    public void printMap(double lat, double lng){
        final LatLng Loc = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 16));

        // 여기서 범위 이탈 체크하고 알림 주기

    }

    public void initFire(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("TargetLatlng");
        // 임시로 붙인 레퍼런스 이름 - 정동이 디비에 보낼 때 지정하는 이름



        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // 나와 연결 된 대상의 좌표 읽어오기 - 대상의 yId가 나일 때 읽어오기 등등
                // 업데이트 된 정보의 yId가 나일 때 적용
                Target message = dataSnapshot.getValue(Target.class);
                // 현재 좌표로만 36.14578 - 127.568978 로 저장되어있다고 가정했을 때 받아오는 방식
                printMap(message.getLatitude(), message.getLongitude());
                // DB 값이 변화됐을 때 -
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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

    }

    @Override
    public void onConnectionSuspended(int i) {
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

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        latitude = 37.5557750;
        longitude = 126.9724722;
        // 가장 최근 대상 위치

        LatLng Loc = new LatLng(latitude, longitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Loc, 16));

    }
}
