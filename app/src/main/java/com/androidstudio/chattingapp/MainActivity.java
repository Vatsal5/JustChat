package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<UserDetail> contacts;
    ArrayList<UserDetail> contacts1;

    ListView lv;
    FirebaseDatabase database;
    DatabaseReference reference;
    String name1,number1;

    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contacts=new ArrayList<>();
        contacts1=new ArrayList<>();

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();


        lv=findViewById(R.id.lv);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
        }
        else
        {
            getcontact();


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getcontact();


            }
        }
    }

    public  void getcontact()
    {
        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        while (cursor.moveToNext())
        {

           final String name= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          final   String number= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new UserDetail(number, name));




        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int c=0;
                for(int i=0;i<contacts.size();i++) {
                    if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).exists()) {
                        c++;
                        contacts1.add(new UserDetail(contacts.get(i).getPh_number(), contacts.get(i).getuID()));
                        //  Toast.makeText(getApplicationContext(),contacts.get(0).getuID(),Toast.LENGTH_LONG).show();
                        //          Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();


                    }
                }
                userAdapter=new UserAdapter(MainActivity.this,contacts1);
                lv.setAdapter(userAdapter);
              //  Toast.makeText(getApplicationContext(),contacts1.get().getuID(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Toast.makeText(getApplicationContext(),contacts.get(0).getuID(),Toast.LENGTH_LONG).show();


    }
}
