package com.gilbut.shproject.gilbut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//대상이 보호자에게 연결요청, 이 후 보호자가 대상에게 위치정보요청
//status 설명
//-1 : nothing
//0 : 대상->보호자 연결요청보낸상태
//1 : 연결만 되고 위치정보 수락안된상태
//2 : 모든 연결 완료
//3 : 보호자가 거절, 후 -1로 변경.(대상이 확인하면)

public class TargetActivity extends AppCompatActivity {

    Target target;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        target = new Target();
        init();
    }

    public void init(){
        //MainActivity에서 넘긴 정보를 가지고 DB에 저장되어있는 대상 / 연결정보를 가져온다.
        intent = getIntent();

        String y_Id = null;         //protector
        int status = 10;            //상태값
        Boolean alarm = false;      //알람
        String m_Id = intent.getStringExtra("mid");
        int checkMod = intent.getIntExtra("mod",-1);


        if(checkMod==1){
            //checkMod가 대상모드로 제대로 선택돼서 들어왔을 경우
            //break;
        }
        else if(checkMod==0){
            //checkMod가 보호자로 선택돼서 들어옴
            //error처리
        }
        else{
            //checkMod가 이상한 값이 들어왔을 경우.
            //error.
        }

        //m_id로 연결db에서 정보들을 가져와 y_id, status를 초기화한다.
//        target.setM_Id(m_Id);
//        target.setY_Id(y_Id);
//        target.setStatus(status);
//        target.setAlarm(alarm);
    }

    public void checkAgreement(){
        //status값이 1이고 isAgreed값이 true면 showToggle()을, false면 showAgreementPop()실행.
        //그 반환 값이 true면 다시 showToggle()을 실행하고 false면 showWarning()을 띄운다

        int status = target.getStatus();
        if(status == 2){
            //연결이 완료되어있는경우
            //showToggle();
            //토글 띄우고 지속적으로 location 서버전송
        }
        else if(status == 1){
            //showWarning();
            //수락만 했고 위치정보는 허락 안한경우
        }
        else if(status == 3){
            //showRefused();
            //target.setStatus(-1);
            //거절당한경우
        }
        else if(status == -1){
            //아무것도 아닌 경우
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
    }

    public void connect(){
        //checkStatus()를 실행한다. 이 후 보호자의 이메일을 등록할 수 있는
        //입력 창이 보이며, 이메일을 입력 후 연결 요청을 누르면 멤버변수 yid의 값에
        //입력한 이메일이 등록이 되고 멤버변수 status의 값이 0으로 바뀌고 서버에 갱신한다
    }

    public void checkStatus(){
        //서버에서 status값을 받아와 멤버변수 status에 저장하고, 그 값이 -1이면 아무것도
        //띄우지 않고 0이면 요청을 보낸 상태라는 문구를 띄우고 2이면 showRefused()를 실행
        //1이면 checkAgreement()를 실행한다
    }


    public void setLocation(){
        //3분 간격으로 내 위도, 경도를 lat,log에 저장해 서버에 전송한다.
    }

    public void showAgreementPop(){
        //위치정보공유를 위해 사용자에게 팝업을 띄워 허락을 받는다. 사용자가 수락한다면
        //멤버변수 isAgreed를 true로 저장하고 true를 반환, 거절하면 바로 false를 반환
    }

    public void showWarning(){
        //위치정보공유를 허락해야만 서비스 이용이 가능하다는 화면을 띄운다.
    }

    public void showRefused(){
        //연결요청을 거부당했다는 팝업을 띄운다.
    }
}
