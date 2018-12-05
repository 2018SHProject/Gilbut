package com.gilbut.shproject.gilbut.model;

public class ProtectorMember{
    public String mId;
    public boolean prevent;

    public ProtectorMember() {}

    public ProtectorMember(String pId){
        this.mId = pId;
    }

    public String getmId() {
        return mId;
    }
}