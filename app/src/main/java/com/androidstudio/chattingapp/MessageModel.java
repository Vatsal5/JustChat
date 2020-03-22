package com.androidstudio.chattingapp;

import android.net.Uri;

public class MessageModel
{
    private String id;
    private String sender;
    private String reciever;
    private String message;
    private String type;
    private int isDownloaded;

    public MessageModel(){
        this.id = null;
        this.sender = null;
        this.reciever = null;
        this.message = null;
        this.type = null;
        this.isDownloaded = -1;
    };

    public MessageModel(String id,String sender, String reciever, String message,String type,int isDownloaded) {
        this.id = id;
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.type = type;
        this.isDownloaded = isDownloaded;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
