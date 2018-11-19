package com.gilbut.shproject.gilbut.model;
public class Connection {
    public  Long status;
    public String tId;
    public String pId;
    public String date;
    public String range;

    public Connection(){}

    public Connection(Long status, String tId, String pId, String date, String range){
        this.status = status;
        this.tId = tId;
        this.pId = pId;
        this.date = date;
        this.range = range;
    }
}