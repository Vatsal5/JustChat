package com.androidstudio.chattingapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    TextView tvCreateGroup,tvtitle;
    int x=0;
    DatabaseReference reference;
    ArrayList <String> members;
    FriendsAdapter userAdapter;
    ArrayList<UserDetail> contacts;
    ChildEventListener childEvent,Group;
    ArrayList<UserDetailWithStatus> contacts1;
    Toolbar toolbar;
    CardView cvCreate;
    ImageView ivBack;
    ArrayList<String> number1,membersToadd;
    int yes=0;
    int c=0;
    int k=0;
    String groupkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        tvtitle=findViewById(R.id.tvhead);
        cvCreate=findViewById(R.id.ivCreate);
        ivBack=findViewById(R.id.ivBack);

         toolbar = findViewById(R.id.toolbar);
        SharedPreferences preftheme;
        preftheme=getSharedPreferences("theme",0);

        String theme=preftheme.getString("theme","red");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendsActivity.this.finish();
            }
        });

        switch (theme)
        { case "orange":

            toolbar.setBackgroundColor(getResources().getColor(R.color.Orange));
            cvCreate.setBackgroundColor(getResources().getColor(R.color.Orange));
            break;

            case "blue":

                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.blue));

                break;


            case "bluish":
                toolbar.setBackgroundColor(getResources().getColor(R.color.bluish));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.bluish));
                break;


            case "deepred":
                toolbar.setBackgroundColor(getResources().getColor(R.color.deepred));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.deepred));
                break;

            case "faintpink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.faintpink));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.faintpink));

                break;

            case "darkblue":
                toolbar.setBackgroundColor(getResources().getColor(R.color.darkblue));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.darkblue));
                break;


            case "green":
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.green));
                break;

            case "lightorange":
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightorange));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.lightorange));
                break;

            case "lightred":
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightred));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.lightred));
                break;


            case "mustard":
                toolbar.setBackgroundColor(getResources().getColor(R.color.mustard));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.mustard));
                break;

            case "pink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pink));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.pink));

                break;

            case "pureorange":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pureorange));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.pureorange));
                break;

            case "purepink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purepink));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.purepink));
                break;

            case "purple":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.purple));
                break;

            default:
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                cvCreate.setBackgroundColor(getResources().getColor(R.color.red));

        }
        tvCreateGroup=findViewById(R.id.tvCreate);
        members=new ArrayList<>();
        groupkey=getIntent().getStringExtra("groupkey");


        setTitle(null);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupkey).child("members").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        members.add(dataSnapshot.getValue().toString());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            cvCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(tvCreateGroup.getText().equals("ADD"))
                    {
                        ApplicationClass.addmembers=0;
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
                        tvCreateGroup.setText("Create Group");
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
                                                dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
                                    } else {

                                        contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",
                                                dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
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
                                                    dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
                                        } else {

                                            contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null", dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
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
                                            dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
                                }
                                else{
                                    contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",
                                            dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
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
                                                dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
                                    }
                                    else{
                                        contacts1.add(new UserDetailWithStatus(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",
                                                dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("status").getValue(String.class),0,null));
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

                childEvent = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (!(dataSnapshot.getKey().equals("name") || dataSnapshot.getKey().equals("groups") || dataSnapshot.getKey().equals("profile") ||
                                dataSnapshot.getKey().equals("status"))) {
                            //  Log.d("contacts",dataSnapshot.getKey());
                            int tell = 0;


                            for (int i = 0; i < contacts1.size(); i++) {

                                // Log.d("contact",contacts1.get(i).getPh_number());


                                if (contacts1.get(i).getPh_number().substring(0, 3).equals("+91")) {
                                    if (contacts1.get(i).getPh_number().equals(dataSnapshot.getKey())) {
                                        tell = 1;
                                        break;
                                    }
                                } else {
                                    String ph = "+91" + contacts1.get(i).getPh_number();
                                    if (ph.equals(dataSnapshot.getKey())) {
                                        tell = 1;
                                        break;
                                    }
                                }

                            }
                            if (tell == 0) {
                                if (dataSnapshot.child("info").child("friend").exists()) {
                                    if (dataSnapshot.child("info").child("friend").getValue().equals("yes")) {
                                        contacts1.add(new UserDetailWithStatus(dataSnapshot.getKey(), "", "null",
                                                "",0,null));
                                    }
                                }
                            }



                            userAdapter = new FriendsAdapter(FriendsActivity.this,contacts1 );
                            lv.setAdapter(userAdapter);

                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                reference.child("users").child(currentUserNumber).addChildEventListener(childEvent);


//                if (getIntent().getIntExtra("path", 2) == 1) {
//
//                    Group = new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                            contacts1.add(new UserDetailWithStatus(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", ""
//                                    , 0,dataSnapshot.getKey()));
//
//                            reference.child("groups").child(dataSnapshot.getKey()).child("profile").addValueEventListener(
//                                    new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            if(dataSnapshot.exists())
//                                            {
//                                                contacts1.get(contacts1.size()-1).setUrl(dataSnapshot.getValue().toString());
//                                                userAdapter.notifyDataSetChanged();}
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    }
//                            );
//
//                        }
//
//
//                        @Override
//                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    };
//
//                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").addChildEventListener(Group);
//
//                }
               // Log.d("tag",contacts1.get(0).getPh_number());
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
            cvCreate.setVisibility(View.VISIBLE);


            if(contacts1.get(index).getSelected()==0)
            contacts1.get(index).setSelected(1);
            else
                contacts1.get(index).setSelected(0);
            userAdapter.notifyDataSetChanged();


        }
        else if(ApplicationClass.addmembers==1)
        {

            tvCreateGroup.setVisibility(View.VISIBLE);
            cvCreate.setVisibility(View.VISIBLE);

    for(int i=0;i<members.size();i++)
    {


    if(members.get(i).equals(contacts1.get(index).getPh_number()) || members.get(i).equals("+91"+contacts1.get(index).getPh_number()))
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
    else{
        x=0;
    }
        }

        else {
            // tvtitle.setText("Forward To");
            Intent intent = new Intent(FriendsActivity.this, MessageActivity.class);
            intent.putExtra("title", contacts1.get(index).getuID());

            if (contacts1.get(index).getPh_number().substring(0, 3).equals("+91")) {
                intent.putExtra("phone", contacts1.get(index).getPh_number());
            } else {
                intent.putExtra("phone", "+91" + contacts1.get(index).getPh_number());

            }
            if (getIntent().getIntExtra("path", 2) == 1) {
                if(contacts1.get(index).getPh_number().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()) ||
                        ("+91"+contacts1.get(index).getPh_number()).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                {
                    Intent intent1=new Intent(FriendsActivity.this,MessageActivity2.class);
                    intent1.putExtra("groupname", contacts1.get(index).getuID());

                    intent1.putExtra("groupkey",contacts1.get(index).getKey());
                    intent1.putExtra("type", getIntent().getStringExtra("type"));
                    intent1.putExtra("path", 2);
                    intent1.putExtra("message", getIntent().getStringExtra("message"));
                    this.finish();
                    startActivity(intent1);

                }
                else {
                    intent.putExtra("path", 2);
                    intent.putExtra("type", getIntent().getStringExtra("type"));
                    intent.putExtra("message", getIntent().getStringExtra("message"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.finish();
                }
            }
            intent.putExtra("profile", contacts1.get(index).getUrl());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvtitle.setText("Contacts");
        tvCreateGroup.setText("Create Group");
        ApplicationClass.addmembers=0;
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
            case R.id.Settings:
                startActivity(new Intent(FriendsActivity.this,Settings.class));
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
        tvCreateGroup.setText("Create Group");
        ApplicationClass.members.clear();

    }
}
