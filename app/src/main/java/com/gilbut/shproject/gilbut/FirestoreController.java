package com.gilbut.shproject.gilbut;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static android.support.constraint.Constraints.TAG;

public class FirestoreController {
    public OnDatabaseGetEventListener getEventListener;
    public OnDatabaseSetEventListener setEventListener;

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
                    Connection newData = new Connection(document.getReference(), document.getLong("connection"),document.getString("target_id"), document.getString("protector_id"), document.getLong("id"),document.getTimestamp("date"), document.getDocumentReference("round"));
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

    public void updateConnection (Connection connection) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("connection");

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
