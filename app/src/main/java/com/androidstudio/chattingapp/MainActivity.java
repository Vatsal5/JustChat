package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements UserAdapter.itemSelected
{

    ArrayList<UserDetail> contacts;
    ArrayList<UserDetail> contacts1;

    ListView lv;
    FirebaseDatabase database;
    String currentUserNumber;
    DatabaseReference reference;

    UserAdapter userAdapter;
    int c=0;
    int k=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contacts = new ArrayList<>();
        contacts1 = new ArrayList<>();

        database=FirebaseDatabase.getInstance();

        reference=database.getReference();
        currentUserNumber= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();


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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
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
                null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME );
        while (cursor.moveToNext())
        {

           final String name= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
           final String number= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
           contacts.add(new UserDetail(number, name));

        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(int i=0;i<contacts.size();i++) {
                    if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).exists()) {
                        if(i==0)
                        {
                            contacts1.add(new UserDetail(contacts.get(i).getPh_number(), contacts.get(i).getuID()));
                            (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue(null);
                            c=1;
                        }
                        else
                            {
                                for(int j=0;j<c;j++)
                                {
                                    if(contacts.get(i).getPh_number().equals(contacts1.get(j).getPh_number()))
                                    {
                                        k=1;
                                        break;
                                    }
                                }
                                if(k==0)
                                {
                                    contacts1.add(new UserDetail(contacts.get(i).getPh_number(), contacts.get(i).getuID()));
                                    (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue("hi");

                                    c++;

                                }

                            }
                        k=0;
                    }
                }
                userAdapter=new UserAdapter(MainActivity.this,contacts1);
                lv.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Toast.makeText(getApplicationContext(),contacts.get(0).getuID(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(int index) {

        Intent intent = new Intent(MainActivity.this,MessageActivity.class);
        intent.putExtra("title",contacts1.get(index).getuID());
        intent.putExtra("phone",contacts1.get(index).getPh_number());
        startActivity(intent);
    }
}