package com.androidstudio.chattingapp;

public class UserDetailWithStatus {

    String ph_number;
    String uID;
    String url;
    String status;

    public UserDetailWithStatus(String ph_number, String uID,String url,String status) {
        this.ph_number = ph_number;
        this.uID = uID;
        this.url=url;
        this.status=status;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
