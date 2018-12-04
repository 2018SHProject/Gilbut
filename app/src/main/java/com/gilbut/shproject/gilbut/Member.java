package com.gilbut.shproject.gilbut;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gilbut.shproject.gilbut.model.Connection;
import com.gilbut.shproject.gilbut.model.ProtectorMember;
import com.gilbut.shproject.gilbut.model.TargetMember;
import com.google.android.gms.maps.model.LatLng;
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

    //타겟멤버 가져오는 함수. null이면 없는것.
    public void getTarget(final String targetId, final OnGetTargetListener onGetTargetListener){
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("target");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    TargetMember target = snapshot.getValue(TargetMember.class);
                    if( target != null && target.mId.equals(targetId)){
                        onGetTargetListener.onGetData(target);
                        return;
                    }
                }
                onGetTargetListener.onGetData(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //보호자가 있나 없나 검사하는 함수.
    public void isProtector(final String protectorId, final OnCheckMemberListener onCheckMemberListener){
        db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("protector");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ProtectorMember protector = snapshot.getValue(ProtectorMember.class);
                    if(protector != null && protector.mId != null && protector.mId.equals(protectorId)){
                        onCheckMemberListener.onComplete(true);
                        return;
                    }
                }
                onCheckMemberListener.onComplete(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        targetRef2.child(uid).child("alarm").setValue(true);
        targetRef2.child(uid).child("emergency").setValue(false);
        HashMap<String, Object> location = new HashMap<>();
        location.put("latitude", 0);
        location.put("longitude", 0);
        targetRef2.child(uid).child("location").setValue(location);
        // Protector DB에 정보 넣기
        DatabaseReference protectorRef2 = db.getReference("protector");
        Map<String, String> protectorValues = new HashMap<>();
        protectorValues.put("mId", uemail);
        protectorValues.put("yId", "");
        protectorRef2.child(uid).child("mId").setValue(protectorValues.get("mId"));
        protectorRef2.child(uid).child("yId").setValue(targetValues.get("yId"));
    }

    public void getLocation(final String targetId, final OnGetLocationListener onGetLocationListener){
        DatabaseReference ref = db.getReference("target");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TargetMember target = snapshot.getValue(TargetMember.class);
                    if (target != null && target.mId.equals(targetId)) {
                        LatLng newLocation = target.getLocation();
                        onGetLocationListener.onComplete(newLocation);
                    }else{
                        onGetLocationListener.onFailure("타겟 없음");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetLocationListener.onFailure(databaseError.toString());
            }
        });
    }

    // 타겟의 위치를 업데이트 하는 함수.
    public void updateLocation(final String targetId, final LatLng location, final OnSetCompleteListener onUpdateCompleteListener){
        final DatabaseReference ref = db.getReference("target");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    TargetMember target = snapshot.getValue(TargetMember.class);
                    if(target != null && target.mId.equals(targetId)){
                        HashMap<String, Object> locationUpdates = new HashMap<>();
                        locationUpdates.put("latitude", location.latitude);
                        locationUpdates.put("longitude", location.longitude);
                        snapshot.getRef().child("location").updateChildren(locationUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (onUpdateCompleteListener != null) {
                                    onUpdateCompleteListener.onComplete();
                                }
                            }
                        });
                    }else{
                        onUpdateCompleteListener.onFailure("no target");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //위치 정보를 보낸다, 안보낸다 설정.
    public void setAlarm(final String targetId, final boolean alarm, final OnSetCompleteListener onSetCompleteListener){
        final DatabaseReference ref = db.getReference("target");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TargetMember target = snapshot.getValue(TargetMember.class);
                    if (target != null && target.mId.equals(targetId)) {
                        snapshot.getRef().child("alarm").setValue(alarm, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(onSetCompleteListener!=null){
                                    onSetCompleteListener.onComplete();
                                }
                            }
                        });
                    }else{
                        onSetCompleteListener.onFailure("no target");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                onSetCompleteListener.onFailure("on cancel");
            }
        });
    }

    // 위치 정보 보내는지 안보내는지 가져오기.
    public void getAlarm(final String targetId, final OnGetAlarmListener onGetAlarmListener){
        DatabaseReference ref = db.getReference("target");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TargetMember target = snapshot.getValue(TargetMember.class);
                    if (target != null && target.mId.equals(targetId)) {
                        onGetAlarmListener.onComplete(target.alarm);
                    }else{
                        onGetAlarmListener.onFailure("타겟 없음");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetAlarmListener.onFailure(databaseError.toString());
            }
        });
    }

    //긴급상황 이라고 보내기! ( 값을 바꾸면 이 값을 관찰하고 있던 보호자 측에서 알게 되는걸로..)
    public void setEmergency(final String targetId, final boolean emergency, final OnSetCompleteListener onSetCompleteListener){
        final DatabaseReference ref = db.getReference("target");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TargetMember target = snapshot.getValue(TargetMember.class);
                    if (target != null && target.mId.equals(targetId)) {
                        snapshot.getRef().child("emergency").setValue(emergency, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(onSetCompleteListener!=null){
                                    onSetCompleteListener.onComplete();
                                }
                            }
                        });
                    }else{
                        onSetCompleteListener.onFailure("no target");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                onSetCompleteListener.onFailure("on cancel");
            }
        });
    }

    // 긴급 상황인지 가져오기(노쓸모 예정)
    public void getEmergency(final String targetId, final OnGetEmergencyListener onGetEmergencyListener){
        DatabaseReference ref = db.getReference("target");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TargetMember target = snapshot.getValue(TargetMember.class);
                    if (target != null && target.mId.equals(targetId)) {
                        onGetEmergencyListener.onComplete(target.emergency);
                    }else{
                        onGetEmergencyListener.onFailure("타겟 없음");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetEmergencyListener.onFailure(databaseError.toString());
            }
        });
    }


    public interface OnGetTargetListener{
        public void onGetData(TargetMember target);
    }

    public interface OnCheckMemberListener{
        public void onComplete(boolean isMember);
        public void onFailure();
    }

    public interface OnGetAlarmListener {
        public void onComplete(boolean alarm);
        public void onFailure(String err);
    }

    public interface OnGetEmergencyListener {
        public void onComplete(boolean emergency);
        public void onFailure(String err);
    }

    public interface OnSetCompleteListener {
        public void onComplete();
        public void onFailure(String err);
    }

    public interface OnGetLocationListener {
        public void onComplete(LatLng location);
        public void onFailure(String err);
    }
}
