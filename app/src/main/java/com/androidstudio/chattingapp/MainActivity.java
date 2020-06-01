package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Button;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

import static com.androidstudio.chattingapp.ApplicationClass.contacts1;
import static com.androidstudio.chattingapp.ApplicationClass.keyid;
import static com.androidstudio.chattingapp.ApplicationClass.keyid1;
import static com.androidstudio.chattingapp.ApplicationClass.keyid2;
import static com.androidstudio.chattingapp.ApplicationClass.num;
import static com.androidstudio.chattingapp.MessageActivity.getPath;

public class MainActivity extends AppCompatActivity implements UserAdapter.itemSelected, PopupMenu.OnMenuItemClickListener
{

    ArrayList<UserDetail> contacts;

    ArrayList<UserDetailwithUrl> contacts2;
    LinearLayout toolbar;

    ArrayList<String> number1;
    SharedPreferences preftheme;
    ImageView iv;
    FirebaseDatabase database1;
    DatabaseReference reference1;
    DatabaseReference UserStatus;
    ChildEventListener chreceiver,gifreceiver,stickerreceiver,pdfreceiver,deletedgroups;
    ValueEventListener dataCreater,deleteimage,deletevideo,deletepdf,Status;
    DBHandler Handler;


    LinearLayoutManager linearLayoutManager;
    ArrayList<MessageModel> chats;
    Intent intent1;
    RecyclerView lv;
    FirebaseDatabase database;
    FloatingActionButton btnContacts;
    String currentUserNumber;
    DatabaseReference reference;

    AdView mAdView;

    SearchView searchView;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ImageView ivOptions;

    ChildEventListener Group;
    ValueEventListener profile,check;
    ChildEventListener childEvent,deleteGroupimages,deleteGroupvideos,deletegrouppdfs;

    int  pos;
    boolean flag=false,flag2=false;

    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(contacts1!=null)
            Log.d("aass",contacts1.size()+"");

        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        ApplicationClass.MainActivityContext=this;
        if(getIntent()!=null) {
            intent1 = getIntent();
            Log.d("asdf", intent1.getType()+"");
            Log.d("asdf", intent1.getStringExtra(Intent.EXTRA_TEXT)+"");
        }






//            if (ApplicationClass.splash == true) {
//
//                llSplash.setVisibility(View.VISIBLE);
//                ApplicationClass.splash = false;
//
//            } else
//                llSplash.setVisibility(View.GONE);

        searchView = findViewById(R.id.SearchView);



            toolbar = findViewById(R.id.toolbar);
            iv=findViewById(R.id.ivOptions);
            btnContacts = findViewById(R.id.btnContacts);
            ivOptions = findViewById(R.id.ivOptions);
            preftheme = getSharedPreferences("theme", 0);

            pref = getApplicationContext().getSharedPreferences("Names", 0);
            String theme = preftheme.getString("theme", "red");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


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

               toolbar.setBackgroundColor(getResources().getColor(R.color.deepred));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.deepred)));

                break;
            case "faintpink":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));

              toolbar.setBackgroundColor(getResources().getColor(R.color.faintpink));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));

                break;
            case "darkblue":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));

                toolbar.setBackgroundColor(getResources().getColor(R.color.darkblue));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));

                break;
            case "green":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));

                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));

                break;
            case "lightorange":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));

                toolbar.setBackgroundColor(getResources().getColor(R.color.lightorange));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));

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
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purepink)));

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
            lv.setAdapter(userAdapter);
            lv.addItemDecoration(new DividerItemDecoration(lv.getContext(),DividerItemDecoration.VERTICAL));

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                if(num==0)
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

        if(contacts1!=null)
            Log.d("aass",contacts1.size()+"");

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

            case R.id.contacts:
                Intent intent1=new Intent(MainActivity.this,FriendsActivity.class);
                startActivity(intent1);
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

                         int i=keyid1.indexOf(key);



                         if (dataSnapshot.getValue(String.class).equals("online") || dataSnapshot.getValue(String.class).substring(0, 6).equals("typing")) {
                             contacts1.get(i).setStatus("online");
                         } else {
                             contacts1.get(i).setStatus("offline");
                         }
                         userAdapter.notifyItemChanged(keyid1.indexOf(key));
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             };

             reference.child("UserStatus").child(contacts1.get(index).getPh_number()).addValueEventListener(Status);
         }

         public void pdflistener()
         {
             pdfreceiver=new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                     UserDetailwithUrl userDetailwithUrl;
                     int j=keyid1.indexOf(key);

                     userDetailwithUrl=contacts1.get(j);
                     contacts1.remove(j);
                     keyid1.remove(j);
                     userAdapter.notifyDataSetChanged();
                     contacts1.add(0,userDetailwithUrl);
                     keyid1.add(0,key);

                     userAdapter.notifyDataSetChanged();
                     int i=contacts1.indexOf(userDetailwithUrl);
                     contacts1.get(i).setLastmessage("     "+" "+contacts1.get(index).getPh_number());
                     contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(5, 15));
                     contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                     userAdapter.notifyItemChanged(keyid1.indexOf(key));
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

             reference.child("users").child(contacts1.get(index).getPh_number()).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("pdf").addChildEventListener(pdfreceiver);

         }

         public void profilelistener()
         {
             profile=new ValueEventListener(){
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if(dataSnapshot.exists()) {
                        // Log.d("abcd",dataSnapshot.getValue().toString());






                         contacts1.get(keyid1.indexOf(key)).setUrl(dataSnapshot.getValue().toString());


//                                                contacts1.add(new UserDetailwithUrl(key, "", dataSnapshot.getValue().toString(), 2
//                                                        , "", "",null,null));
                         //   userAdapter.notifyItemInserted(contacts1.size()-1);


                         userAdapter.notifyItemChanged(keyid1.indexOf(key));


                         //   Log.d("asdf",contacts1.get(contacts1.size()-1).getUrl());
                     }


                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             };
             reference.child("users").child(contacts1.get(index).getPh_number()).child("profile").addValueEventListener(profile);
             // userAdapter.notifyDataSetChanged();

         }

         public void giflistener()
         {
             gifreceiver= new ChildEventListener() {
                 @Override
                 public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                     //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();


                     UserDetailwithUrl userDetailwithUrl;
                     int j=keyid1.indexOf(key);

                     userDetailwithUrl=contacts1.get(j);
                     contacts1.remove(j);
                     keyid1.remove(j);
                     userAdapter.notifyDataSetChanged();
                     contacts1.add(0,userDetailwithUrl);
                     keyid1.add(0,key);

                     userAdapter.notifyDataSetChanged();
                     int i=contacts1.indexOf(userDetailwithUrl);
                     contacts1.get(i).setLastmessage("   "+" "+contacts1.get(index).getPh_number());
                     contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(5, 15));
                     contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                     userAdapter.notifyItemChanged(keyid1.indexOf(key));




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
                    int j=keyid1.indexOf(key);

                    userDetailwithUrl=contacts1.get(j);
                    contacts1.remove(j);
                    keyid1.remove(j);
                    userAdapter.notifyDataSetChanged();
                    contacts1.add(0,userDetailwithUrl);
                    keyid1.add(0,key);

                    userAdapter.notifyDataSetChanged();
                    int i=contacts1.indexOf(userDetailwithUrl);
                     contacts1.get(i).setLastmessage("    "+" "+contacts1.get(index).getPh_number());
                    contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(5, 15));
                    contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                    userAdapter.notifyItemChanged(keyid1.indexOf(key));



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
                     int j=keyid1.indexOf(key);

                     userDetailwithUrl=contacts1.get(j);
                     contacts1.remove(j);
                     keyid1.remove(j);
                     userAdapter.notifyDataSetChanged();
                     contacts1.add(0,userDetailwithUrl);
                     keyid1.add(0,key);

                     userAdapter.notifyDataSetChanged();
                     int i=contacts1.indexOf(userDetailwithUrl);

                         contacts1.get(i).setLastmessage("  "+" "+contacts1.get(index).getPh_number());
                     contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(5, 15));
                         contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                     userAdapter.notifyItemChanged(keyid1.indexOf(key));



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
                             int j=keyid1.indexOf(key);

                             userDetailwithUrl=contacts1.get(j);
                             contacts1.remove(j);
                             keyid1.remove(j);
                             userAdapter.notifyDataSetChanged();
                             contacts1.add(0,userDetailwithUrl);
                             keyid1.add(0,key);

                             userAdapter.notifyDataSetChanged();
                             int i=contacts1.indexOf(userDetailwithUrl);

                             contacts1.get(i).setLastmessage(dataSnapshot.getValue().toString().substring(15)+" "+contacts1.get(index).getPh_number());
                                 contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(5, 15));
                                 contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                             userAdapter.notifyItemChanged(keyid1.indexOf(key));





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
                     int j=keyid1.indexOf(key);

                     userDetailwithUrl=contacts1.get(j);
                     contacts1.remove(j);
                     keyid1.remove(j);
                     userAdapter.notifyDataSetChanged();
                     contacts1.add(0,userDetailwithUrl);
                     keyid1.add(0,key);

                     userAdapter.notifyDataSetChanged();
                     int i=contacts1.indexOf(userDetailwithUrl);
                         contacts1.get(i).setLastmessage(" "+" "+contacts1.get(index).getPh_number());
                     contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(5, 15));
                         contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                     userAdapter.notifyItemChanged(keyid1.indexOf(key));



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
                            int j=keyid1.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid1.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid1.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage("  "+" "+dataSnapshot.getValue().toString().substring(21, 34));
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(11, 21));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid1.indexOf(key));
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
                            int j=keyid1.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid1.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid1.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage("   "+" "+dataSnapshot.getValue().toString().substring(21, 34));
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(11, 21));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid1.indexOf(key));
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
                            int j=keyid1.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid1.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid1.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage("    "+" "+dataSnapshot.getValue().toString().substring(21, 34));
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(11, 21));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid1.indexOf(key));
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
                            int j=keyid1.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid1.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid1.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage(dataSnapshot.getValue().toString().substring(34)+" "+dataSnapshot.getValue().toString().substring(21, 34));
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(11, 21));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid1.indexOf(key));
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
                            int j=keyid1.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid1.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid1.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage(" "+" "+dataSnapshot.getValue().toString().substring(21, 34));
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(11, 21));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid1.indexOf(key));
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

        public void pdflistener()
        {
            reference.child("groups").child(contacts1.get(index).getGroupkey()).child("pdf")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            UserDetailwithUrl userDetailwithUrl;
                            int j=keyid1.indexOf(key);

                            userDetailwithUrl=contacts1.get(j);
                            contacts1.remove(j);
                            keyid1.remove(j);
                            userAdapter.notifyDataSetChanged();
                            contacts1.add(0,userDetailwithUrl);
                            keyid1.add(0,key);

                            userAdapter.notifyDataSetChanged();
                            int i=contacts1.indexOf(userDetailwithUrl);
                            contacts1.get(i).setLastmessage("     "+" "+dataSnapshot.getValue().toString().substring(21, 34));
                            contacts1.get(i).setTime(dataSnapshot.getValue().toString().substring(0, 5)+" "+dataSnapshot.getValue().toString().substring(11, 21));
                            contacts1.get(i).setMessagenum(contacts1.get(i).getMessagenum() + 1);
                            userAdapter.notifyItemChanged(keyid1.indexOf(key));
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
            FirebaseDatabase.getInstance().getReference().child("groups").child(contacts1.get(index).getGroupkey()).addChildEventListener(
                    new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot!=null && dataSnapshot.getKey().equals("profile")) {

                                int i=keyid1.indexOf(key);


                                contacts1.get(i).setUrl(dataSnapshot.getValue().toString());


                                userAdapter.notifyItemChanged(keyid1.indexOf(key));

                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            if ( dataSnapshot.getKey().equals("profile")) {


                                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString());
                                reference.delete();
                            }

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
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


                    deletepdf=new ValueEventListener() {
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    reference.child("users").child(dataSnapshot.getKey()).child(FirebaseAuth.getInstance().getCurrentUser()
                            .getPhoneNumber()).child("info").child("deletepdf").addListenerForSingleValueEvent(deletepdf);

//
//
//                            }

                    if (dataSnapshot.child("info").exists()) {

                        contacts1.add(new UserDetailwithUrl(dataSnapshot.getKey(), pref.getString(dataSnapshot.getKey(), dataSnapshot.getKey()), "null", 2
                                , Handler.getLastMessage(dataSnapshot.getKey()), Handler.getLastMessageTime(dataSnapshot.getKey()), null, null));
                        userAdapter.notifyItemInserted(contacts1.size() - 1);
                        keyid1.add(dataSnapshot.getKey());
                        listener listener=new listener(contacts1.size() - 1, keyid1.get(keyid1.size()-1));
                        listener.profilelistener();
                        listener.statusListener();
                        listener.piclistener();
                        listener.VideoListener();
                        listener.giflistener();
                        listener.stickerlistener();
                        listener.child();
                        listener.pdflistener();


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


                    if (Handler.getGroupMessages(dataSnapshot.getKey(), 0).first.size() > 0) {
                        contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                                , Handler.getLastMessageGroup(dataSnapshot.getKey()),
                                Handler.getLastGroupMessageTime(dataSnapshot.getKey()), dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
                        userAdapter.notifyItemInserted(contacts1.size() - 1);
                        keyid1.add(dataSnapshot.getKey());
                        grouplistener grouplistener = new grouplistener(contacts1.size() - 1, dataSnapshot.getKey());

                        grouplistener.piclistener();
                        grouplistener.VideoListener();
                        grouplistener.child();
                        grouplistener.gifListener();
                        grouplistener.stickerListener();
                        grouplistener.pdflistener();
                        grouplistener.ProfileListener();


                    } else {
                        contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                                , "null",
                                "null", dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
                        userAdapter.notifyItemInserted(contacts1.size() - 1);

                        keyid1.add(dataSnapshot.getKey());
                        grouplistener grouplistener = new grouplistener(contacts1.size() - 1, dataSnapshot.getKey());
                        grouplistener.piclistener();
                        grouplistener.VideoListener();
                        grouplistener.child();
                        grouplistener.gifListener();
                        grouplistener.stickerListener();
                        grouplistener.pdflistener();
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


                deletegrouppdfs=new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                        long millis = System.currentTimeMillis();
                        java.sql.Date date1 = new java.sql.Date(millis);



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

                FirebaseDatabase.getInstance().getReference().child("groups").child(dataSnapshot.getKey()).child("deletepdf").addChildEventListener(deletegrouppdfs);

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
                    contacts1.remove(keyid1.indexOf(dataSnapshot.getKey()));
                userAdapter.notifyItemRemoved(keyid1.indexOf(dataSnapshot.getKey()));

                    MessageModel messageModel = new MessageModel(-347,"null","null","This group has been deleted","grpinfo",9876,"null","null", dataSnapshot.getKey(),"null");
                    Handler.addMessage(messageModel);


                    keyid1.remove(dataSnapshot.getKey());
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

        deletedgroups=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (Handler.getGroupMessages(dataSnapshot.getKey(), 0).first.size() > 0) {
                    contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                            , Handler.getLastMessageGroup(dataSnapshot.getKey()),
                            Handler.getLastGroupMessageTime(dataSnapshot.getKey()), dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
                    userAdapter.notifyItemInserted(contacts1.size() - 1);
                    contacts1.get(contacts1.size()-1).setStatus("delete");
                    keyid1.add(dataSnapshot.getKey());





                } else {
                    contacts1.add(new UserDetailwithUrl(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", 2
                            , "null",
                            "null", dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
                    userAdapter.notifyItemInserted(contacts1.size() - 1);
                    contacts1.get(contacts1.size()-1).setStatus("delete");
                    keyid1.add(dataSnapshot.getKey());

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                if (keyid1.contains(dataSnapshot.getKey())) {
                    final int index = keyid1.indexOf(dataSnapshot.getKey());
                    contacts1.remove(index);


                    userAdapter.notifyItemRemoved(index);


                    keyid1.remove(dataSnapshot.getKey());

                }
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").addChildEventListener(Group);
            reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("deletedgroups").addChildEventListener(deletedgroups);

        }
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
    public void showInternetWarning1()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection")
                .setMessage("Can't Share ! Turn On Your Internet Connection ")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void showInternetWarning()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection")
                .setMessage("Turn on your internet connection to read new messages")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemSelected(String key) {

        int index = keyid1.indexOf(key);

        keyid2=keyid1.get(index);
        boolean b=false;
        if(contacts1.get(index).getMessagenum()>2) {

            if (isConnected()) {

                b=true;
            }
        }
        else {
            if (!isConnected() && intent1 != null && intent1.getAction() != null && intent1.getAction().equals(Intent.ACTION_SEND) && intent1.getType() != null)
                b = false;
            else
                b = true;
        }

        if(b )
        {
            if (intent1 != null && intent1.getAction() != null && intent1.getAction().equals(Intent.ACTION_SEND) && intent1.getType() != null) {
                if (contacts1.get(keyid1.indexOf(key)).getGroupkey() == null) {
                    flag = true;


                    Intent intent = new Intent(this, MessageActivity.class);
                    intent.putExtra("title", contacts1.get(keyid1.indexOf(key)).getPh_number());
                    if ("text/plain".equals(intent1.getType())) {
                        intent.putExtra("type", "text");
                        intent.putExtra("message", intent1.getStringExtra(Intent.EXTRA_TEXT));
                    } else if (intent1.getType().equals("image/*") || intent1.getType().equals("image/jpeg") || intent1.getType().equals("image/png") || intent1.getType().equals("image/jpg") || intent1.getType().equals("image/webp") || intent1.getType().equals("image/gif")) {
                        intent.putExtra("type", "image");
                        Uri imageUri = (Uri) intent1.getParcelableExtra(Intent.EXTRA_STREAM);
                        intent.putExtra("message", imageUri.toString());
                    } else if (intent1.getType().equals("video/mp4") || intent1.getType().equals("video/*") || intent1.getType().equals("video/3gp") || intent1.getType().equals("video/avi")) {
                        intent.putExtra("type", "video");
                        Uri imageUri = (Uri) intent1.getParcelableExtra(Intent.EXTRA_STREAM);
                        intent.putExtra("message", imageUri.toString());
                    } else if (intent1.getType().equals("application/pdf")) {
                        intent.putExtra("type", "pdf");
                        Uri imageUri = (Uri) intent1.getParcelableExtra(Intent.EXTRA_STREAM);
                        intent.putExtra("message", getPath(this, imageUri));
                    }

                    // tvtitle.setText("Forward To");
                    //   ******  To forward a message in messageactivity ****


                    //  intent.putExtra("title","+91"+contacts1.get(index).getPh_number());
                    // Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                    //else
                    //intent.putExtra("title", contacts1.get(index).getuID());

                    intent.putExtra("phone", contacts1.get(keyid1.indexOf(key)).getPh_number());

//                if(contacts1.get(index).getPh_number().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()) ||
//                        ("+91"+contacts1.get(index).getPh_number()).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
//                {
//                    Intent intent1=new Intent(FriendsActivity.this,MessageActivity2.class);
//                    intent1.putExtra("groupKey", contacts1.get(index).getuID());
//
//                    intent1.putExtra("groupkey",contacts1.get(index).getKey());
//                    intent1.putExtra("type", getIntent().getStringExtra("type"));
//                    intent1.putExtra("path", 2);
//                    intent1.putExtra("message", getIntent().getStringExtra("message"));
//                    this.finish();
//                    startActivity(intent1);
//
//                }

                    intent.putExtra("path", 2);

                    intent.putExtra("profile", contacts1.get(keyid1.indexOf(key)).getUrl());
                    intent1 = null;
                    startActivity(intent);


                } else if(contacts1.get(keyid1.indexOf(key)).getStatus()==null) {
                    flag2=true;

                    //   ******  To share a message in messageactivity2 ****
                    Intent intent = new Intent(this, MessageActivity2.class);
                    intent.putExtra("title", contacts1.get(keyid1.indexOf(key)).getPh_number());
                    intent.putExtra("path", 2);
                    intent.putExtra("groupkey", contacts1.get(keyid1.indexOf(key)).getGroupkey());
                    intent.putExtra("groupName", contacts1.get(keyid1.indexOf(key)).getuID());
                    if ("text/plain".equals(intent1.getType())) {
                        intent.putExtra("type", "text");
                        intent.putExtra("message", intent1.getStringExtra(Intent.EXTRA_TEXT));
                    } else if (intent1.getType().equals("image/*") || intent1.getType().equals("image/jpeg") || intent1.getType().equals("image/png") || intent1.getType().equals("image/jpg") || intent1.getType().equals("image/webp") || intent1.getType().equals("image/gif")) {
                        intent.putExtra("type", "image");
                        Uri imageUri = (Uri) intent1.getParcelableExtra(Intent.EXTRA_STREAM);
                        intent.putExtra("message", imageUri.toString());
                    } else if (intent1.getType().equals("video/mp4") || intent1.getType().equals("video/*") || intent1.getType().equals("video/3gp") || intent1.getType().equals("video/avi")) {
                        intent.putExtra("type", "video");
                        Uri imageUri = (Uri) intent1.getParcelableExtra(Intent.EXTRA_STREAM);
                        intent.putExtra("message", imageUri.toString());
                    } else if (intent1.getType().equals("application/pdf")) {
                        intent.putExtra("type", "pdf");
                        Uri imageUri = (Uri) intent1.getParcelableExtra(Intent.EXTRA_STREAM);
                        intent.putExtra("message", getPath(this, imageUri));
                    }

                    intent.putExtra("profile", contacts1.get(index).getUrl());
                    startActivity(intent);
                    intent1 = null;
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Can't share ! This group doesn't exist",Toast.LENGTH_LONG).show();
                }

            } else {

                if (contacts1.get(index).getGroupname() == null) {
                    flag = true;

                    Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                    intent.putExtra("type", " ");
                    intent.putExtra("messagecount", contacts1.get(index).getMessagenum());
                    if (contacts1.get(index).getuID().equals("")) {
                        intent.putExtra("title", contacts1.get(index).getPh_number());

                    } else {
                        intent.putExtra("title", contacts1.get(index).getuID());
                    }


                    intent.putExtra("phone", contacts1.get(index).getPh_number());


                    if (!contacts1.get(index).getUrl().equals("null"))
                        intent.putExtra("profile", contacts1.get(index).getUrl());
                    startActivity(intent);
                } else {

                    flag2 = true;
                    Intent intent = new Intent(MainActivity.this, MessageActivity2.class);
                    intent.putExtra("groupName", contacts1.get(index).getuID());
                    intent.putExtra("groupkey", contacts1.get(index).getGroupkey());
                    intent.putExtra("status", contacts1.get(index).getStatus());

                    intent.putExtra("messagecount", contacts1.get(index).getMessagenum());


                    intent.putExtra("profile", contacts1.get(index).getUrl());
                    startActivity(intent);
                }
            }
        }
        else if (intent1 != null && intent1.getAction() != null && intent1.getAction().equals(Intent.ACTION_SEND) && intent1.getType() != null)
        {
            showInternetWarning1();
        }
            else
                showInternetWarning();

    }

    @Override
    public void onImageSelected(String key) {

        int index = keyid1.indexOf(key);

        if(!contacts1.get(index).getUrl().equals("null")) {
            Intent intent = new Intent(MainActivity.this, ShowImage.class);
            intent.putExtra("source", contacts1.get(index).getUrl());
            intent.putExtra("title", contacts1.get(index).getuID());
            startActivity(intent);
        }
    }

    @Override
    public void onLongclick(final String key) {

        if(contacts1.get(keyid1.indexOf(key)).getStatus()!=null)
        {
            String []choices ={"Remove"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case 0:
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setMessage("Are you sure you want to remove this group");
                            builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    contacts1.remove(keyid1.indexOf(key));
                                    userAdapter.notifyItemRemoved(keyid1.indexOf(key));
                                    keyid1.remove(key);
                                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                            .child("deletedgroups").child(key).getRef().removeValue();


                                }
                            });
                            builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder1.setCancelable(false);
                            builder1.show();
                            break;
                    }


                }
            });
            builder.show();

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
            contacts1.get(keyid1.indexOf(keyid2)).setuID(ApplicationClass.RenameGroup);
            userAdapter.notifyItemChanged(keyid1.indexOf(keyid2));
            ApplicationClass.RenameGroup=null;
        }
        if(ApplicationClass.rename!=null) {
            contacts1.get(keyid1.indexOf(keyid2)).setuID(ApplicationClass.rename);
            ApplicationClass.rename=null;
            userAdapter.notifyItemChanged(keyid1.indexOf(keyid2));
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

                toolbar.setBackgroundColor(getResources().getColor(R.color.deepred));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.deepred)));

                break;
            case "faintpink":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));

                toolbar.setBackgroundColor(getResources().getColor(R.color.faintpink));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.faintpink)));

                break;
            case "darkblue":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));

                toolbar.setBackgroundColor(getResources().getColor(R.color.darkblue));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkblue)));

                break;
            case "green":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));

                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));

                break;
            case "lightorange":
                iv.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));

                toolbar.setBackgroundColor(getResources().getColor(R.color.lightorange));
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightorange)));

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
                btnContacts.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.purepink)));

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


        if(ApplicationClass.keyid!=null && keyid1.contains(keyid)) {
            if(keyid1.contains(ApplicationClass.keyid)) {
                keyid2 = ApplicationClass.keyid;
                if (keyid2.substring(0, 3).equals("+91")) {
                    if (keyid1.indexOf(keyid2) <= (contacts1.size() - 1)) {
                        flag = false;
                        contacts1.get(keyid1.indexOf(keyid2)).setMessagenum(2);

                        contacts1.get(keyid1.indexOf(keyid2)).setLastmessage(Handler.getLastMessage(contacts1.get(keyid1.indexOf(keyid2)).getPh_number()));
                        contacts1.get(keyid1.indexOf(keyid2)).setTime(Handler.getLastMessageTime(contacts1.get(keyid1.indexOf(keyid2)).getPh_number()));

                        userAdapter.notifyItemChanged(keyid1.indexOf(keyid2));
                    }
                } else {
                    if (keyid1.indexOf(keyid2) <= (contacts1.size() - 1)) {
                        flag2 = false;
                        if (keyid1.indexOf(keyid2) <= (contacts1.size() - 1))
                            contacts1.get(keyid1.indexOf(keyid2)).setLastmessage(Handler.getLastMessageGroup(contacts1.get(keyid1.indexOf(keyid2)).getGroupkey()));
                        if (Handler.getGroupMessages(contacts1.get(keyid1.indexOf(keyid2)).getGroupname(), 0).first.size() > 0)
                            contacts1.get(keyid1.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid1.indexOf(keyid2)).getGroupkey()));
                        else
                            contacts1.get(keyid1.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid1.indexOf(keyid2)).getGroupkey()));

                        contacts1.get(keyid1.indexOf(keyid2)).setMessagenum(2);
                        userAdapter.notifyItemChanged(keyid1.indexOf(keyid2));
                    }

                }
            }
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
if(keyid2!=null && keyid1.contains(keyid2)) {
    if (ApplicationClass.messagesent == 1) {
        ApplicationClass.messagesent = 0;
        UserDetailwithUrl userDetailwithUrl;

        userDetailwithUrl = contacts1.get(keyid1.indexOf(keyid2));
        String key = keyid1.get(keyid1.indexOf(keyid2));
        contacts1.remove(keyid1.indexOf(keyid2));
        keyid1.remove(keyid1.indexOf(keyid2));
        userAdapter.notifyDataSetChanged();
        contacts1.add(0, userDetailwithUrl);
        keyid1.add(0, key);

        userAdapter.notifyDataSetChanged();
    }

    if (flag == true) {
        if (keyid1.indexOf(keyid2) <= (contacts1.size() - 1)) {
            flag = false;
            contacts1.get(keyid1.indexOf(keyid2)).setMessagenum(2);

            contacts1.get(keyid1.indexOf(keyid2)).setLastmessage(Handler.getLastMessage(contacts1.get(keyid1.indexOf(keyid2)).getPh_number()));
            contacts1.get(keyid1.indexOf(keyid2)).setTime(Handler.getLastMessageTime(contacts1.get(keyid1.indexOf(keyid2)).getPh_number()));

            userAdapter.notifyItemChanged(keyid1.indexOf(keyid2));
        }

    }
    if (flag2 == true) {
        if (keyid1.indexOf(keyid2) <= (contacts1.size() - 1)) {
            flag2 = false;
            if (keyid1.indexOf(keyid2) <= (contacts1.size() - 1))
                contacts1.get(keyid1.indexOf(keyid2)).setLastmessage(Handler.getLastMessageGroup(contacts1.get(keyid1.indexOf(keyid2)).getGroupkey()));
            if (Handler.getGroupMessages(contacts1.get(keyid1.indexOf(keyid2)).getGroupname(), 0).first.size() > 0)
                contacts1.get(keyid1.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid1.indexOf(keyid2)).getGroupkey()));
            else
                contacts1.get(keyid1.indexOf(keyid2)).setTime(Handler.getLastGroupMessageTime(contacts1.get(keyid1.indexOf(keyid2)).getGroupkey()));

            contacts1.get(keyid1.indexOf(keyid2)).setMessagenum(2);
            userAdapter.notifyItemChanged(keyid1.indexOf(keyid2));
        }

    }
    ApplicationClass.keyid=null;
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


//        if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEND) && getIntent().getType() != null) {
//
//            if ("text/plain".equals(getIntent().getType())) {
//                intent.putExtra("type", "text");
//                intent.putExtra("message", getIntent().getStringArrayExtra(Intent.EXTRA_TEXT));
//            } else if (getIntent().getType().equals("image/*")) {
//                intent.putExtra("type", "image");
//                intent.putExtra("message", getPath(FriendsActivity.this, imageUri));
//            } else if (getIntent().getType().equals("video/mp4")) {
//                intent.putExtra("type", "video");
//                Uri imageUri = (Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
//                intent.putExtra("message", getPath(FriendsActivity.this, imageUri));
//            } else if (getIntent().getType().equals("application/pdf")) {
//                intent.putExtra("type", "pdf");
//                Uri imageUri = (Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
//                intent.putExtra("message", getPath(FriendsActivity.this, imageUri));
//            }
//        }
    }