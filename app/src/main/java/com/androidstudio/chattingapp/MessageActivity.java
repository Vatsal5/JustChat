package com.androidstudio.chattingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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
    ImageView ivSend,ivProfile,ivBack,ivStatus,ivTyping;
    String RecieverPhone;
    FirebaseDatabase database;
    DatabaseReference reference;
    String lastpath;

    StorageReference rf;

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
    SharedPreferences pref;
    ChildEventListener imagereceiver;
    ValueEventListener Status;

    OnCompleteListener SendMesage;
    String defaultvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ApplicationClass.MessageActivityContext = MessageActivity.this;


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

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        title.setText(String.valueOf(getIntent().getStringExtra("title")));

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecieverPhone = getIntent().getStringExtra("phone");
        sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        chats = new ArrayList<>();

         pref= getApplicationContext().getSharedPreferences("Mode"+RecieverPhone,0);
        defaultvalue = pref.getString("mode"+RecieverPhone,"null");
        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        ivProfile = findViewById(R.id.ivProfile);
        ivBack = findViewById(R.id.ivBack);
        ivStatus = findViewById(R.id.ivStatus);
        ivTyping = findViewById(R.id.ivTyping);
        tvMode = findViewById(R.id.tvMode);


        if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 150);
        }

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

        if(getIntent().getStringExtra("profile") !=null){
            ApplicationClass.url=getIntent().getStringExtra("profile");
            Glide.with(MessageActivity.this).load(getIntent().getStringExtra("profile")).into(ivProfile);}
        else{
            ApplicationClass.url="null";
            ivProfile.setImageResource(R.drawable.person);}


        Glide.with(MessageActivity.this).load(R.drawable.typing).into(ivTyping);

        Status = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class).substring(0,6).equals("typing") && dataSnapshot.getValue(String.class).substring(7).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                    ivStatus.setBackgroundResource(R.drawable.white);

                    ivTyping.setVisibility(View.VISIBLE);

                }

                else if(dataSnapshot.getValue().equals("online") || (dataSnapshot.getValue(String.class).substring(0,6).equals("typing") && !dataSnapshot.getValue(String.class).substring(7).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
                    ivStatus.setBackgroundResource(R.drawable.white);

                    ivTyping.setVisibility(View.GONE);
                }

                else {
                    ivStatus.setBackground(null);

                    ivTyping.setVisibility(View.GONE);
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
                                                } else
                                                    CropImage.startPickImageActivity(MessageActivity.this);
                                                break;
                                            case 1:
                                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                                intent.setType("video/*");
                                                startActivityForResult(intent, 100);
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
                sendFCMPush();


                if (etMessage.getText().toString().trim().isEmpty())
                    Toast.makeText(MessageActivity.this, "Please enter a message", Toast.LENGTH_LONG).show();
                else {

                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    long millis = System.currentTimeMillis();
                    java.sql.Date date1 = new java.sql.Date(millis);

                    MessageModel model = new MessageModel(-1, sender, RecieverPhone, etMessage.getText().toString(), "text", -2, simpleDateFormat.format(date).substring(0, 5), date1.toString());
                    etMessage.setText(null);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    }
                    else
                    {
                        if((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
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
        //chats.add(new MessageModel(RecieverPhone,sender,"https://images.unsplash.com/photo-1579256308218-d162fd41c801?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjF9&auto=format&fit=crop&w=500&q=60","image",0));

        adapter = new MessageAdapter(MessageActivity.this, chats);



        if((defaultvalue.equals("null"))){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("mode"+RecieverPhone, "public");
            editor.apply();
        }

        Log.d("mode",defaultvalue);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
        });

        Messages.setAdapter(adapter);

        if(!defaultvalue.equals("private"))
            chats.addAll(Handler.getMessages(RecieverPhone));

        if(chats.size()!=0)
            adapter.notifyItemInserted(chats.size()-1);

        // to forward a message


        String type=getIntent().getStringExtra("type");
        String message1=getIntent().getStringExtra("message");

        if(!(type.equals(" ")))
        {
            Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

            if(type.equals("text"))
            {
                reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
                reference.child("users").child(RecieverPhone).child(sender).child("info").child("friend").setValue("yes");
                sendFCMPush();


                    MessageModel model = new MessageModel(-1, sender, RecieverPhone,message1 , "text", -2, simpleDateFormat.format(date).substring(0, 5), date1.toString());
                    etMessage.setText(null);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    }
                    else
                    {
                        if((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
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
            else if(type.equals("image"))
            {
                new CompressImage().execute(Uri.parse(message1));
            }
            else
            {
                MessageModel model = new MessageModel(1190,sender,RecieverPhone,message1,"video",100,simpleDateFormat.format(date).substring(0,5),date1.toString());

                int id = Handler.addMessage(model);
                model.setId(id);
                chats.add(model);

                adapter.notifyItemInserted(chats.size()-1);
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


                MessageModel messageModel = new MessageModel(-1, RecieverPhone, sender, dataSnapshot.getValue(String.class).substring(15), "image", 0,time,date);
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if(!(defaultvalue.equals("private")))
                    {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                }
                }

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                dataSnapshot.getRef().removeValue();

                chats.add(messageModel);

//                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(chats.size()-1);
                received.start();
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

                MessageModel messageModel = new MessageModel(-1,RecieverPhone,sender,uri,"video",101,time,date);
                Log.d("video",messageModel.getMessage());

                Toast.makeText(getApplicationContext(),"galbaat",Toast.LENGTH_LONG).show();

                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if (!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                dataSnapshot.getRef().removeValue();

                chats.add(messageModel);

//                adapter.notifyDataSetChanged();
                 adapter.notifyItemInserted(chats.size()-1);
                received.start();
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

                        MessageModel messageModel = new MessageModel(435, RecieverPhone, sender, dataSnapshot.getValue().toString().substring(15), "text", -1,time,date);

                        if(chats.size()!=0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                                MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                                int id = Handler.addMessage(message);
                                message.setId(id);
                                chats.add(message);
                            }
                        }
                        else {
                            if(!(defaultvalue.equals("private")))
                            {
                            MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                            int id = Handler.addMessage(message);
                            message.setId(id);
                            chats.add(message);
                        }
                        }

                        int id = Handler.addMessage(messageModel);
                        messageModel.setId(id);
                        chats.add(messageModel);
                        dataSnapshot.getRef().removeValue();


                        adapter.notifyItemInserted(chats.size()-1);
                       // adapter.notifyItemRangeInserted(chats.size()-1,1);
                        received.start();
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



            if(pos<chats.size()-1) {
                if (chats.get(pos - 1).getSender().equals("null") && chats.get(pos + 1).getSender().equals("null")) {
                    chats.remove(model);
                    Handler.DeleteMessage(model);
                    model = chats.get(pos - 1);
                    chats.remove(model);
                    Handler.DeleteMessage(model);
                }
                else{
                    chats.remove(model);
                    Handler.DeleteMessage(model);
                }
            }
            else{
                if (chats.get(pos - 1).getSender().equals("null") ) {
                    chats.remove(model);
                    Handler.DeleteMessage(model);
                    model = chats.get(pos - 1);
                    chats.remove(model);
                    Handler.DeleteMessage(model);
                }
                else{
                    chats.remove(model);
                    Handler.DeleteMessage(model);
                }

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

            int dragFlags,swipeFlags;
            if(!(chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 0 ||chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 2
                    ||chats.get(viewHolder.getAdapterPosition()).getDownloaded() == -2 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == -3 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 60)) {

                if(chats.get(viewHolder.getAdapterPosition()).getSender().equals(RecieverPhone)) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlags = ItemTouchHelper.END;
                }
                else
                {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlags = ItemTouchHelper.START;
                }
            }
            else
            {
                dragFlags = 0;
                swipeFlags =0;
            }

            return makeMovementFlags(dragFlags, swipeFlags);
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
        //Handler.close();

        FirebaseDatabase.getInstance().getReference("UserStatus").child(RecieverPhone).removeEventListener(Status);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("videos").removeEventListener(videoreceiver);

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

    private void sendFCMPush() {

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

            objData.put("body", msg);
            objData.put("title", title);
            objData.put("sound", R.raw.notificationsound);
            objData.put("icon", R.drawable.icon); //   icon_name image must be there in drawable
            objData.put("tag", token);
            objData.put("priority", "high");

            dataobjData = new JSONObject();
            dataobjData.put("text", msg);
            dataobjData.put("title", title);

            obj.put("to", token);
            //obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("!_@rj@_@@_PASS:>", obj.toString());
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
                CropImage.startPickImageActivity(MessageActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref= getApplicationContext().getSharedPreferences("Mode"+RecieverPhone,0);
        defaultvalue = pref.getString("mode"+RecieverPhone,"null");
        Log.d("mode",defaultvalue);

        if(defaultvalue.equals("private"))
        {
            if(chats.size()!=0)
            {
                chats.clear();
                if(!Messages.isComputingLayout())
                    adapter.notifyDataSetChanged();
            }
        }
        else
        {
            if(chats.size()==0)
            {
                chats.addAll(Handler.getMessages(RecieverPhone));
                if(!Messages.isComputingLayout())
                    adapter.notifyItemInserted(chats.size()-1);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").removeEventListener(imagereceiver);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    //*****************************************************************************************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK)
        {

            Uri uri = CropImage.getPickImageResultUri(this,data);
            new CompressImage().execute(uri);
        }

        if(requestCode==100)
        {
            if(resultCode==RESULT_OK)
            {
                Uri selectedImageUri = data.getData();

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

                Date date=new Date();
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

                long millis=System.currentTimeMillis();
                java.sql.Date date1=new java.sql.Date(millis);

                MessageModel model = new MessageModel(1190,sender,RecieverPhone,selectedImageUri.toString(),"video",100,simpleDateFormat.format(date).substring(0,5),date1.toString());

                int id = Handler.addMessage(model);
                model.setId(id);
                chats.add(model);

                adapter.notifyItemInserted(chats.size()-1);

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

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,5),date1.toString());

            if(chats.size()!=0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }
            else {
                if(!(defaultvalue.equals("private")))
                {
                MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
                int id = Handler.addMessage(message);
                message.setId(id);
                chats.add(message);
            }}

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            adapter.notifyItemInserted(chats.size()-1);
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
        Intent intent = new Intent(MessageActivity.this,ShowImage.class);

        intent.putExtra("source",chats.get(index).getMessage());

        startActivity(intent);
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
        protected Uri doInBackground(String... strings) {
                InputStream urlInputStream = null;

                URLConnection urlConnection;

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
            } catch (MalformedURLException e) {
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

        protected void onPreExecute(){
        }

        protected Uri doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;
            Uri imageInternalUri = null;

            try{

                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                StorageReference file;
                file=FirebaseStorage.getInstance().getReferenceFromUrl(message.getMessage());
                file.delete();

                imageInternalUri = saveImageToInternalStorage(bmp);
                return imageInternalUri;

            }catch(IOException e){
                e.printStackTrace();
            } finally{
                // Disconnect the http url connection
                connection.disconnect();
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


}
