package com.androidstudio.chattingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.emoji.widget.EmojiEditText;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.ImageSelected {

    EmojiEditText etMessage;
    ImageView ivSend,ivProfile,ivBack,ivStatus;
    String RecieverPhone;
    FirebaseDatabase database;
    DatabaseReference reference;
    String lastpath;
    SharedPreferences pref,wallpaper;
    SharedPreferences preftheme;

    StorageReference rf;
    int messagecount;
    ConstraintLayout llMessageActivity;
    LinearLayout ll;
    SharedPreferences pref1;

    TextView title,tvMode;
    String to = "";
    RecyclerView Messages;
    String sender;
    LinearLayoutManager manager;
    MessageAdapter adapter;
    ArrayList<MessageModel> chats;
    ChildEventListener chreceiver,videoreceiver;

    DBHandler Handler;
    int l;
    int flag=0;
    ChildEventListener imagereceiver;
    ValueEventListener Status;

    OnCompleteListener SendMesage;
    String defaultvalue;

    boolean flag1 = false;
    String dpUrl ="null";
    ValueEventListener dp;

    RecyclerView.AdapterDataObserver observer;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ll=findViewById(R.id.ll);
        ivSend = findViewById(R.id.ivSend);
        preftheme=getSharedPreferences("theme",0);

        pref1 = getSharedPreferences("Names",0);
        wallpaper = getSharedPreferences("Wallpaper",0);
        String theme=preftheme.getString("theme","red");

                if(theme.equals("orange"))
        {
                ll.setBackgroundColor(getResources().getColor(R.color.Orange));
                }

                else if(theme.equals("blue"))
        {
                ll.setBackgroundColor(getResources().getColor(R.color.blue));
                }


            else if(theme.equals("bluish")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.bluish));

                }

             else if(theme.equals("deepred")) {
                 ll.setBackgroundColor(getResources().getColor(R.color.deepred));

             }

             else if(theme.equals("faintpink")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.faintpink));

                }

             else if(theme.equals("darkblue")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.darkblue));

                }

             else if (theme.equals("green")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.green));

                }

             else if (theme.equals("lightorange")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.lightorange));

                }

             else  if (theme.equals("lightred")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.lightred));

                }

            else if(theme.equals( "mustard")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.mustard));

                }

             else if (theme.equals("pink")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.pink));

                }

             else if(theme.equals("pureorange")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.pureorange));

                }

            else if(theme.equals( "purepink")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.purepink));

                }

            else if(theme.equals( "purple")) {
                    ll.setBackgroundColor(getResources().getColor(R.color.purple));

                }

            else {
                ll.setBackgroundColor(getResources().getColor(R.color.red));


        }

        ApplicationClass.MessageActivityContext = MessageActivity.this;

        dp = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null)
                    dpUrl = dataSnapshot.getValue(String.class);
                else
                    dpUrl = "null";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        rf = FirebaseStorage.getInstance().getReference("docs/");
        lastpath = "";

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        final int[] index = new int[1];

        title = findViewById(R.id.title);
        Handler = new DBHandler(MessageActivity.this);
        Handler.Open();
        messagecount=getIntent().getIntExtra("messagecount",2);

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        title.setText(pref1.getString(getIntent().getStringExtra("title"),getIntent().getStringExtra("title")));

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecieverPhone = getIntent().getStringExtra("phone");
        sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        chats = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users").child(sender).child("profile").addListenerForSingleValueEvent(dp);


        pref= getApplicationContext().getSharedPreferences("Mode",0);
        defaultvalue = pref.getString("mode"+RecieverPhone,"null");
        etMessage = findViewById(R.id.etMessage);

        ivProfile = findViewById(R.id.ivProfile);
        ivBack = findViewById(R.id.ivBack);
        ivStatus = findViewById(R.id.ivStatus);
        tvMode = findViewById(R.id.tvMode);
        llMessageActivity = findViewById(R.id.llMessageActivity);


        tvMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this,Mode.class);
                intent.putExtra("number",RecieverPhone);
                startActivity(intent);
            }
        });


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity.this.finish();
            }
        });


    if (getIntent().getStringExtra("profile") != null && !getIntent().getStringExtra("profile").equals("null") ) {

        ApplicationClass.url = getIntent().getStringExtra("profile");
        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("profile")).into(ivProfile);
    }
    else if(getIntent().getStringExtra("profile")==null)
    {
        ApplicationClass.url = "null";
        ivProfile.setImageResource(R.drawable.person);
    }
    else {
        ApplicationClass.url = "null";
        ivProfile.setImageResource(R.drawable.person);
    }



        Status = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long millis = System.currentTimeMillis();
                java.sql.Date date1 = new java.sql.Date(millis);
                if(dataSnapshot.getValue(String.class).substring(0,6).equals("typing") && dataSnapshot.getValue(String.class).substring(7).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                    ivStatus.setBackgroundResource(R.drawable.orange);

                    if(!flag1) {
                        chats.add(new MessageModel(-678, "null  ", "null  ", "jgvjhv", "typing", 45, "null  ", date1.toString(),"null"));
                        if (!Messages.isComputingLayout()) {
                            adapter.notifyItemInserted(chats.size() - 1);
                        }
                        flag1=true;
                    }
                }

                else if(dataSnapshot.getValue().equals("online") || (dataSnapshot.getValue(String.class).substring(0,6).equals("typing") && !dataSnapshot.getValue(String.class).substring(7).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
                    ivStatus.setBackgroundResource(R.drawable.orange);

                    if(chats.size()!=0) {

                        if (chats.get(chats.size() - 1).getType().equals("typing")) {
                            chats.remove(chats.size() - 1);
                            if (!Messages.isComputingLayout())
                                adapter.notifyDataSetChanged();

                            flag1 = false;
                        }
                    }

                }

                else {
                    ivStatus.setBackground(null);

                    if(chats.size()!=0) {

                        if (chats.get(chats.size() - 1).getType().equals("typing")) {
                            chats.remove(chats.size() - 1);
                            if (!Messages.isComputingLayout())
                                adapter.notifyDataSetChanged();

                            flag1 = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference("UserStatus").child(RecieverPhone).addValueEventListener(Status);

        etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                String [] choices = {"Image","Video"};

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etMessage.getRight() - etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                        builder.setTitle("Send...")
                                .setItems(choices, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i){
                                            case 0:
                                                if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
                                                } else{
                                                    Intent intent = new Intent();
                                                    intent.setType("image/*");
                                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), 10);
                                                }
                                                break;
                                            case 1:
                                                if (Build.VERSION.SDK_INT <19){
                                                    Intent intent = new Intent();
                                                    intent.setType("video/mp4");
                                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                                    startActivityForResult(Intent.createChooser(intent, "Select videos"),100);
                                                } else {
                                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                    intent.setType("video/mp4");
                                                    startActivityForResult(intent, 100);
                                                }
                                                break;
                                        }
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                return false;
            }
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (etMessage.getText().toString().trim().length() > 0) {

                    if (!(flag == 1)) {
                        FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue("typing " + RecieverPhone);
                        flag = 1;
                    }

                } else {
                    flag = 0;
                    FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue("online");
                }

            }
        });

//******************************************************************************************************************************************************

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
                reference.child("users").child(RecieverPhone).child(sender).child("info").child("friend").setValue("yes");


                if (etMessage.getText().toString().trim().isEmpty())
                    Toast.makeText(MessageActivity.this, "Please enter a message", Toast.LENGTH_LONG).show();
                else {

                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    long millis = System.currentTimeMillis();
                    java.sql.Date date1 = new java.sql.Date(millis);

                    MessageModel model = new MessageModel(-1, sender, RecieverPhone, etMessage.getText().toString(), "text", -2, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null");
                    etMessage.setText(null);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    }
                    else
                    {
                        if((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    }
                    if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing"))
                    {
                        int id = Handler.addMessage(model);
                        model.setId(id);
                        if(flag1==true) {

                            chats.add(chats.size() - 1, model);
                            adapter.notifyItemInserted(chats.size() - 1);
                            sendFCMPush(model.getMessage());
                        }
                    }
                    else{

                    int id = Handler.addMessage(model);
                    model.setId(id);
                    chats.add(model);
                    adapter.notifyItemInserted(chats.size() - 1);}
                    sendFCMPush(model.getMessage());
                }
            }
        });

//*******************************************************************************************************************************************************

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(false);
        Messages.setLayoutManager(manager);

        for (int i = 0; i < chats.size(); i++) {
            Log.d("messageme", chats.get(i).getMessage()+"");
        }

        if(!wallpaper.getString("value","null").equals("null"))
        {
            if(getBackground(Uri.parse(wallpaper.getString("value","null")))!=null)
                getWindow().setBackgroundDrawable(getBackground(Uri.parse(wallpaper.getString("value","null"))));
        }

        //chats.add(new MessageModel(RecieverPhone,sender,"https://images.unsplash.com/photo-1579256308218-d162fd41c801?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjF9&auto=format&fit=crop&w=500&q=60","image",0));

        adapter = new MessageAdapter(MessageActivity.this, chats);



        if((defaultvalue.equals("null"))){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("mode"+RecieverPhone, "public");
            editor.apply();
        }

        Log.d("mode",defaultvalue);

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

//                Messages.smoothScrollToPosition(chats.size()-1);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);

                Messages.smoothScrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                Log.d("position",chats.size()-1+"");

                Messages.scrollToPosition(chats.size()-1);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        };

        adapter.registerAdapterDataObserver(observer);

        Messages.setAdapter(adapter);

        if(!defaultvalue.equals("private"))
            chats.addAll(Handler.getMessages(RecieverPhone));

        if(chats.size()!=0)
            adapter.notifyItemInserted(chats.size()-1);

        // to forward a message

if(getIntent().getIntExtra("path",1)==2) {
    String type = getIntent().getStringExtra("type");
    String message1 = getIntent().getStringExtra("message");

    if (!(type.equals(" "))) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

        if (type.equals("text")) {
            reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
            reference.child("users").child(RecieverPhone).child(sender).child("info").child("friend").setValue("yes");


            MessageModel model = new MessageModel(-1, sender, RecieverPhone, message1, "text", -2, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null");
            etMessage.setText(null);
            sendFCMPush(model.getMessage());

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            } else {
                if ((!(defaultvalue.equals("private")))) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            }

            int id = Handler.addMessage(model);
            model.setId(id);
            chats.add(model);
            adapter.notifyItemInserted(chats.size() - 1);


        } else if (type.equals("image")) {

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, message1, "image", 2, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null");

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            } else {
                if (!(defaultvalue.equals("private"))) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            adapter.notifyItemInserted(chats.size() - 1);
        } else {
            MessageModel model = new MessageModel(1190, sender, RecieverPhone, message1, "video", 100, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null");

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            } else {
                if ((!(defaultvalue.equals("private")))) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            }

            int id = Handler.addMessage(model);
            model.setId(id);
            chats.add(model);

            adapter.notifyItemInserted(chats.size() - 1);
        }
    }
}



//**************************************************************************************************************************************************************************

        imagereceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;
                MediaPlayer received = MediaPlayer.create(MessageActivity.this, R.raw.received);

                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);

                Log.d("Received","Image");


                MessageModel messageModel = new MessageModel(-1, RecieverPhone, sender, dataSnapshot.getValue(String.class).substring(15), "image", 0,time,date,"null");
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if(!(defaultvalue.equals("private")))
                    {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }

                if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {


                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    dataSnapshot.getRef().removeValue();

                    chats.add(chats.size()-1,messageModel);

//                adapter.notifyDataSetChanged();
                    adapter.notifyItemInserted(chats.size() - 1);
                }
                else
                {
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);



                        dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);

//                adapter.notifyDataSetChanged();
                    adapter.notifyItemInserted(chats.size() - 1);
                }
                if(messagecount==2)
                    received.start();
                else messagecount--;
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

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").addChildEventListener(imagereceiver);

        videoreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;
                String uri;

                MediaPlayer received = MediaPlayer.create(MessageActivity.this, R.raw.received);

                Toast.makeText(MessageActivity.this, "Video", Toast.LENGTH_SHORT).show();

                Log.d("Received","Video");

                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);
                uri=dataSnapshot.getValue(String.class).substring(15);

                MessageModel messageModel = new MessageModel(-1,RecieverPhone,sender,uri,"video",101,time,date,"null");
                Log.d("video",messageModel.getMessage());

                Toast.makeText(getApplicationContext(),"galbaat",Toast.LENGTH_LONG).show();

                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if (!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {

                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    if(flag1==true) {

                        dataSnapshot.getRef().removeValue();

                    chats.add(chats.size()-1,messageModel);}
                }
                else
                {
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);
                }

//                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(chats.size()-1);
                if(messagecount==2)
                    received.start();
                else messagecount--;
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

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("videos").addChildEventListener(videoreceiver);


//********************************************************************************************************************************************************

        chreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                String time, date;

                MediaPlayer received = MediaPlayer.create(MessageActivity.this, R.raw.received);

                if (!(dataSnapshot.getKey().equals("message"))) {
                    if (dataSnapshot.getKey().equals("info")) {
                        if (!(dataSnapshot.child("friend").exists())) {
                            reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
                        }
                    } else if (!(dataSnapshot.getKey().equals("info"))) {

                        time=dataSnapshot.getValue().toString().substring(0,5);
                        date=dataSnapshot.getValue().toString().substring(5,15);

                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");

                        MessageModel messageModel = new MessageModel(435, RecieverPhone, sender, dataSnapshot.getValue().toString().substring(15), "text", -1,time,date,"null");

                        if(chats.size()!=0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                                MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null");
                                int id = Handler.addMessage(message);
                                message.setId(id);
                                chats.add(message);
                            }
                        }
                        else {
                            if(!(defaultvalue.equals("private")))
                            {
                                MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null");
                                int id = Handler.addMessage(message);
                                message.setId(id);
                                chats.add(message);
                            }
                        }

                        if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            if(flag1==true) {
                                chats.add(chats.size()-1,messageModel);
                                dataSnapshot.getRef().removeValue();
                            }
                        }
                        else{
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);

                            chats.add(messageModel);
                            dataSnapshot.getRef().removeValue();
                        }


                        adapter.notifyItemInserted(chats.size()-1);
                        // adapter.notifyItemRangeInserted(chats.size()-1,1);
                        if(messagecount==2)
                            received.start();
                        else messagecount--;
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

        reference.child("users").child(RecieverPhone).child(sender).addChildEventListener(chreceiver);
        ItemTouchHelper itemTouchHelper= new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(Messages);

        if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 150);
        }

    }

    ItemTouchHelper.SimpleCallback simpleCallback= new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int pos = viewHolder.getAdapterPosition();
            MessageModel model = chats.get(pos);


            if(pos!=0) {
                if (pos < chats.size() - 1) {
                    if (chats.get(pos - 1).getSender().equals("null") && chats.get(pos + 1).getSender().equals("null")) {
                        chats.remove(model);
                        Handler.DeleteMessage(model);
                        model = chats.get(pos - 1);
                        chats.remove(model);
                        Handler.DeleteMessage(model);
                    } else {
                        chats.remove(model);
                        Handler.DeleteMessage(model);
                    }
                } else {
                    if (chats.get(pos - 1).getSender().equals("null")) {
                        chats.remove(model);
                        Handler.DeleteMessage(model);
                        model = chats.get(pos - 1);
                        chats.remove(model);
                        Handler.DeleteMessage(model);
                    } else {
                        chats.remove(model);
                        Handler.DeleteMessage(model);
                    }

                }
            }
            else {
                chats.remove(model);
                Handler.DeleteMessage(model);
            }

            adapter.notifyDataSetChanged();

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            int swipeFlags;
            if(!(chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 103 ||chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 3 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 60)){

                if(chats.get(viewHolder.getAdapterPosition()).getSender().equals(RecieverPhone)) {
                    swipeFlags = ItemTouchHelper.END;
                }
                else
                {
                    swipeFlags = ItemTouchHelper.START;
                }
            }
            else
            {
                swipeFlags =0;
            }

            return makeMovementFlags(0, swipeFlags);
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("activity", 0);

        super.onSaveInstanceState(outState);
    }

//*******************************************************************************************************************************************************

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.child("users").child(RecieverPhone).child(sender).removeEventListener(chreceiver);
        //reference.child("users").child(sender).removeEventListener(chsender);
        chats.clear();

        adapter.unregisterAdapterDataObserver(observer);

        //Handler.close();

        FirebaseDatabase.getInstance().getReference("UserStatus").child(RecieverPhone).removeEventListener(Status);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").removeEventListener(imagereceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("videos").removeEventListener(videoreceiver);
        FirebaseDatabase.getInstance().getReference("users").child(sender).child("profile").removeEventListener(dp);

        Handler.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                MessageActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//*****************************************************************************************************************************************************

    private void sendFCMPush(String message) {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("tag", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        reference.child("tokens").child(sender).setValue(token);

                        // Log and toast
                        // String msg = getString(R.string.msg_token_fmt, token);
                        // Log.d("tag", msg);
                        // Toast.makeText(MessageActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        final String Legacy_SERVER_KEY = "AIzaSyBdu42ejssWEllOGpOlDYiEnlZRkWD1rgI";
        String msg = etMessage.getText().toString();
        String title = sender;
        String token = to;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", message);
            objData.put("title", title);
            objData.put("sound", R.raw.notificationsound);
            objData.put("icon", R.drawable.icon); //   icon_name image must be there in drawable
            objData.put("tag", token);
            objData.put("priority", "high");

            Log.d("dp1",dpUrl+" back ");

            dataobjData = new JSONObject();
            dataobjData.put("text", message);
            dataobjData.put("title", title);
            dataobjData.put("dp",dpUrl);

            obj.put("to", token);
            //obj.put("priority", "high");

//            obj.put("notification", objData);
            obj.put("data", dataobjData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

//*****************************************************************************************************************************************************


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==5)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Permission Required")
                        .setMessage("Permission to write External storage is required")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},5);
                            }
                        })
                        .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
            }
            else
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 10);
            }
        }

        if(requestCode==150)
        {
            if(grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Permission Required")
                        .setMessage("Permission is required to receive and send media")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},150);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.child("tokens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                to = dataSnapshot.child(RecieverPhone).getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class getMessages extends AsyncTask<Void,Void,String>
    {
        @Override
        protected String doInBackground(Void... voids) {

            SharedPreferences pref= getApplicationContext().getSharedPreferences("Mode",0);
            defaultvalue = pref.getString("mode"+RecieverPhone,"null");
            Log.d("mode",defaultvalue);

            if(defaultvalue.equals("private"))
            {
//                tvMode.setText("Private");
                if(chats.size()!=0)
                {
                    chats.clear();
                }
                return "Private";
            }
            else
            {
//                tvMode.setText("Public");
                if(chats.size()!=0) {
                    chats.clear();
                }
                chats.addAll(Handler.getMessages(RecieverPhone));

                return "Public";
            }

        }

        @Override
        protected void onPostExecute(String Status) {
            super.onPostExecute(Status);

            tvMode.setText(Status);

            if(!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();

            if(flag1) {
                chats.add(new MessageModel(-678, "null  ", "null  ", "jgvjhv", "typing", 45, "null  ", "null","null"));
                if(!Messages.isComputingLayout())
                    adapter.notifyItemInserted(chats.size()-1);
            }

        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onResume() {
        super.onResume();

        new getMessages().execute();

    }

    @Override
    protected void onPause() {
        super.onPause();

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").removeEventListener(imagereceiver);
        reference.child("users").child(RecieverPhone).child(sender).removeEventListener(chreceiver);
        //reference.child("users").child(sender).removeEventListener(chsender);

        //Handler.close();



        reference.child("users").child(RecieverPhone).child(sender).child("info").child("videos").removeEventListener(videoreceiver);

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").addChildEventListener(imagereceiver);
        reference.child("users").child(RecieverPhone).child(sender).addChildEventListener(chreceiver);
        //reference.child("users").child(sender).removeEventListener(chsender);

        //Handler.close();



        reference.child("users").child(RecieverPhone).child(sender).child("info").child("videos").addChildEventListener(videoreceiver);


    }

    //*****************************************************************************************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK)
        {
            if(data.getClipData()!=null)
            {
                if(data.getClipData().getItemCount()<=20) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        ClipData.Item imageItem = data.getClipData().getItemAt(i);
                        Uri uri = imageItem.getUri();

                        new CompressImage().execute(uri);
                    }
                }
                else
                    Toast.makeText(this, "You Cannot send more than 15 images at a time", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Uri uri = data.getData();
                new CompressImage().execute(uri);
            }

        }

        if(requestCode==100)
        {
            if(resultCode==RESULT_OK)
            {

                if(data.getClipData() != null) {

                    if(data.getClipData().getItemCount()<=5) {

                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            ClipData.Item videoItem = data.getClipData().getItemAt(i);
                            Uri videoURI = videoItem.getUri();
                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Sent");
                if(!imagesFolder.exists())
                {
                    imagesFolder.mkdirs();
                }

                         //    Create a file to save the image
                File file = new File(imagesFolder, new Timestamp(System.currentTimeMillis())+".mp4");

                try {
                    InputStream in = getContentResolver().openInputStream(videoURI);
                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                            long fileSizeInBytes = file.length();
                            long fileSizeInKB = fileSizeInBytes / 1024;
                            long fileSizeInMB = fileSizeInKB / 1024;
                            Toast.makeText(this,""+fileSizeInBytes,Toast.LENGTH_LONG).show();


                            if (fileSizeInMB >= 15) {
                                Toast.makeText(this,"Video files lesser than 15MB are allowed",Toast.LENGTH_LONG).show();

                            }

                            else{
                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                            long millis = System.currentTimeMillis();
                            java.sql.Date date1 = new java.sql.Date(millis);

                            MessageModel model = new MessageModel(1190, sender, RecieverPhone, videoURI.toString(), "video", 100, simpleDateFormat.format(date).substring(0, 5), date1.toString(), "null");

                            if (chats.size() != 0) {
                                if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                                    int id = Handler.addMessage(messageModel);
                                    messageModel.setId(id);
                                    chats.add(messageModel);
                                }
                            } else {
                                if ((!(defaultvalue.equals("private")))) {
                                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null");
                                    int id = Handler.addMessage(messageModel);
                                    messageModel.setId(id);
                                    chats.add(messageModel);
                                }
                            }

                            int id = Handler.addMessage(model);
                            model.setId(id);
                            if (chats.size() > 0 && chats.get(chats.size() - 1).getType().equals("typing")) {
                                if (flag1 == true)
                                    chats.add(chats.size() - 1, model);
                            } else
                                chats.add(model);

                                adapter.notifyItemInserted(chats.size()-1);


                          }
                   }
                    }
                    else
                    {
                        Toast.makeText(this, "You cannot send more than 5 videos at a time", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Uri videoURI = data.getData();


                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Sent");
                    if(!imagesFolder.exists())
                    {
                        imagesFolder.mkdirs();
                    }

                    //    Create a file to save the image
                    File file = new File(imagesFolder, new Timestamp(System.currentTimeMillis())+".mp4");

                    try {
                        InputStream in = getContentResolver().openInputStream(videoURI);
                        OutputStream out = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.close();
                        in.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    long fileSizeInBytes = file.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;

                    if (fileSizeInMB >= 15) {
                        Toast.makeText(this,"Video files lesser than 15MB are allowed",Toast.LENGTH_LONG).show();

                    }

                    else
                    {
                    Date date=new Date();
                    SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

                    long millis=System.currentTimeMillis();
                    java.sql.Date date1=new java.sql.Date(millis);

                    MessageModel model = new MessageModel(1190,sender,RecieverPhone,videoURI.toString(),"video",100,simpleDateFormat.format(date).substring(0,5),date1.toString(),"null");

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    } else {
                        if ((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null");
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    }

                    int id = Handler.addMessage(model);
                    model.setId(id);
                    if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {
                        if(flag1==true)
                            chats.add(chats.size() - 1, model);
                    }
                    else
                        chats.add(model);

                        adapter.notifyItemInserted(chats.size()-1);


                    }}



//                File file = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Sent/"+new Timestamp(System.currentTimeMillis())+".mp4");
//
//                try {
//                    InputStream in = getContentResolver().openInputStream(selectedImageUri);
//                    OutputStream out = new FileOutputStream(file);
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = in.read(buf)) > 0) {
//                        out.write(buf, 0, len);
//                    }
//                    out.close();
//                    in.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                selectedImageUri = Uri.fromFile(file);



//                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Sent");
//                if(!imagesFolder.exists())
//                {
//                    imagesFolder.mkdirs();
//                }

                // Create a file to save the image
//                File file = new File(imagesFolder, new Timestamp(System.currentTimeMillis())+".mp4");

//                try {
//                    InputStream in = getContentResolver().openInputStream(selectedImageUri);
//                    OutputStream out = new FileOutputStream(file);
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = in.read(buf)) > 0) {
//                        out.write(buf, 0, len);
//                    }
//                    out.close();
//                    in.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                //selectedImageUri = Uri.fromFile(new File(filepath));
            }
        }
    }private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public class CompressImage extends AsyncTask<Uri,Void,Uri>
    {
        @Override
        protected Uri doInBackground(Uri... uris) {
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(MessageActivity.this.getContentResolver(),uris[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes=null;
            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            bytes=stream.toByteArray();
            Bitmap bitmap1= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            String path = MediaStore.Images.Media.insertImage(MessageActivity.this.getContentResolver(), bitmap1, "Title", null);
            Uri uri=Uri.parse(path);



            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Sent");
            if(!imagesFolder.exists())
            {
                imagesFolder.mkdirs();
            }

            // Create a file to save the image
            File file = new File(imagesFolder, new Timestamp(System.currentTimeMillis())+".jpg");

            try {
                InputStream in = getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            uri = Uri.fromFile(file);
            return uri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            Date date=new Date();
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

            long millis=System.currentTimeMillis();
            java.sql.Date date1=new java.sql.Date(millis);

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,5),date1.toString(),"null");

            if(chats.size()!=0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }
            else {
                if(!(defaultvalue.equals("private")))
                {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }}


            if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                if(flag1==true) {

                    chats.add(chats.size() - 1, messageModel);

                    adapter.notifyItemInserted(chats.size() - 1);
                }
            }
            else
            {
                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                chats.add(messageModel);

                adapter.notifyItemInserted(chats.size() - 1);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void uploadVideo(final int index, final MessageModel message)
    {
        message.setDownloaded(103);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(103);

        if(!Messages.isComputingLayout())
            adapter.notifyDataSetChanged();

        rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("videos").push().setValue(message.getTime()+message.getDate()+uri.toString());
                                Toast.makeText(getApplicationContext(),"Ghaint",Toast.LENGTH_LONG).show();

                                message.setDownloaded(102);
                                Handler.UpdateMessage(message);

                                sendFCMPush("Video");

                                if(!MessageActivity.this.isDestroyed())
                                {
                                    chats.get(index).setDownloaded(102);

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                if(MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                                    Intent intent = getIntent();
                                    ((Activity) ApplicationClass.MessageActivityContext).finish();
                                    startActivity(intent);


                                    overridePendingTransition(0, 0);
                                }
                            }
                        });

            }
        });
    }

    public void UploadImage(final int index, final MessageModel message)
    {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sharp);

        message.setDownloaded(3);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(3);

        if(!Messages.isComputingLayout())
            adapter.notifyDataSetChanged();

        rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("images").push().setValue(message.getTime()+message.getDate()+uri.toString());

                                message.setDownloaded(1);
                                Handler.UpdateMessage(message);

                                sendFCMPush("Image");

                                if(!MessageActivity.this.isDestroyed())
                                {
                                    chats.get(index).setDownloaded(1);
                                    mp.start();

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                if(MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                                    Intent intent = getIntent();
                                    ((Activity) ApplicationClass.MessageActivityContext).finish();
                                    startActivity(intent);

                                    mp.start();

                                    overridePendingTransition(0, 0);
                                }
                            }
                        });

            }
        });
    }
    //***********************************************************************************************************************************************
    @Override
    public void showImage(int index) {

        if(chats.get(index).getDownloaded()!=0 && chats.get(index).getDownloaded()!=4 && !chats.get(index).getType().equals("video")) {
            Log.d("URIURI",index +" "+ chats.get(index).getMessage());

            Intent intent = new Intent(MessageActivity.this, ShowImage.class);

            intent.putExtra("source", chats.get(index).getMessage());

            startActivity(intent);
        }
    }

    @Override
    public void downloadImage(int index)
    {

        new DownloadTask(index,chats.get(index)).execute(stringToURL(chats.get(index).getMessage()));
    }

    @Override
    public void sentTextMessage(final int index) {
        SendMessage(index,chats.get(index));
    }

    @Override
    public void sendImage(int index) {
        UploadImage(index,chats.get(index));
    }

    @Override
    public void SendVideo(int index) {
        uploadVideo(index,chats.get(index));
    }

    @Override
    public void Downloadvideo(int index) {
        //new DownloadVideo(index,chats.get(index)).execute(chats.get(index).getMessage());

        new DownloadVideo(index,chats.get(index)).execute(chats.get(index).getMessage());
    }

    @Override
    public void showVideo(int index) {
        Intent intent= new Intent(MessageActivity.this,VideoActivity.class);
        intent.putExtra("uri",chats.get(index).getMessage());
        startActivity(intent);
    }

    @SuppressLint("CheckResult")
    @Override
    public void Onlongclick(final int index) {

        Log.d("LONGCLICK",index+"");
        Log.d("LONGCLICK",chats.get(index).getMessage()+"");
        Log.d("LONGCLICK",chats.get(index).getDownloaded()+"");

        if (!chats.get(index).getMessage().equals("null") && chats.get(index).getDownloaded()!=0 && chats.get(index).getDownloaded()!=4) {

            Log.d("LONGCLICK","INSIDE");

            if (!(chats.get(index).getType().equals("video") || chats.get(index).getType().equals("image"))) {
                String[] choices = {"Copy", "Forward"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);

                builder.setTitle("Choose")
                        .setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("copy", chats.get(index).getMessage());
                                        clipboard.setPrimaryClip(clip);
                                        break;
                                    case 1:
                                        Intent intent = new Intent(MessageActivity.this, FriendsActivity.class);
                                        intent.putExtra("type", chats.get(index).getType());
                                        intent.putExtra("path", 1);
                                        intent.putExtra("message", chats.get(index).getMessage());

                                        startActivity(intent);
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {



                String[] choices = {"Forward"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);

                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(MessageActivity.this, FriendsActivity.class);
                                intent.putExtra("type", chats.get(index).getType());
                                intent.putExtra("path", 1);
                                intent.putExtra("message", chats.get(index).getMessage());

                                startActivity(intent);
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        }
    }

    @Override
    public void OnFileDeleted(int index) {

        chats.get(index).setMessage("null");

        Handler.UpdateMessage(chats.get(index));

        if(!Messages.isComputingLayout())
            adapter.notifyDataSetChanged();

    }

    //***********************************************************************************************************************************************
    @SuppressLint("StaticFieldLeak")
    private class DownloadVideo extends AsyncTask<String, Void, Uri>
    {
        int index;
        MessageModel message;

        DownloadVideo(int position,MessageModel message)
        {
            index = position;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            message.setDownloaded(104);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(104);
            if(!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();

        }

        @Override
        protected Uri doInBackground(String... strings) {
            InputStream urlInputStream = null;

            URLConnection urlConnection;

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received/"+System.currentTimeMillis()+".mp4");

            try{
                //Form a new URL
                URL finalUrl = new URL(strings[0]);

                urlConnection = finalUrl.openConnection();

                //Get the size of the (file) inputstream from server..
                int contentLength = urlConnection.getContentLength();

                DataInputStream stream = new DataInputStream(finalUrl.openStream());

                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                if (buffer.length > 0) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        Log.d("5FILE", "Writing from buffer to the new file..");
                        fos.write(buffer);
                        fos.flush();
                        fos.close();
                        StorageReference file1;
                        file1=FirebaseStorage.getInstance().getReferenceFromUrl(message.getMessage());
                        file1.delete();

                        return Uri.fromFile(file);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        /*Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();*/
                    }
                } else {
                    //Could not download the file...
                    Log.e("8ERROR", "Buffer size is zero ! & returning 'false'.......");

                }
            }catch (FileNotFoundException e){

                return null;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            Log.d("videoDownload","PostExecute");

            if (uri != null) {

                message.setDownloaded(102);
                message.setMessage(uri.toString());

                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(102);
                    chats.get(index).setMessage(uri.toString());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyDataSetChanged();
                    }
                }

                if (MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    Intent intent = getIntent();
                    ((Activity) ApplicationClass.MessageActivityContext).finish();
                    startActivity(intent);

                    overridePendingTransition(0, 0);
                }

            }
            else
            {
                message.setDownloaded(101);
                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(101);
                    adapter.notifyDataSetChanged();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Could not download Video");
                builder.setMessage("Please ask "+pref1.getString(getIntent().getStringExtra("title"),getIntent().getStringExtra("title"))+
                        " to resend the video")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private class DownloadTask extends AsyncTask<URL,Void,Uri>
    {
        int index;
        MessageModel message;

        DownloadTask(int position,MessageModel message)
        {
            index = position;
            this.message = message;
        }

        protected void onPreExecute()
        {
            message.setDownloaded(4);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(4);
            if(!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();
        }

        protected Uri doInBackground(URL...urls){
            InputStream urlInputStream = null;

            URLConnection urlConnection;

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received/"+System.currentTimeMillis()+".jpg");

            try{
                //Form a new URL
                URL finalUrl = urls[0];

                urlConnection = finalUrl.openConnection();

                //Get the size of the (file) inputstream from server..
                int contentLength = urlConnection.getContentLength();

                DataInputStream stream = new DataInputStream(finalUrl.openStream());

                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                if (buffer.length > 0) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        Log.d("5FILE", "Writing from buffer to the new file..");
                        fos.write(buffer);
                        fos.flush();
                        fos.close();

                        StorageReference file1;
                        file1=FirebaseStorage.getInstance().getReferenceFromUrl(message.getMessage());
                        file1.delete();

                        return Uri.fromFile(file);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        /*Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();*/
                    }
                } else {
                    //Could not download the file...
                    Log.e("8ERROR", "Buffer size is zero ! & returning 'false'.......");

                }
            }
            catch (FileNotFoundException e){
                return null;
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Uri result) {
            if (result != null) {

                message.setDownloaded(1);
                message.setMessage(result.toString());

                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(1);
                    chats.get(index).setMessage(result.toString());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyDataSetChanged();
                    }
                }

                if (MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    Intent intent = getIntent();
                    ((Activity) ApplicationClass.MessageActivityContext).finish();
                    startActivity(intent);

                    overridePendingTransition(0, 0);
                }

            }
            else{
                message.setDownloaded(0);
                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(0);
                    adapter.notifyDataSetChanged();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Could not download Image");
                builder.setMessage("Please ask "+pref1.getString(getIntent().getStringExtra("title"),getIntent().getStringExtra("title"))+
                        " to resend the image")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void SendMessage(final int index, final MessageModel message)
    {

        message.setDownloaded(-3);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(-3);

        if(!Messages.isComputingLayout())
            adapter.notifyDataSetChanged();


        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sharp);
        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);


        SendMesage = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                reference.child("users").child(RecieverPhone).child(sender).child("info").child("friend").setValue("yes");
                reference.child("users").child(RecieverPhone).child(sender).child("message").setValue("/null");

                message.setDownloaded(-1);
                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(-1);
                    mp.start();

                    if(!Messages.isComputingLayout())
                        adapter.notifyDataSetChanged();
                }

                if(MessageActivity.this.isDestroyed()  && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed())
                {
                    Intent intent = getIntent();
                    ((Activity) ApplicationClass.MessageActivityContext).finish();
                    startActivity(intent);
                    mp.start();

                    overridePendingTransition(0, 0);
                }
            }
        };

        reference.child("users").child(sender).child(RecieverPhone).push().setValue(message.getTime()+date.toString() +message.getMessage().trim()).addOnCompleteListener(SendMesage);
    }


    // Custom method to convert string to url
    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap){

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Received");
        if(!imagesFolder.exists())
        {
            imagesFolder.mkdirs();
        }

        // Create a file to save the image
        File file = new File(imagesFolder, new Timestamp(System.currentTimeMillis())+".jpg");
        MessageActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        try{
            OutputStream stream = null;

            stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            stream.flush();

            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        return savedImageURI;
    }
//**************************************************************************************************************************************

    public Drawable getBackground(Uri uri)
    {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
             return Drawable.createFromStream(inputStream, uri.toString() );
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Wallpaper set to default as file not found!", Toast.LENGTH_SHORT).show();
            llMessageActivity.setBackground(null);
            SharedPreferences.Editor editor = wallpaper.edit();
            editor.putString("value",null);
            editor.apply();
        }
        return null;
    }

}
