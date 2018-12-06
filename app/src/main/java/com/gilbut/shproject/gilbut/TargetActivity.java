package com.gilbut.shproject.gilbut;

        import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gilbut.shproject.gilbut.model.Connection;
        import com.github.clans.fab.FloatingActionMenu;
        import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

//대상이 보호자에게 연결요청, 이 후 보호자가 대상에게 위치정보요청
//status 설명
//-1 : nothing
//0 : 대상->보호자 연결요청보낸상태
//1 : 모든 연결 완료
//2 : 보호자가 거절 (이 후 -1로 변경)



public class TargetActivity extends AppCompatActivity {

    ConnectionController connectionController;
    LocationManager locationManager;
    Target target;                                                              //대상 유저 클래스
    Member member;
    Intent intent;
    Button onBtn;                                                               //위치전송 on 버튼
    Button offBtn;                                                              //위치전송 off 버튼
    Button eBtn;                                                                //긴급버튼
    TextView noProtector;                                                       //연결된 보호자가 없을 때 띄울 메세지
    TextView tWait;                                                             //보호자의 수락을 기다릴 때 보일 메세지.
    FloatingActionMenu fmenu;
    com.github.clans.fab.FloatingActionButton add;
    com.github.clans.fab.FloatingActionButton list;

    double latitude;
    double longitude;
    ArrayList<Connection> plist;                                                //연결된 보호자 리스트

    // 우용 추가
    Button logoutBtn;                                                           // 로그아웃 버튼

    LocationService locationService;
     //연결상태
    int isConnected;                                                                //연결상태

    //-1 : 연결이 아예 없는 경우
    //0 : 커넥션은 있으나(요청은 보냈으나) 어느 누구하고도 연결되지 않은 경우
    //1 ; 한명이라도 연결된 경우

    String[] permission_list={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        auth = FirebaseAuth.getInstance();

        if(ContextCompat.checkSelfPermission(this, String.valueOf(permission_list)) == PackageManager.PERMISSION_DENIED){
            checkPer();
            // 퍼미션 허용
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            init();                                                                 //제대로 들어왔는지 확인
            setting();                                                              //초기값 세팅
            checkConnection();
        }
    }


    public void checkPer(){
        ActivityCompat.requestPermissions(this,
                permission_list,
                0
        );
    }


    @Override
    protected void onDestroy() {
        //백그라운드에서 로케이션 보내게 한번 만들어봄.
        Intent serviceIntent = new Intent(getApplicationContext(),LocationService.class);
        startService(serviceIntent);
        super.onDestroy();
    }

    public void init(){
        //MainActivity에서 넘긴 정보를 가지고 DB에 저장되어있는 대상 / 연결정보를 가져온다.

        intent = getIntent();
        target = new Target();
        onBtn = (Button)findViewById(R.id.tAlarmOn);
        offBtn = (Button)findViewById(R.id.tAlarmOff);
        eBtn = (Button)findViewById(R.id.emergencyBtn);
        noProtector = (TextView)findViewById(R.id.noProtector);
        tWait = (TextView)findViewById(R.id.tWait);
        connectionController = new ConnectionController();
        fmenu = (FloatingActionMenu)findViewById(R.id.fmenu);
        add = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.add);
        list = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.list);
        member = new Member();
        plist = new ArrayList<>();
        // 우용 추가
        logoutBtn = (Button)findViewById(R.id.targetlogoutBtn);
        isConnected= -1;

        // Auth를 이용해서 아이디 받아오기.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        target.set_Id(auth.getCurrentUser().getEmail());

        //위치 보내는 백그라운드 서비스 종료. 포어그라운드일땐 종료하고 백그라운드일때 실행해보게.
        locationService = new LocationService();
        Intent serviceIntent = new Intent(getApplicationContext(),LocationService.class);
        stopService(serviceIntent);

        add.setOnClickListener(new View.OnClickListener() {
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
                    public void onClick(final DialogInterface dialog, int which) {
                        //파베에 새로운 연결 생성.
                        final String p_Id = editText.getText().toString();
                        // p_id (대상이 연결을 원하는 보호자의 아이디)를 입력받아서, 즉 target id랑 protector Id를 입력받아서 connection list에 있는지를 확인.
                        //있으면 이미 요청된 거라고 알람을 띄우고 없으면 아래 로직 진행. 그리고 y_id필요없댔으니까 get_id로만.
                        connectionController.getConnection(target.get_Id(), p_Id, new ConnectionController.OnGetConnectionListener() {
                            @Override
                            public void onComplete(final Connection connection) {
                                //연결 존재.
                                if (connection != null) {

                                    Toast.makeText(getApplicationContext(), "이미 연결된 보호자입니다!", Toast.LENGTH_LONG).show();
                                } else { // 연결 없음. -> 새로 연결 생성.
                                    connectionController.addNewConnection(target.get_Id(), p_Id, 0, new ConnectionController.OnSetCompleteListener() {
                                        @Override
                                        public void onComplete() {
                                            //새로운 연결 생성 완료.
                                            Toast.makeText(getApplicationContext(), "요청완료", Toast.LENGTH_SHORT).show();
                                            // status값이 바뀔 때마다 검사할 Observer 등록.
                                            final Observer statusObserver = new Observer();
                                            statusObserver.setObservingConnectionStatus(target.get_Id(), p_Id, new Observer.OnObservedDataChange() {
                                                @Override
                                                public void OnDataChange(Object object) {
                                                    int status = (int)object;
                                                    Toast.makeText(getApplicationContext(), "status: "+status, Toast.LENGTH_LONG).show();
                                                    switch(status){
                                                        case 1:
                                                            for(int i = 0; i < plist.size(); i++){
                                                                if(plist.get(i).pId.equals(p_Id)){
                                                                    plist.get(i).status = (long)1;
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        case 2:
                                                            statusObserver.removeObserver();
                                                            connectionController.removeConnection(target.get_Id(), p_Id, new ConnectionController.OnRemoveListener() {
                                                                @Override
                                                                public void onCompletet() {
                                                                    //삭제 완료후 처리.
                                                                    showRefused(p_Id);
                                                                    for(int i = 0; i < plist.size(); i++){
                                                                        if(plist.get(i).pId.equals(p_Id)){
                                                                            plist.remove(i);
                                                                            int size = plist.size();
                                                                            if(size == 0) {
                                                                                onBtn.setVisibility(View.GONE);
                                                                                offBtn.setVisibility(View.GONE);
                                                                                tWait.setVisibility(View.GONE);
                                                                                eBtn.setVisibility(View.GONE);
                                                                                noProtector.setVisibility(View.VISIBLE);
                                                                            }
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                            break;
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(String err) { // 연결 입력이 왠지 취소됬을 때인듯.
                                            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show(); // "CANCELED"
                                        }
                                    });

                                    target.setStatus(0);
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(String err) {

                            }
                        });
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

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //리스트 띄우기. 이것도 다이얼로그로?
            }
        });

        int checkMod = intent.getIntExtra("value",-1);
        if(checkMod==1){
            //checkMod가 대상모드로 제대로 선택돼서 들어왔을 경우 그냥 넘어감
        }
        else{
            Toast.makeText(getApplicationContext(),"진입 값이 잘못되었습니다",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void setting(){
        String m_Id = auth.getCurrentUser().getEmail();
        //m_id로 연결db에서 정보들을 가져와 y_id, status를 초기화한다.
        connectionController = new ConnectionController();
        //m_id를 입력받아서 연결된 모든 정보를 받아와야함. (보호자 리스트) => 수정
        connectionController.geTargetConnections(m_Id, new ConnectionController.OnGetConnectionsListener() {
            @Override
            public void onComplete(ArrayList<Connection> connections) {

                boolean check = false;
                for(Connection connection : connections){
                    plist.add(connection);
                    if(!check) {
                        if (connection.status.intValue() == 1) {
                            isConnected = 1;
                            check = true;
                        }
                    }
                    if(connection.status.intValue() == 2){
                        String pid = connection.pId;
                        showRefused(pid);
                    }

                }

                if(isConnected != 1)            //연결은 있으나 완료가 되지 않은 경우(어느 누구와도)
                    isConnected = 0;

                checkConnection();
        }

            @Override
            public void onFailure(String err) {
                if(err.equals("NO_DATA")){
                    // 연결이 없을 때.
                    target.setStatus(-1);
                    isConnected = -1;
                    target.setAlarm(false);
                    Toast.makeText(getApplicationContext(), "NO_DATA", Toast.LENGTH_LONG).show();
                }
                checkConnection();
            }
        });
    }

    public void checkConnection(){
        //status를 확인해서 각각 상황에 맞는 작업을 수행한다.
//        int status = target.getStatus();
        onBtn.setVisibility(View.GONE);
        offBtn.setVisibility(View.GONE);
        tWait.setVisibility(View.GONE);
        eBtn.setVisibility(View.GONE);
        noProtector.setVisibility(View.GONE);
        //fab.show(); //TODO: ??여기서 오류가 나는데 이게 뭐야?
        setLocation();

        if(isConnected == 1) {
            //연결이 완료되어있는 경우
            showToggle();
        }else if(isConnected == 0){
            //어느 누구와도 연결이 되지 않은 경우
            tWait.setVisibility(View.VISIBLE);
        }else if (isConnected == -1){
            //연결 자체가 없는 경우
            noProtector.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getApplicationContext(),"Status 값이 잘못되었습니다",Toast.LENGTH_SHORT).show();
            //finish();
        }
    }

    public void showToggle(){
        eBtn.setVisibility(View.VISIBLE);
        // 우용 추가
        logoutBtn.setVisibility(View.VISIBLE);

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
                onBtn.setVisibility(View.GONE);
                offBtn.setVisibility(View.VISIBLE);
                member.setAlarm(target.get_Id(), true, new Member.OnSetCompleteListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getApplicationContext(),"보호자에게 알림을 보냅니다",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String err) {
                        Toast.makeText(getApplicationContext(),"BtnOn 실패",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target.setAlarm(false);
                onBtn.setVisibility(View.VISIBLE);
                offBtn.setVisibility(View.GONE);

                member.setAlarm(target.get_Id(), false, new Member.OnSetCompleteListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getApplicationContext(),"보호자에게 알림이 가지 않습니다",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String err) {
                        Toast.makeText(getApplicationContext(),"BtnOff 실패",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        eBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target.setEmergency(true);
                Member member = new Member();
                member.setEmergency(target.get_Id(), true, new Member.OnSetCompleteListener() {
                    @Override
                    public void onComplete() {
                        // 바꾸는거 성공시. (안써도됨)
                        Toast.makeText(getApplicationContext(),"모든 보호자에게 긴급 알람을 보냅니다",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String err) {
                        //바꾸는거 실패시.(안써도됨)
                        Toast.makeText(getApplicationContext(),"긴급 알람 실패",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        // 우용 추가
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
    public void setLocation(){

        ContextCompat.checkSelfPermission(this, String.valueOf(permission_list));                                                   //권한확인하고
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,5000,10, locationListener );                        //위치받을세팅 완료
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,5000,10, locationListener );
        //1000 : 1초, 180 : 3분
    }


    public void showRefused(String pid){
        //연결요청을 거부당했다는 팝업을 띄운다.
//        noProtector.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),pid+"님이 연결 요청을 거부했습니다.",Toast.LENGTH_SHORT).show();
    }



    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();                                  //위도 받아오기
            longitude = location.getLongitude();                                //경도 받아오기
            target.setLocation(latitude,longitude);                             //target에 저장
            //db에도 저장
            member.getAlarm(target.get_Id(), new Member.OnGetAlarmListener() {
                @Override
                public void onComplete(boolean alarm) {
                    if (alarm) {
                        member.updateLocation(target.get_Id(), new LatLng(latitude, longitude), new Member.OnSetCompleteListener() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(getApplicationContext(), "위도 " + latitude + " 경도 " + longitude, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String err) {

                            }
                        });
                    }
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
