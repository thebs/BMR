package com.darker.bmr;

/**
 * Created by Darker on 24/12/59.
 */

public class BMR {
    private int id;
    private String time;
    private String bmr;

    public BMR(){}

    public BMR(int id, String time, String bmr){
        this.id = id;
        this.time = time;
        this.bmr = bmr;
    }

    public BMR(String time, String bmr){
        this.time = time;
        this.bmr = bmr;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public void setBmr(String bmr){
        this.bmr = bmr;
    }

    public String getBmr(){
        return bmr;
    }
}
