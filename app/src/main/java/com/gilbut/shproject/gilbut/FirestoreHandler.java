package com.gilbut.shproject.gilbut;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gilbut.shproject.gilbut.models.Connection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.security.auth.callback.Callback;

import static android.support.constraint.Constraints.TAG;

public class FirestoreHandler {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ConnectionFirestoreListener mConnectionFirestoreListener ;

    public void getConnection(String targetId, String protectorId){
        CollectionReference connectionCollectionRef = db.collection("connection");
        Query connectionQuery = connectionCollectionRef.whereEqualTo("tId", targetId).whereEqualTo("pId", protectorId);
        connectionQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Connection connection = document.toObject(Connection.class);
                        Log.d(TAG, "받아오기 성공 "+connection);
                        mConnectionFirestoreListener.onReceivedData(connection);
                        Log.d(TAG,  "connection: "+connection);
                    }
                }else{
                    Log.e(TAG, "쿼리 실패");
                }
            }
        });
    }

    public void setConnectionFirestoreListener(ConnectionFirestoreListener connectionFirestoreListener) {
        this.mConnectionFirestoreListener = connectionFirestoreListener;
    }

    public interface ConnectionFirestoreListener{
        void onReceivedData(Connection connection);
    }
}
