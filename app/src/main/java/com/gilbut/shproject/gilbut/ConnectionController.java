package com.gilbut.shproject.gilbut;

import android.support.annotation.Nullable;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ConnectionController {
    //Connection

    public FirebaseDatabase db= FirebaseDatabase.getInstance();

    public void updateConnectionStatus(String targetId, String protectorId, int status, @Nullable final OnSetCompleteListener onUpdateCompleteListener) {
        DatabaseReference ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".",""));
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

    //새로운 연결 추가.
    public void addNewConnection(final String targetId, final String protectorId, final int status, @Nullable final OnSetCompleteListener onSetCompleteListener) {
//        // 이미 있는 보호자인지 검사.
        Member member = new Member();
        member.isProtector(protectorId, new Member.OnCheckMemberListener() {
            @Override
            public void onComplete(boolean isMember) {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = transFormat.format(date);
                DatabaseReference ref = db.getReference("connection");
                Connection newConnection = new Connection((long) status, targetId, protectorId,  "", time );
                String path = targetId.replace(".","")+"-"+protectorId.replace(".","");

                ref.child(path).setValue(newConnection, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(onSetCompleteListener!=null){
                            onSetCompleteListener.onComplete();
                        }
                    }
                });
            }

            @Override
            public void onFailure(){
                // 보호자가 없어서 실패..
                onSetCompleteListener.onFailure("보호자가 없습니다!");
            }
        });

    }

    public void removeConnection(String targetId, String protectorId, OnRemoveListener onRemoveListener){
        DatabaseReference ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".",""));
        ref.removeValue();
    }

    public void getConnectionStatus(String targetId, String protectorId, final OnGetCompleteListener onGetCompleteListener){
        DatabaseReference ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".",""));
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
                onGetCompleteListener.onFailure("CANCELLED");
            }
        });
    }

    public void getConnection(String targetId, String protectorId, final OnGetConnectionListener onGetConnectionListener){
        DatabaseReference ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".",""));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Connection connection = dataSnapshot.getValue(Connection.class);
                onGetConnectionListener.onComplete(connection);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetConnectionListener.onFailure("CANCELLED");
            }
        });
    }

    public void geTargetConnections(final String targetId, final OnGetConnectionsListener onGetConnectionsListener){
        DatabaseReference ref = db.getReference("connection");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Connection> connections = new ArrayList<>();
                for(DataSnapshot targetSnapshot: dataSnapshot.getChildren()) {
                    Connection connection = targetSnapshot.getValue(Connection.class);
                    if (connection != null && connection.tId != null && connection.tId.equals(targetId)) {
                        connections.add(connection);
                    }
                }
                onGetConnectionsListener.onComplete(connections);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetConnectionsListener.onFailure("CANCELLED");
            }
        });
    }

    // protectorID로 연결이있는지 찾기
    public void getProtectorConnections(final String protectorId, final OnGetConnectionsListener onGetConnectionsListener){
        DatabaseReference ref = db.getReference("connection");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Connection> connections = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Connection connection = snapshot.getValue(Connection.class);
                    if(connection != null && connection.pId !=null){
                        if(Objects.equals(connection.pId, protectorId)) {
                            connections.add(connection);
                        }
                    }
                }
                onGetConnectionsListener.onComplete(connections);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetConnectionsListener.onFailure(databaseError.toString());
            }
        });
    }

    public interface OnSetCompleteListener {
        public void onComplete();
        public void onFailure(String err);
    }

    public interface OnGetCompleteListener {
        public void onComplete(int status);
        public void onFailure(String err);
    }

    public interface OnGetConnectionListener {
        public void onComplete(Connection connection);
        public void onFailure(String err);
    }

    public interface OnGetConnectionsListener {
        public void onComplete(ArrayList<Connection> connections);
        public void onFailure(String err);
    }

    public interface OnRemoveListener{
        public void onCompletet();
    }
}
