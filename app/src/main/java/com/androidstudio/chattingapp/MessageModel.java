package com.androidstudio.chattingapp;

public class MessageModel
{
    private int id;
    private String sender;
    private String reciever;
    private String message;
    private String type;
    private int isDownloaded;
    private String time;
    private String date;
    private String groupKey;
    private String firebaseId;

    public MessageModel(){
        this.id = -1;
        this.sender = null;
        this.reciever = null;
        this.message = null;
        this.type = null;
        this.isDownloaded = -1;
        this.time = null;
        this.date = null;
        this.groupKey = null;
        this.firebaseId = null;
    };

    public MessageModel(int id, String sender, String reciever, String message, String type, int isDownloaded, String time, String date, String groupKey, String firebaseId) {
        this.id = id;
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.type = type;
        this.isDownloaded = isDownloaded;
        this.time = time;
        this.date=date;
        this.groupKey = groupKey;
        this.firebaseId = firebaseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(int downloaded) {
        isDownloaded = downloaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
