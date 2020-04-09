package com.androidstudio.chattingapp;

import android.app.Application;
import android.content.Context;
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
    public static DBHandler handler;
    public static String url;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference("users").keepSynced(true);

        FirebaseDatabase.getInstance().purgeOutstandingWrites();

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

            if(messages.get(i).getDownloaded() ==-3)
            {
                FirebaseDatabase.getInstance().getReference("users").child(messages.get(i).getSender()).child(messages.get(i).getReciever()).push().setValue(messages.get(i)
                        .getTime()+messages.get(i).getDate() +messages.get(i).getMessage().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        model.setDownloaded(-1);
                        handler.UpdateMessage(model);
                    }
                });
            }
        }

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
    }


}
