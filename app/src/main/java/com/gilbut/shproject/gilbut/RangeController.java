package com.gilbut.shproject.gilbut;

import android.support.annotation.NonNull;

import com.gilbut.shproject.gilbut.model.Location;
import com.google.android.gms.fitness.request.DataReadRequest;
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

public class RangeController {
    public  ArrayList<Location> range;

    public RangeController(){}

    public RangeController(ArrayList<Location> range){
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
                    Location location = rangeSnapshot.getValue(Location.class);
                    if(location != null ){
                        range.add(location);
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
                for(DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
                    Location location = locationSnapshot.getValue(Location.class);
                    if(location != null){
                        range.add(location);
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

    // TODO: 레인지 추가하는거 만들어야되는데.. 음..
    public void addRange(ArrayList<Location> range, final OnSetRangeListener setRangeListener) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("range").push();
        ArrayList<Map<String, Double>> mapRange = new ArrayList<>();
        for(Location location : range){
            HashMap<String, Double> pos = new HashMap<>();
            pos.put("latitude", location.getLatitude());
            pos.put("longitude", location.getLongitude());
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

    public void updateRange(ArrayList<Location> range, final OnSetRangeListener setRangeListener) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("range").push();
        ArrayList<Map<String, Double>> mapRange = new ArrayList<>();
        for(Location location : range){
            HashMap<String, Double> pos = new HashMap<>();
            pos.put("latitude", location.getLatitude());
            pos.put("longitude", location.getLongitude());
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
        public void onComplete(List<Location> range);
        public void onFailure(String err);
    }

    public interface OnSetRangeListener {
        public void onComplete(String rangeRef);
        public void onFailure(String err);
    }
}
