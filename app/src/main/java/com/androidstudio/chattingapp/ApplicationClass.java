package com.androidstudio.chattingapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.FontRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.logging.Handler;

public class ApplicationClass extends Application
{
    public static Context MessageActivityContext;
    public static Context MessageActivity2Context;
    public static DBHandler handler;
    public static String url;
    public static ArrayList<Long>DownloadIds;
    public static ArrayList<String>members;
    public static String Groupname,GroupDp,groupkey,RenameGroup;
    public  static  int create,addmembers,activity, groupusers;
    public  static boolean splash=false;

    @Override
    public void onCreate() {
        super.onCreate();

//        if(FirebaseAuth.getInstance().getCurrentUser())
//        {
//            Intent intent=new Intent(ApplicationClass.this,Registration.class);
//
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(intent);
//        }
//        else
//            splash=true;

        members=new ArrayList<>();
        Groupname=null;
        GroupDp="null";
        RenameGroup=null;
        groupkey=null;
        create=0;
        groupusers=0;
        addmembers=0;
        activity=0;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference("users").keepSynced(true);

        FirebaseDatabase.getInstance().purgeOutstandingWrites();

        DownloadIds = new ArrayList<>();

        handler = new DBHandler(ApplicationClass.this);
        handler.Open();

        ArrayList<MessageModel> messages = new ArrayList<>();
        messages.addAll(handler.getAllMessages());

        for(int i=0;i<messages.size();i++)
        {
            final MessageModel model = messages.get(i);
            if(messages.get(i).getDownloaded() ==3)
            {
                model.setDownloaded(2);
                handler.UpdateMessage(model);
            }

            if(messages.get(i).getGroupName().equals("null"))
                if(messages.get(i).getDownloaded() ==-3) {
                    FirebaseDatabase.getInstance().getReference("users").child(messages.get(i).getSender()).child(messages.get(i).getReciever()).push().setValue(messages.get(i)
                            .getTime()+messages.get(i).getDate() +messages.get(i).getMessage().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            model.setDownloaded(-1);
                            handler.UpdateMessage(model);
                        }
                    });
                }
            if(messages.get(i).getDownloaded()==103)
            {
                model.setDownloaded(100);
                handler.UpdateMessage(model);
            }

            if(messages.get(i).getDownloaded()==104)
            {
                model.setDownloaded(101);
                handler.UpdateMessage(model);
            }
            if(messages.get(i).getDownloaded()==4)
            {
                model.setDownloaded(0);
                handler.UpdateMessage(model);
            }
        }

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
    }


}
