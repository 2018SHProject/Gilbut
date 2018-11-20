package com.gilbut.shproject.gilbut;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Member {
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private boolean isMem;
    private String uid, uemail;

    public Member() { // Member 객체 생성자를 호출하면 자동 DB 검사해서 ID 없으면 putMember
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        uemail = mAuth.getCurrentUser().getEmail();
        db = FirebaseDatabase.getInstance();
    }
    public boolean isMember() {
        return isMem;
    }

    // DB에 내 uid에 해당하는 테이블이 없으면 내 정보를 대상, 보호자 DB에 넣음
    // 내 uid에 해당하는 테이블이 있으면 넣지 않음!
    // Main에서 이 메소드를 호출하면 있는지 없는지 검사 후 없으면 정보 넣고 있으면 정보 넣지 않음
    public void putMember() {
        DatabaseReference targetRef = db.getReference("target");
        targetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean put_this = false;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(uid)) {
                        Log.d("target", snapshot.getKey());
                        put_this = true;
                        isMem = true;
                    }
                }
                if(!put_this) {
                    Log.d("없다!", "putMember() 실행!");
                    putthis();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference protectorRef = db.getReference("protector");
        protectorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(uid)) {
                        Log.d("Protector", snapshot.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 내 정보 넣는 함수
    public void putthis() {
        Log.d("put", "member");
        // Target DB에 정보 넣기
        DatabaseReference targetRef2 = db.getReference("target");
        Map<String, String> targetValues = new HashMap<>();
        targetValues.put("mId", uemail);
        targetValues.put("yId", "");
        targetRef2.child(uid).child("mId").setValue(targetValues.get("mId"));
        targetRef2.child(uid).child("yId").setValue(targetValues.get("yId"));
        // Protector DB에 정보 넣기
        DatabaseReference protectorRef2 = db.getReference("protector");
        Map<String, String> protectorValues = new HashMap<>();
        protectorValues.put("mId", uemail);
        protectorValues.put("yId", "");
        protectorRef2.child(uid).child("mId").setValue(protectorValues.get("mId"));
        protectorRef2.child(uid).child("yId").setValue(targetValues.get("yId"));
    }
}
