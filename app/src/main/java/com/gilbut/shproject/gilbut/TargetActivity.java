package com.gilbut.shproject.gilbut;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    Target target;                                                              //대상 유저 클래스
    Intent intent;
    Button onBtn;                                                               //위치전송 on 버튼
    Button offBtn;                                                              //위치전송 off 버튼
    TextView noProtector;                                                       //연결된 보호자가 없을 때 띄울 메세지
    TextView tWait;                                                             //보호자의 수락을 기다릴 때 보일 메세지.
    FloatingActionButton fab;                                                   //Fab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        init();                                                                 //제대로 들어왔는지 확인
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                AlertDialog ad = new AlertDialog.Builder(getApplicationContext())
                        .create();

                ad.setTitle("보호자 등록");       // 제목 설정
                ad.setMessage("보호자로 등록할 아이디를 적어주세요. 해당 요청은 위치정보공유허락을 의미합니다.");   // 내용 설정

                // EditText 삽입하기
                final EditText editText = new EditText(getApplicationContext());
                ad.setView(editText);

                // 확인 버튼 설정
                ad.setButton(DialogInterface.BUTTON_POSITIVE,"요청 전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        target.setStatus(0);
//                        String y_Id = editText.getText().toString();
                        //바로 이전의 키값을 저장하고 있다고 할까? (db에 적용), 그러면 새로 넣을 경우에 이전 키값을 지워버리면 되잖아!
                        Toast.makeText(getApplicationContext(), "요청완료", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE,"취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        //한 자리에 오래 머무를 경우 보호자에게 알림이 가는 서비스를
        //on/off할 수 있는 토글을 보여주고, 사용자가 on/off일 경우 서버에 그 변경된
        //값을 저장한다. default값은 true
        //toggle을 visible하게 만들고 listener 달자. 이건 메인에서? 아니면 여기서?
        //이거 그냥 저 위에 바로 넣어도 될 듯한데?
        //그리고 db의 알람값 가져와서 true, false일 경우 나눠서 설정해야함!
//        onBtn.setVisibility(View.VISIBLE);
//        offBtn.setVisibility(View.GONE);
    }
    public void setLocation(){
        //3분 간격으로 내 위도, 경도를 lat,log에 저장해 서버에 전송한다.
        //메인에서 listener? 아니면 여기서?
    }

    public void showRefused(){
        //연결요청을 거부당했다는 팝업을 띄운다.
        Toast.makeText(getApplicationContext(),"연결 요청이 거절당했습니다.",Toast.LENGTH_SHORT).show();
        target.setStatus(-1);
        noProtector.setVisibility(View.VISIBLE);
        //db도 갱신
        //
    }
}
