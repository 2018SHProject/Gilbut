package com.gilbut.shproject.gilbut;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity{
    SignInButton loginBtn;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) { // 현재 로그인한 유저가 없으면 자동 로그인
            Log.d("AutoLogin", mAuth.getCurrentUser().getEmail());
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else { // 로그인한 적이 없으면 Google 계정으로 로그인(및 회원가입) 시도
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            // 버튼 누르면 얘로 Google 계정 고르라고 보여줄거임
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText(getApplicationContext(), "구글 계정정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            // 버튼
            loginBtn = findViewById(R.id.Google_Login);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    // 1. Google PlayStore 계정을 먼저 인증할거임
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    mGoogleApiClient.connect();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "구글 인증 실패", Toast.LENGTH_SHORT).show();
            }
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if(result.isSuccess()) {
//                // 2. Google PlayStore 계정이 인증 된거임
//                GoogleSignInAccount account = result.getSignInAccount();
//                // 3. 인증된 계정으로 Firebase Auth에 인증 요청 보낼거임
//                firebaseAuthWithGoogle(account);
//            }
//            else {
//                // 구글 플레이스토어 인증 실패
//                Toast.makeText(getApplicationContext(), "구글 계정 인증 실패", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // 4. Google PlayStore 계정 정보로 Firebase Auth에 로그인할거임
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            // 실패
                            Toast.makeText(getApplicationContext(), "파이어베이스 인증 실패", Toast.LENGTH_SHORT).show();
                        } else {
                            // 5. Firebase Auth에 이미 있는 계정이면 그거로 로그인, 없으면 자동으로 회원가입->UID 생성
                            Toast.makeText(getApplicationContext(), "파이어베이스 인증 성공", Toast.LENGTH_SHORT).show();
                            // 6. Member 클래스의 putMember() 메소드로 DB에 정보 없으면 내 gmail계정으로 테이블 생성, 없으면 다시 access하지 않음!
                            member = new Member();
                            member.putMember();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

}
