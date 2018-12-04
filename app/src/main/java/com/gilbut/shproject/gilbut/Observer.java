package com.gilbut.shproject.gilbut;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Observer {
    FirebaseDatabase db;
    ValueEventListener eventListener;
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

    public void removeObserver(){
        ref.removeEventListener(eventListener);
    }

    public interface OnObservedDataChange{
        public void OnDataChange(Object object);
    }
}
