package com.androidstudio.chattingapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.FontRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    public static String url,rename;
    public static DBHandler Handler;
    public  static int messagesent;
    public static ArrayList<String>members,groupmembers;
    public static ArrayList<String>PendingRequests;
    public static ArrayList<UserDetail>Contacts;
    public static String Groupname,GroupDp,groupkey,RenameGroup,CurrentReceiver;
    public  static  int create,addmembers,activity, groupusers;

    static class ChangeStatus extends AsyncTask<Void,Void,Void>
    {
        ArrayList<MessageModel> messages;
        int check;

        ChangeStatus()
        {
            messages = new ArrayList<>();
            check=0;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            messages.addAll(Handler.getAllMessages());

            for(int i=messages.size()-1;i>=0;i--)
            {
                if(check==15)
                    break;

                final MessageModel model = messages.get(i);

                if(messages.get(i).getDownloaded() ==304)
                {
                    model.setDownloaded(303);
                    Handler.UpdateMessage(model);
                    check=0;
                }

                if(messages.get(i).getDownloaded() ==301)
                {
                    model.setDownloaded(300);
                    Handler.UpdateMessage(model);
                    check=0;
                }

                if(messages.get(i).getDownloaded() ==204)
                {
                    model.setDownloaded(203);
                    Handler.UpdateMessage(model);
                    check=0;
                }

                if(messages.get(i).getDownloaded() ==201)
                {
                    model.setDownloaded(200);
                    Handler.UpdateMessage(model);
                    check=0;
                }

                if(messages.get(i).getDownloaded() ==3)
                {
                    model.setDownloaded(2);
                    Handler.UpdateMessage(model);
                    check=0;
                }

                    if (messages.get(i).getDownloaded() == -3) {
                        model.setDownloaded(-2);
                        Handler.UpdateMessage(model);
                        check=0;
                    }

                if(messages.get(i).getDownloaded()==103)
                {
                    model.setDownloaded(100);
                    Handler.UpdateMessage(model);
                    check=0;
                }

                if(messages.get(i).getDownloaded()==104)
                {
                    model.setDownloaded(101);
                    Handler.UpdateMessage(model);
                    check=0;
                }
                if(messages.get(i).getDownloaded()==4)
                {
                    model.setDownloaded(0);
                    Handler.UpdateMessage(model);
                    check=0;
                }
                check++;
            }
            return null;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        rename=null;
        messagesent=0;

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

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference("users").keepSynced(true);

        FirebaseDatabase.getInstance().purgeOutstandingWrites();

        Handler = new DBHandler(this);
        Handler.Open();
        groupmembers=new ArrayList<>();

        new ChangeStatus().execute();

        members=new ArrayList<>();
        PendingRequests=new ArrayList<>();
        Contacts=new ArrayList<>();
        Groupname=null;
        GroupDp="null";
        RenameGroup=null;
        groupkey=null;
        create=0;
        groupusers=0;
        addmembers=0;
        activity=0;
        CurrentReceiver = null;

    }


}
