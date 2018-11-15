package com.gilbut.shproject.gilbut;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class Connection implements Parcelable{
    private Long connection;
    private  String target_id;
    private  String protector_id;
    private  Long id;
    private Timestamp date;
    private DocumentReference round;
    private DocumentReference documentRef;

    Connection(DocumentReference documentRef,Long connection, String targetId, String protectorId, Long id, Timestamp date, DocumentReference round){
        this.connection = connection;
        this.target_id = targetId;
        this.protector_id = protectorId;
        this.id = id;
        this.date = date;
        this.round = round;
    }

    Connection(DocumentReference documentRef, Long connection, String targetId, String protectorId, Long id, Timestamp date){
        this.connection = connection;
        this.target_id = targetId;
        this.protector_id = protectorId;
        this.id = id;
        this.date = date;
        this.round = null;
    }

    Connection(DocumentReference documentRef, Long connection, String targetId, String protectorId, Long id){
        this.connection = connection;
        this.target_id = targetId;
        this.protector_id = protectorId;
        this.id = id;
        this.date = null;
        this.round = null;
    }

    protected Connection(Parcel in) {
        connection = in.readLong();
        target_id = in.readString();
        protector_id = in.readString();
        id = in.readLong();
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

    public Long getConnection() {
        return connection;
    }

    public Long getId() {
        return id;
    }

    public String getProtector_id() {
        return protector_id;
    }

    public String getTarget_id() {
        return target_id;
    }

    public DocumentReference getRound() {
        return round;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setConnection(Long connection) {
        this.connection = connection;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProtector_id(String protector_id) {
        this.protector_id = protector_id;
    }

    public void setRound(DocumentReference round) {
        this.round = round;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(connection);
        parcel.writeString(target_id);
        parcel.writeString(protector_id);
        parcel.writeLong(id);
    }
}

