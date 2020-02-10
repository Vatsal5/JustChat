package com.androidstudio.chattingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class DBHandler
{
    private static final String KEY_SENDER = "sender";
    private static final String KEY_RECEIVER = "receiver";
    private static final String KEY_MESSAGE = "message";

    private static final String DATABASE_NAME = "CHATS_DATABASE";
    private static final String DATABASE_TABLE = "CHATS_TABLE";
    private static  final int DATABASE_VERSION = 1;

    private DBHelper Helper;
    private SQLiteDatabase database;
    private final Context context;


    public DBHandler(Context ourContext)
    {
        this.context = ourContext;
    }

    private class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            String query = "CREATE TABLE "+DATABASE_TABLE+" ("+KEY_SENDER+" TEXT NOT NULL, "+
                    KEY_RECEIVER+" TEXT NOT NULL, "+KEY_MESSAGE+" TEXT NOT NULL);";

            sqLiteDatabase.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
        {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

    public DBHandler Open()
    {
        Helper = new DBHelper(context);
        database = Helper.getWritableDatabase();

        return this;
    }

    public void close()
    {
        Helper.close();
    }

    public long addMessage(MessageModel messageModel)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_SENDER,messageModel.getSender());
        contentValues.put(KEY_RECEIVER,messageModel.getReciever());
        contentValues.put(KEY_MESSAGE,messageModel.getMessage());

        return database.insert(DATABASE_TABLE,null,contentValues);
    }

    public ArrayList<MessageModel> getMessages(String receiver)
    {
        ArrayList<MessageModel> messages = new ArrayList<>();
        String [] columns = {KEY_SENDER,KEY_RECEIVER,KEY_MESSAGE};
        Cursor c = database.query(DATABASE_TABLE,columns,null,null,null,null,null,null);

        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            if((c.getString(iSender).equals(receiver) || c.getString(iReceiver).equals(receiver)))
                messages.add(new MessageModel(c.getString(iSender),c.getString(iReceiver),c.getString(iMessage)));
        }
        c.close();
        return messages;

    }

}