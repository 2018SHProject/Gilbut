package com.gilbut.shproject.gilbut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {
    Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void targetBtnClick(View view) {
        Intent targetIntent = new Intent(this, TargetActivity.class);
    }
    public void protectorBtnClick(View view) {

    }
}
