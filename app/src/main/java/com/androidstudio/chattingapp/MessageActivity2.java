package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageActivity2 extends AppCompatActivity {

    String groupKey,groupname;
    ImageView ivSend;
    EditText etMessage;
    StorageReference rf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);
        ivSend=findViewById(R.id.ivSend);
        rf = FirebaseStorage.getInstance().getReference("docs/");
        etMessage=findViewById(R.id.etMessage);
        groupKey=getIntent().getStringExtra("groupname");
        groupname=getIntent().getStringExtra("groupkey");

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMessage.getText().toString().trim().length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please enter some text",Toast.LENGTH_LONG).show();

                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").addChildEventListener(
                            new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages")
                                            .child(dataSnapshot.getValue().toString()).setValue(etMessage.getText().toString().trim());
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
                }
            }
        });




    }

    public void sendImage()
    {
        if(ContextCompat.checkSelfPermission(MessageActivity2.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MessageActivity2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

        }
        else{
            //pick_image();
            CropImage.startPickImageActivity(MessageActivity2.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                ActivityCompat.requestPermissions(MessageActivity2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                startCrop(imageuri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                Uri uri = result.getUri();
                new MessageActivity2.CompressImage().execute(uri);


            }
        }
    }

    public class CompressImage extends AsyncTask<Uri,Void,Uri>
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

//            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,5),date1.toString());
//
//            if(chats.size()!=0) {
//                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
//                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
//                    int id = Handler.addMessage(message);
//                    message.setId(id);
//                    chats.add(message);
//                }
//            }
//            else {
//                if(!(defaultvalue.equals("private")))
//                {
//                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString());
//                    int id = Handler.addMessage(message);
//                    message.setId(id);
//                    chats.add(message);
//                }}
//
//            Log.d("type",chats.get(chats.size()-1).getType());
//
//            if(chats.get(chats.size()-1).getType().equals("typing")) {
//
//                int id = Handler.addMessage(messageModel);
//                messageModel.setId(id);
//
//                if(flag1==true) {
//
//                    chats.add(chats.size() - 1, messageModel);
//
//                    adapter.notifyItemInserted(chats.size() - 1);
//                }
//            }
//            else
//            {
//                int id = Handler.addMessage(messageModel);
//                messageModel.setId(id);
//
//                chats.add(messageModel);
//
//                adapter.notifyItemInserted(chats.size() - 1);
  //          }
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

    public void UploadImage(final int index, final MessageModel message)
    {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sharp);

//        message.setDownloaded(3);
//        Handler.UpdateMessage(message);
//
//        chats.get(index).setDownloaded(3);
//
//        if(!Messages.isComputingLayout())
//            adapter.notifyDataSetChanged();

        rf.child(groupKey).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                FirebaseDatabase.getInstance().getReference().child("groups").child("images").child(groupKey).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).

                                       setValue(message.getTime()+message.getDate()+uri.toString());

//                                message.setDownloaded(1);
//                                Handler.UpdateMessage(message);
//
//                                sendFCMPush("Image");
//
//                                if(!MessageActivity.this.isDestroyed())
//                                {
//                                    chats.get(index).setDownloaded(1);
//                                    mp.start();
//
//                                    if(!Messages.isComputingLayout())
//                                    {
//                                        adapter.notifyDataSetChanged();
//                                    }
//                                }
//
//                                if(MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
//                                    Intent intent = getIntent();
//                                    ((Activity) ApplicationClass.MessageActivityContext).finish();
//                                    startActivity(intent);
//
//                                    mp.start();
//
//                                    overridePendingTransition(0, 0);
//                                }
                            }
                       });

            }
        });
    }


    public void uploadVideo(final int index, final MessageModel message)
    {
        rf.child(groupKey).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).
                                        child("videos").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        setValue(message.getTime()+message.getDate()+uri.toString());
                                Toast.makeText(getApplicationContext(),"Ghaint",Toast.LENGTH_LONG).show();

//                                message.setDownloaded(102);
//                                Handler.UpdateMessage(message);
//
//                                sendFCMPush("Video");
//
//                                if(!MessageActivity.this.isDestroyed())
//                                {
//                                    chats.get(index).setDownloaded(102);
//
//                                    if(!Messages.isComputingLayout())
//                                    {
//                                        adapter.notifyDataSetChanged();
//                                    }
//                                }
//
//                                if(MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
//                                    Intent intent = getIntent();
//                                    ((Activity) ApplicationClass.MessageActivityContext).finish();
//                                    startActivity(intent);
//
//
//                                    overridePendingTransition(0, 0);
//                                }
                            }
                        });

            }
        });
    }


}
