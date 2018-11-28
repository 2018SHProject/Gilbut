package com.gilbut.shproject.gilbut;

import android.support.annotation.Nullable;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConnectionController {
    //Connection

    public FirebaseDatabase db= FirebaseDatabase.getInstance();

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
//              DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference("target");
//        targetRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        if(snapshot.child("mID").getValue().toString().equals("jse525@naver.com")) {
//
//                        }
//                    }
//                }
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
        Connection newConnection = new Connection((long) status, targetId, protectorId,  "", time,  true, 37.502340, 127.019027 );
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
        Connection newConnection = new Connection((long) status, targetId, protectorId, "", time,  true, latitude, longitude );
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
                onGetCompleteListener.onFailure("CANCELLED");
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
                onGetConnectionListener.onFailure("CANCELLED");
            }
        });
    }

    public void getConnection(final String targetId, final OnGetConnectionListener onGetConnectionListener){
        DatabaseReference ref = db.getReference("connection");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot targetSnapshot: dataSnapshot.getChildren()) {
                    Connection connection = targetSnapshot.getValue(Connection.class);
                    if (connection != null && connection.tId != null && connection.tId.equals(targetId)) {
                        onGetConnectionListener.onComplete(connection);
                    }else {
                        onGetConnectionListener.onFailure("NO_DATA");
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetConnectionListener.onFailure("CANCELLED");
            }
        });
    }

    // protectorID로 연결이있는지 찾기
    public void getConnections(final String protectorId, final OnGetConnectionsListener onGetConnectionsListener){
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


    public void updateLocation(String targetId, String protectorId, double latitude, double longitude, final OnSetCompleteListener onUpdateCompleteListener){
        DatabaseReference ref = db.getReference("connection/"+targetId+"-"+protectorId);
        Map<String, Object> locationUpdates = new HashMap<>();
        locationUpdates.put("latitude", latitude);
        locationUpdates.put("longitude", longitude);
        ref.updateChildren(locationUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (onUpdateCompleteListener != null) {
                    onUpdateCompleteListener.onComplete();
                }
            }
        });
    }

    public void setAlarm(String targetId, String protectorId, boolean alarm, final OnSetCompleteListener onSetCompleteListener){
        DatabaseReference ref = db.getReference("connection");
        String path = targetId + "-" + protectorId;

        ref.child(path).child("alarm").setValue(alarm, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(onSetCompleteListener!=null){
                    onSetCompleteListener.onComplete();
                }
            }
        });
    }

    public void getAlarm(String targetId, String protectorId, final OnGetAlarmListener onGetAlarmListener){
        DatabaseReference ref = db.getReference("connection/"+targetId+"-"+protectorId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Connection connection = dataSnapshot.getValue(Connection.class);
                if(connection != null) {
                    onGetAlarmListener.onComplete(connection.alarm);
                }
                else{
                    onGetAlarmListener.onFailure("NULL");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetAlarmListener.onFailure(databaseError.toString());
            }
        });
    }

    public void setObserveConnectionStatus(String targetId, String protectorId, final OnObservedDataChange onObservedDataChange){
        DatabaseReference ref = db.getReference("connection/"+targetId+"-"+protectorId).child("status");
        ValueEventListener valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int status =  ((Long)dataSnapshot.getValue()).intValue();
                onObservedDataChange.OnDataChange(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setObserveLocation(String targetId, String protectorId, final OnObservedDataChange onObservedDataChange){
        DatabaseReference ref = db.getReference("connection/"+targetId+"-"+protectorId);
        ValueEventListener latitudeEventListener = ref.child("location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                onObservedDataChange.OnDataChange(new LatLng(latitude, longitude));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public interface OnSetCompleteListener {
        public void onComplete();
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
        public void onComplete(List<Connection> connections);
        public void onFailure(String err);
    }

    public interface OnGetAlarmListener {
        public void onComplete(boolean alarm);
        public void onFailure(String err);
    }

    public interface OnObservedDataChange{
        public void OnDataChange(Object object);
    }
}
