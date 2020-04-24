package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MessageActivity2 extends AppCompatActivity implements MessageAdapter.ImageSelected {

    String groupKey, groupname,profile;
    ImageView ivSend,ivBack,ivProfile;
    RecyclerView Messages;
    TextView tvTitle,tvMode;
    ArrayList<String> membernumber;
    EditText etMessage;
    ConstraintLayout llMessageActivity2;
    ConstraintLayout rl;
    LinearLayout ll;
    StorageReference rf;
    int numberOfMembers=-1;
    SharedPreferences preftheme;
    RecyclerView.LayoutManager manager;
    MessageAdapter adapter;
    ArrayList<MessageModel> chats;
    DBHandler Handler;
    int y=0,z=0;
    String sender;
    ChildEventListener imagereceiver, videoreceiver, chreceiver;
    String defaultvalue;
    SharedPreferences pref,wallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);
        ivProfile=findViewById(R.id.ivProfile);
        ll=findViewById(R.id.ll);

        ApplicationClass.MessageActivity2Context = MessageActivity2.this;
        membernumber=new ArrayList<>();
        preftheme=getSharedPreferences("theme",0);



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

        profile=getIntent().getStringExtra("profile");

        if(profile.equals("null"))
            ivProfile.setImageResource(R.drawable.group);
        else
        Glide.with(MessageActivity2.this).load(Uri.parse(profile)).into(ivProfile);

        groupname = getIntent().getStringExtra("groupname");
        groupKey = getIntent().getStringExtra("groupkey");
        sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        ivSend = findViewById(R.id.ivSend);
        rf = FirebaseStorage.getInstance().getReference("docs/");
        etMessage = findViewById(R.id.etMessage);
        tvTitle = findViewById(R.id.title);
        ivBack = findViewById(R.id.ivBack);
        tvMode = findViewById(R.id.tvMode);
        rl = findViewById(R.id.rl);
        llMessageActivity2 = findViewById(R.id.llMessageActivity2);

        tvTitle.setText(groupname);

        tvMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity2.this,Mode.class);
                intent.putExtra("number",groupKey);
                startActivity(intent);
            }
        });

        wallpaper = getSharedPreferences("Wallpaper",0);

        pref= getApplicationContext().getSharedPreferences("Mode",0);
        defaultvalue = pref.getString("mode"+groupKey,"null");

        if((defaultvalue.equals("null"))){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("mode"+groupKey, "public");
            editor.apply();
        }


        if(!wallpaper.getString("value","null").equals("null"))
        {
            getWindow().setBackgroundDrawable(getBackground(Uri.parse(wallpaper.getString("value","null"))));
        }

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        Messages.setLayoutManager(manager);

        Handler = new DBHandler(this);
        Handler.Open();

        chats = new ArrayList<>();

        adapter = new MessageAdapter(MessageActivity2.this, chats);
        Messages.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                manager.scrollToPosition(chats.size()-1);
            }
        });

        chats.addAll(Handler.getGroupMessages(groupname));
        if(chats.size()>0)
            adapter.notifyItemInserted(chats.size()-1);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity2.this,GroupDetails.class);
                if(ApplicationClass.RenameGroup==null){
                intent.putExtra("groupname",groupname);}
                else
                { intent.putExtra("groupname",ApplicationClass.RenameGroup);}
                intent.putExtra("groupkey",groupKey);
                intent.putExtra("profile",getIntent().getStringExtra("profile"));
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deleteimages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,1))==0)
                {
                    StorageReference file1;
                    file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(1));
                    file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataSnapshot.getRef().removeValue();

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

                if(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,1))==0)
                {
                    StorageReference file1;
                    file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(1));
                    file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataSnapshot.getRef().removeValue();

                        }
                    });


                }
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                if(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,1))==0)
                {

                    StorageReference file1;
                    file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(1));
                    file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataSnapshot.getRef().removeValue();

                        }
                    });

                }
            }

            @Override
            public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

                if(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,1))==0)
                {

                            StorageReference file1;
                            file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(1));
                            file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dataSnapshot.getRef().removeValue();

                                }
                            });


                }
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


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity2.this.finish();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                      //  Log.d("asdf","hi");

                        if(!(dataSnapshot.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())))
                       membernumber.add(dataSnapshot.getValue(String.class));
                        numberOfMembers++;
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
                }
        );

        etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                String[] choices = {"Image", "Video"};

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etMessage.getRight() - etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);
                        builder.setTitle("Send...")
                                .setItems(choices, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i) {
                                            case 0:
                                                if (ContextCompat.checkSelfPermission(MessageActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(MessageActivity2.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
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


        imagereceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time, date, sender;
                MediaPlayer received = MediaPlayer.create(MessageActivity2.this, R.raw.received);

                time = dataSnapshot.getValue(String.class).substring(0, 11);
                date = dataSnapshot.getValue(String.class).substring(11, 21);

                sender = dataSnapshot.getValue(String.class).substring(21, 34);

                Log.d("Received", "Image");


                MessageModel messageModel = new MessageModel(-1, sender, "null", dataSnapshot.getValue(String.class).substring(34), "image", 0, time, date, groupname);
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if (chats.size() != 0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                } else {
                    if(!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                chats.add(messageModel);

                adapter.notifyItemInserted(chats.size()-1);

                received.start();

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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(imagereceiver);


        videoreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time, date,sender;
                String uri;

                MediaPlayer received = MediaPlayer.create(MessageActivity2.this, R.raw.received);

                Toast.makeText(MessageActivity2.this, "Video", Toast.LENGTH_SHORT).show();

                Log.d("Received", "Video");

                time = dataSnapshot.getValue(String.class).substring(0, 11);
                date = dataSnapshot.getValue(String.class).substring(11, 21);
                uri = dataSnapshot.getValue(String.class).substring(34);
                sender = dataSnapshot.getValue(String.class).substring(21,34);

                MessageModel messageModel = new MessageModel(-1,sender,"null",uri,"video",101,time,date,groupname);
                Log.d("video",messageModel.getMessage());

                Toast.makeText(getApplicationContext(),"galbaat",Toast.LENGTH_LONG).show();

                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if(!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                   // dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);

//                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(chats.size()-1);

                    received.start();
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("videos").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(videoreceiver);


        chreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                String time, date,sender;

                MediaPlayer received = MediaPlayer.create(MessageActivity2.this, R.raw.received);

//                if (!(dataSnapshot.getKey().equals("message"))) {
//                    if (dataSnapshot.getKey().equals("info")) {
//                        if (!(dataSnapshot.child("friend").exists())) {
//                            reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
//                        }
//                    } else if (!(dataSnapshot.getKey().equals("info"))) {
//
                        time=dataSnapshot.getValue().toString().substring(0,11);
                        date=dataSnapshot.getValue().toString().substring(11,21);
                        sender = dataSnapshot.getValue(String.class).substring(21,34);
//
//                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
//
                        MessageModel messageModel = new MessageModel(435, sender, "null", dataSnapshot.getValue().toString().substring(34), "text", -1,time,date,groupname);

                        if(chats.size()!=0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                                MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname);
                                int id = Handler.addMessage(message);
                                message.setId(id);
                                chats.add(message);
                            }
                        }
                        else {
                            if(!(defaultvalue.equals("private"))) {
                                MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname);
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
//                    }
//                }
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(chreceiver);


        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter some text", Toast.LENGTH_LONG).show();

                } else {

                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                    long millis = System.currentTimeMillis();
                    java.sql.Date date1 = new java.sql.Date(millis);

                    MessageModel model = new MessageModel(-1, sender, "null", etMessage.getText().toString(), "text", -2, simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9), date1.toString(), groupname);
                    etMessage.setText(null);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    } else {
                        if((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
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
            if(!(chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 103 ||chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 3 ||
                    chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 60)){

                if(!chats.get(viewHolder.getAdapterPosition()).getSender().equals(sender)) {
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
    protected void onRestart() {
        super.onRestart();
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(imagereceiver);
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(chreceiver);
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("videos").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(videoreceiver);
        if(ApplicationClass.RenameGroup!=null)
        {
            tvTitle.setText(ApplicationClass.RenameGroup);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==5)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);
                builder.setTitle("Permission Required")
                        .setMessage("Permission to write External storage is required")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MessageActivity2.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},5);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);
                builder.setTitle("Permission Required")
                        .setMessage("Permission is required to receive and send media")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MessageActivity2.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},150);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK)
        {
            if(data.getClipData()!=null)
            {
                if(data.getClipData().getItemCount()<=15) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        ClipData.Item imageItem = data.getClipData().getItemAt(i);
                        Uri uri = imageItem.getUri();

                        new MessageActivity2.CompressImage().execute(uri);
                    }
                }
                else
                    Toast.makeText(this, "You Cannot send more than 15 images at a time", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Uri uri = data.getData();
                new MessageActivity2.CompressImage().execute(uri);
            }

        }

        if(requestCode==100) {
            if (resultCode == RESULT_OK) {

                if (data.getClipData() != null) {

                    if (data.getClipData().getItemCount() <= 5) {

                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {

                            ClipData.Item videoItem = data.getClipData().getItemAt(i);
                            Uri videoURI = videoItem.getUri();

                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

                            long millis = System.currentTimeMillis();
                            java.sql.Date date1 = new java.sql.Date(millis);

                            MessageModel model = new MessageModel(1190, sender, "null", videoURI.toString(), "video", 100, simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9), date1.toString(), groupname);

                            if (chats.size() != 0) {
                                if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                                    MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
                                    int id = Handler.addMessage(messageModel);
                                    messageModel.setId(id);
                                    chats.add(messageModel);
                                }
                            } else {
                                if ((!(defaultvalue.equals("private")))) {
                                    MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
                                    int id = Handler.addMessage(messageModel);
                                    messageModel.setId(id);
                                    chats.add(messageModel);
                                }
                            }
                            int id = Handler.addMessage(model);
                            model.setId(id);
                            chats.add(model);
                            adapter.notifyItemInserted(chats.size()-1);
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "You cannot send more than 5 videos at a time", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Uri uri = data.getData();
                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

                    long millis = System.currentTimeMillis();
                    java.sql.Date date1 = new java.sql.Date(millis);

                    MessageModel model = new MessageModel(1190, sender, "null", uri.toString(), "video", 100, simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9), date1.toString(), groupname);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    } else {
                        if ((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    }
                    int id = Handler.addMessage(model);
                    model.setId(id);
                    chats.add(model);
                    adapter.notifyItemInserted(chats.size()-1);
                }
            }
        }

    }

    @Override
    public void showImage(int index) {
        Intent intent = new Intent(MessageActivity2.this,ShowImage.class);

        intent.putExtra("source",chats.get(index).getMessage());

        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(imagereceiver);
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(chreceiver);
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("videos").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(videoreceiver);


        overridePendingTransition(0, 0);
    }

    @Override
    public void downloadImage(int index) {
        Log.d("imagereceived","downloadImage");
        new DownloadTask(index,chats.get(index)).execute(stringToURL(chats.get(index).getMessage()));
    }

    @Override
    public void sentTextMessage(int index) {
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
        new DownloadVideo(index,chats.get(index)).execute(chats.get(index).getMessage());
    }

    @Override
    public void showVideo(int index) {
        Intent intent= new Intent(MessageActivity2.this,VideoActivity.class);
        intent.putExtra("uri",chats.get(index).getMessage());
        startActivity(intent);
    }

    @Override
    public void Onlongclick(final int index) {

        if(!(chats.get(index).getType().equals("video") || chats.get(index).getType().equals("image"))) {
            String[] choices = {"Copy", "Forward"};

            AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);

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
                                    Intent intent = new Intent(MessageActivity2.this, FriendsActivity.class);
                                    intent.putExtra("type", chats.get(index).getType());
                                    intent.putExtra("path", 1);
                                    intent.putExtra("message", chats.get(index).getMessage());
                                    MessageActivity2.this.finish();

                                    startActivity(intent);
                                    break;
                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            String [] choices = {"Forward"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);

            builder.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            Intent intent = new Intent(MessageActivity2.this, FriendsActivity.class);
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
            public void SendMessage(final int index, final MessageModel model)
            {
                final int[] x = {0};
            final MediaPlayer mp = MediaPlayer.create(MessageActivity2.this, R.raw.sharp);

            model.setDownloaded(-3);
            Handler.UpdateMessage(model);

            chats.get(index).setDownloaded(-3);

            if(!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();


                Log.d("asdf",membernumber.size()+"");
            for(int i=0;i<membernumber.size();i++) {
                Log.d("asdf", "hi");
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages")
                        .child(membernumber.get(i)).push().setValue(
                        model.getTime() + model.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + model.getMessage())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (x[0] == 0) {
                                    x[0] = 1;
                                    model.setDownloaded(-1);
                                    Handler.UpdateMessage(model);

                                    if (!MessageActivity2.this.isDestroyed()) {
                                        chats.get(index).setDownloaded(-1);
                                        mp.start();

                                        if (!Messages.isComputingLayout())
                                            adapter.notifyDataSetChanged();
                                    }

                                    if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                                        Intent intent = getIntent();
                                        ((Activity) ApplicationClass.MessageActivity2Context).finish();
                                        startActivity(intent);
                                        mp.start();

                                        overridePendingTransition(0, 0);
                                    }
                                }
                            }
                        });
            }

        }



    private void startCrop(Uri imageuri)
    {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAllowRotation(true)
                .setAspectRatio(25,25)
                .start(this);
    }

    class CompressImage extends AsyncTask<Uri,Void,Uri>
    {
        @Override
        protected Uri doInBackground(Uri... uris) {
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(MessageActivity2.this.getContentResolver(),uris[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes=null;
            ByteArrayOutputStream stream= new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            bytes=stream.toByteArray();
            Bitmap bitmap1= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            String path = MediaStore.Images.Media.insertImage(MessageActivity2.this.getContentResolver(), bitmap1, "Title", null);
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
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm:ss.SSS");

            long millis=System.currentTimeMillis();
            java.sql.Date date1=new java.sql.Date(millis);

            MessageModel messageModel = new MessageModel(-1, sender, "nul", uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9),date1.toString(),groupname);

            if(chats.size()!=0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }
            else {
                if(!(defaultvalue.equals("private"))) {
                    MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                chats.add(messageModel);

                adapter.notifyItemInserted(chats.size() - 1);
            }
        }

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

            File directory = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received");
            if(!directory.exists())
                directory.mkdirs();

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
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").child(message.getTime()+message.getDate()
                                +message.getSender())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").child(dataSnapshot.getKey())
                                                .setValue(( Integer.parseInt(dataSnapshot.getValue().toString().substring(0,1))-1)+dataSnapshot.getValue().toString().substring(1));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

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

                if(!MessageActivity2.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(102);
                    chats.get(index).setMessage(uri.toString());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyDataSetChanged();
                    }
                }

                if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                    Intent intent = getIntent();
                    ((Activity) ApplicationClass.MessageActivity2Context).finish();
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
            message.setDownloaded(4);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(4);
            if(!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();
        }

        protected Uri doInBackground(URL...urls){
            Log.d("imagereceived","background");
            InputStream urlInputStream = null;

            URLConnection urlConnection;

            File directory = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received");
            if(!directory.exists())
                directory.mkdirs();

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
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deleteimages").child(message.getTime()+message.getDate()
                        +message.getSender())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deleteimages").child(dataSnapshot.getKey())
                                                .setValue(( Integer.parseInt(dataSnapshot.getValue().toString().substring(0,1))-1)+dataSnapshot.getValue().toString().substring(1));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                        StorageReference file1;
                        file1=FirebaseStorage.getInstance().getReferenceFromUrl(message.getMessage());
                      //  file1.delete();

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

        // When all async task done
        protected void onPostExecute(Uri result) {
            Log.d("imagereceived","postexecute");
            if (result != null) {

                message.setDownloaded(1);
                message.setMessage(result.toString());

                Handler.UpdateMessage(message);

                if(!MessageActivity2.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(1);
                    chats.get(index).setMessage(result.toString());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyDataSetChanged();
                    }
                }

                if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                    Intent intent = getIntent();
                    ((Activity) ApplicationClass.MessageActivity2Context).finish();
                    startActivity(intent);

                    overridePendingTransition(0, 0);
                }

            }
        }
    }

    public void UploadImage(final int index, final MessageModel message)
    {
        final int[] y = {0};
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sharp);

        message.setDownloaded(3);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(3);

        if(!Messages.isComputingLayout())
            adapter.notifyDataSetChanged();

        rf.child(groupKey).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(groupKey).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deleteimages").

                                        child(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(numberOfMembers+""+  uri.toString());


                                for(int i=0;i<membernumber.size();i++) {

                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(membernumber.get(i)).

                                            push().setValue(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + uri.toString());

                                    if(y[0] ==0) {
                                        y[0] =1;
                                        message.setDownloaded(1);
                                        Handler.UpdateMessage(message);

                                        if (!MessageActivity2.this.isDestroyed()) {
                                            chats.get(index).setDownloaded(1);
                                            mp.start();

                                            if (!Messages.isComputingLayout()) {
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                        if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                                            Intent intent = getIntent();
                                            ((Activity) ApplicationClass.MessageActivity2Context).finish();
                                            startActivity(intent);

                                            mp.start();

                                            overridePendingTransition(0, 0);
                                        }
                                    }
                                }
                            }
                       });

            }
        });
    }


    public void uploadVideo(final int index, final MessageModel message)
    {
        final int[] z = {0};
        message.setDownloaded(103);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(103);

        if(!Messages.isComputingLayout())
            adapter.notifyDataSetChanged();

        rf.child(groupKey).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(groupKey).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").

                                        child(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() ).setValue(numberOfMembers+  uri.toString());

                                for(int i=0;i<membernumber.size();i++) {

                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).
                                            child("videos").child(membernumber.get(i)).
                                            push().setValue(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + uri.toString());
                                    Toast.makeText(getApplicationContext(), "Ghaint", Toast.LENGTH_LONG).show();

                                    if (z[0] == 0) {
                                        z[0] = 1;

                                        message.setDownloaded(102);
                                        Handler.UpdateMessage(message);


                                        if (!MessageActivity2.this.isDestroyed()) {
                                            chats.get(index).setDownloaded(102);

                                            if (!Messages.isComputingLayout()) {
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                        if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                                            Intent intent = getIntent();
                                            ((Activity) ApplicationClass.MessageActivity2Context).finish();
                                            startActivity(intent);


                                            overridePendingTransition(0, 0);
                                        }
                                    }
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(imagereceiver);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("videos").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(videoreceiver);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(chreceiver);
    }

    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref= getApplicationContext().getSharedPreferences("Mode",0);
        defaultvalue = pref.getString("mode"+groupKey,"null");
        Log.d("mode",defaultvalue);

        if(defaultvalue.equals("private"))
        {
            tvMode.setText("Private");
            if(chats.size()!=0)
            {
                chats.clear();
                if(!Messages.isComputingLayout())
                    adapter.notifyDataSetChanged();
            }
        }
        else
        {
            tvMode.setText("Public");
            if(chats.size()!=0) {
                chats.clear();
                adapter.notifyDataSetChanged();
            }
            chats.addAll(Handler.getGroupMessages(groupname));
            if(!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size()-1);
        }
    }

    public Drawable getBackground(Uri uri)
    {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return Drawable.createFromStream(inputStream, uri.toString() );
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Wallpaper set to default as file not found!", Toast.LENGTH_SHORT).show();
            llMessageActivity2.setBackground(null);
            SharedPreferences.Editor editor = wallpaper.edit();
            editor.putString("value",null);
            editor.apply();
        }
        return null;
    }
}
