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
    private String uid;

    public Member() { // Member 객체 생성자를 호출하면 자동 DB 검사해서 ID 없으면 putMember
        isMem = isMember();
        if(!isMem) {
            putMember(mAuth);
        }
    }
    public boolean isMember() {
        isMem = false;
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance();
        DatabaseReference targetRef = db.getReference("target");
        targetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("/id").getValue().toString() == mAuth.getCurrentUser().getEmail()) {
                        Log.e("target", snapshot.getValue().toString());
                        isMem = true;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference protectorRef = db.getReference("protector");
        protectorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("/id").getValue().toString() == mAuth.getCurrentUser().getEmail()) {
                        Log.e("protector", snapshot.getValue().toString());
                        isMem = true;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return isMem;
    }
    public void putMember(FirebaseAuth auth) {
        // Email 가져오기
        String id = auth.getCurrentUser().getEmail();
        // Target DB에 정보 넣기
        DatabaseReference targetRef = db.getReference("target");
        Map<String, String> targetValues = new HashMap<>();
        targetValues.put("id", id);
        targetRef.child(uid+"/id").setValue(targetValues.get("id"));
        // Protector DB에 정보 넣기
        DatabaseReference protectorRef = db.getReference("protector");
        Map<String, String> protectorValues = new HashMap<>();
        protectorValues.put("id", id);
        protectorRef.child(uid+"/id").setValue(protectorValues.get("id"));
    }

}
