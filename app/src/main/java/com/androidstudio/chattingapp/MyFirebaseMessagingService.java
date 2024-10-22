package com.androidstudio.chattingapp;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static com.androidstudio.chattingapp.ApplicationClass.MessageActivity2Context;
import static com.androidstudio.chattingapp.ApplicationClass.MessageActivityContext;
import static com.androidstudio.chattingapp.ApplicationClass.condition1;
import static com.androidstudio.chattingapp.ApplicationClass.condition2;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    SharedPreferences pref;
    private final String ADMIN_CHANNEL_ID ="admin_channel";
    Bitmap largeIcon;
    NotificationCompat.Builder notificationBuilder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if(!ApplicationClass.condition1) {
            if (ApplicationClass.MainActivityContext == null)
                ApplicationClass.condition = true;
            else {
                if (((Activity) ApplicationClass.MainActivityContext).isDestroyed())
                    ApplicationClass.condition = true;
            }
        }
        else
        {
            if((MessageActivityContext!=null && !((Activity)MessageActivityContext).isDestroyed()) || (MessageActivity2Context!=null && !((Activity)MessageActivity2Context).isDestroyed()))
            {
                if(remoteMessage.getData().get("sender1")!=null) {
                    String sender1 = remoteMessage.getData().get("sender1");
                    if (!ApplicationClass.CurrentSender.equals(sender1))
                        ApplicationClass.condition = true;
                    else {
                        if (ApplicationClass.condition2)
                            ApplicationClass.condition = true;
                    }
                }else if(ApplicationClass.condition2){
                    ApplicationClass.condition=true;
                }
            }
            else {
                ApplicationClass.condition = true;
            }
        }


            if(ApplicationClass.condition){

            final Intent intent = new Intent(this,

                    MainActivity.class);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationID = new Random().nextInt(3000);

            pref = getApplicationContext().getSharedPreferences("Names", 0);
            String name = pref.getString(remoteMessage.getData().get("title"), "null");
            String sender=pref.getString(remoteMessage.getData().get("sender"), "null");

            if (name.equals("null")) {
                name = remoteMessage.getData().get("title");
            }


      /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels(notificationManager);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);


            if (!remoteMessage.getData().get("dp").equals("null"))
                largeIcon = getBitmapFromURL(remoteMessage.getData().get("dp"));
            else
                if(!remoteMessage.getData().get("sender").equals("null"))
                    largeIcon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.group);
                else
                    largeIcon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.person);

            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (!remoteMessage.getData().get("sender").equals("null")) {
                    notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                            .setSmallIcon(R.drawable.icon1)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(name)

                            .setContentText(sender+":  "+remoteMessage.getData().get("text"))
                            .setAutoCancel(true)
                            .setSound(notificationSoundUri)
                            .setContentIntent(pendingIntent);
                }
                else
                {
                    notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                            .setSmallIcon(R.drawable.icon1)
                            .setLargeIcon(largeIcon)
                            .setContentTitle(name)

                            .setContentText(remoteMessage.getData().get("text"))
                            .setAutoCancel(true)
                            .setSound(notificationSoundUri)
                            .setContentIntent(pendingIntent);

                }

            //Set notification color to match your app color template
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setColor(getResources().getColor(R.color.notificationColor));
            }
            notificationManager.notify(notificationID, notificationBuilder.build());
                ApplicationClass.condition=false;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(getResources().getColor(R.color.notificationColor));
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDefaultUseCaches(true);
            connection.setUseCaches(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
