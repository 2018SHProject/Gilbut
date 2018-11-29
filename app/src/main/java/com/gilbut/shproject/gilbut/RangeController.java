package com.gilbut.shproject.gilbut;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangeController{
    public ArrayList<LatLng> range;

    public RangeController(){}

    public RangeController(ArrayList<LatLng> range){
        this.range = range;
    }

    public RangeController(final String rangeRef, final OnGetRangeListener onGetRangeListener){
        range = new ArrayList<>();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(rangeRef);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot rangeSnapshot : dataSnapshot.getChildren()){
                    LatLng latLng = rangeSnapshot.getValue(LatLng.class);
                    if(latLng != null ){
                        range.add(latLng);
                    }
                }
                onGetRangeListener.onComplete(range);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetRangeListener.onFailure(databaseError.toString());
            }
        });

    }


    public void getRange(String rangeRef, final OnGetRangeListener onGetRangeListener) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(rangeRef);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot latlngSnapshot : dataSnapshot.getChildren()){
                    LatLng latLng = latlngSnapshot.getValue(LatLng.class);
                    if(latLng != null){
                        range.add(latLng);
                    }
                }
                onGetRangeListener.onComplete(range);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetRangeListener.onFailure(databaseError.toString());
            }
        });
    }

    public void addRange(ArrayList<LatLng> range, final OnSetRangeListener setRangeListener) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("range").push();
        ArrayList<Map<String, Double>> mapRange = new ArrayList<>();
        for(LatLng latLng : range){
            HashMap<String, Double> pos = new HashMap<>();
            pos.put("latitude", latLng.latitude);
            pos.put("longitude", latLng.longitude);
            mapRange.add(pos);
        }
        final String uid = ref.getKey();
        ref.setValue(mapRange).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setRangeListener.onComplete("range/"+uid);
            }
        });
    }

    public void updateRange(ArrayList<LatLng> range, final OnSetRangeListener setRangeListener) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("range").push();
        ArrayList<Map<String, Double>> mapRange = new ArrayList<>();
        for(LatLng latLng : range){
            HashMap<String, Double> pos = new HashMap<>();
            pos.put("latitude", latLng.latitude);
            pos.put("longitude",latLng.longitude);
            mapRange.add(pos);
        }
        final String uid = ref.getKey();
        ref.setValue(mapRange).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setRangeListener.onComplete("range/"+uid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setRangeListener.onFailure(e.toString());
            }
        });
    }

    public interface OnGetRangeListener {
        public void onComplete(List<LatLng> range);
        public void onFailure(String err);
    }

    public interface OnSetRangeListener {
        public void onComplete(String rangeRef);
        public void onFailure(String err);
    }
}
