package com.androidstudio.chattingapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.icu.util.MeasureUnit;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements UserAdapter.itemSelected
{

    ArrayList<UserDetail> contacts;
    ArrayList<UserDetailwithUrl> contacts1;
    ArrayList<UserDetailwithUrl> contacts2;

    ArrayList<String> number1;

    FirebaseDatabase database1;
    DatabaseReference reference1;
    DatabaseReference UserStatus;
    ChildEventListener chreceiver;
    ValueEventListener dataCreater;
    DBHandler Handler;
    LinearLayoutManager linearLayoutManager;

    ArrayList<MessageModel> chats;
    RecyclerView lv;
    FirebaseDatabase database;
    FloatingActionButton btnContacts;
    String currentUserNumber;
    DatabaseReference reference;

    int l, pos;
    boolean flag=false;

    UserAdapter userAdapter;
    int c=0;
    int u;
    int messageno;
    int k=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler = new DBHandler(MainActivity.this);
        Handler.Open();
        chats = new ArrayList<>();
        database1= FirebaseDatabase.getInstance();
        reference1 = database1.getReference();
        linearLayoutManager= new LinearLayoutManager(MainActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(null);

        number1= new ArrayList<String>();
        contacts = new ArrayList<UserDetail>();
        contacts1 = new ArrayList<UserDetailwithUrl>();
        contacts2 = new ArrayList<UserDetailwithUrl>();

        btnContacts=findViewById(R.id.btnContacts);

        database=FirebaseDatabase.getInstance();

        reference=database.getReference();

        currentUserNumber= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        lv=findViewById(R.id.lv);
        lv.setLayoutManager(linearLayoutManager);

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }
        else
        {
            getcontact();

        }

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,FriendsActivity.class);
                startActivity(intent);
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            DatabaseReference rf = FirebaseDatabase.getInstance().getReference(".info/connected");
            UserStatus = FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

            rf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    boolean connected = dataSnapshot.getValue(Boolean.class);

                    UserStatus.onDisconnect().setValue("offline");

                    if (connected)
                    {

                        UserStatus.setValue("online").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               // if(task.isSuccessful())
                                   // Toast.makeText(MainActivity.this, "Online", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        final android.os.Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(contacts1.size()>0) {
                    for ( int q = 0; q < contacts1.size(); ) {
                        new listener(q).piclistener();
                        new listener(q).VideoListener();
                        new listener(q).child();

                        q++;
                    }
                }

            }
        },3000);

        ItemTouchHelper itemTouchHelper= new ItemTouchHelper(simpleCallback);

        itemTouchHelper.attachToRecyclerView(lv);




    }

    ItemTouchHelper.SimpleCallback simpleCallback= new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            pos= viewHolder.getAdapterPosition();

            if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},2);
            }
            else
            {
                makecall();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.my_background))
                    .addActionIcon(R.drawable.ic_call)
                    .setIconHorizontalMargin(20)
                    .addSwipeRightLabel("  Voice Call").setSwipeRightLabelColor(getResources().getColor(R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


        }
    };



    public class listener
    {
        int index;
         listener(int index)
         {
            this.index=index;
         }

         public void VideoListener()
         {
             chreceiver = new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                     contacts1.get(index).setLastmessage("  ");
                     contacts1.get(index).setMessagenum(contacts1.get(index).getMessagenum() + 1);
                     userAdapter.notifyDataSetChanged();

                     MessageModel model;

                     if (contacts1.get(index).getPh_number().substring(0,3).equals("+91")) {
                         model = new MessageModel(1110, contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                 , dataSnapshot.getValue(String.class).substring(15), "video", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
                     }
                     else
                     {
                         model = new MessageModel(1110,"+91"+contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                 , dataSnapshot.getValue(String.class).substring(15), "video", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
                     }
                     Handler.addMessage(model);

                     dataSnapshot.getRef().removeValue();

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
             if (contacts1.get(index).getPh_number().substring(0,3).equals("+91") ) {
                 reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("videos").addChildEventListener(chreceiver);
             }
             else
             {
                 reference.child("users").child("+91"+contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("videos").addChildEventListener(chreceiver);

             }
         }


         public void child()
         {
             chreceiver = new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();


                     if (!(dataSnapshot.getKey().equals("message"))) {

                         if (!(dataSnapshot.getKey().equals("info"))) {
                             contacts1.get(index).setLastmessage(dataSnapshot.getValue(String.class).substring(15));
                             contacts1.get(index).setTime(dataSnapshot.getValue(String.class).substring(0,5));
                             contacts1.get(index).setMessagenum(contacts1.get(index).getMessagenum() + 1);

                             MessageModel model;

                             if (contacts1.get(index).getPh_number().substring(0,3).equals("+91"))
                             {
                                 model = new MessageModel(1110, contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                         , dataSnapshot.getValue(String.class).substring(15), "text", -1, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));

                             }
                             else
                             {
                                 model = new MessageModel(1110, "+91"+contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                         , dataSnapshot.getValue(String.class).substring(15), "text", -1, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
                             }

                             Handler.addMessage(model);

                             dataSnapshot.getRef().removeValue();

                             userAdapter.notifyDataSetChanged();
                            // Log.d("messagecount",contacts1.get(index-1).getMessagenum()+"");


                         }
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
             if (contacts1.get(index).getPh_number().substring(0,3).equals("+91") ) {
                 reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addChildEventListener(chreceiver);
             }
             else
             {
                 reference.child("users").child("+91"+contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addChildEventListener(chreceiver);
             }
         }

         public void piclistener()
         {
             chreceiver = new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                     contacts1.get(index).setLastmessage(" ");
                     contacts1.get(index).setMessagenum(contacts1.get(index).getMessagenum() + 1);
                     userAdapter.notifyDataSetChanged();

                     MessageModel model;

                     if (contacts1.get(index).getPh_number().substring(0,3).equals("+91")) {
                         model = new MessageModel(1110, contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                 , dataSnapshot.getValue(String.class).substring(15), "image", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
                     }
                     else
                     {
                         model = new MessageModel(1110,"+91"+contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                 , dataSnapshot.getValue(String.class).substring(15), "image", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
                     }
                     Handler.addMessage(model);

                     dataSnapshot.getRef().removeValue();

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
             if (contacts1.get(index).getPh_number().substring(0,3).equals("+91") ) {
                 reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("images").addChildEventListener(chreceiver);
             }
             else
             {
                 reference.child("users").child("+91"+contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("images").addChildEventListener(chreceiver);

             }
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
        if(requestCode==2)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                makecall();
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
           number1.add(number);

        }
        dataCreater=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(int i=0;i<contacts.size();i++) {


                        if ((number1.get(i)).substring(0, 3).equals("+91")) {
                            if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).exists()) {


                                if (i == 0) {
                                    if((dataSnapshot.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend").exists()))
                                    { if(dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").exists()) {
                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class),2
                                         ,"",""));
                                    }
                                    else{

                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",2
                                         ,"",""));
                                    }


                                        (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue("/null");
                                   // (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                    //  (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("activeStatus")).setValue("online");
                                    c = 1;}
                                } else {
                                    for (int j = 0; j < c; j++) {
                                        if (contacts.get(i).getPh_number().equals(contacts1.get(j).getPh_number())) {
                                            k = 1;
                                            break;
                                        }
                                    }
                                    if (k == 0) {
                                        if((dataSnapshot.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend").exists()))
                                        { if(dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").exists()) {
                                            Log.d("myapp",dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class));
                                            contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class),2
                                             ,"",""));
                                        }
                                        else{

                                            contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",2
                                            ,"",""));
                                        }


                                            (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue("/null");

                                      //  (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                        c++;
                                    }}
                                }
                                k = 0;
                            }

                        }

                    else
                    {
                        if (dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).exists()) {
                            if (i == 0) {
                                if((dataSnapshot.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("info").child("friend").exists()))
                                {if(dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").exists()) {
                                    contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").getValue(String.class),2
                                        ,"",""  ));
                                }
                                else{
                                    contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",2
                                   ,"",""));
                                }

                                    (reference.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("message")).setValue("/null");
                              //  (reference.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                c = 1;}
                            } else {
                                for (int j = 0; j < c; j++) {
                                    if (contacts.get(i).getPh_number().equals(contacts1.get(j).getPh_number())) {
                                        k = 1;
                                        break;
                                    }
                                }
                                if (k == 0) {
                                    if((dataSnapshot.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("info").child("friend").exists()))
                                    {  if(dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").exists()) {
                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child("+91"+contacts.get(i).getPh_number()).child("profile").getValue(String.class),2
                                        ,"",""));
                                    }
                                    else{
                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null",2
                                        ,"",""));
                                    }


                                        (reference.child("users").child(currentUserNumber).child(("+91"+contacts.get(i).getPh_number())).child("message")).setValue("/null");
                                  //  (reference.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                  //  (reference.child("users").child(currentUserNumber).child(("+91"+contacts.get(i).getPh_number())).child("activeStatus")).setValue("online");


                                    c++;
                                }}
                            }
                            k = 0;
                        }
                    }
                }
                // both the arraylists got clone
                //Toast.makeText(MainActivity.this,contacts2.size()+"",Toast.LENGTH_LONG).show();



                reference.child("users").child(currentUserNumber).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(!(dataSnapshot.getKey().equals("name") || dataSnapshot.getKey().equals("contact") ||dataSnapshot.getKey().equals("profile") ||
                                dataSnapshot.getKey().equals("status"))) {
                          //  Log.d("contacts",dataSnapshot.getKey());
                            int tell=0;


                            for (int i = 0; i < contacts1.size(); i++) {

                               // Log.d("contact",contacts1.get(i).getPh_number());




                                if(contacts1.get(i).getPh_number().substring(0,3).equals("+91")) {
                                    if (contacts1.get(i).getPh_number().equals(dataSnapshot.getKey())) {
                                        tell = 1;
                                        break;
                                    }
                                }

                                else
                                {
                                    String ph="+91"+contacts1.get(i).getPh_number();
                                    if (ph.equals(dataSnapshot.getKey())) {
                                        tell = 1;
                                        break;
                                    }
                                }

                            }
                            if (tell == 0) {
                                if(dataSnapshot.child("info").child("friend").exists())
                                {
                                if(dataSnapshot.child("info").child("friend").getValue().equals("yes")) {

                                    contacts1.add(new UserDetailwithUrl(dataSnapshot.getKey(), "","null" , 2
                                            , "", ""));
                                }
                            }}

                            for(int i=0;i<contacts1.size();i++)
                            {
                                if(!contacts1.get(i).getPh_number().substring(0,3).equals("+91"))
                                {
                                    contacts1.get(i).setLastmessage(Handler.getLastMessage("+91"+contacts1.get(i).getPh_number()));
                                    contacts1.get(i).setTime(Handler.getLastMessageTime("+91"+contacts1.get(i).getPh_number()));
                                }
                                else
                                {
                                    contacts1.get(i).setLastmessage(Handler.getLastMessage(contacts1.get(i).getPh_number()));
                                    contacts1.get(i).setTime(Handler.getLastMessageTime(contacts1.get(i).getPh_number()));
                                }
                            }
                            userAdapter=new UserAdapter(MainActivity.this,contacts1);
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
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(dataCreater);

    }

    @Override
    public void onItemSelected(int index) {

        Intent intent = new Intent(MainActivity.this,MessageActivity.class);
        flag=true;
        if(contacts1.get(index).getuID().equals(""))
        {
            intent.putExtra("title",contacts1.get(index).getPh_number());

        }
        else {
            intent.putExtra("title", contacts1.get(index).getuID());
        }contacts1.get(index).setMessagenum(2);

        l=index;

        if( contacts1.get(index).getPh_number().substring(0,3).equals("+91")) {
            intent.putExtra("phone", contacts1.get(index).getPh_number());
        }
        else{
            intent.putExtra("phone", "+91" + contacts1.get(index).getPh_number());

        }
        if(!contacts1.get(index).getUrl().equals("null"))
            intent.putExtra("profile",contacts1.get(index).getUrl());
        startActivity(intent);
    }

    @Override
    public void onImageSelected(int index) {
        Intent intent = new Intent(MainActivity.this,ShowImage.class);
        intent.putExtra("source",contacts1.get(index).getUrl());
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
                startActivity(new Intent(MainActivity.this,Profile.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Status("offline");
    }
    public void Status(String Status)
    {
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("UserStatus");
        rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(Status);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(flag==true)
        {
            flag=false;
            contacts1.get(l).setMessagenum(2);
        }
        getcontact();
    }
    public void makecall()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED)
        {
            Intent intent= new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+contacts1.get(pos).getPh_number()));
            startActivity(intent);
        }
    }
}