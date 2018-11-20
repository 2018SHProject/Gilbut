package com.gilbut.shproject.gilbut;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//대상이 보호자에게 연결요청, 이 후 보호자가 대상에게 위치정보요청
//status 설명
//-1 : nothing
//0 : 대상->보호자 연결요청보낸상태
//1 : 모든 연결 완료
//2 : 보호자가 거절 (이 후 -1로 변경)

//checkStatus를 주기적으로 실행할? 아니면 status값에 대한 변동이 일어나면 실행을!

public class TargetActivity extends AppCompatActivity {

    LocationManager locationManager;
    Target target;                                                              //대상 유저 클래스
    Intent intent;
    Button onBtn;                                                               //위치전송 on 버튼
    Button offBtn;                                                              //위치전송 off 버튼
    TextView noProtector;                                                       //연결된 보호자가 없을 때 띄울 메세지
    TextView tWait;                                                             //보호자의 수락을 기다릴 때 보일 메세지.
    FloatingActionButton fab;                                                   //Fab
    double latitude;
    double longitude;

    String[] permission_list={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        init();                                                                 //제대로 들어왔는지 확인
        setting();                                                              //초기값 세팅
        checkStatus();

    }




    public void init(){
        //MainActivity에서 넘긴 정보를 가지고 DB에 저장되어있는 대상 / 연결정보를 가져온다.
        intent = getIntent();
        target = new Target();
        onBtn = (Button)findViewById(R.id.tAlarmOn);
        offBtn = (Button)findViewById(R.id.tAlarmOff);
        noProtector = (TextView)findViewById(R.id.noProtector);
        tWait = (TextView)findViewById(R.id.tWait);
        fab = (FloatingActionButton)findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog ad = new AlertDialog.Builder(TargetActivity.this).create();

                ad.setTitle("보호자 등록");       // 제목 설정
                ad.setMessage("보호자로 등록할 아이디를 적어주세요. 해당 요청은 위치정보공유허락을 의미합니다.");   // 내용 설정

                // EditText 삽입하기
                final EditText editText = new EditText(getApplicationContext());
                ad.setView(editText);

                // 확인 버튼 설정
                ad.setButton(DialogInterface.BUTTON_POSITIVE,"요청 전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //파베에 새로운 연결 생성.
                        ConnectionController connectionController = new ConnectionController();
                        // (대상 id, 보호자 id, status, 성공시 콜백-OnSetCompleteListener)
                        connectionController.addNewConnection(target.getM_Id(), target.getY_Id(), -1, new ConnectionController.OnSetCompleteListener() {
                            @Override
                            public void onComplete() {
                                //새로운 연결 생성 완료.
                                 Toast.makeText(getApplicationContext(), "요청완료", Toast.LENGTH_SHORT).show();
                            }
                        });
//                        target.setStatus(0);
//                        String y_Id = editText.getText().toString();
                        //바로 이전의 키값을 저장하고 있다고 할까? (db에 적용), 그러면 새로 넣을 경우에 이전 키값을 지워버리면 되잖아!
                        dialog.dismiss();
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE,"취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소하였습니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                // 창 띄우기
                ad.show();
            }
        });

        int checkMod = intent.getIntExtra("mod",-1);
        if(checkMod==1){
            //checkMod가 대상모드로 제대로 선택돼서 들어왔을 경우 그냥 넘어감
        }
        else if(checkMod==0){
            //checkMod가 보호자로 선택돼서 들어옴
            //error처리
        }
        else{
            //checkMod가 이상한 값이 들어왔을 경우.
            //error.
        }

    }

    public void setting(){
        String y_Id = null;         //protector
        int status = 10;            //상태값
        Boolean alarm = false;      //알람
        String m_Id = intent.getStringExtra("mid");

        //m_id로 연결db에서 정보들을 가져와 y_id, status를 초기화한다.
//        target.setM_Id(m_Id);
//        target.setY_Id(y_Id);
//        target.setStatus(status);
//        target.setAlarm(alarm);
    }


    public void checkStatus(){
        //status를 확인해서 각각 상황에 맞는 작업을 수행한다.
        int status = target.getStatus();
        onBtn.setVisibility(View.GONE);
        offBtn.setVisibility(View.GONE);
        tWait.setVisibility(View.GONE);
        noProtector.setVisibility(View.GONE);
        fab.show();
        if(status == 1){
            //연결이 완료되어있는경우
            showToggle();
            fab.hide();
            //토글 띄우고 지속적으로 location 서버전송
        }
        else if(status == 0){
            //연결 요청 후 보류상태.
            //아니면 계속 새로 보낼 수 있게 만들어! (Floating action button)
            //그러면 이전의 보낸 메세지는 무효화되게 만들어야함!.
            tWait.setVisibility(View.VISIBLE);
        }
        else if(status == -1){
            //아무것도 아닌 경우
            noProtector.setVisibility(View.VISIBLE);
        }
        else if(status == 2){
            //거절당한경우
            showRefused();
        }
        else {
            //잘못된 값이 들어온 경우
            //error
        }
    }

    public void showToggle(){
        if(target.getAlarm()){                          //만약 alarm을 켜 놓은 경우라면?
            onBtn.setVisibility(View.GONE);
            offBtn.setVisibility(View.VISIBLE);
        }else{                                          //alarm을 끈 경우
            onBtn.setVisibility(View.VISIBLE);
            offBtn.setVisibility(View.GONE);
        }

        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target.setAlarm(true);
                //db에도 저장
                Toast.makeText(getApplicationContext(),"보호자에게 알림을 보냅니다",Toast.LENGTH_SHORT).show();
            }
        });

        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target.setAlarm(false);
                //db에도 저장
                Toast.makeText(getApplicationContext(),"보호자에게 알림이 가지 않습니다",Toast.LENGTH_SHORT).show();
            }
        });

        setLocation();
    }
    public void setLocation(){

        String locationProvider = null;
        ContextCompat.checkSelfPermission(this, String.valueOf(permission_list));                                                   //권한확인하고

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER) == true){                                                    //네트워크 우선!
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        else if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) == true){
            locationProvider = LocationManager.GPS_PROVIDER;
        }
        locationManager.requestLocationUpdates(locationProvider,1000*180,10, locationListener );                        //위치받을세팅 완료
        //1000 : 1초, 180 : 3분
    }

    public void select(){

    }

    public void showRefused(){
        //연결요청을 거부당했다는 팝업을 띄운다.
        Toast.makeText(getApplicationContext(),"연결 요청이 거절당했습니다.",Toast.LENGTH_SHORT).show();
        target.setStatus(-1);
        noProtector.setVisibility(View.VISIBLE);
        //db도 갱신
        //
    }



    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();                                  //위도 받아오기
            longitude = location.getLongitude();                                //경도 받아오기
            target.setLocation(latitude,longitude);                             //target에 저장
            //db에도 저장
            setLocation();                                                      //다시 해줘서 gps, 네트워크중 최적으로!
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 아래는 정동 코드에 있어야 할 내용들
            //firebaseDatabase = FirebaseDatabase.getInstance();
            //databaseReference = firebaseDatabase.getReference("TargetLatlng");

            // 임시로 붙인 레퍼런스 이름 - 정동이 디비에 보낼 때 지정하는 이름


            // 가장 처음 들어갈 때 코드
            // Target target = new Target("대상Id", "보호자Id", 1 , latitude, longitude, true);
            // databaseReferece.child("대상Id").setValue(target);

            // 대상 별 좌표 업뎃됐을 때 코드
            //Map<String, Object> map = new HashMap<String, Object>();
            //map.put("대상Id"/latitude",36.457821);
            //map.put("대상Id"/longitude",127.457821);
            //databaseReference.updateChildren(map);
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
