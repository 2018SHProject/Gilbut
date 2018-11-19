package com.gilbut.shproject.gilbut;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button button;
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

        setContentView(R.layout.activity_main);

        checkPermission();

        /////////////진입 시 보호자와 대상 DB 생성/////////////

        //////////////////////////////////////

        ////////지금은 화면 선택 후 접속////////
        ////////TargetActivity 불러오기///////

        button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProtectorActivity.class);
                startActivity(intent);
            }
        });

        //////ProtectorActivity 불러오기//////
        /////////////////////////////////////
    }


    public void checkPermission() {                                                                                 //권한확인
        int pCheck = ContextCompat.checkSelfPermission(this, String.valueOf(permission_list));

        if(pCheck == PackageManager.PERMISSION_DENIED){
            //권한 추가
            ActivityCompat.requestPermissions(this, permission_list, 0);
        }
    }
}
