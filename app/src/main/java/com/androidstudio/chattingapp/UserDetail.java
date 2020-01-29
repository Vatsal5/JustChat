package com.androidstudio.chattingapp;

public class UserDetail {
    String ph_number;
    String uID;

    public UserDetail(String ph_number, String uID) {
        this.ph_number = ph_number;
        this.uID = uID;
    }

    public String getPh_number() {
        return ph_number;
    }

    public void setPh_number(String ph_number) {
        this.ph_number = ph_number;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}
