package com.androidstudio.chattingapp;

public class UserDetailwithUrl {

    String ph_number;
    String uID;
    String url;
    String lastmessage;
    String time,groupkey, groupname;
    int messagenum;

    public UserDetailwithUrl(String ph_number, String uID,String url,int messagenum,String lastmessage,String time,String groupkey, String groupname) {
        this.ph_number = ph_number;
        this.uID = uID;
        this.url=url;
        this.messagenum=messagenum;
        this.lastmessage=lastmessage;
        this.time=time;
        this.groupkey=groupkey;
        this.groupname=groupname;

    }

    public String getGroupkey() {
        return groupkey;
    }

    public void setGroupkey(String groupkey) {
        this.groupkey = groupkey;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
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

    public int getMessagenum() {
        return messagenum;
    }

    public void setMessagenum(int messagenum) {
        this.messagenum = messagenum;
    }
}
