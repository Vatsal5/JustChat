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
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    TextView tvCreateGroup;

    DatabaseReference reference;
    ArrayList <String> members;
    FriendsAdapter userAdapter;
    ArrayList<UserDetail> contacts;
    ArrayList<UserDetailWithStatus> contacts1;
    ArrayList<String> number1,membersToadd;
    int yes=0;
    int c=0;
    int k=0;
    String groupkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvCreateGroup=findViewById(R.id.tvCreate);
        members=new ArrayList<>();
        groupkey=getIntent().getStringExtra("groupkey");


        setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            number1= new ArrayList<String>();
        membersToadd= new ArrayList<String>();
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

            if(ApplicationClass.activity==1) {
                ApplicationClass.activity=0;
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupkey).child("members").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                members.add(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
            }

            tvCreateGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(tvCreateGroup.getText().equals("ADD"))
                    {
                        for(int index=0; index<contacts1.size();index++) {
                            if(contacts1.get(index).getSelected()==1) {
                                if (contacts1.get(index).getPh_number().substring(0, 3).equals("+91")) {
                                   FirebaseDatabase.getInstance().getReference().child("groups").child(getIntent().getStringExtra("groupkey"))
                                   .child("members").push().setValue(contacts1.get(index).getPh_number());
                                    FirebaseDatabase.getInstance().getReference().child("users").child(contacts1.get(index).getPh_number()).child("groups").
                                            child(groupkey).setValue(getIntent().getStringExtra("groupname"));
                                } else {
                                    FirebaseDatabase.getInstance().getReference().child("groups").child(getIntent().getStringExtra("groupkey"))
                                            .child("members").push().setValue("+91"+contacts1.get(index).getPh_number());
                                    FirebaseDatabase.getInstance().getReference().child("users").child("+91"+contacts1.get(index).getPh_number()).child("groups").
                                            child(groupkey).setValue(getIntent().getStringExtra("groupname"));

                                }
                            }
                        }
                        Intent intent=new Intent(FriendsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        FriendsActivity.this.finish();

                        startActivity(intent);

                    }
                    else
                    {

                    for(int index=0; index<contacts1.size();index++) {
                        if(contacts1.get(index).getSelected()==1) {
                            if (contacts1.get(index).getPh_number().substring(0, 3).equals("+91")) {
                                ApplicationClass.members.add(contacts1.get(index).getPh_number());
                            } else {
                                ApplicationClass.members.add("+91" + contacts1.get(index).getPh_number());
                            }
                        }
                    }

                    FriendsActivity.this.finish();
                    startActivity(new Intent(FriendsActivity.this,CreateGroup.class));
                }}
            });

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
            if(IsValid(number)==0) {
                if (number.substring(0, 3).equals("+91")) {
                    if (!(number.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
                        contacts.add(new UserDetail(number, name));
                        number1.add(number);
                    }
                } else {

                    if (!(("+91" + number).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {

                        contacts.add(new UserDetail(number, name));
                        number1.add(number);
                    }
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
    public void onItemSelected(int index) {

        if (getIntent().getIntExtra("createGroup", 2) == 1) {
            yes=1;
            tvCreateGroup.setVisibility(View.VISIBLE);
            tvCreateGroup.setText("Create Group");


            if(contacts1.get(index).getSelected()==0)
            contacts1.get(index).setSelected(1);
            else
                contacts1.get(index).setSelected(0);
            userAdapter.notifyDataSetChanged();


        }
        else if(ApplicationClass.addmembers==1)
        {
            ApplicationClass.addmembers=0;
            tvCreateGroup.setVisibility(View.VISIBLE);
            int x=0;
    for(int i=0;i<members.size();i++)
    {


    if(members.get(i).equals(contacts1.get(index)) || members.get(i).equals("+91"+contacts1.get(index)))
    {
        x=1;
        break;
    }}
    if(x==0) {
        if (contacts1.get(index).getSelected() == 0)
            contacts1.get(index).setSelected(1);
        else
            contacts1.get(index).setSelected(0);
        userAdapter.notifyDataSetChanged();
        tvCreateGroup.setText("ADD");
    }
        }

        else {
            Intent intent = new Intent(FriendsActivity.this, MessageActivity.class);
            intent.putExtra("title", contacts1.get(index).getuID());

            if (contacts1.get(index).getPh_number().substring(0, 3).equals("+91")) {
                intent.putExtra("phone", contacts1.get(index).getPh_number());
            } else {
                intent.putExtra("phone", "+91" + contacts1.get(index).getPh_number());

            }
            if (getIntent().getIntExtra("path", 2) == 1) {
                intent.putExtra("path", 2);
                intent.putExtra("type", getIntent().getStringExtra("type"));
                intent.putExtra("message", getIntent().getStringExtra("message"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.finish();
            }
            intent.putExtra("profile", contacts1.get(index).getUrl());
            startActivity(intent);
        }
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

    @Override
    protected void onRestart() {
        super.onRestart();
        ApplicationClass.members.clear();
    }
}
