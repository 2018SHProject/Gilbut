package com.gilbut.shproject.gilbut;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gilbut.shproject.gilbut.model.Connection;
import com.gilbut.shproject.gilbut.model.MemberData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionController {
    //Connection

    public FirebaseDatabase db;

    ConnectionController() {
        db = FirebaseDatabase.getInstance();
    }

    public void updateConnectionStatus(String targetId, String protectorId, int status, @Nullable final OnSetCompleteListener onUpdateCompleteListener) {
        DatabaseReference ref = db.getReference("connection/" + targetId + "-" + protectorId);
        Map<String, Object> connectionUpdates = new HashMap<>();
        connectionUpdates.put("status", status);
        ref.updateChildren(connectionUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (onUpdateCompleteListener != null) {
                    onUpdateCompleteListener.onComplete();
                }
            }
        });
    }

    public void addNewConnection(final String targetId, final String protectorId, final int status, @Nullable final OnSetCompleteListener onSetCompleteListener) {
//        // 이미 있는 보호자인지 검사.
//        DatabaseReference protectorRef = db.getReference("protector");
//        protectorRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot protectorSnapshot : dataSnapshot.getChildren()) {
//                    MemberData protector = protectorSnapshot.getValue(MemberData.class);
//                    if(protector != null &&protectorId.equals(protector.getmId())){
//
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference("target");
//        targetRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    if(snapshot.child("mID").getValue().toString().equals("jse525@naver.com")) {
//                        int a = 123+ 23;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = transFormat.format(date);
        DatabaseReference ref = db.getReference("connection");
        Connection newConnection = new Connection((long) status, targetId, protectorId, time, "", true, 37.502340, 127.019027 );
        String path = targetId + "-" + protectorId;

        ref.child(path).setValue(newConnection, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(onSetCompleteListener!=null){
                    onSetCompleteListener.onComplete();
                }
            }
        });
    }

    public void addNewConnection(final String targetId, final String protectorId, final int status, final double latitude, final double longitude, @Nullable final OnSetCompleteListener onSetCompleteListener) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = transFormat.format(date);
        DatabaseReference ref = db.getReference("connection");
        Connection newConnection = new Connection((long) status, targetId, protectorId, time, "", true, latitude, longitude );
        String path = targetId + "-" + protectorId;

        ref.child(path).setValue(newConnection, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(onSetCompleteListener!=null){
                    onSetCompleteListener.onComplete();
                }
            }
        });
    }


    public void getConnectionStatus(String targetId, String protectorId, final OnGetCompleteListener onGetCompleteListener){
        DatabaseReference ref = db.getReference("connection/"+targetId+"-"+protectorId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Connection connection = dataSnapshot.getValue(Connection.class);
                if(connection != null){
                    onGetCompleteListener.onComplete(connection.status.intValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetCompleteListener.onFailure();
            }
        });
    }

    public void getConnection(String targetId, String protectorId, final OnGetConnectionListener onGetConnectionListener){
        DatabaseReference ref = db.getReference("connection/"+targetId+"-"+protectorId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Connection connection = dataSnapshot.getValue(Connection.class);
                if(connection != null){
                    onGetConnectionListener.onComplete(connection);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetConnectionListener.onFailure();
            }
        });
    }



    public interface OnSetCompleteListener {
        public void onComplete();
    }

    public interface OnGetCompleteListener {
        public void onComplete(int status);
        public void onFailure();
    }

    public interface OnGetConnectionListener {
        public void onComplete(Connection connection);
        public void onFailure();
    }

    public interface OnGetFailureListener {

    }
}
