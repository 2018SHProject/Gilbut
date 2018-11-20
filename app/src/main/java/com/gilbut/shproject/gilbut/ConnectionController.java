package com.gilbut.shproject.gilbut;

import android.support.annotation.Nullable;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConnectionController {
    // int status;
    // String tId;
    // String pId;
    // String date;
    // String range;

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

    public void addNewConnection(String targetId, String protectorId, int status, @Nullable final OnSetCompleteListener onSetCompleteListener) {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = transFormat.format(date);
        DatabaseReference ref = db.getReference("connection");
        Connection newConnection = new Connection((long) status, targetId, protectorId, time, "");
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

    public interface OnGetFailureListener {

    }
}
