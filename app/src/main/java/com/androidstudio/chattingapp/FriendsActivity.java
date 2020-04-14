package com.androidstudio.chattingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity implements FriendsAdapter.itemSelected{

    ListView lv;
    FirebaseDatabase database;
    String currentUserNumber;
    DatabaseReference reference;
    FriendsAdapter userAdapter;
    ArrayList<UserDetail> contacts;
    ArrayList<UserDetailWithStatus> contacts1;
    ArrayList<String> number1;
    int c=0;
    int k=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            number1= new ArrayList<String>();
            contacts = new ArrayList<>();
            contacts1 = new ArrayList<>();

            database=FirebaseDatabase.getInstance();

            reference=database.getReference();
            currentUserNumber= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

            lv=findViewById(R.id.lv);

            if(ContextCompat.checkSelfPermission(FriendsActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(FriendsActivity.this,new String[]{Manifest.permission.READ_CONTACTS},1);
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
            if(number.substring(0,3).equals("+91")) {
                if (!(number.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
                    contacts.add(new UserDetail(number, name));
                    number1.add(number);
                }
            }
            else {

                if (!(("+91" + number).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {

                    contacts.add(new UserDetail(number, name));
                    number1.add(number);
                }
            }

        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(int i=0;i<contacts.size();i++) {

                    if ((number1.get(i)).substring(0, 3).equals("+91")) {

                            if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).exists()) {


                                if (i == 0) {
                                    if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").exists()) {
                                        contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class),
                                                dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                    } else {

                                        contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",
                                                dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                    }
                                    (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue("/null");
                                    //  (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("activeStatus")).setValue("online");
                                    c = 1;
                                } else {
                                    for (int j = 0; j < c; j++) {
                                        if (contacts.get(i).getPh_number().equals(contacts1.get(j).getPh_number())) {
                                            k = 1;
                                            break;
                                        }
                                    }
                                    if (k == 0) {
                                        if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").exists()) {
                                            Log.d("myapp", dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class));
                                            contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class),
                                                    dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                        } else {

                                            contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null", dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                        }
                                        (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue("/null");

                                        c++;
                                    }
                                }
                                k = 0;
                            }

                    }

                    else
                    {


                            if (dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).exists()) {
                            if (i == 0) {
                                if(dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").exists()) {
                                    contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").getValue(String.class),
                                            dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                }
                                else{
                                    contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",
                                            dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                }                                 (reference.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("message")).setValue("/null");
                                c = 1;
                            } else {
                                for (int j = 0; j < c; j++) {
                                    if (contacts.get(i).getPh_number().equals(contacts1.get(j).getPh_number())) {
                                        k = 1;
                                        break;
                                    }
                                }
                                if (k == 0) {
                                    if(dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").exists()) {
                                        contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").getValue(String.class),
                                                dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                    }
                                    else{
                                        contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",
                                                dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0));
                                    }
                                    (reference.child("users").child(currentUserNumber).child(("+91"+contacts.get(i).getPh_number())).child("message")).setValue("/null");

                                    //  (reference.child("users").child(currentUserNumber).child(("+91"+contacts.get(i).getPh_number())).child("activeStatus")).setValue("online");


                                    c++;
                                }
                            }
                            k = 0;
                        }
                    }
                }
               // Log.d("tag",contacts1.get(0).getPh_number());
                userAdapter=new FriendsAdapter(FriendsActivity.this,contacts1);
                lv.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemSelected(int index) {

        Intent intent = new Intent(FriendsActivity.this,MessageActivity.class);
        intent.putExtra("title",contacts1.get(index).getuID());

        if( contacts1.get(index).getPh_number().substring(0,3).equals("+91")) {
            intent.putExtra("phone", contacts1.get(index).getPh_number());
        }
        else{
            intent.putExtra("phone", "+91" + contacts1.get(index).getPh_number());

        }
        if(getIntent().getIntExtra("path",2)==1) {
            intent.putExtra("path",2);
            intent.putExtra("type", getIntent().getStringExtra("type"));
            intent.putExtra("message", getIntent().getStringExtra("message"));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.finish();
        }
        intent.putExtra("profile",contacts1.get(index).getUrl());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case R.id.Profile:
                startActivity(new Intent(FriendsActivity.this,Profile.class));
                break;

            case android.R.id.home:
                FriendsActivity.this.finish();
                break;

            case R.id.CreateGroup:
                startActivity(new Intent(FriendsActivity.this,CreateGroup.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
