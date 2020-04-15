package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
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

public class MessageActivity2 extends AppCompatActivity implements MessageAdapter.ImageSelected {

    String groupKey, groupname;
    ImageView ivSend;
    RecyclerView Messages;
    ArrayList<String> membernumber;
    EditText etMessage;
    StorageReference rf;
    RecyclerView.LayoutManager manager;
    MessageAdapter adapter;
    ArrayList<MessageModel> chats;
    DBHandler Handler;
    int x=0;
    String sender;
    ChildEventListener imagereceiver, videoreceiver, chreceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);

        ApplicationClass.MessageActivity2Context = MessageActivity2.this;
        membernumber=new ArrayList<>();

        groupKey = getIntent().getStringExtra("groupname");
        groupname = getIntent().getStringExtra("groupkey");
        sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        ivSend = findViewById(R.id.ivSend);
        rf = FirebaseStorage.getInstance().getReference("docs/");
        etMessage = findViewById(R.id.etMessage);

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        Messages.setLayoutManager(manager);

        Handler = new DBHandler(this);
        Handler.Open();

        chats = new ArrayList<>();
        chats.addAll(Handler.getGroupMessages(groupname));

        adapter = new MessageAdapter(MessageActivity2.this, chats);
        Messages.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if(!(dataSnapshot.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())))
                       membernumber.add(dataSnapshot.getKey());
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
                                                } else
                                                    CropImage.startPickImageActivity(MessageActivity2.this);
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

        imagereceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time, date, sender;
                MediaPlayer received = MediaPlayer.create(MessageActivity2.this, R.raw.received);

                time = dataSnapshot.getValue(String.class).substring(0, 5);
                date = dataSnapshot.getValue(String.class).substring(5, 15);

                sender = dataSnapshot.getValue(String.class).substring(15, 28);

                Log.d("Received", "Image");


                MessageModel messageModel = new MessageModel(-1, sender, "null", dataSnapshot.getValue(String.class).substring(28), "image", 0, time, date, groupname);
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if (chats.size() != 0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                } else {
                    MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname);
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages").child(
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

                time = dataSnapshot.getValue(String.class).substring(0, 5);
                date = dataSnapshot.getValue(String.class).substring(5, 15);
                uri = dataSnapshot.getValue(String.class).substring(28);
                sender = dataSnapshot.getValue(String.class).substring(15,28);

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
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname);
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                }
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    dataSnapshot.getRef().removeValue();

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
                        time=dataSnapshot.getValue().toString().substring(0,5);
                        date=dataSnapshot.getValue().toString().substring(5,15);
                        sender = dataSnapshot.getValue(String.class).substring(15,28);
//
//                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
//
                        MessageModel messageModel = new MessageModel(435, "null", sender, dataSnapshot.getValue().toString().substring(28), "text", -1,time,date,groupname);

                        if(chats.size()!=0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                                MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname);
                                int id = Handler.addMessage(message);
                                message.setId(id);
                                chats.add(message);
                            }
                        }
                        else {
                                MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname);
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(chreceiver);


        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter some text", Toast.LENGTH_LONG).show();

                } else {

                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    long millis = System.currentTimeMillis();
                    java.sql.Date date1 = new java.sql.Date(millis);

                    MessageModel model = new MessageModel(-1, sender, "null", etMessage.getText().toString(), "text", -2, simpleDateFormat.format(date).substring(0, 5), date1.toString(), groupname);
                    etMessage.setText(null);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    } else {
                        MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname);
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
                CropImage.startPickImageActivity(MessageActivity2.this);
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

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);

            new CompressImage().execute(imageuri);
        }

        if(requestCode==100) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                Date date=new Date();
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

                long millis=System.currentTimeMillis();
                java.sql.Date date1=new java.sql.Date(millis);

                MessageModel model = new MessageModel(1190,sender,"null",selectedImageUri.toString(),"video",100,simpleDateFormat.format(date).substring(0,5),date1.toString(),groupname);

                int id = Handler.addMessage(model);
                model.setId(id);

                chats.add(model);

                adapter.notifyItemInserted(chats.size()-1);
            }
        }

    }

    @Override
    public void showImage(int index) {

    }

    @Override
    public void downloadImage(int index) {
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

    }

    @Override
    public void Onlongclick(int index) {

    }
            public void SendMessage(final int index, final MessageModel model)
            {
            final MediaPlayer mp = MediaPlayer.create(MessageActivity2.this, R.raw.sharp);

            model.setDownloaded(-3);
            Handler.UpdateMessage(model);

            chats.get(index).setDownloaded(-3);

            if(!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();



            for(int i=0;i<membernumber.size();i++) {
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages")
                        .child(membernumber.get(i)).push().setValue(
                        model.getTime() + model.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + model.getMessage())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                               if(x==0)
                               {
                                x=1;
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
                            }}
                        });
            }
            x=0;

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
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

            long millis=System.currentTimeMillis();
            java.sql.Date date1=new java.sql.Date(millis);

            MessageModel messageModel = new MessageModel(-1, sender, "nul", uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,5),date1.toString(),groupname);

            if(chats.size()!=0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }
            else {
                    MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
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
            InputStream urlInputStream = null;

            URLConnection urlConnection;

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
            } catch (MalformedURLException e) {
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
                    ((Activity) ApplicationClass.MessageActivityContext).finish();
                    startActivity(intent);

                    overridePendingTransition(0, 0);
                }

            }
        }
    }

    public void UploadImage(final int index, final MessageModel message)
    {
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

                                FirebaseDatabase.getInstance().getReference().child("groups").child("images").child(groupKey).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).

                                       push().setValue(message.getTime()+message.getDate()+   FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+uri.toString());

                                message.setDownloaded(1);
                                Handler.UpdateMessage(message);

                                if(!MessageActivity2.this.isDestroyed())
                                {
                                    chats.get(index).setDownloaded(1);
                                    mp.start();

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                if(MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
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


    public void uploadVideo(final int index, final MessageModel message)
    {
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

                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).
                                        child("videos").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        push().setValue(message.getTime()+message.getDate()+   FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+uri.toString());
                                Toast.makeText(getApplicationContext(),"Ghaint",Toast.LENGTH_LONG).show();

                                message.setDownloaded(102);
                                Handler.UpdateMessage(message);


                                if(!MessageActivity2.this.isDestroyed())
                                {
                                    chats.get(index).setDownloaded(102);

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                if(MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
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

    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }


}
