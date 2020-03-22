package com.androidstudio.chattingapp;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.ImageSelected {

    EditText etMessage;
    ImageView ivSend;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        rf = FirebaseStorage.getInstance().getReference("docs/");
        lastpath = "";


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        title = findViewById(R.id.title);
        Handler = new DBHandler(MessageActivity.this);
        Handler.Open();

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        title.setText(String.valueOf(getIntent().getStringExtra("title")));
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecieverPhone = getIntent().getStringExtra("phone");
        sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        chats = new ArrayList<>();

        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);

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
                    final String id = reference.child("users").child(sender).child(RecieverPhone).push().getKey();
                    reference.child("users").child(sender).child(RecieverPhone).child(id).setValue(etMessage.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                chats.add(new MessageModel(id, sender, RecieverPhone, etMessage.getText().toString(), "text", -1));
                                Handler.addMessage(new MessageModel(id, sender, RecieverPhone, etMessage.getText().toString(), "text", -1));

                                adapter.notifyDataSetChanged();
                                etMessage.setText(null);
                            } else {
                                Toast.makeText(MessageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

//*******************************************************************************************************************************************************

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(true);
        Messages.setLayoutManager(manager);

        chats = Handler.getMessages(RecieverPhone);
        for (int i = 0; i < chats.size(); i++) {
            Log.d("messages", chats.get(i).getMessage());
        }
        //chats.add(new MessageModel(RecieverPhone,sender,"https://images.unsplash.com/photo-1579256308218-d162fd41c801?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjF9&auto=format&fit=crop&w=500&q=60","image",0));

        adapter = new MessageAdapter(MessageActivity.this, chats);

        Messages.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                Messages.scrollToPosition(chats.size() - 1);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);

                Messages.scrollToPosition(chats.size() - 1);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);

                Messages.scrollToPosition(chats.size() - 1);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                manager.smoothScrollToPosition(Messages,null, adapter.getItemCount());
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Messages.scrollToPosition(chats.size() - 1);
            }
        }, 500);

//**************************************************************************************************************************************************************************

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                MessageModel messageModel = new MessageModel(dataSnapshot.getKey(), RecieverPhone, sender, dataSnapshot.getValue(String.class), "image", 0);
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                chats.add(messageModel);
                Handler.addMessage(messageModel);

                dataSnapshot.getRef().removeValue();

                adapter.notifyDataSetChanged();
                Messages.scrollToPosition(chats.size() - 1);

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

//********************************************************************************************************************************************************

        chreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();


                if (!(dataSnapshot.getKey().equals("message"))) {
                    if (dataSnapshot.getKey().equals("info")) {
                        if (!(dataSnapshot.child("friend").exists())) {
                            reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
                        }
                    } else if (!(dataSnapshot.getKey().equals("info"))) {

                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");

                        chats.add(new MessageModel(dataSnapshot.getKey(), RecieverPhone, sender, dataSnapshot.getValue().toString(), "text", -1));
                        Handler.addMessage(new MessageModel(dataSnapshot.getKey(), RecieverPhone, sender, dataSnapshot.getValue().toString(), "text", -1));
                        dataSnapshot.getRef().removeValue();


                        adapter.notifyDataSetChanged();
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

//*******************************************************************************************************************************************************

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.child("users").child(RecieverPhone).child(sender).removeEventListener(chreceiver);
        //reference.child("users").child(sender).removeEventListener(chsender);
        chats.clear();
        //Handler.close();
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

            MessageModel messageModel = new MessageModel("0", sender, RecieverPhone, uri.toString(), "image", 2);
            chats.add(messageModel);
            adapter.notifyDataSetChanged();
            Messages.scrollToPosition(chats.size() - 1);

            new UploadImage(chats.size() - 1).execute();
    }
    }

    private class UploadImage extends AsyncTask<Void,Void,Boolean>
    {
        int index;

        UploadImage(int position)
        {
            index = position;
        }

        @Override
        protected Boolean doInBackground(Void... avoid) {

            UploadTask uploadTask =rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/"+chats.get(index).getReciever()).child("images/"+Uri.parse(chats.get(index).getMessage()).getLastPathSegment()).
                    putFile(Uri.parse(chats.get(index).getMessage()));

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + chats.get(index).getReciever()).child("images/" + Uri.parse(chats.get(index).getMessage()).getLastPathSegment()).getDownloadUrl().
                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //  Toast.makeText(context, "hi", Toast.LENGTH_LONG).show();

                                    String id = reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                            child(chats.get(index).getReciever()).child("info").
                                            child("images").push().getKey();

                                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                            child(chats.get(index).getReciever()).child("info").
                                            child("images").child(id).setValue(uri.toString());

                                    chats.get(index).setId(id);
                                    chats.get(index).setDownloaded(1);
                                    adapter.notifyDataSetChanged();
                                    Handler.addMessage(chats.get(index));
                                }
                            });
                }
            });
            return null;
        }
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
        new DownloadTask(index).execute(stringToURL(chats.get(index).getMessage()));
    }

    //***********************************************************************************************************************************************
    private class DownloadTask extends AsyncTask<URL,Void,Void>
    {
        int index;

        DownloadTask(int position)
        {
            index = position;
        }

        protected void onPreExecute(){
        }

        protected Void doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                StorageReference file;
                file=FirebaseStorage.getInstance().getReferenceFromUrl(chats.get(index).getMessage());
                file.delete();

                Uri imageInternalUri = saveImageToInternalStorage(bmp);
                chats.get(index).setDownloaded(1);
                chats.get(index).setMessage(imageInternalUri.toString());
                adapter.notifyDataSetChanged();
                Handler.UpdateMessage(chats.get(index));

            }catch(IOException e){
                e.printStackTrace();
            } finally{
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Void avoid){

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
