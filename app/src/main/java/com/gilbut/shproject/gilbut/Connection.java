package com.gilbut.shproject.gilbut;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Connection {
    private int status;
    private  String target_id;
    private  String protector_id;
    private  Long id;
    private Timestamp date;
    private DocumentReference round;
    private DocumentReference documentRef;

    Connection(DocumentReference documentRef,int status, String targetId, String protectorId, Long id, Timestamp date, DocumentReference round){
        this.status = status;
        this.target_id = targetId;
        this.protector_id = protectorId;
        this.id = id;
        this.date = date;
        this.round = round;
    }

    Connection(DocumentReference documentRef, int status, String targetId, String protectorId, Long id, Timestamp date){
        this.status = status;
        this.target_id = targetId;
        this.protector_id = protectorId;
        this.id = id;
        this.date = date;
        this.round = null;
    }

    Connection(DocumentReference documentRef, int status, String targetId, String protectorId, Long id){
        this.status = status;
        this.target_id = targetId;
        this.protector_id = protectorId;
        this.id = id;
        this.date = null;
        this.round = null;
    }


    public int getStatus() {
        return status;
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

    public Map<String,Object> getDataMap(){
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        data.put("target_id", target_id);
        data.put("protector_id", protector_id);
        data.put("id", id);
        data.put("date", new Date());
        data.put("round", round);
        return data;
    }

    public void setStatus(int status) {
        this.status = status;
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
}

