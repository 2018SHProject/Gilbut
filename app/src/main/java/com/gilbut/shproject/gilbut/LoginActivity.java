package com.gilbut.shproject.gilbut;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }

    public void targetClick(View view) {
        String mId = mAuth.getCurrentUser().getEmail();
        Intent intent = new Intent(this, TargetActivity.class);
        intent.putExtra("mId", mId);
        intent.putExtra("value", 1);
        startActivity(intent);
    }
    public void protectorClick(View view) {
        String mId = mAuth.getCurrentUser().getEmail();
        Intent intent = new Intent(this, ProtectorActivity.class);
        intent.putExtra("mId", mId);
        intent.putExtra("value", 2);
        startActivity(intent);
    }
}