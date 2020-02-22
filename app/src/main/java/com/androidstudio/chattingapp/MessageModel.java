package com.androidstudio.chattingapp;

import android.net.Uri;

public class MessageModel
{
    private String sender;
    private String reciever;
    private String message;
    private String type;
    private int isDownloaded;
    private Uri uri;

    public MessageModel(String sender, String reciever, String message,String type,int isDownloaded) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.type = type;
        this.isDownloaded = isDownloaded;
        uri = null;
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
