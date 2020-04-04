package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.ImageSelected {

    EditText etMessage;
    ImageView ivSend,ivProfile,ivBack;
    String RecieverPhone;
    FirebaseDatabase database;
    DatabaseReference reference;
    String lastpath;

    StorageReference rf;

    TextView title;
    String to = "";
    RecyclerView Messages;
    String sender;
    LinearLayoutManager manager;
    MessageAdapter adapter;
    ArrayList<MessageModel> chats;
    ChildEventListener chreceiver, chsender;

    DBHandler Handler;
    int l;

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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity.this.finish();
            }
        });

        if(getIntent().getStringExtra("profile") !=null)
            Glide.with(MessageActivity.this).load(getIntent().getStringExtra("profile")).into(ivProfile);
        else
            ivProfile.setImageResource(R.drawable.person);

        Status = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().equals("online")) {
                    title.setTextColor(getResources().getColor(R.color.Orange));
                }
                else {
                    title.setTextColor(getResources().getColor(R.color.black));
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

                    Date date=new Date();
                    SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

                    MessageModel model = new MessageModel(-1, sender, RecieverPhone, etMessage.getText().toString(), "text", -2,simpleDateFormat.format(date).substring(0,5));
                    etMessage.setText(null);

                    int id = Handler.addMessage(model);
                    model.setId(id);
                    chats.add(model);
                    ApplicationClass.SameActivity =true;
                    adapter.notifyItemInserted(chats.size()-1);

                }
            }
        });

//*******************************************************************************************************************************************************

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(true);
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

                Messages.smoothScrollToPosition(adapter.getItemCount()-1);

                super.onItemRangeInserted(positionStart, itemCount);
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
        if(chats.size()>0)
            adapter.notifyItemRangeInserted(0,chats.size());
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
                String time;
                time=dataSnapshot.getValue(String.class).substring(0,5);

                MessageModel messageModel = new MessageModel(-1, RecieverPhone, sender, dataSnapshot.getValue(String.class).substring(5), "image", 0,time);
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                dataSnapshot.getRef().removeValue();

                chats.add(messageModel);

                adapter.notifyItemInserted(chats.size()-1);
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

                String time;

                if (!(dataSnapshot.getKey().equals("message"))) {
                    if (dataSnapshot.getKey().equals("info")) {
                        if (!(dataSnapshot.child("friend").exists())) {
                            reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
                        }
                    } else if (!(dataSnapshot.getKey().equals("info"))) {

                        time=dataSnapshot.getValue().toString().substring(0,5);

                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");

                        int id = Handler.addMessage(new MessageModel(-1, RecieverPhone, sender, dataSnapshot.getValue().toString().substring(5), "text", -1,time));
                        chats.add(new MessageModel(id, RecieverPhone, sender, dataSnapshot.getValue().toString().substring(5), "text", -1,time));
                        dataSnapshot.getRef().removeValue();


                        adapter.notifyItemRangeInserted(chats.size()-1,1);
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
    }

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
        ApplicationClass.SameActivity = false;
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

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,5));

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            adapter.notifyItemInserted(chats.size()-1);

            UploadImage(chats.size() - 1,messageModel);
    }
    }

    public void UploadImage(final int index, final MessageModel message)
    {
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
                                        child("images").push().setValue(message.getTime()+uri.toString());

                                message.setDownloaded(1);
                                Handler.UpdateMessage(message);

                                if(!MessageActivity.this.isDestroyed())
                                {
                                    chats.get(index).setDownloaded(1);

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyItemChanged(index);
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
        reference.child("users").child(sender).child(RecieverPhone).push().setValue(message.getTime()+message.getMessage().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.child("users").child(RecieverPhone).child(sender).child("info").child("friend").setValue("yes");
                reference.child("users").child(RecieverPhone).child(sender).child("message").setValue("/null");

                message.setDownloaded(-1);
                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(-1);

                    if(!Messages.isComputingLayout())
                        adapter.notifyItemChanged(index);
                }

                if(MessageActivity.this.isDestroyed()  && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed())
                {
                    Intent intent = getIntent();
                    ((Activity) ApplicationClass.MessageActivityContext).finish();
                    startActivity(intent);

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
