package com.gilbut.shproject.gilbut.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gilbut.shproject.gilbut.CallbackEvent;
import com.gilbut.shproject.gilbut.FirestoreHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.Source;

import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.security.auth.callback.Callback;

import static android.support.constraint.Constraints.TAG;

public class Connection implements Parcelable
{
    private int connection;
    private  @ServerTimestamp Date date;
    private String target_id;
    private String protector_id;
    private int id;
    private DocumentReference range;


    public Connection(){
        this.connection = -1;
        this.target_id = "target";
        this.protector_id = "protector1";
        this.id=0;
    }

    protected Connection(Parcel in) {
        connection = in.readInt();
        target_id = in.readString();
        protector_id = in.readString();
        id = in.readInt();
    }

    protected Connection(String targetId, String protectorId) {
        this.connection = -1;
        this.target_id = targetId;
        this.protector_id = protectorId;
        id = -1;
    }

    private void setDataAll(Connection newValue){
        this.connection = newValue.connection;
        this.date = newValue.date;
        this.target_id = newValue.target_id;
        this.protector_id = newValue.protector_id;
        this.id = newValue.id;
    }

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {
        @Override
        public Connection createFromParcel(Parcel in) {
            return new Connection(in);
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };

    public int getConnection() {
        return connection;
    }

    public Date getTimestamp() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getProtectorId() {
        return protector_id;
    }

    public String getTargetId() {
        return target_id;
    }

    public void setConnection(int id, int connection) {
        this.connection = connection;
        this.id = id;
    }

    public void setConnection(String targetId, String protectorId, int connection) {
        this.connection = connection;
        this.target_id = targetId;
        this.protector_id = protectorId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(connection);
        parcel.writeString(target_id);
        parcel.writeString(target_id);
        parcel.writeInt(id);
    }
}
