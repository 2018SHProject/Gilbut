package com.gilbut.shproject.gilbut;

import com.gilbut.shproject.gilbut.model.Location;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Range {
    public  ArrayList<Location> range;

    public Range(){}

    public Range(ArrayList<Location> range){
        this.range = range;
    }

    public Range(final String rangeRef, final OnGetRangeListener onGetRangeListener){
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
    public void addRange(ArrayList<Location> range) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("range");
        //ArrayList<>
    }

    public void updateRange(ArrayList<Location> range) {

    }

    public interface OnGetRangeListener {
        public void onComplete(List<Location> range);
        public void onFailure(String err);
    }

    public interface OnSetRangeListener {
        public void onComplete();
        public void onFailure(String err);
    }
}
