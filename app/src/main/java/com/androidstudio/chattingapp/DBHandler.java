package com.androidstudio.chattingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class DBHandler
{
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_RECEIVER = "receiver";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ISDOWNLOADED = "downloaded";
    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";
    private static final String KEY_GROUPNAME = "groupname";
    private static final String KEY_FIREBASEID = "firebaseid";

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
            String query = "CREATE TABLE "+DATABASE_TABLE+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_SENDER+" TEXT NOT NULL, "+
                    KEY_RECEIVER+" TEXT NOT NULL, "+KEY_MESSAGE+" TEXT NOT NULL, "+KEY_TYPE+" TEXT NOT NULL, "+KEY_ISDOWNLOADED+" TINYINT DEFAULT NULL, "+KEY_TIME+" TEXT NOT NULL, "+KEY_DATE+" TEXT NOT NULL, "+KEY_GROUPNAME+" TEXT NOT NULL, "+KEY_FIREBASEID+" TEXT NOT NULL);";

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

    public int addMessage(MessageModel messageModel)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_SENDER,messageModel.getSender());
        contentValues.put(KEY_RECEIVER,messageModel.getReciever());
        contentValues.put(KEY_MESSAGE,messageModel.getMessage());
        contentValues.put(KEY_TYPE,messageModel.getType());
        contentValues.put(KEY_ISDOWNLOADED,messageModel.getDownloaded());
        contentValues.put(KEY_TIME,messageModel.getTime());
        contentValues.put(KEY_DATE,messageModel.getDate());
        contentValues.put(KEY_GROUPNAME,messageModel.getGroupName());
        contentValues.put(KEY_FIREBASEID,messageModel.getFirebaseId());

        database.insert(DATABASE_TABLE,null,contentValues);

        return getID();
    }

    public Pair<ArrayList<MessageModel>, Integer> getMessages(String receiver, int index)
    {
        ArrayList<MessageModel> messages = new ArrayList<>();
        String [] columns = {KEY_ID,KEY_SENDER,KEY_RECEIVER,KEY_MESSAGE,KEY_TYPE,KEY_ISDOWNLOADED,KEY_TIME,KEY_DATE,KEY_GROUPNAME,KEY_FIREBASEID};
        Cursor c = database.query(true,DATABASE_TABLE,columns,null,null,null,null,null,null);

        int global;

        if(index==0)
            global=c.getCount()-1;
        else
            global=index;

        int i=0;

        int iId = c.getColumnIndex(KEY_ID);
        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDownloaded = c.getColumnIndex(KEY_ISDOWNLOADED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iDate = c.getColumnIndex(KEY_DATE);
        int iGroup = c.getColumnIndex(KEY_GROUPNAME);
        int iFirebaseId = c.getColumnIndex(KEY_FIREBASEID);

        for(c.moveToPosition(global);!c.isBeforeFirst();c.moveToPrevious())
        {
            global--;
            if(c.getString(iGroup).equals("null") && (c.getString(iSender).equals(receiver) || c.getString(iReceiver).equals(receiver))) {
                messages.add(0,new MessageModel(c.getInt(iId), c.getString(iSender), c.getString(iReceiver), c.getString(iMessage), c.getString(iType), c.getInt(iDownloaded), c.getString(iTime), c.getString(iDate), c.getString(iGroup),c.getString(iFirebaseId)));
                i++;
            }

            if(i==30)
                break;

        }
        c.close();
        return new Pair<>(messages, global);

    }

    public Pair<ArrayList<MessageModel>,Integer> getGroupMessages(String grpName,int index)
    {
        ArrayList<MessageModel> messages = new ArrayList<>();
        String [] columns = {KEY_ID,KEY_SENDER,KEY_RECEIVER,KEY_MESSAGE,KEY_TYPE,KEY_ISDOWNLOADED,KEY_TIME,KEY_DATE,KEY_GROUPNAME,KEY_FIREBASEID};
        Cursor c = database.query(true,DATABASE_TABLE,columns,null,null,null,null,null,null);

        int global;

        if(index==0)
            global=c.getCount()-1;
        else
            global=index;

        int i=0;

        int iId = c.getColumnIndex(KEY_ID);
        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDownloaded = c.getColumnIndex(KEY_ISDOWNLOADED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iDate = c.getColumnIndex(KEY_DATE);
        int iGroup = c.getColumnIndex(KEY_GROUPNAME);
        int iFirebaseId = c.getColumnIndex(KEY_FIREBASEID);

        MessageModel model = null;

        for(c.moveToPosition(global);!c.isBeforeFirst();c.moveToPrevious())
        {
            global--;
            model = new MessageModel(c.getInt(iId),c.getString(iSender),c.getString(iReceiver),c.getString(iMessage),c.getString(iType),c.getInt(iDownloaded),c.getString(iTime),c.getString(iDate),c.getString(iGroup),c.getString(iFirebaseId));
            if(model.getGroupName().equals(grpName)) {
                messages.add(0,model);
                i++;
            }

            if(i==30)
                break;

        }
        c.close();
        return new Pair<>(messages, global);

    }

    public long UpdateMessage(MessageModel message)
    {
        ContentValues values = new ContentValues();

        values.put(KEY_SENDER,message.getSender());
        values.put(KEY_RECEIVER,message.getReciever());
        values.put(KEY_MESSAGE,message.getMessage());
        values.put(KEY_TYPE,message.getType());
        values.put(KEY_ISDOWNLOADED,message.getDownloaded());
        values.put(KEY_TIME,message.getTime());
        values.put(KEY_DATE,message.getDate());
        values.put(KEY_GROUPNAME,message.getGroupName());
        values.put(KEY_FIREBASEID,message.getFirebaseId());

        return database.update(DATABASE_TABLE,values,KEY_ID+"=?",new String[] {message.getId()+""});
    }

    public long UpdateMessageByFirebaseID(String firebaseId,String type,int seenbyall)
    {
        ContentValues values = new ContentValues();

        //Cursor cursor = database.rawQuery("Select * from "+DATABASE_TABLE+" where "+KEY_FIREBASEID+" = "+firebaseId ,null);

        if(seenbyall==1) {
            if (type.equals("text"))
                values.put(KEY_ISDOWNLOADED, -5);
            else if (type.equals("image"))
                values.put(KEY_ISDOWNLOADED, 6);
            else if (type.equals("video"))
                values.put(KEY_ISDOWNLOADED, 106);
            else if (type.equals("gif"))
                values.put(KEY_ISDOWNLOADED, 206);
            else if (type.equals("sticker"))
                values.put(KEY_ISDOWNLOADED, 306);
        }
        else{
            if (type.equals("text"))
                values.put(KEY_ISDOWNLOADED, -4);
            else if (type.equals("image"))
                values.put(KEY_ISDOWNLOADED, 5);
            else if (type.equals("video"))
                values.put(KEY_ISDOWNLOADED, 105);
            else if (type.equals("gif"))
                values.put(KEY_ISDOWNLOADED, 205);
            else if (type.equals("sticker"))
                values.put(KEY_ISDOWNLOADED, 305);
        }

        return database.update(DATABASE_TABLE,values,KEY_FIREBASEID+"=?",new String[] {firebaseId+""});
    }

    public void DeleteMessagesofReceiver(String receiver)
    {
        String query = "DELETE FROM "+DATABASE_TABLE+" where "+KEY_SENDER+" = '"+receiver+"' OR "+KEY_RECEIVER+" = '"+receiver+"';";
        database.execSQL(query);
    }

    public void DeleteMessagesofGroup(String grpname)
    {
        String query = "DELETE FROM "+DATABASE_TABLE+" where "+KEY_GROUPNAME+" = '"+grpname+"';";
        database.execSQL(query);
    }

    public long DeleteMessage(MessageModel message)
    {
        return database.delete(DATABASE_TABLE,KEY_ID+"=?",new String[]{message.getId()+""});
    }

    public String getLastMessage(String receiver)
    {
        MessageModel model=null;
        String [] columns = {KEY_ID,KEY_SENDER,KEY_RECEIVER,KEY_MESSAGE,KEY_TYPE,KEY_ISDOWNLOADED,KEY_TIME,KEY_DATE,KEY_GROUPNAME,KEY_FIREBASEID};
        Cursor c = database.query(true,DATABASE_TABLE,columns,null,null,null,null,null,null);

        int iId = c.getColumnIndex(KEY_ID);
        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDownloaded = c.getColumnIndex(KEY_ISDOWNLOADED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iDate = c.getColumnIndex(KEY_DATE);
        int iGroup = c.getColumnIndex(KEY_GROUPNAME);
        int iFirebaseId = c.getColumnIndex(KEY_FIREBASEID);

        for(c.moveToLast();!c.isBeforeFirst();c.moveToPrevious())
        {
            if(c.getString(iGroup).equals("null") && (c.getString(iSender).equals(receiver) || c.getString(iReceiver).equals(receiver))) {
                model = new MessageModel(c.getInt(iId),c.getString(iSender),c.getString(iReceiver),c.getString(iMessage),c.getString(iType),c.getInt(iDownloaded),c.getString(iTime),c.getString(iDate),c.getString(iGroup),c.getString(iFirebaseId));
                break;
            }
        }

        if(model !=null) {
            if (model.getType().equals("text"))
                return model.getMessage();
            else if (model.getType().equals("image"))
                return " ";
            else if(model.getType().equals("Date"))
                return "null";
            else if(model.getType().equals("video"))
                return "  ";
            else if(model.getType().equals("gif"))
                return "   ";
            else if(model.getType().equals("sticker"))
                return "    ";
        }
        else
        {
            return "null";
        }

        return null;
    }

    public String getLastMessageTime(String receiver)
    {
        MessageModel model=null;
        String [] columns = {KEY_ID,KEY_SENDER,KEY_RECEIVER,KEY_MESSAGE,KEY_TYPE,KEY_ISDOWNLOADED,KEY_TIME,KEY_DATE,KEY_GROUPNAME,KEY_FIREBASEID};
        Cursor c = database.query(true,DATABASE_TABLE,columns,null,null,null,null,null,null);

        int iId = c.getColumnIndex(KEY_ID);
        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDownloaded = c.getColumnIndex(KEY_ISDOWNLOADED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iDate = c.getColumnIndex(KEY_TIME);
        int iGroup = c.getColumnIndex(KEY_GROUPNAME);
        int iFirebaseId = c.getColumnIndex(KEY_FIREBASEID);

        for(c.moveToLast();!c.isBeforeFirst();c.moveToPrevious())
        {
            if(c.getString(iGroup).equals("null") && (c.getString(iSender).equals(receiver) || c.getString(iReceiver).equals(receiver))) {
                model = new MessageModel(c.getInt(iId),c.getString(iSender),c.getString(iReceiver),c.getString(iMessage),c.getString(iType),c.getInt(iDownloaded),c.getString(iTime),c.getString(iDate),c.getString(iGroup),c.getString(iFirebaseId));
                break;
            }
        }

        if(model !=null) {
            return model.getTime();
        }
        else
        {
            return "null";
        }
    }

    public int getID()
    {
        int id = -1;
        String [] columns = {KEY_ID};
        Cursor c = database.query(true,DATABASE_TABLE,columns,null,null,null,null,null,null);

        int iId = c.getColumnIndex(KEY_ID);

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            id = c.getInt(iId);
        }
        return id;
    }

    public ArrayList<MessageModel> getAllMessages()
    {
        ArrayList<MessageModel> messages = new ArrayList<>();
        String [] columns = {KEY_ID,KEY_SENDER,KEY_RECEIVER,KEY_MESSAGE,KEY_TYPE,KEY_ISDOWNLOADED,KEY_TIME,KEY_DATE,KEY_GROUPNAME,KEY_FIREBASEID};
        Cursor c = database.query(true,DATABASE_TABLE,columns,null,null,null,null,null,null);

        int iId = c.getColumnIndex(KEY_ID);
        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDownloaded = c.getColumnIndex(KEY_ISDOWNLOADED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iDate = c.getColumnIndex(KEY_DATE);
        int iGroup = c.getColumnIndex(KEY_GROUPNAME);
        int iFirebaseId = c.getColumnIndex(KEY_FIREBASEID);

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
                messages.add(new MessageModel(c.getInt(iId),c.getString(iSender),c.getString(iReceiver),c.getString(iMessage),c.getString(iType),c.getInt(iDownloaded),c.getString(iTime),c.getString(iDate),c.getString(iGroup),c.getString(iFirebaseId)));
        }
        c.close();
        return messages;

    }


    public String getLastMessageGroup(String grpName) {
        MessageModel model = null;
        String[] columns = {KEY_ID, KEY_SENDER, KEY_RECEIVER, KEY_MESSAGE, KEY_TYPE, KEY_ISDOWNLOADED, KEY_TIME, KEY_DATE, KEY_GROUPNAME,KEY_FIREBASEID};
        Cursor c = database.query(true, DATABASE_TABLE, columns, null, null, null, null, null, null);

        int iId = c.getColumnIndex(KEY_ID);
        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDownloaded = c.getColumnIndex(KEY_ISDOWNLOADED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iDate = c.getColumnIndex(KEY_DATE);
        int iGroup = c.getColumnIndex(KEY_GROUPNAME);
        int iFirebaseId = c.getColumnIndex(KEY_FIREBASEID);

        for(c.moveToLast();!c.isBeforeFirst();c.moveToPrevious())
        {
            if(c.getString(iGroup).equals(grpName)) {
                model = new MessageModel(c.getInt(iId),c.getString(iSender),c.getString(iReceiver),c.getString(iMessage),c.getString(iType),c.getInt(iDownloaded),c.getString(iTime),c.getString(iDate),c.getString(iGroup),c.getString(iFirebaseId));
                break;
            }
        }

        if(model !=null) {
            if (model.getType().equals("text"))
                return model.getMessage();
            else if (model.getType().equals("image"))
                return " ";
            else if(model.getType().equals("Date"))
                return "null";
            else if(model.getType().equals("video"))
                return "  ";
            else if(model.getType().equals("gif"))
                return "   ";
            else if(model.getType().equals("sticker"))
                return "    ";
        }
        else
        {
            return "null";
        }

        return null;

    }

    public String getLastGroupMessageTime(String grpName)
    {
        MessageModel model=null;
        String [] columns = {KEY_ID,KEY_SENDER,KEY_RECEIVER,KEY_MESSAGE,KEY_TYPE,KEY_ISDOWNLOADED,KEY_TIME,KEY_DATE,KEY_GROUPNAME,KEY_FIREBASEID};
        Cursor c = database.query(true,DATABASE_TABLE,columns,null,null,null,null,null,null);

        int iId = c.getColumnIndex(KEY_ID);
        int iSender = c.getColumnIndex(KEY_SENDER);
        int iReceiver = c.getColumnIndex(KEY_RECEIVER);
        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDownloaded = c.getColumnIndex(KEY_ISDOWNLOADED);
        int iTime = c.getColumnIndex(KEY_TIME);
        int iDate = c.getColumnIndex(KEY_TIME);
        int iGroup = c.getColumnIndex(KEY_GROUPNAME);
        int iFirebaseId = c.getColumnIndex(KEY_FIREBASEID);

        for(c.moveToLast();!c.isBeforeFirst();c.moveToPrevious())
        {
            if(c.getString(iGroup).equals(grpName)) {
                model = new MessageModel(c.getInt(iId),c.getString(iSender),c.getString(iReceiver),c.getString(iMessage),c.getString(iType),c.getInt(iDownloaded),c.getString(iTime),c.getString(iDate),c.getString(iGroup),c.getString(iFirebaseId));
                break;
            }
        }

        if(model !=null) {
            return model.getTime();
        }
        else
        {
            return "null";
        }
    }

}
