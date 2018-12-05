package com.gilbut.shproject.gilbut;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import static com.gilbut.shproject.gilbut.R.id;
import static com.gilbut.shproject.gilbut.R.layout;

public class ProtectorActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    final int setting_Result = 1;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    // Firebase DB 임시 설정

    ConnectionController connectionController;

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

    ListView listView;
    ArrayList<Connection> connections;
    TargetListAdapter arrayAdapter;

    // 우용 추가
    LatLng me; // 내 위치정보 저장
    LocationManager locationManager;
    String[] permission_list={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    Context Pcontext;


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
        Pcontext = getApplication();


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


    void openSetting(String targetId){

        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        intent.putExtra("targetId", targetId);
        startActivityForResult(intent, setting_Result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == setting_Result){
            //setting 화면에서 돌아왔을 때
            //Toast.makeText(this,"범위를 생성/수정 하였습니다",Toast.LENGTH_LONG).show();

            Intent i = getIntent();
            //RangeController latLngs = bundle.getParcelable("setting");
            ArrayList<LatLng> latLngs = data.getParcelableArrayListExtra("setting");
            if(latLngs != null) {
                Toast.makeText(this,"Latlng 넘어옴"  ,Toast.LENGTH_LONG).show();

                printMap(latLngs.get(0).latitude,latLngs.get(0).longitude);
                PolygonOptions polygonOptions = new PolygonOptions();
               polygonOptions.addAll(latLngs);
                polygonOptions.strokeWidth(15);
                polygonOptions.strokeColor(Color.rgb(255, 203, 81));
                Polygon polygon = map.addPolygon(polygonOptions);
            }
            else{
                Toast.makeText(this,"Latlng 안 넘어옴",Toast.LENGTH_LONG).show();

            }
            Toast.makeText(this,data.getStringExtra("test"), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TextView targetIdTextView = (TextView)item.getActionView().findViewById(id.target_id);
        openSetting(targetIdTextView.getText().toString());
        return true;
    }

    public void init(){

        Intent intent = getIntent();

        protector = new Protector();
        //protector.mId = intent.getStringExtra("mId");
        // Auth에서 받아오도록 수정완료.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        protector.mId = auth.getCurrentUser().getEmail();
        Toast.makeText(this, protector.mId, Toast.LENGTH_SHORT).show();
        protector.status = 1;

        // 보호자에게 연결을 시도할 때마다 실행!
        Observer observer = new Observer();
        observer.setObserveringNewConnection(protector.mId, new Observer.OnObservedDataChange() {
            @Override
            public void OnDataChange(Object object) {
                Connection connection = (Connection)object;
                //TODO: 여기서 연결하겠냐는 팝업 띄우면될거야. connection 정보에 보호자 정보도 있구. 연결 오키 하면 연결 status 1로 바꿔주기도 잊지말기!
            }
        });

        connectionController = new ConnectionController();

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

        listView = (ListView)findViewById(id.TargetList);


        connections = new ArrayList<Connection>();
        connectionController.getProtectorConnections(protector.mId, new ConnectionController.OnGetConnectionsListener() {
            @Override
            public void onComplete(ArrayList<Connection> connection) {
                connections.addAll(connection);

                arrayAdapter = new TargetListAdapter(Pcontext, layout.target_list, connections);
                listView.setAdapter(arrayAdapter);
                //TODO: 모가 문제일까요 => 조금뒤에 해결해보겠습니다.
            }

            @Override
            public void onFailure(String err) {

            }
        });


        //나와 연결된 타겟들 받아와서 추가하기

    }

    public void printMap(double lat, double lng){
        final LatLng Loc = new LatLng(lat, lng);
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


    //우용 추가
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();                                  //위도 받아오기
            double lng = location.getLongitude();                                //경도 받아오기
            me = new LatLng(lat, lng);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    public double distfromTarget(Target target) {
        ContextCompat.checkSelfPermission(this, String.valueOf(permission_list));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,5000,10, locationListener );
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,5000,10, locationListener );
        LatLng target_latlng = new LatLng(target.getLatitude(), target.getLongitude());

        return (SphericalUtil.computeDistanceBetween(me, target_latlng));
    }

    public void logoutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
