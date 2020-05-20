package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor edit;
    ValueEventListener datecheck;
    DatabaseReference offsetRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref= getApplicationContext().getSharedPreferences("Names",0);
        edit = pref.edit();

        if(isConnected()) {

             offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
                    datecheck=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    long offset = snapshot.getValue(Long.class);
                    long estimatedServerTimeMs = System.currentTimeMillis() + offset;
                    long millis = System.currentTimeMillis();
                    java.sql.Date date1 = new java.sql.Date(millis);


                    long milliSecondsElapsed = date1.getTime() - estimatedServerTimeMs;
                    //  Log.d("poiu",date1.getTime()+"");
                    // Log.d("poiu",date.getTime()+"");
                    // long diff = TimeUnit.MINUTES.convert(milliSecondsElapsed, TimeUnit.MILLISECONDS);
                    if (milliSecondsElapsed / (24 * 60 * 60 * 1000) >= 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                        builder.setTitle("Alert")
                                .setMessage("Date Of Your Device Is Not Accurate. Please Correct It And Open App Again")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                                        dialogInterface.dismiss();
                                        SplashActivity.this.finish();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    } else {
                        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                        } else {
                            getContacts();

                            Intent intent;
                            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//
//                    if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
//                    } else {
//                        getContacts();
//                    }

                                intent = new Intent(SplashActivity.this, Registration.class);
                                finish();

                            } else {
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                                finish();
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                            startActivity(intent);

                            overridePendingTransition(0, 0);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // System.err.println("Listener was cancelled");
                }
            };
            offsetRef.addValueEventListener(datecheck);



        }
        else{
            if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                getContacts();

                Intent intent;
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//
//                    if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
//                    } else {
//                        getContacts();
//                    }

                    intent = new Intent(SplashActivity.this, Registration.class);
                    finish();

                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    finish();
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                startActivity(intent);

                overridePendingTransition(0, 0);

            }
        }

    }

    public void getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        while (cursor.moveToNext()) {

            final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            final String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (IsValid(number) == 0) {
                if (number.substring(0, 3).equals("+91")) {
                        ApplicationClass.Contacts.add(new UserDetail(number, name));
                        //number1.add(number);

                        if (!pref.getString(number, number).equals(name)) {
                            edit.putString(number, name);
                            edit.apply();
                        }


                } else {


                        ApplicationClass.Contacts.add(new UserDetail("+91" + number, name));
                        //number1.add("+91" + number);

                        if (!pref.getString("+91" + number, "+91" + number).equals(name)) {
                            edit.putString("+91" + number, name);
                            edit.apply();
                        }


                }

            }
        }
    }



    public int IsValid(String number)
    {int c=0;
        for(int i=0; i<number.length();i++)
        {
            if(number.charAt(i)=='#' || number.charAt(i)=='$' || number.charAt(i)=='.' || number.charAt(i)=='[' || number.charAt(i)==']')
            {
                c=1;
                break;
            }

        }
        if(c==1)
        {
            return 1;
        }
        else
            return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getContacts();
                Intent intent;
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//
//                    if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
//                    } else {
//                        getContacts();
//                    }

                    intent = new Intent(SplashActivity.this, Registration.class);
                    finish();

                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    finish();
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                startActivity(intent);

                overridePendingTransition(0, 0);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Permission Required")
                        .setMessage("Without this permission app will not work")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                SplashActivity.this.finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isConnected()) {
            offsetRef.removeEventListener(datecheck);
        }

    }
}
