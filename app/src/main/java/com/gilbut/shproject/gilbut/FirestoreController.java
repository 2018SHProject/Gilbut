package com.gilbut.shproject.gilbut;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static android.support.constraint.Constraints.TAG;

public class FirestoreController {
    public OnDatabaseGetEventListener getEventListener;
    public OnDatabaseSetEventListener setEventListener;

    // 커넥션 정보 모두(status, targetId, protectorId, id(커넥션 아이디), date(등록된 날짜), range
    public void getConnection (String targetId, String protectorId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("connection");
        Query query = collectionRef.whereEqualTo("target_id", targetId).whereEqualTo("protector_id", protectorId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                    Log.d(TAG, "connection(document) => "+ document.getData());
                    Log.d(TAG, "connection(toObject) => "+ document.getString("target_id"));
                    Connection newData = new Connection(document.getReference(), document.getDouble("status").intValue(),document.getString("target_id"), document.getString("protector_id"), document.getLong("id"),document.getTimestamp("date"), document.getDocumentReference("range"));
                    getEventListener.onGetConnectionSuccess(newData);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "쿼리 get 실패!");
                getEventListener.onGetConnectionFailure();
            }
        });
    }

    public void setConnection (Connection connection){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("connection");
        //Query query = collectionRef.whereEqualTo("target_id", connection.getTarget_id()).whereEqualTo("protector_id",connection.getProtector_id());
        collectionRef
                .document(connection.getTarget_id() + "-"+connection.getProtector_id())
                .set(connection.getDataMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setEventListener.onSetConnectionSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      setEventListener.onSetConnectionFailure();
                    }
                });
    }

    public void updateStatus (String targetId, String protectorId, int status) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("connection");
        collectionRef.document(targetId+"-"+protectorId)
                .update("status", status )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setEventListener.onSetConnectionSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setEventListener.onSetConnectionFailure();
                    }
                });
    }

    public void setOnDatabaseGetEventListener(OnDatabaseGetEventListener listener) {
        this.getEventListener = listener;
    }

    public void setDatabaseSetEventListener(OnDatabaseSetEventListener listener) {
        this.setEventListener = listener;
    }

    public interface OnDatabaseGetEventListener{
        public void onGetConnectionSuccess(Connection newData);
        public void onGetConnectionFailure();
    }

    public interface OnDatabaseSetEventListener {
        public void onSetConnectionSuccess();
        public void onSetConnectionFailure();
    }
}
