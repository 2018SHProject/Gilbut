package com.gilbut.shproject.gilbut;

import com.gilbut.shproject.gilbut.model.Connection;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Observer {
    FirebaseDatabase db;
    ValueEventListener eventListener;
    ChildEventListener childEventListener;
    DatabaseReference ref;

    Observer(){
        db = FirebaseDatabase.getInstance();
    }


    public void setObservingConnectionStatus(String targetId, String protectorId, final OnObservedDataChange onObservedDataChange){
        ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".","")).child("status");
        eventListener = ref.addValueEventListener(new ValueEventListener() {
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

    public void setObservingConnectionPrevent(String targetId, String protectorId, final OnObservedDataChange onObservedDataChange){
        ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".","")).child("prevent");
        eventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean prevent =  (boolean)dataSnapshot.getValue();
                onObservedDataChange.OnDataChange(prevent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setObservingConnectionAlarm(String targetId, String protectorId, final OnObservedDataChange onObservedDataChange){
        ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".","")).child("alarm");
        eventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean alarm =  (boolean)dataSnapshot.getValue();
                onObservedDataChange.OnDataChange(alarm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setObservingLocation(String targetId, String protectorId, final OnObservedDataChange onObservedDataChange){
        ref = db.getReference("connection/"+targetId.replace(".","")+"-"+protectorId.replace(".","")).child("location");
        eventListener = ref.addValueEventListener(new ValueEventListener() {
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

    public void setObserveringNewConnection(final String protectorId, final OnObservedDataChange onObservedDataChange){
        ref = db.getReference("connection/");
        childEventListener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(protectorId.equals(dataSnapshot.child("pId").getValue(String.class))){
                    onObservedDataChange.OnDataChange(dataSnapshot.getValue(Connection.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeObserver(){
        ref.removeEventListener(eventListener);
        ref.removeEventListener(childEventListener);
    }

    public interface OnObservedDataChange{
        public void OnDataChange(Object object);
    }
}
