package com.gilbut.shproject.gilbut.model;

public class MemberData {
    String mId;
    String yId;

    MemberData(String mId, String yId){
        this.mId = mId;
        this.yId = yId;
    }

    public String getmId() {
        return mId;
    }

    public String getyId() {
        return yId;
    }
}
