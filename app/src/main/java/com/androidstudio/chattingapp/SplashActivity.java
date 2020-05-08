package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref= getApplicationContext().getSharedPreferences("Names",0);
        edit = pref.edit();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
//
//                    if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
//                    } else {
//                        getContacts();
//                    }

                     intent = new Intent(SplashActivity.this, Registration.class);
                     finish();

                }
                else
                {
                    getContacts();
                     intent = new Intent(SplashActivity.this, MainActivity.class);
                     finish();
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                startActivity(intent);

                overridePendingTransition(0, 0);
            }
        },1200);


    }

    public void getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        while (cursor.moveToNext()) {

            final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            final String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (IsValid(number) == 0) {
                if (number.substring(0, 3).equals("+91")) {
                    if (!(number.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
                        ApplicationClass.Contacts.add(new UserDetail(number, name));
                        //number1.add(number);

                        if (!pref.getString(number, number).equals(name)) {
                            edit.putString(number, name);
                            edit.apply();
                        }

                    }
                } else {

                    if (!(("+91" + number).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {

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
}
