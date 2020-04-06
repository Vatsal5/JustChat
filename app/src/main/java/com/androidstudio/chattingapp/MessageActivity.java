package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

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
import com.bumptech.glide.request.RequestOptions;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.ImageSelected {

    EmojiEditText etMessage;
    ImageView ivSend,ivProfile,ivBack,ivStatus,ivTyping;
    String RecieverPhone;
    FirebaseDatabase database;
    DatabaseReference reference;
    String lastpath;

    String message;

    StorageReference rf;

    TextView title;
    String to = "";
    RecyclerView Messages;
    String sender;
    LinearLayoutManager manager;
    MessageAdapter adapter;
    ArrayList<MessageModel> chats;
    ChildEventListener chreceiver;

    DBHandler Handler;
    int l;
    int flag=0;

    ChildEventListener imagereceiver;
    ValueEventListener Status;


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

        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
        ivProfile = findViewById(R.id.ivProfile);
        ivBack = findViewById(R.id.ivBack);
        ivTyping = findViewById(R.id.ivTyping);
        ivStatus = findViewById(R.id.ivStatus);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity.this.finish();
            }
        });

        Glide.with(MessageActivity.this).load(R.drawable.online).into(ivTyping);

        if(getIntent().getStringExtra("profile") !=null)
            Glide.with(MessageActivity.this).load(getIntent().getStringExtra("profile")).into(ivProfile);
        else
            ivProfile.setImageResource(R.drawable.person);

        Status = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().equals("typing")) {
                    ivStatus.setBackgroundResource(R.drawable.orange);
                    ivTyping.setVisibility(View.VISIBLE);
                    Glide.with(MessageActivity.this).load(R.drawable.online).into(ivTyping);
                }

                else if(dataSnapshot.getValue().equals("online")) {
                    ivStatus.setBackgroundResource(R.drawable.orange);
                    ivTyping.setVisibility(View.GONE);
                }

                else {
                    ivStatus.setBackground(null);
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

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etMessage.getRight() - etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                    {
                        if(ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},5);
                        }
                        else
                            CropImage.startPickImageActivity(MessageActivity.this);
                        return true;
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

                if(etMessage.getText().toString().trim().length()==0)
                {
                    flag=0;
                }

                if(etMessage.getText().toString().trim().length()>0)
                {  if(!(flag==1)) {
                    FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue("typing");
                    message = etMessage.getText().toString().trim();
                    flag = 1;
                }

                }
                else
                {
                    FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue("online");
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(etMessage.getText().toString().trim().equals(message))
                            FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue("online");
                    }
                },5000);
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
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
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

        chats.addAll(Handler.getMessages(RecieverPhone));

        for(int i=0;i<chats.size();i++)
        {
            Log.d("messages",chats.get(i).getMessage());
        }
        if(chats.size()>0)
            adapter.notifyItemInserted(chats.size()-1);
//        if(chats.size()!=0)
//            adapter.notifyItemRangeInserted(0,chats.size());

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Messages.scrollToPosition(chats.size() - 1);
//            }
//        }, 500);

//**************************************************************************************************************************************************************************

        imagereceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;

                MediaPlayer received = MediaPlayer.create(MessageActivity.this, R.raw.received);

                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);

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
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                }

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                dataSnapshot.getRef().removeValue();

                chats.add(messageModel);

                adapter.notifyDataSetChanged();
               // adapter.notifyItemInserted(chats.size()-1);
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
                            MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date);
                            int id = Handler.addMessage(message);
                            message.setId(id);
                            chats.add(message);
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
                chats.remove(model);
                Handler.DeleteMessage(model);

                adapter.notifyItemRemoved(pos);
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
                    ||chats.get(viewHolder.getAdapterPosition()).getDownloaded() == -2 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == -3)) {

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
                MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
                int id = Handler.addMessage(message);
                message.setId(id);
                chats.add(message);
            }

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            adapter.notifyItemInserted(chats.size()-1);

            UploadImage(chats.size() - 1,messageModel);
    }
    }

    public void UploadImage(final int index, final MessageModel message)
    {
       final MediaPlayer mp = MediaPlayer.create(this, R.raw.sharp);

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
                                        adapter.notifyItemChanged(index);
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

    //***********************************************************************************************************************************************
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
                        adapter.notifyItemChanged(index);
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

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sharp);
        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);


        reference.child("users").child(sender).child(RecieverPhone).push().setValue(message.getTime()+date.toString() +message.getMessage().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.child("users").child(RecieverPhone).child(sender).child("info").child("friend").setValue("yes");
                reference.child("users").child(RecieverPhone).child(sender).child("message").setValue("/null");

                message.setDownloaded(-1);
                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(-1);
                    mp.start();

                    if(!Messages.isComputingLayout())
                        adapter.notifyItemChanged(index);
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
        });

        message.setDownloaded(-3);
        Handler.UpdateMessage(message);

        if(MessageActivity.this.isDestroyed())
        {
            chats.get(index).setDownloaded(-3);

            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);
        }
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
