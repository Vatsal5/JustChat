package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
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
    ConstraintLayout llSplash;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ChildEventListener Group;
    ChildEventListener childEvent;

    int l, pos;
    boolean flag=false,flag2=false;

    UserAdapter userAdapter;
    int c=0;
    int u;
    int messageno;

    int k=0;

    int num=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(0,0);

llSplash=findViewById(R.id.llsplash);

        pref= getApplicationContext().getSharedPreferences("Names",0);
        editor = pref.edit();



        if(ApplicationClass.splash==true)
        {
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  llSplash.setVisibility(View.GONE);
                }
            },2000);
        }
        else
            llSplash.setVisibility(View.GONE);

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

                        if(contacts1.get(q).getGroupname()==null) {
                            new listener(q).piclistener();
                            new listener(q).VideoListener();
                            new listener(q).child();
                        }
                        else{
                            new grouplistener(q).piclistener();
                            new grouplistener(q).VideoListener();
                            new grouplistener(q).child();
                        }

                            q++;

                    }
                }

            }
        },3000);
        final android.os.Handler handler1= new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < contacts1.size(); i++) {
                    if (!(contacts1.get(i).getGroupname() == null)) {
                        (new GroupDp(i)).ProfileListener();
                    }
                }

            }
        },2000);

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



//                     if (contacts1.get(index).getPh_number().substring(0,3).equals("+91")) {
//                         model = new MessageModel(1110, contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//                                 , dataSnapshot.getValue(String.class).substring(15), "video", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
//                     }
//                     else
//                     {
//                         model = new MessageModel(1110,"+91"+contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//                                 , dataSnapshot.getValue(String.class).substring(15), "video", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
//                     }
//                     Handler.addMessage(model);


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

                                 contacts1.get(index).setLastmessage(dataSnapshot.getValue().toString().substring(15));
                                 contacts1.get(index).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                                 contacts1.get(index).setMessagenum(contacts1.get(index).getMessagenum() + 1);


//                             MessageModel model;
//
//                             if (contacts1.get(index).getPh_number().substring(0,3).equals("+91"))
//                             {
//                                 model = new MessageModel(1110, contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//                                         , dataSnapshot.getValue(String.class).substring(15), "text", -1, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
//
//                             }
//                             else
//                             {
//                                 model = new MessageModel(1110, "+91"+contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//                                         , dataSnapshot.getValue(String.class).substring(15), "text", -1, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
//                             }
//
//                             Handler.addMessage(model);

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



//                     MessageModel model;
//
//                     if (contacts1.get(index).getPh_number().substring(0,3).equals("+91")) {
//                         model = new MessageModel(1110, contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//                                 , dataSnapshot.getValue(String.class).substring(15), "image", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
//                     }
//                     else
//                     {
//                         model = new MessageModel(1110,"+91"+contacts1.get(index).getPh_number(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
//                                 , dataSnapshot.getValue(String.class).substring(15), "image", 0, dataSnapshot.getValue(String.class).substring(0, 5), dataSnapshot.getValue(String.class).substring(5, 15));
//                     }
//                     Handler.addMessage(model);


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


    public class grouplistener
    {
        int index;
        grouplistener(int index)
        {
            this.index=index;
        }

        public void VideoListener()
        {
            reference.child("groups").child(contacts1.get(index).getGroupkey()).child("videos")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            contacts1.get(index).setLastmessage("  ");
                            contacts1.get(index).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(index).setMessagenum(contacts1.get(index).getMessagenum() + 1);
                            userAdapter.notifyDataSetChanged();
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


        public void child()
        {
            reference.child("groups").child(contacts1.get(index).getGroupkey()).child("messages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            contacts1.get(index).setLastmessage(dataSnapshot.getValue().toString().substring(34));
                            contacts1.get(index).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(index).setMessagenum(contacts1.get(index).getMessagenum() + 1);
                            userAdapter.notifyDataSetChanged();
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

        public void piclistener()
        {
            reference.child("groups").child(contacts1.get(index).getGroupkey()).child("images")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            contacts1.get(index).setLastmessage(" ");
                            contacts1.get(index).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(index).setMessagenum(contacts1.get(index).getMessagenum() + 1);
                            userAdapter.notifyDataSetChanged();
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
           if(IsValid(number)==0) {
               contacts.add(new UserDetail(number, name));
               number1.add(number);
           }

        }
        dataCreater=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (int i = 0; i < contacts.size(); i++) {


                    if ((number1.get(i)).substring(0, 3).equals("+91")) {
                        if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).exists()) {


                            if (i == 0) {
                                if ((dataSnapshot.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend").exists())) {
                                    if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").exists()) {
                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class), 2
                                                , "", "",null,null));
                                    } else {

                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null", 2
                                                , "", "",null,null));
                                    }


                                    (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue("/null");
                                    // (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                    //  (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("activeStatus")).setValue("online");
                                    c = 1;
                                }
                            } else {
                                for (int j = 0; j < c; j++) {
                                    if (contacts.get(i).getPh_number().equals(contacts1.get(j).getPh_number())) {
                                        k = 1;
                                        break;
                                    }
                                }
                                if (k == 0) {
                                    if ((dataSnapshot.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend").exists())) {
                                        if (dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").exists()) {
                                            Log.d("myapp", dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class));
                                            contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child(contacts.get(i).getPh_number()).child("profile").getValue(String.class), 2
                                                    , "", "",null,null));
                                        } else {

                                            contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null", 2
                                                    , "", "",null,null));
                                        }


                                        (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("message")).setValue("/null");

                                        //  (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                        c++;
                                    }
                                }
                            }
                            k = 0;
                        }

                    } else {
                        if (dataSnapshot.child("users").child("+91" + contacts.get(i).getPh_number()).exists()) {
                            if (i == 0) {
                                if ((dataSnapshot.child("users").child(currentUserNumber).child("+91" + contacts.get(i).getPh_number()).child("info").child("friend").exists())) {
                                    if (dataSnapshot.child("users").child("+91" + contacts.get(i).getPh_number()).child("profile").exists()) {
                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child("+91" + contacts.get(i).getPh_number()).child("profile").getValue(String.class), 2
                                                , "", "",null,null));
                                    } else {
                                        contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null", 2
                                                , "", "",null,null));
                                    }

                                    (reference.child("users").child(currentUserNumber).child("+91" + contacts.get(i).getPh_number()).child("message")).setValue("/null");
                                    //  (reference.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                    c = 1;
                                }
                            } else {
                                for (int j = 0; j < c; j++) {
                                    if (contacts.get(i).getPh_number().equals(contacts1.get(j).getPh_number())) {
                                        k = 1;
                                        break;
                                    }
                                }
                                if (k == 0) {
                                    if ((dataSnapshot.child("users").child(currentUserNumber).child("+91" + contacts.get(i).getPh_number()).child("info").child("friend").exists())) {
                                        if (dataSnapshot.child("users").child("+91" + contacts.get(i).getPh_number()).child("profile").exists()) {
                                            contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), dataSnapshot.child("users").child("+91" + contacts.get(i).getPh_number()).child("profile").getValue(String.class), 2
                                                    , "", "",null,null));
                                        } else {
                                            contacts1.add(new UserDetailwithUrl(contacts.get(i).getPh_number(), contacts.get(i).getuID(), "null", 2
                                                    , "", "",null,null));
                                        }


                                        (reference.child("users").child(currentUserNumber).child(("+91" + contacts.get(i).getPh_number())).child("message")).setValue("/null");
                                        //  (reference.child("users").child(currentUserNumber).child("+91"+contacts.get(i).getPh_number()).child("info").child("friend")).setValue("no");

                                        //  (reference.child("users").child(currentUserNumber).child(("+91"+contacts.get(i).getPh_number())).child("activeStatus")).setValue("online");


                                        c++;
                                    }
                                }
                            }
                            k = 0;
                        }
                    }
                }
                // both the arraylists got clone
                //Toast.makeText(MainActivity.this,contacts2.size()+"",Toast.LENGTH_LONG).show();

                childEvent = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (!(dataSnapshot.getKey().equals("name") || dataSnapshot.getKey().equals("contact") || dataSnapshot.getKey().equals("profile") ||
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

                                        contacts1.add(new UserDetailwithUrl(dataSnapshot.getKey(), "", "null", 2
                                                , "", "",null,null));
                                    }
                                }
                            }

                            for (int i = 0; i < contacts1.size(); i++) {
                                if (contacts1.get(i).getGroupname() == null) {
                                    if (!contacts1.get(i).getPh_number().substring(0, 3).equals("+91")) {
                                        String name = pref.getString("+91" + contacts1.get(i).getPh_number(), "null");
                                        if (name.equals("null")) {
                                            editor.putString("+91" + contacts1.get(i).getPh_number(), contacts1.get(i).getuID());
                                            editor.apply();
                                        }
                                        contacts1.get(i).setLastmessage(Handler.getLastMessage("+91" + contacts1.get(i).getPh_number()));
                                        contacts1.get(i).setTime(Handler.getLastMessageTime("+91" + contacts1.get(i).getPh_number()));
                                    } else {
                                        String name = pref.getString(contacts1.get(i).getPh_number(), "null");
                                        if (name.equals("null")) {
                                            editor.putString(contacts1.get(i).getPh_number(), contacts1.get(i).getuID());
                                            editor.apply();
                                        }
                                        contacts1.get(i).setLastmessage(Handler.getLastMessage(contacts1.get(i).getPh_number()));
                                        contacts1.get(i).setTime(Handler.getLastMessageTime(contacts1.get(i).getPh_number()));
                                    }
                                } else {
                                    contacts1.get(i).setLastmessage(Handler.getLastMessageGroup(contacts1.get(i).getGroupname()));
                                    contacts1.get(i).setTime(Handler.getLastGroupMessageTime(contacts1.get(i).getGroupname()));
                                }
                            }

                            userAdapter = new UserAdapter(MainActivity.this, contacts1);
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

                Group = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if(Handler.getGroupMessages(dataSnapshot.getValue().toString()).size()>0)
                                contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                                        , Handler.getLastMessageGroup(dataSnapshot.getValue().toString()),
                                        Handler.getLastGroupMessageTime(dataSnapshot.getValue().toString()).substring(0,5), dataSnapshot.getKey(),dataSnapshot.getValue().toString()));
                        else
                            contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                                    , "",
                                    "", dataSnapshot.getKey(),dataSnapshot.getValue().toString()));

                                userAdapter.notifyDataSetChanged();
                                num++;
                                reference.child("groups").child(dataSnapshot.getKey()).child("profile").addValueEventListener(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists())
                                                {
                                                    contacts1.get(contacts1.size()-1).setUrl(dataSnapshot.getValue().toString());
                                                userAdapter.notifyDataSetChanged();}
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        }
                                );

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

                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").addChildEventListener(Group);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        if(num==0)
        {
            reference.addListenerForSingleValueEvent(dataCreater);
        }

    }

    @Override
    public void onItemSelected(int index) {

        if(contacts1.get(index).getGroupname() == null)
        {
            flag=true;
            l=index;


        Intent intent = new Intent(MainActivity.this,MessageActivity.class);

        intent.putExtra("type"," ");
        intent.putExtra("messagecount",contacts1.get(index).getMessagenum());
        if(contacts1.get(index).getuID().equals(""))
        {
            intent.putExtra("title",contacts1.get(index).getPh_number());

        }
        else {
            intent.putExtra("title", contacts1.get(index).getuID());
        }



        if( contacts1.get(index).getPh_number().substring(0,3).equals("+91")) {
            intent.putExtra("phone", contacts1.get(index).getPh_number());

        }
        else{
            intent.putExtra("phone", "+91" + contacts1.get(index).getPh_number());


        }
        if(!contacts1.get(index).getUrl().equals("null"))
            intent.putExtra("profile",contacts1.get(index).getUrl());
        startActivity(intent);}
        else
        {
            l=index;
            flag2=true;
            Intent intent = new Intent(MainActivity.this,MessageActivity2.class);
            intent.putExtra("groupname",contacts1.get(index).getGroupname());
            intent.putExtra("groupkey",contacts1.get(index).getGroupkey());
            intent.putExtra("profile",contacts1.get(index).getUrl());
            startActivity(intent);
        }
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
            case R.id.Settings:
                startActivity(new Intent(MainActivity.this,Settings.class));
                break;

            case R.id.CreateGroup:
                Intent intent=new Intent(MainActivity.this,FriendsActivity.class);
                intent.putExtra("createGroup",1);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
    protected void onResume() {
        super.onResume();
        Status("online");
        if(ApplicationClass.RenameGroup!=null)
        {

            contacts1.get(l).setuID(ApplicationClass.RenameGroup);
            userAdapter.notifyDataSetChanged();
            ApplicationClass.RenameGroup=null;
        }

        if(ApplicationClass.create==1)
        {

            final android.os.Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < contacts1.size(); i++) {
                        if (!(contacts1.get(i).getGroupname() == null)) {
                            (new GroupDp(i)).ProfileListener();
                        }
                    }
                    ApplicationClass.groupkey = null;

                }
            },1000);
        }

        else
            ApplicationClass.members.clear();

    }

    public class GroupDp
    {
        int index;
        GroupDp(int index)
        {
            this.index=index;
        }

        public void ProfileListener()
        {
            FirebaseDatabase.getInstance().getReference().child("groups").child(contacts1.get(index).getGroupkey()).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        contacts1.get(index).setUrl(dataSnapshot.getValue().toString());
                        userAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("destroy","App is killed");

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

        reference.removeEventListener(dataCreater);
        reference.child("users").child(currentUserNumber).removeEventListener(childEvent);

        getcontact();

        for (int i = 0; i < contacts1.size(); i++) {
            if(contacts1.get(i).getGroupname()==null) {
                if (!contacts1.get(i).getPh_number().substring(0, 3).equals("+91")) {
                    contacts1.get(i).setLastmessage(Handler.getLastMessage("+91" + contacts1.get(i).getPh_number()));
                    contacts1.get(i).setTime(Handler.getLastMessageTime("+91" + contacts1.get(i).getPh_number()));
                } else {
                    contacts1.get(i).setLastmessage(Handler.getLastMessage(contacts1.get(i).getPh_number()));
                    contacts1.get(i).setTime(Handler.getLastMessageTime(contacts1.get(i).getPh_number()));
                }
            }
            else {
                if(Handler.getGroupMessages(contacts1.get(i).getGroupname()).size()>0) {
                    contacts1.get(i).setLastmessage(Handler.getLastMessageGroup(contacts1.get(i).getGroupname()));
                    contacts1.get(i).setTime(Handler.getLastGroupMessageTime(contacts1.get(i).getGroupname()).substring(0, 5));
                }
            }
        }
        userAdapter.notifyDataSetChanged();

        if(flag==true)
        {
            flag=false;
            contacts1.get(l).setMessagenum(2);
        }

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

    @Override
    protected void onPause() {
        super.onPause();
        if(flag==true) {
            contacts1.get(l).setMessagenum(2);
            userAdapter.notifyDataSetChanged();
        }
        if(flag2==true)
        {
            flag2=false;
            contacts1.get(l).setMessagenum(2);
            userAdapter.notifyDataSetChanged();
        }

        Log.d("Destroy","onPause");


    }

    @Override
    protected void onStop() {
        super.onStop();


        Log.d("Destroy","onStop");
    }
}