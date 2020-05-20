package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.lang.UCharacter;
import android.icu.util.MeasureUnit;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements UserAdapter.itemSelected, PopupMenu.OnMenuItemClickListener
{

    ArrayList<UserDetail> contacts;
    ArrayList<UserDetailwithUrl> contacts1;
    ArrayList<UserDetailwithUrl> contacts2;
    LinearLayout toolbar;

    ArrayList<String> number1;
    SharedPreferences preftheme;
    ImageView iv;
    FirebaseDatabase database1;
    DatabaseReference reference1;
    DatabaseReference UserStatus;
    ChildEventListener chreceiver,gifreceiver,stickerreceiver;
    ValueEventListener dataCreater,deleteimage,deletevideo,Status;
    DBHandler Handler;
    ArrayList<String> keyid;
    String keyid2;
    LinearLayoutManager linearLayoutManager;

    ArrayList<MessageModel> chats;
    RecyclerView lv;
    FirebaseDatabase database;
    FloatingActionButton btnContacts;
    String currentUserNumber;
    DatabaseReference reference;

    SearchView searchView;


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ImageView ivOptions;

    ChildEventListener Group;
    ValueEventListener profile,check;
    ChildEventListener childEvent,deleteGroupimages,deleteGroupvideos;

    int  pos;
    boolean flag=false,flag2=false;

    UserAdapter userAdapter;

    int num=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





//            if (ApplicationClass.splash == true) {
//
//                llSplash.setVisibility(View.VISIBLE);
//                ApplicationClass.splash = false;
//
//            } else
//                llSplash.setVisibility(View.GONE);

        searchView = findViewById(R.id.SearchView);

        keyid= new ArrayList<>();
            toolbar = findViewById(R.id.toolbar);
            iv=findViewById(R.id.ivOptions);
            btnContacts = findViewById(R.id.btnContacts);
            ivOptions = findViewById(R.id.ivOptions);
            preftheme = getSharedPreferences("theme", 0);

            pref = getApplicationContext().getSharedPreferences("Names", 0);
            String theme = preftheme.getString("theme", "red");


        switch (theme) {
            case "orange":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.Orange)));
               toolbar.setBackgroundColor(getResources().getColor(R.color.Orange));

                btnContacts.setBackgroundColor(getResources().getColor(R.color.Orange));
                break;
            case "blue":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));

                break;
            case "bluish":
               toolbar.setBackgroundColor(getResources().getColor(R.color.bluish));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluish)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluish)));

                break;
            case "deepred":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.deepred)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.deepred)));
               toolbar.setBackgroundColor(getResources().getColor(R.color.deepred));

                break;
            case "faintpink":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));
              toolbar.setBackgroundColor(getResources().getColor(R.color.faintpink));

                break;
            case "darkblue":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.darkblue));

                break;
            case "green":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));

                break;
            case "lightorange":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightorange));

                break;
            case "lightred":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightred)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightred)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightred));

                break;
            case "mustard":
                toolbar.setBackgroundColor(getResources().getColor(R.color.mustard));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.mustard)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mustard)));

                break;
            case "pink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pink));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));

                break;
            case "pureorange":
               toolbar.setBackgroundColor(getResources().getColor(R.color.pureorange));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.pureorange)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pureorange)));

                break;
            case "purepink":
               toolbar.setBackgroundColor(getResources().getColor(R.color.purepink));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purepink)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(
                        getResources().getColor(R.color.purepink)));

                break;
            case "purple":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));

                break;
            default:
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                break;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });

        ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(MainActivity.this,ivOptions, Gravity.END);
                menu.inflate(R.menu.profile);
                menu.setOnMenuItemClickListener(MainActivity.this);
                menu.show();
            }
        });


            Handler = new DBHandler(MainActivity.this);
            Handler.Open();
            chats = new ArrayList<>();
            database1 = FirebaseDatabase.getInstance();
            reference1 = database1.getReference();
            linearLayoutManager = new LinearLayoutManager(MainActivity.this);



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              //  toolbar.setOverflowIcon(getDrawable(R.drawable.overflow));
            }

            setTitle(null);

            number1 = new ArrayList<String>();
            contacts = new ArrayList<UserDetail>();
            contacts1 = new ArrayList<UserDetailwithUrl>();
            contacts2 = new ArrayList<UserDetailwithUrl>();


            database = FirebaseDatabase.getInstance();

            reference = database.getReference();

            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                currentUserNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            else
                currentUserNumber = "null";

            lv = findViewById(R.id.lv);
            lv.setHasFixedSize(true);
            lv.setLayoutManager(linearLayoutManager);
            userAdapter = new UserAdapter(MainActivity.this, contacts1);
            userAdapter.setHasStableIds(true);
            lv.setAdapter(userAdapter);
            lv.addItemDecoration(new DividerItemDecoration(lv.getContext(),DividerItemDecoration.VERTICAL));

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                getcontact();

            }

            btnContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                    startActivity(intent);
                }
            });


            DatabaseReference rf = FirebaseDatabase.getInstance().getReference(".info/connected");
            UserStatus = FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

            rf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);

                    UserStatus.onDisconnect().setValue("offline");

                    if (connected) {

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

            // final android.os.Handler handler1= new Handler();


            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

            itemTouchHelper.attachToRecyclerView(lv);

    }

    ItemTouchHelper.SimpleCallback simpleCallback= new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            pos = viewHolder.getAdapterPosition();
            userAdapter.notifyDataSetChanged();

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 2);
            } else {
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

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

            if (contacts1.get(viewHolder.getAdapterPosition()).getGroupname() != null)
                return makeMovementFlags(0, 0);

            else
                return makeMovementFlags(0, ItemTouchHelper.END);

        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Settings:
                startActivity(new Intent(MainActivity.this,Settings.class));
                return true;

            case R.id.CreateGroup:
                Intent intent=new Intent(MainActivity.this,FriendsActivity.class);
                intent.putExtra("createGroup",1);
                startActivity(intent);
                return true;

            default:
                return false;

        }
    }


    public class listener
    {
        int index;
        String key;
         listener(int index, String key)
         {
            this.index=index;
            this.key=key;
         }

         public void statusListener()
         {
             Status = new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.getValue() != null) {

                         int i=keyid.indexOf(key);



                         if (dataSnapshot.getValue(String.class).equals("online") || dataSnapshot.getValue(String.class).substring(0, 6).equals("typing")) {
                             contacts1.get(i).setStatus("online");
                         } else {
                             contacts1.get(i).setStatus("offline");
                         }
                         userAdapter.notifyItemChanged(keyid.indexOf(key));
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             };

             reference.child("UserStatus").child(contacts1.get(index).getPh_number()).addValueEventListener(Status);
         }


         public void profilelistener()
         {
             profile=new ValueEventListener(){
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if(dataSnapshot.exists()) {
                        // Log.d("abcd",dataSnapshot.getValue().toString());


                         int i=keyid.indexOf(key);



                         contacts1.get(i).setUrl(dataSnapshot.getValue().toString());


//                                                contacts1.add(new UserDetailwithUrl(key, "", dataSnapshot.getValue().toString(), 2
//                                                        , "", "",null,null));
                         //   userAdapter.notifyItemInserted(contacts1.size()-1);


                         userAdapter.notifyItemChanged(keyid.indexOf(key));


                         //   Log.d("asdf",contacts1.get(contacts1.size()-1).getUrl());
                     }

                     reference.child("users").child(contacts1.get(index).getPh_number()).child("profile").removeEventListener(profile);
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             };
             reference.child("users").child(contacts1.get(index).getPh_number()).child("profile").addListenerForSingleValueEvent(profile);
             // userAdapter.notifyDataSetChanged();

         }

         public void giflistener()
         {
             gifreceiver= new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();


                     UserDetailwithUrl userDetailwithUrl;
                     int j=keyid.indexOf(key);

                     userDetailwithUrl=contacts1.get(j);
                     contacts1.remove(j);
                     keyid.remove(j);
                     userAdapter.notifyDataSetChanged();
                     contacts1.add(0,userDetailwithUrl);
                     keyid.add(0,key);

                     userAdapter.notifyDataSetChanged();
                     int i=contacts1.indexOf(userDetailwithUrl);
                     contacts1.get(i).setLastmessage("   ");
                     contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                     contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                     userAdapter.notifyItemChanged(keyid.indexOf(key));



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
             reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("gif").addChildEventListener(gifreceiver);
         }

        public void stickerlistener()
        {
            stickerreceiver= new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();


                    UserDetailwithUrl userDetailwithUrl;
                    int j=keyid.indexOf(key);

                    userDetailwithUrl=contacts1.get(j);
                    contacts1.remove(j);
                    keyid.remove(j);
                    userAdapter.notifyDataSetChanged();
                    contacts1.add(0,userDetailwithUrl);
                    keyid.add(0,key);

                    userAdapter.notifyDataSetChanged();
                    int i=contacts1.indexOf(userDetailwithUrl);
                     contacts1.get(i).setLastmessage("    ");
                    contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                    contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                    userAdapter.notifyItemChanged(keyid.indexOf(key));



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
            reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("sticker").addChildEventListener(stickerreceiver);
        }


        public void VideoListener()
         {
             chreceiver = new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                     UserDetailwithUrl userDetailwithUrl;
                     int j=keyid.indexOf(key);

                     userDetailwithUrl=contacts1.get(j);
                     contacts1.remove(j);
                     keyid.remove(j);
                     userAdapter.notifyDataSetChanged();
                     contacts1.add(0,userDetailwithUrl);
                     keyid.add(0,key);

                     userAdapter.notifyDataSetChanged();
                     int i=contacts1.indexOf(userDetailwithUrl);

                         contacts1.get(i).setLastmessage("  ");
                     contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                         contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                     userAdapter.notifyItemChanged(keyid.indexOf(key));



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
                 reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("videos").addChildEventListener(chreceiver);


         }


         public void child()
         {
             chreceiver = new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();


                     if (!(dataSnapshot.getKey().equals("message"))) {

                         if (!(dataSnapshot.getKey().equals("info"))) {

                             UserDetailwithUrl userDetailwithUrl;
                             int j=keyid.indexOf(key);

                             userDetailwithUrl=contacts1.get(j);
                             contacts1.remove(j);
                             keyid.remove(j);
                             userAdapter.notifyDataSetChanged();
                             contacts1.add(0,userDetailwithUrl);
                             keyid.add(0,key);

                             userAdapter.notifyDataSetChanged();
                             int i=contacts1.indexOf(userDetailwithUrl);

                             contacts1.get(i).setLastmessage(dataSnapshot.getValue().toString().substring(15));
                                 contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                                 contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                             userAdapter.notifyItemChanged(keyid.indexOf(key));





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
                 reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addChildEventListener(chreceiver);


         }

         public void piclistener()
         {
             chreceiver = new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();


                     UserDetailwithUrl userDetailwithUrl;
                     int j=keyid.indexOf(key);

                     userDetailwithUrl=contacts1.get(j);
                     contacts1.remove(j);
                     keyid.remove(j);
                     userAdapter.notifyDataSetChanged();
                     contacts1.add(0,userDetailwithUrl);
                     keyid.add(0,key);

                     userAdapter.notifyDataSetChanged();
                     int i=contacts1.indexOf(userDetailwithUrl);
                         contacts1.get(i).setLastmessage(" ");
                     contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                         contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                     userAdapter.notifyItemChanged(keyid.indexOf(key));



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
                 reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("images").addChildEventListener(chreceiver);


         }
    }


    public class grouplistener
    {
        int index;
        String key;
        grouplistener(int index, String key)
        {
            this.index=index;
            this.key=key;
        }

        public void VideoListener()
        {
            reference.child("groups").child(contacts1.get(index).getGroupkey()).child("videos")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            UserDetailwithUrl userDetailwithUrl;
                            int j=keyid.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage("  ");
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid.indexOf(key));
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

        public void gifListener()
        {
            reference.child("groups").child(contacts1.get(index).getGroupkey()).child("gif")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            UserDetailwithUrl userDetailwithUrl;
                            int j=keyid.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage("   ");
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid.indexOf(key));
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

        public void stickerListener()
        {
            reference.child("groups").child(contacts1.get(index).getGroupkey()).child("sticker")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            UserDetailwithUrl userDetailwithUrl;
                            int j=keyid.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage("    ");
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid.indexOf(key));
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

                            UserDetailwithUrl userDetailwithUrl;
                            int j=keyid.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage(dataSnapshot.getValue().toString().substring(34));
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid.indexOf(key));
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

                            UserDetailwithUrl userDetailwithUrl;
                            int j=keyid.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage(" ");
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid.indexOf(key));
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

        public void ProfileListener()
        {
            FirebaseDatabase.getInstance().getReference().child("groups").child(contacts1.get(index).getGroupkey()).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        int i=keyid.indexOf(key);


                        contacts1.get(i).setUrl(dataSnapshot.getValue().toString());


                        userAdapter.notifyItemChanged(keyid.indexOf(key));

                    }


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


    public  void getcontact() {


        if (num == 0) {
        childEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!(dataSnapshot.getKey().equals("name") || dataSnapshot.getKey().equals("groups") || dataSnapshot.getKey().equals("profile") ||
                        dataSnapshot.getKey().equals("status"))) {

                    // to delete a 3 days old image in one

                    deleteimage = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                long millis = System.currentTimeMillis();
                                java.sql.Date date1 = new java.sql.Date(millis);
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dataSnapshot1.getValue().toString().substring(
                                            5, 15
                                    ));
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }

                                long milliSecondsElapsed = date1.getTime() - date.getTime();
                                //  Log.d("poiu",date1.getTime()+"");
                                // Log.d("poiu",date.getTime()+"");
                                // long diff = TimeUnit.MINUTES.convert(milliSecondsElapsed, TimeUnit.MILLISECONDS);
                                if (milliSecondsElapsed / (24 * 60 * 60 * 1000) >= 3) {
                                    // Log.d("poiu",diff+"");
                                    StorageReference file1;
                                    file1 = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot1.getValue().toString().substring(15));
                                    file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dataSnapshot1.getRef().removeValue();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dataSnapshot1.getRef().removeValue();

                                        }
                                    });
                                }
                            }


                            reference.child("users").child(dataSnapshot.getKey()).child(FirebaseAuth.getInstance().getCurrentUser()
                                    .getPhoneNumber()).child("info").child("deleteimages").removeEventListener(deleteimage);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    reference.child("users").child(dataSnapshot.getKey()).child(FirebaseAuth.getInstance().getCurrentUser()
                            .getPhoneNumber()).child("info").child("deleteimages").addListenerForSingleValueEvent(deleteimage);

                    deletevideo = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                long millis = System.currentTimeMillis();
                                java.sql.Date date1 = new java.sql.Date(millis);
                                Date date = null;
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dataSnapshot1.getValue().toString().substring(
                                            5, 15
                                    ));
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }

                                long milliSecondsElapsed = date1.getTime() - date.getTime();
                                //  Log.d("poiu",date1.getTime()+"");
                                // Log.d("poiu",date.getTime()+"");
                                // long diff = TimeUnit.MINUTES.convert(milliSecondsElapsed, TimeUnit.MILLISECONDS);
                                if (milliSecondsElapsed / (24 * 60 * 60 * 1000) >= 3) {
                                    // Log.d("poiu",diff+"");
                                    StorageReference file1;
                                    file1 = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot1.getValue().toString().substring(15));
                                    file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dataSnapshot1.getRef().removeValue();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dataSnapshot1.getRef().removeValue();

                                        }
                                    });
                                }
                            }

                            reference.child("users").child(dataSnapshot.getKey()).child(FirebaseAuth.getInstance().getCurrentUser()
                                    .getPhoneNumber()).child("info").child("deletevideos").removeEventListener(deletevideo);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    reference.child("users").child(dataSnapshot.getKey()).child(FirebaseAuth.getInstance().getCurrentUser()
                            .getPhoneNumber()).child("info").child("deletevideos").addListenerForSingleValueEvent(deletevideo);



//
//
//                            }

                    if (dataSnapshot.child("info").child("friend").exists() && dataSnapshot.child("info").child("friend").getValue().equals("yes")) {

                        contacts1.add(new UserDetailwithUrl(dataSnapshot.getKey(), pref.getString(dataSnapshot.getKey(), dataSnapshot.getKey()), "null", 2
                                , Handler.getLastMessage(dataSnapshot.getKey()), Handler.getLastMessageTime(dataSnapshot.getKey()), null, null));
                        userAdapter.notifyItemInserted(contacts1.size() - 1);
                        keyid.add(dataSnapshot.getKey());
                        new listener(contacts1.size() - 1, keyid.get(keyid.size()-1)).profilelistener();
                        new listener(contacts1.size()-1,keyid.get(keyid.size()-1)).statusListener();
                        new listener(contacts1.size() - 1,keyid.get(keyid.size()-1)).piclistener();
                        new listener(contacts1.size() - 1,keyid.get(keyid.size()-1)).VideoListener();
                        new listener(contacts1.size() - 1,keyid.get(keyid.size()-1)).giflistener();
                        new listener(contacts1.size() - 1,keyid.get(keyid.size()-1)).stickerlistener();
                        new listener(contacts1.size() - 1,keyid.get(keyid.size()-1)).child();


                    }

                    //                           }



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
                // Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                if (Handler.getGroupMessages(dataSnapshot.getKey(), 0).first.size() > 0) {
                    contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                            , Handler.getLastMessageGroup(dataSnapshot.getKey()),
                            Handler.getLastGroupMessageTime(dataSnapshot.getKey()), dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
                    userAdapter.notifyItemInserted(contacts1.size() - 1);
                    keyid.add(dataSnapshot.getKey());
                    grouplistener grouplistener=new grouplistener(contacts1.size()-1,dataSnapshot.getKey());

                    grouplistener.piclistener();
                    grouplistener.VideoListener();
                    grouplistener.child();
                    grouplistener.gifListener();
                    grouplistener.stickerListener();

                    grouplistener.ProfileListener();


                } else {
                    contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                            , "null",
                            "null", dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
                    userAdapter.notifyItemInserted(contacts1.size() - 1);
                    keyid.add(dataSnapshot.getKey());
                    grouplistener grouplistener=new grouplistener(contacts1.size()-1,dataSnapshot.getKey());
                    grouplistener.piclistener();
                    grouplistener.VideoListener();
                    grouplistener.child();
                    grouplistener.gifListener();
                    grouplistener.stickerListener();

                    grouplistener.ProfileListener();
                }



                final String key = dataSnapshot.getKey();
                deleteGroupimages = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                        long millis = System.currentTimeMillis();
                        java.sql.Date date1 = new java.sql.Date(millis);
                        Log.d("zxcv", dataSnapshot.getValue().toString());


//

                        try {


                            // get difference between two dates in MINUTES
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dataSnapshot.getValue().toString().substring(
                                    dataSnapshot.getValue().toString().indexOf("h") - 10, dataSnapshot.getValue().toString().indexOf("h")
                            ));

                            long milliSecondsElapsed = date1.getTime() - date.getTime();
                            //  Log.d("poiu",date1.getTime()+"");
                            // Log.d("poiu",date.getTime()+"");
                            // long diff = TimeUnit.MINUTES.convert(milliSecondsElapsed, TimeUnit.MILLISECONDS);
                            if (milliSecondsElapsed / (24 * 60 * 60 * 1000) >= 3) {
                                // Log.d("poiu",diff+"");

                                StorageReference file1;
                                file1 = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot
                                        .getValue().toString().indexOf("h")));
                                file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }


                        // FirebaseDatabase.getInstance().getReference().child("groups").child(key).child("deleteimages").removeEventListener(deleteGroupimages);

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

                FirebaseDatabase.getInstance().getReference().child("groups").child(dataSnapshot.getKey()).child("deleteimages").addChildEventListener(deleteGroupimages);


                deleteGroupvideos = new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                        long millis = System.currentTimeMillis();
                        java.sql.Date date1 = new java.sql.Date(millis);
                        Log.d("zxcv", dataSnapshot.getValue().toString());


//                                    if(date1.toString().substring(0,4).equals(dataSnapshot.getValue().toString().substring(1,5))
//                                     && date1.toString().substring(5,7).equals(dataSnapshot.getValue().toString().substring(6,8))
//                                    && (Integer.parseInt(date1.toString().substring(8,10))-Integer.parseInt(dataSnapshot.getValue().toString().substring(9,11)))>=1)
//                                    {
//                                        dataSnapshot.getRef().removeValue();
//                                    }
//                                    else if(date1.toString().substring(0,4).equals(dataSnapshot.getValue().toString().substring(1,5))
//                                            && (Integer.parseInt(date1.toString().substring(5,7))-Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8)))>=1
//                                            && (Integer.parseInt(date1.toString().substring(0,4))-Integer.parseInt(dataSnapshot.getValue().toString().substring(9,11)))>=1)
//                                    {
//                                        if(Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8))==1 || Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8))==3 ||
//                                        Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8))==5 || Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8))==7
//                                        ||Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8))==8 || Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8))==10 ||
//                                                Integer.parseInt(dataSnapshot.getValue().toString().substring(6,8))==12)
//                                        {
//                                            if(Integer.parseInt(dataSnapshot.getValue().toString().substring(9,11)==29))
//                                                dataSnapshot.getRef().removeValue();
//                                    }
//
//                                    }


                        try {


                            // get difference between two dates in MINUTES
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dataSnapshot.getValue().toString().substring(
                                    dataSnapshot.getValue().toString().indexOf("h") - 10, dataSnapshot.getValue().toString().indexOf("h")
                            ));

                            long milliSecondsElapsed = date1.getTime() - date.getTime();


                            // Log.d("poiu",date.getTime()+"");
                            // long diff = TimeUnit.MINUTES.convert(milliSecondsElapsed, TimeUnit.MILLISECONDS);
                            if (milliSecondsElapsed / (24 * 60 * 60 * 1000) >= 3) {
                                // Log.d("poiu",diff+"");
                                Log.d("poiu", milliSecondsElapsed / (24 * 60 * 60 * 1000) + "");

                                StorageReference file1;
                                file1 = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot
                                        .getValue().toString().indexOf("h")));
                                file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
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

                FirebaseDatabase.getInstance().getReference().child("groups").child(dataSnapshot.getKey()).child("deletevideos").addChildEventListener(deleteGroupvideos);


//

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

//                for (int i = 0; i < contacts1.size(); i++) {
//                    if (contacts1.get(i).getGroupname() != null) {
//                        if (contacts1.get(i).getGroupname().equals(dataSnapshot.getValue().toString())) {
//                            contacts1.remove(i);
//                            userAdapter.notifyItemRemoved(contacts1.size()-1);
//                        }
//
//                    }
//                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").addChildEventListener(Group);
        num++;
    }

    }



//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        if(num==0)
//        {
//            reference.addListenerForSingleValueEvent(dataCreater);
//        }

   // }

    @Override
    public void onItemSelected(String key) {

        int index = keyid.indexOf(key);

        keyid2=keyid.get(index);

        if(contacts1.get(index).getGroupname() == null)
        {
            flag=true;



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



            intent.putExtra("phone", contacts1.get(index).getPh_number());


        if(!contacts1.get(index).getUrl().equals("null"))
            intent.putExtra("profile",contacts1.get(index).getUrl());
        startActivity(intent);
        }
        else
        {

            flag2=true;
            Intent intent = new Intent(MainActivity.this,MessageActivity2.class);
            intent.putExtra("groupName",contacts1.get(index).getuID());
            intent.putExtra("groupkey",contacts1.get(index).getGroupkey());
            intent.putExtra("messagecount",contacts1.get(index).getMessagenum());

            intent.putExtra("profile",contacts1.get(index).getUrl());
            startActivity(intent);
        }
    }

    @Override
    public void onImageSelected(String key) {

        int index = keyid.indexOf(key);

        if(!contacts1.get(index).getUrl().equals("null")) {
            Intent intent = new Intent(MainActivity.this, ShowImage.class);
            intent.putExtra("source", contacts1.get(index).getUrl());
            intent.putExtra("title", contacts1.get(index).getuID());
            startActivity(intent);
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
    protected void onResume() {
        super.onResume();
        Status("online");

        if(ApplicationClass.RenameGroup!=null)
        {

            contacts1.get(keyid.indexOf(keyid2)).setuID(ApplicationClass.RenameGroup);
            userAdapter.notifyItemChanged(keyid.indexOf(keyid2));
            ApplicationClass.RenameGroup=null;
        }
        if(ApplicationClass.rename!=null) {
            contacts1.get(keyid.indexOf(keyid2)).setuID(ApplicationClass.rename);
            ApplicationClass.rename=null;
            userAdapter.notifyItemChanged(keyid.indexOf(keyid2));
        }



//        if(ApplicationClass.create==1)
//        {
//
//            final android.os.Handler handler= new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i = 0; i < contacts1.size(); i++) {
//                        if (!(contacts1.get(i).getGroupname() == null)) {
//                            (new GroupDp(i)).ProfileListener();
//                        }
//                    }
//                    ApplicationClass.groupkey = null;
//
//                }
//            },1000);
//        }
//
//        else
//            ApplicationClass.members.clear();

    }

//    public class GroupDp
//    {
//        int index;
//        GroupDp(int index)
//        {
//            this.index=index;
//        }
//
//        public void ProfileListener()
//        {
//            FirebaseDatabase.getInstance().getReference().child("groups").child(contacts1.get(index).getGroupkey()).child("profile").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        contacts1.get(index).setUrl(dataSnapshot.getValue().toString());
//                        userAdapter.notifyItemChanged(index);
//                    }
//
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }

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

        String theme=preftheme.getString("theme","red");
        switch (theme) {
            case "orange":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.Orange)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.Orange));

                btnContacts.setBackgroundColor(getResources().getColor(R.color.Orange));
                break;
            case "blue":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));

                break;
            case "bluish":
                toolbar.setBackgroundColor(getResources().getColor(R.color.bluish));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluish)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluish)));

                break;
            case "deepred":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.deepred)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.deepred)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.deepred));

                break;
            case "faintpink":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.faintpink));

                break;
            case "darkblue":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.darkblue));

                break;
            case "green":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));

                break;
            case "lightorange":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightorange));

                break;
            case "lightred":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightred)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightred)));
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightred));

                break;
            case "mustard":
                toolbar.setBackgroundColor(getResources().getColor(R.color.mustard));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.mustard)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.mustard)));

                break;
            case "pink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pink));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pink)));

                break;
            case "pureorange":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pureorange));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.pureorange)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pureorange)));

                break;
            case "purepink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purepink));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purepink)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(
                        getResources().getColor(R.color.purepink)));

                break;
            case "purple":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purple)));

                break;
            default:
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                break;
        }



//        if(keyid.indexOf(keyid2)<=(contacts1.size()-1))
//        {
//            if(contacts1.get(keyid.indexOf(keyid2)).getGroupname()==null) {
//
//                    contacts1.get(keyid.indexOf(keyid2)).setLastmessage(Handler.getLastMessage(contacts1.get(keyid.indexOf(keyid2)).getPh_number()));
//                    contacts1.get(keyid.indexOf(keyid2)).setTime(Handler.getLastMessageTime(contacts1.get(keyid.indexOf(keyid2)).getPh_number()));
//
//            }
//            else {
//                if(keyid.indexOf(keyid2)<=(contacts1.size()-1))
//                    contacts1.get(keyid.indexOf(keyid2)).setLastmessage(Handler.getLastMessageGroup(contacts1.get(keyid.indexOf(keyid2)).getGroupkey()));
//                if(Handler.getGroupMessages(contacts1.get(keyid.indexOf(keyid2)).getGroupname(),0).first.size()>0)
//                    contacts1.get(keyid.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid.indexOf(keyid2)).getGroupkey()));
//                else
//                    contacts1.get(keyid.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid.indexOf(keyid2)).getGroupkey()));
//                }
//            userAdapter.notifyItemChanged(keyid.indexOf(keyid2));
//
//        }

        if(ApplicationClass.messagesent==1)
        {
            ApplicationClass.messagesent=0;
            UserDetailwithUrl userDetailwithUrl;

            userDetailwithUrl=contacts1.get(keyid.indexOf(keyid2));
            String key=keyid.get(keyid.indexOf(keyid2));
            contacts1.remove(keyid.indexOf(keyid2));
            keyid.remove(keyid.indexOf(keyid2));
            userAdapter.notifyDataSetChanged();
            contacts1.add(0,userDetailwithUrl);
            keyid.add(0,key);

            userAdapter.notifyDataSetChanged();
        }

        if(flag==true)
        {
            if(keyid.indexOf(keyid2)<=(contacts1.size()-1))
            {
            flag=false;
            contacts1.get(keyid.indexOf(keyid2)).setMessagenum(2);

                contacts1.get(keyid.indexOf(keyid2)).setLastmessage(Handler.getLastMessage(contacts1.get(keyid.indexOf(keyid2)).getPh_number()));
                contacts1.get(keyid.indexOf(keyid2)).setTime(Handler.getLastMessageTime(contacts1.get(keyid.indexOf(keyid2)).getPh_number()));

                userAdapter.notifyItemChanged(keyid.indexOf(keyid2));
            }

        }
        if(flag2==true)
        {
            if(keyid.indexOf(keyid2)<=(contacts1.size()-1)) {
                flag2 = false;
                if(keyid.indexOf(keyid2)<=(contacts1.size()-1))
                    contacts1.get(keyid.indexOf(keyid2)).setLastmessage(Handler.getLastMessageGroup(contacts1.get(keyid.indexOf(keyid2)).getGroupkey()));
                if(Handler.getGroupMessages(contacts1.get(keyid.indexOf(keyid2)).getGroupname(),0).first.size()>0)
                    contacts1.get(keyid.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid.indexOf(keyid2)).getGroupkey()));
                else
                    contacts1.get(keyid.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid.indexOf(keyid2)).getGroupkey()));

                contacts1.get(keyid.indexOf(keyid2)).setMessagenum(2);
                userAdapter.notifyItemChanged(keyid.indexOf(keyid2));
            }

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
//        if(flag==true) {
//            contacts1.get(l).setMessagenum(2);
//            userAdapter.notifyDataSetChanged();
//        }
//        if(flag2==true)
//        {
//
//            contacts1.get(l).setMessagenum(2);
//            userAdapter.notifyDataSetChanged();
//        }
//
//        Log.d("Destroy","onPause");


    }

    @Override
    protected void onStop() {
        super.onStop();

//        int l=keyid.indexOf(keyid2);
//        if(flag==true)
//        {
//
//            contacts1.get(l).setMessagenum(2);
//            userAdapter.notifyItemChanged(l);
//        }
//        if(flag2==true)
//        {
//            if(l<=(contacts1.size()-1)) {
//
//                contacts1.get(l).setMessagenum(2);
//                userAdapter.notifyItemChanged(l);
//            }
//
//        }

    }
}