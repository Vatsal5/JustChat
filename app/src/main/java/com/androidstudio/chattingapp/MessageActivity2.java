package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.androidstudio.chattingapp.MessageActivity.getPath;
import static java.lang.System.in;
import static java.lang.System.out;

public class MessageActivity2 extends AppCompatActivity implements MessageAdapter.ImageSelected, com.androidstudio.chattingapp.gif_adapter.ItemSelected {

    String groupKey, groupname,profile;
    ImageView ivSend,ivBack,ivProfile;
    RecyclerView Messages;
    TextView tvTitle,tvMode;
    ArrayList<String> membernumber;
    EmojiconEditText etMessage;
    ConstraintLayout llMessageActivity2;
    ConstraintLayout rl;
    LinearLayout ll;
    static MessageActivity2 messageActivity2;
    StorageReference rf;
    int messagecount;
    int numberOfMembers=-1;
    SharedPreferences preftheme;

    ArrayList<String > gifurl;
    gif_adapter gif_adapter;
    PopupWindow popupWindow;

    Ringtone sent,received;
    ImageView emojibtn;

    SearchView searchview;

    LinearLayoutManager manager;
    MessageAdapter adapter;
    Integer HandlerIndex;
    ArrayList<MessageModel> chats;
    DBHandler Handler;
    int y=0,z=0;
     String sender;
    ChildEventListener imagereceiver, videoreceiver, chreceiver,seenmessages,gifreceiver;
    ValueEventListener deletevideo,deleteimage,set,set1,set2;
    String defaultvalue;
    SharedPreferences pref,wallpaper;
    RecyclerView.AdapterDataObserver observer;

    public static MessageActivity2 getInstance(){
        return messageActivity2;
    }

    public void getMessages() {
        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Mode", 0);
        defaultvalue = pref.getString("mode" + groupKey, "null");


        if (defaultvalue.equals("private")) {
            tvMode.setText("Private");
            if (chats.size() != 0) {
                chats.clear();
            }
            if (!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();
        } else {
            tvMode.setText("Public");
            if (chats.size() != 0) {
                chats.clear();
                if(!Messages.isComputingLayout())
                    adapter.notifyDataSetChanged();
            }
            Pair<ArrayList<MessageModel> , Integer> pair = Handler.getGroupMessages(groupname,0);
            HandlerIndex = pair.second;
            chats.addAll(pair.first);

            if (!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size() - 1);
        }
    }

    public  void gifSearch(String query){

        gifurl.clear();
        RequestQueue r = Volley.newRequestQueue(MessageActivity2.this);
        JsonObjectRequest j = new JsonObjectRequest(Request.Method.GET,
                "http://api.giphy.com/v1/gifs/search?q="+query+"&api_key=M7poelh7604JssbY9PPRGO9u7FzOfK5l",
                null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray j1 = (JSONArray) response.getJSONArray("data");
                    for (int i = 0; i < j1.length(); i++) {
                        JSONObject j2 = (JSONObject) j1.getJSONObject(i);
                        JSONObject j3 = (JSONObject) j2.getJSONObject("images");


                        JSONObject j4 = (JSONObject) j3.getJSONObject("preview_gif");
                        // Log.d("asdf",names.get(j).toString());
                        // JSONObject j3 = (JSONObject) j2.getJSONObject("images");
                        String url= j4.getString("url");
                        gifurl.add("g"+url);
                        gif_adapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();
            }
        });

        r.add(j);
    }

    public  void stickerSearch(String query){

        gifurl.clear();
        RequestQueue r = Volley.newRequestQueue(MessageActivity2.this);
        JsonObjectRequest j = new JsonObjectRequest(Request.Method.GET,
                "http://api.giphy.com/v1/stickers/search?q="+query+"&api_key=M7poelh7604JssbY9PPRGO9u7FzOfK5l",
                null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray j1 = (JSONArray) response.getJSONArray("data");
                    for (int i = 0; i < j1.length(); i++) {
                        JSONObject j2 = (JSONObject) j1.getJSONObject(i);
                        JSONObject j3 = (JSONObject) j2.getJSONObject("images");


                        JSONObject j4 = (JSONObject) j3.getJSONObject("original_still");
                        // Log.d("asdf",names.get(j).toString());
                        // JSONObject j3 = (JSONObject) j2.getJSONObject("images");
                        String url= j4.getString("url");
                        gifurl.add("s"+url);
                        gif_adapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();
            }
        });

        r.add(j);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);

        sent = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://"+getPackageName()+"/raw/sharp"));
        received = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://"+getPackageName()+"/raw/received"));

        ivProfile=findViewById(R.id.ivProfile);
        ll=findViewById(R.id.ll);
        messageActivity2=this;

        messagecount=getIntent().getIntExtra("messagecount",2);

        groupKey = getIntent().getStringExtra("groupkey");
        ApplicationClass.CurrentReceiver = groupKey;
        emojibtn=findViewById(R.id.emoji_btn);

        ApplicationClass.MessageActivity2Context = MessageActivity2.this;
        membernumber=new ArrayList<>();
        preftheme=getSharedPreferences("theme",0);

        pref  = getSharedPreferences("Names",0);



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
        sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        ivSend = findViewById(R.id.ivSend);
        rf = FirebaseStorage.getInstance().getReference("docs/");
        etMessage = findViewById(R.id.etMessage);
        llMessageActivity2 = findViewById(R.id.llMessageActivity2);

        EmojIconActions emojIcon=new EmojIconActions(this,llMessageActivity2,etMessage,emojibtn);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard,R.drawable.smiley);

        tvTitle = findViewById(R.id.title);
        ivBack = findViewById(R.id.ivBack);
        tvMode = findViewById(R.id.tvMode);
        rl = findViewById(R.id.rl);


        tvTitle.setText(groupname);

        tvMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity2.this,Mode.class);
                intent.putExtra("number",groupKey);
                startActivityForResult(intent,1500);
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
            if(getBackground(Uri.parse(wallpaper.getString("value","null")))!=null)
                getWindow().setBackgroundDrawable(getBackground(Uri.parse(wallpaper.getString("value","null"))));
        }
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

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        Messages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(i3<i7) {
                    Messages.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Messages.scrollToPosition(chats.size() - 1);
                        }
                    }, 1);
                }
            }
        });

        manager = new LinearLayoutManager(this);
        Messages.setLayoutManager(manager);

        Handler = new DBHandler(this);
        Handler.Open();

        chats = new ArrayList<>();

        adapter = new MessageAdapter(MessageActivity2.this, chats);
        adapter.setHasStableIds(true);
        Messages.setAdapter(adapter);

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                manager.scrollToPosition(chats.size()-1);
            }
        };

        adapter.registerAdapterDataObserver(observer);

        Messages.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(manager.findFirstCompletelyVisibleItemPosition()==0){

                    if(HandlerIndex!=-1) {
                        final int pos = manager.findLastVisibleItemPosition();
                        final Pair<ArrayList<MessageModel>, Integer> pair = Handler.getGroupMessages(groupname, HandlerIndex);
                        HandlerIndex = pair.second;

                        chats.addAll(0, pair.first);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(!Messages.isComputingLayout())
                                    adapter.notifyDataSetChanged();
                                Messages.scrollToPosition(pair.first.size()-1+pos);
                            }
                        },50);
                    }

                }

            }
        });

        getMessages();

        Date date2 = new Date();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss.SSS");
        long millis1 = System.currentTimeMillis();
        java.sql.Date date3 = new java.sql.Date(millis1);

        if(messagecount>2) {
            if (chats.size() > 0 && chats.get(chats.size() - 1).getDate().equals(date3.toString())) {
                chats.add(new MessageModel(-65, "null", "null", "null123", "unread", 1234, simpleDateFormat1.format(date2).substring(0, 8) + simpleDateFormat1.format(date2).substring(9), date3.toString(), groupname,"null"));
            }
            else
            {
                chats.add(new MessageModel(-65, "null", "null", "null123", "unread", 1234, simpleDateFormat1.format(date2).substring(0, 8) + simpleDateFormat1.format(date2).substring(9), "null", groupname,"null"));
            }
        }

//        for(int i=0;i<chats.size();i++)
//        {
//            Log.d("messageme",chats.get(i).getDate());
//        }

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
                    file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")));
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
                    file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")));
                    file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dataSnapshot.getRef().removeValue();

                        }
                    });

                }
            }

            @Override
            public void onChildRemoved(@NonNull final DataSnapshot dataSnapshot) {
                StorageReference file1;
                file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")));
                file1.delete();


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
                    file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")));
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
                            file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")));
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

                StorageReference file1;
                file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")));
                file1.delete();

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



        etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                String[] choices = {"Image", "Video","GIF","Stickers"};

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (event.getRawX() >= (etMessage.getRight() - etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        etMessage.setShowSoftInputOnFocus(false);

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
                                            case 2:
                                                LayoutInflater inflater = (LayoutInflater)
                                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                                View popupView = inflater.inflate(R.layout.popup_layout, null);

                                                // create the popup window
                                                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                                                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                boolean focusable = true; // lets taps outside the popup also dismiss it
                                                popupWindow = new PopupWindow(popupView, width, height,focusable);

                                                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));

                                                popupWindow.setAnimationStyle(R.style.DialogTheme);

                                                popupWindow.showAsDropDown(ll,0,-5*ll.getHeight()-25,Gravity.TOP);
                                                searchview=popupView.findViewById(R.id.SearchView);

                                                RecyclerView rvgif= popupView.findViewById(R.id.rvgif);
                                                rvgif.setHasFixedSize(true);
                                                gifurl=new ArrayList<>();

                                                GridLayoutManager manager = new GridLayoutManager(MessageActivity2.this,2);
                                                rvgif.setLayoutManager(manager);

                                                gif_adapter=new gif_adapter(MessageActivity2.this,gifurl);
                                                rvgif.setAdapter(gif_adapter);

                                                searchview.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                                                    @Override
                                                    public boolean onQueryTextSubmit(String query) {
                                                        gifSearch(query);
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onQueryTextChange(String newText) {

                                                        return false;
                                                    }
                                                });

                                                RequestQueue r = Volley.newRequestQueue(MessageActivity2.this);
                                                JsonObjectRequest j = new JsonObjectRequest(Request.Method.GET,
                                                        "http://api.giphy.com/v1/gifs/trending?api_key=M7poelh7604JssbY9PPRGO9u7FzOfK5l",
                                                        null, new Response.Listener<JSONObject>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            JSONArray j1 = (JSONArray) response.getJSONArray("data");
                                                            for (int i = 0; i < j1.length(); i++) {
                                                                JSONObject j2 = (JSONObject) j1.getJSONObject(i);
                                                                JSONObject j3 = (JSONObject) j2.getJSONObject("images");


                                                                JSONObject j4 = (JSONObject) j3.getJSONObject("preview_gif");
                                                                // Log.d("asdf",names.get(j).toString());
                                                                // JSONObject j3 = (JSONObject) j2.getJSONObject("images");
                                                                String url= j4.getString("url");
                                                                gifurl.add("g"+url);
                                                                gif_adapter.notifyDataSetChanged();


                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        //Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                r.add(j);
                                                break;
                                            case 3:
                                                LayoutInflater inflater1 = (LayoutInflater)
                                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                                View popupView1 = inflater1.inflate(R.layout.popup_layout, null);

                                                // create the popup window
                                                int width1 = LinearLayout.LayoutParams.MATCH_PARENT;
                                                int height1 = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                boolean focusable1 = true; // lets taps outside the popup also dismiss it
                                                popupWindow = new PopupWindow(popupView1, width1, height1,focusable1);

                                                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));

                                                popupWindow.setAnimationStyle(R.style.DialogTheme);

                                                popupWindow.showAsDropDown(ll,0,-5*ll.getHeight()-25,Gravity.TOP);
                                                searchview=popupView1.findViewById(R.id.SearchView);

                                                RecyclerView rvgif1= popupView1.findViewById(R.id.rvgif);
                                                rvgif1.setHasFixedSize(true);
                                                gifurl=new ArrayList<>();

                                                GridLayoutManager manager1 = new GridLayoutManager(MessageActivity2.this,3);
                                                rvgif1.setLayoutManager(manager1);

                                                gif_adapter=new gif_adapter(MessageActivity2.this,gifurl);
                                                rvgif1.setAdapter(gif_adapter);

                                                searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                    @Override
                                                    public boolean onQueryTextSubmit(String query) {
                                                        stickerSearch(query);
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onQueryTextChange(String newText) {

                                                        return false;
                                                    }
                                                });

                                                RequestQueue r1 = Volley.newRequestQueue(MessageActivity2.this);
                                                JsonObjectRequest j1 = new JsonObjectRequest(Request.Method.GET,
                                                        "http://api.giphy.com/v1/stickers/trending?api_key=M7poelh7604JssbY9PPRGO9u7FzOfK5l",
                                                        null, new Response.Listener<JSONObject>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            JSONArray j1 = (JSONArray) response.getJSONArray("data");
                                                            for (int i = 0; i < j1.length(); i++) {
                                                                JSONObject j2 = (JSONObject) j1.getJSONObject(i);
                                                                JSONObject j3 = (JSONObject) j2.getJSONObject("images");


                                                                JSONObject j4 = (JSONObject) j3.getJSONObject("original_still");
                                                                // Log.d("asdf",names.get(j).toString());
                                                                // JSONObject j3 = (JSONObject) j2.getJSONObject("images");

                                                                gifurl.add("s"+j4.getString("url"));
                                                                gif_adapter.notifyDataSetChanged();


                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        //Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                r1.add(j1);
                                                break;
                                        }
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else
                        etMessage.setShowSoftInputOnFocus(true);
                }
                return false;
            }
        });

        seenmessages= new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String key = dataSnapshot.getKey();
                String type = dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("m")+1);

                Log.d("asdf",type);

                if(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("m")))==0)
                {
                    Handler.UpdateMessageByFirebaseID(key,type,1);

                    for(int i=chats.size()-1;i>=0;i--)
                    {

                        if(chats.get(i).getFirebaseId().equals(key))
                        {
                            Log.d("aass",chats.get(i).getReciever());
                            if(type.equals("text")){

                                chats.get(i).setDownloaded(-5);

                            }else if(type.equals("image")){

                                chats.get(i).setDownloaded(6);

                            }
                            else if(type.equals("video")){

                                chats.get(i).setDownloaded(106);

                            }
                            else if(type.equals("gif")){
                                chats.get(i).setDownloaded(206);
                            }

                            if(!Messages.isComputingLayout())
                                adapter.notifyItemChanged(i);

                            break;
                        }
                    }

                    dataSnapshot.getRef().removeValue();
                }
                else if(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("m")))<membernumber.size())
                {
                    Handler.UpdateMessageByFirebaseID(key,type,0);

                    for(int i=chats.size()-1;i>=0;i--)
                    {

                        if(chats.get(i).getFirebaseId().equals(key))
                        {

                            if(!((chats.get(i).getDownloaded()==-4) || (chats.get(i).getDownloaded()==5) || (chats.get(i).getDownloaded()==105) || (chats.get(i).getDownloaded() == 205))) {
                                if (type.equals("text")) {

                                    chats.get(i).setDownloaded(-4);

                                } else if (type.equals("image")) {

                                    chats.get(i).setDownloaded(5);

                                } else if (type.equals("video")) {

                                    chats.get(i).setDownloaded(105);

                                }
                                else if(type.equals("gif")){
                                    chats.get(i).setDownloaded(205);
                                }

                                if (!Messages.isComputingLayout())
                                    adapter.notifyItemChanged(i);
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String type = dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("m") + 1);

                Log.d("asdf", type);

                if (Integer.parseInt(dataSnapshot.getValue().toString().substring(0, dataSnapshot.getValue().toString().indexOf("m"))) == 0) {
                    Handler.UpdateMessageByFirebaseID(key, type, 1);

                    for (int i = chats.size() - 1; i >= 0; i--) {

                        if (chats.get(i).getFirebaseId().equals(key)) {
                            Log.d("aass", chats.get(i).getReciever());
                            if (type.equals("text")) {

                                chats.get(i).setDownloaded(-5);

                            } else if (type.equals("image")) {

                                chats.get(i).setDownloaded(6);

                            } else if (type.equals("video")) {

                                chats.get(i).setDownloaded(106);

                            }
                            else if(type.equals("gif")){
                                chats.get(i).setDownloaded(206);
                            }

                            if (!Messages.isComputingLayout())
                                adapter.notifyItemChanged(i);

                            break;
                        }
                    }

                    dataSnapshot.getRef().removeValue();
                } else if (Integer.parseInt(dataSnapshot.getValue().toString().substring(0, dataSnapshot.getValue().toString().indexOf("m"))) < membernumber.size()) {
                    Handler.UpdateMessageByFirebaseID(key, type, 0);

                    for (int i = chats.size() - 1; i >= 0; i--) {

                        if (chats.get(i).getFirebaseId().equals(key)) {

                            if (!((chats.get(i).getDownloaded() == -4) || (chats.get(i).getDownloaded() == 5) || (chats.get(i).getDownloaded() == 105) || (chats.get(i).getDownloaded() == 205))) {
                                if (type.equals("text")) {

                                    chats.get(i).setDownloaded(-4);

                                } else if (type.equals("image")) {

                                    chats.get(i).setDownloaded(5);

                                } else if (type.equals("video")) {

                                    chats.get(i).setDownloaded(105);

                                }
                                else if(type.equals("gif")){
                                    chats.get(i).setDownloaded(205);
                                }

                                if (!Messages.isComputingLayout())
                                    adapter.notifyItemChanged(i);
                            }
                        }
                        break;
                    }
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
        };
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(FirebaseAuth
                .getInstance().getCurrentUser().getPhoneNumber()).addChildEventListener(seenmessages);

        imagereceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String time, date, sender;

                time = dataSnapshot.getValue(String.class).substring(0, 11);
                date = dataSnapshot.getValue(String.class).substring(11, 21);

                sender = dataSnapshot.getValue(String.class).substring(21, 34);

                set= new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .setValue(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("m")))-1
                                + dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("m")));
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .removeEventListener(set);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(set);




                MessageModel messageModel = new MessageModel(-1, sender, "null", dataSnapshot.getValue(String.class).substring(34), "image", 0, time, date, groupname,"null");
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if (chats.size() != 0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                } else {
                    if(!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                chats.add(messageModel);

                adapter.notifyItemInserted(chats.size()-1);

                if(messagecount==2)
                    received.play();
                else
                    messagecount--;

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
                final String time, date,sender;
                String uri;


                time = dataSnapshot.getValue(String.class).substring(0, 11);
                date = dataSnapshot.getValue(String.class).substring(11, 21);
                uri = dataSnapshot.getValue(String.class).substring(34);
                sender = dataSnapshot.getValue(String.class).substring(21,34);
                set1= new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .setValue(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("m")))-1
                                        + dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("m")));
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .removeEventListener(set1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(set1);

                MessageModel messageModel = new MessageModel(-1,sender,"null",uri,"video",101,time,date,groupname,"null");


                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if(!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname,"null");
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

                if(messagecount==2)
                    received.play();
                else
                    messagecount--;
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


        gifreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                final String time, date,sender;

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
                set2= new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .setValue(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("m")))-1
                                        + dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("m")));
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .removeEventListener(set2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(set2);
//
//                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
//
                        MessageModel messageModel = new MessageModel(435, sender, "null", dataSnapshot.getValue().toString().substring(34), "gif", 203,time,date,groupname,"null");

                        if(chats.size()!=0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                                MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname,"null");
                                int id = Handler.addMessage(message);
                                message.setId(id);
                                chats.add(message);
                            }
                        }
                        else {
                            if(!(defaultvalue.equals("private"))) {
                                MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname,"null");
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
                if(messagecount==2)
                        received.play();
                else
                    messagecount--;
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("gif").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(gifreceiver);


        chreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                final String time, date,sender;

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
                set2= new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .setValue(Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("m")))-1
                                        + dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("m")));
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                                .removeEventListener(set2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(sender).child(dataSnapshot.getKey())
                        .addListenerForSingleValueEvent(set2);
//
//                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
//
                MessageModel messageModel = new MessageModel(435, sender, "null", dataSnapshot.getValue().toString().substring(34), "text", -1,time,date,groupname,"null");

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date,groupname,"null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if(!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date, groupname,"null");
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
                if(messagecount==2)
                    received.play();
                else
                    messagecount--;
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

                    MessageModel model = new MessageModel(-1, sender, "null", etMessage.getText().toString(), "text", -2, simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9), date1.toString(), groupname,"null");
                    etMessage.setText(null);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    } else {
                        if((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
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

//        if(getIntent().getIntExtra("path",1)==2) {
//            String type = getIntent().getStringExtra("type");
//            String message1 = getIntent().getStringExtra("message");
//
//            if (!(type.equals(" "))) {
//                Date date = new Date();
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
//                long millis = System.currentTimeMillis();
//                java.sql.Date date1 = new java.sql.Date(millis);
//
//                if (type.equals("text")) {
//
//                    MessageModel model = new MessageModel(-1, sender, "null", etMessage.getText().toString(), "text", -2, simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9), date1.toString(), groupname);
//                    etMessage.setText(null);
//
//                    if (chats.size() != 0) {
//                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
//                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
//                            int id = Handler.addMessage(messageModel);
//                            messageModel.setId(id);
//                            chats.add(messageModel);
//                        }
//                    } else {
//                        if ((!(defaultvalue.equals("private")))) {
//                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
//                            int id = Handler.addMessage(messageModel);
//                            messageModel.setId(id);
//                            chats.add(messageModel);
//                        }
//                    }
//
//                    int id = Handler.addMessage(model);
//                    model.setId(id);
//                    chats.add(model);
//                    adapter.notifyItemInserted(chats.size() - 1);
//
//
//                } else if (type.equals("image")) {
//
//                    MessageModel messageModel = new MessageModel(-1, sender, "nul", message1, "image", 2,simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9),date1.toString(),groupname);
//
//                    if (chats.size() != 0) {
//                        if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
//                            MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
//                            int id = Handler.addMessage(message);
//                            message.setId(id);
//                            chats.add(message);
//                        }
//                    } else {
//                        if (!(defaultvalue.equals("private"))) {
//                            MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
//                            int id = Handler.addMessage(message);
//                            message.setId(id);
//                            chats.add(message);
//                        }
//                    }
//
//                    int id = Handler.addMessage(messageModel);
//                    messageModel.setId(id);
//
//                    chats.add(messageModel);
//
//                    adapter.notifyItemInserted(chats.size() - 1);
//                } else {
//                    MessageModel model = new MessageModel(1190, sender, "null", message1, "video", 100, simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9), date1.toString(), groupname);
//
//                    if (chats.size() != 0) {
//                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
//                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
//                            int id = Handler.addMessage(messageModel);
//                            messageModel.setId(id);
//                            chats.add(messageModel);
//                        }
//                    } else {
//                        if ((!(defaultvalue.equals("private")))) {
//                            MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname);
//                            int id = Handler.addMessage(messageModel);
//                            messageModel.setId(id);
//                            chats.add(messageModel);
//                        }
//                    }
//
//                    int id = Handler.addMessage(model);
//                    model.setId(id);
//                    chats.add(model);
//
//                    adapter.notifyItemInserted(chats.size() - 1);
//                }
//            }
//        }

        if (ContextCompat.checkSelfPermission(MessageActivity2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MessageActivity2.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 150);
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
            Boolean flag2=true;
            int counter=0;

            for(int i = chats.size()-1;i>=0;i--)
            {
                if(chats.get(i).getDownloaded()==2 || chats.get(i).getDownloaded()==3 || chats.get(i).getDownloaded()==4 || chats.get(i).getDownloaded()==-2
                        || chats.get(i).getDownloaded()==-3 || chats.get(i).getDownloaded()==100 || chats.get(i).getDownloaded()==103 || chats.get(i).getDownloaded()==104
                ||chats.get(i).getDownloaded()==204 || chats.get(i).getDownloaded()==201) {
                    flag2 = false;
                    break;
                }
                counter++;
                if(counter==5)
                    break;
            }
            if(flag2) {
                if (pos != 0) {
                    if (pos < chats.size() - 1) {
                        if (chats.get(pos - 1).getSender().equals("null") && chats.get(pos + 1).getSender().equals("null")) {
                            chats.remove(model);
                            Handler.DeleteMessage(model);
                            model = chats.get(pos - 1);
                            chats.remove(model);
                            Handler.DeleteMessage(model);
                            adapter.notifyItemRangeRemoved(pos-1,2);
                        } else {
                            chats.remove(model);
                            Handler.DeleteMessage(model);
                            adapter.notifyItemRemoved(pos);
                        }
                    } else {
                        if (chats.get(pos - 1).getSender().equals("null")) {
                            chats.remove(model);
                            Handler.DeleteMessage(model);
                            model = chats.get(pos - 1);
                            chats.remove(model);
                            Handler.DeleteMessage(model);
                            adapter.notifyItemRangeRemoved(pos-1,2);
                        } else {
                            chats.remove(model);
                            Handler.DeleteMessage(model);
                            adapter.notifyItemRemoved(pos);
                        }

                    }
                } else {
                    chats.remove(model);
                    Handler.DeleteMessage(model);
                    adapter.notifyItemRemoved(pos);
                }
            }
            else
            {
                adapter.notifyItemChanged(pos);
                Toast.makeText(MessageActivity2.this, "Messages cannot be deleted if there are pending messages", Toast.LENGTH_SHORT).show();
            }

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
                    chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 204|| chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 201||
                    chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 60 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == -3
            || chats.get(viewHolder.getAdapterPosition()).getType().equals("unread"))){

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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("gif").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).addChildEventListener(gifreceiver);

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
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
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
                                dialogInterface.dismiss();
                                MessageActivity2.this.finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
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

                            File file = new File(getPath(MessageActivity2.this, videoURI));

                            long fileSizeInBytes = file.length();
                            long fileSizeInKB = fileSizeInBytes / 1024;
                            long fileSizeInMB = fileSizeInKB / 1024;


                            if (fileSizeInMB >= 15) {
                                Toast.makeText(this, "Video files lesser than 15MB are allowed", Toast.LENGTH_LONG).show();

                            } else {
                                Date date = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

                                long millis = System.currentTimeMillis();
                                java.sql.Date date1 = new java.sql.Date(millis);

                                MessageModel model = new MessageModel(1190, sender, "null", videoURI.toString(), "video", 100, simpleDateFormat.format(date).substring(0, 8) + simpleDateFormat.format(date).substring(9), date1.toString(), groupname,"null");

                                if (chats.size() != 0) {
                                    if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                                        MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
                                        int id = Handler.addMessage(messageModel);
                                        messageModel.setId(id);
                                        chats.add(messageModel);
                                    }
                                } else {
                                    if ((!(defaultvalue.equals("private")))) {
                                        MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
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
                    } else {
                        Toast.makeText(this, "You cannot send more than 5 videos at a time", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Uri videoURI = data.getData();
                    File file = new File(getPath(MessageActivity2.this, videoURI));

                    long fileSizeInBytes = file.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;


                    if (fileSizeInMB >= 15) {
                        Toast.makeText(this, "Video files lesser than 15MB are allowed", Toast.LENGTH_LONG).show();

                    } else {
                        Uri uri = data.getData();
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

                        long millis = System.currentTimeMillis();
                        java.sql.Date date1 = new java.sql.Date(millis);

                        MessageModel model = new MessageModel(1190, sender, "null", uri.toString(), "video", 100, simpleDateFormat.format(date).substring(0, 8) + simpleDateFormat.format(date).substring(9), date1.toString(), groupname,"null");

                        if (chats.size() != 0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                                MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
                                int id = Handler.addMessage(messageModel);
                                messageModel.setId(id);
                                chats.add(messageModel);
                            }
                        } else {
                            if ((!(defaultvalue.equals("private")))) {
                                MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
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
        }

        if(requestCode==1500)
        {
            getMessages();
        }

    }

    @Override
    public void showImage(int index) {

        if(chats.get(index).getDownloaded()!=0 && chats.get(index).getDownloaded()!=4 && !chats.get(index).getType().equals("video")) {

            Intent intent = new Intent(MessageActivity2.this, ShowImage.class);

            intent.putExtra("source", chats.get(index).getMessage());

            startActivity(intent);
        }
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("gif").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(gifreceiver);


        overridePendingTransition(0, 0);
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
        Intent intent= new Intent(MessageActivity2.this,VideoActivity.class);
        intent.putExtra("uri",chats.get(index).getMessage());
        startActivity(intent);
    }

    @Override
    public void Onlongclick(final int index) {

        if (!chats.get(index).getMessage().equals("null") && chats.get(index).getDownloaded()!=0 && chats.get(index).getDownloaded()!=4) {

            if (!(chats.get(index).getType().equals("video") || chats.get(index).getType().equals("image"))) {
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
            } else {
                String[] choices = {"Forward"};
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


    }

    @Override
    public void OnFileDeleted(int index) {

        chats.get(index).setMessage("null");

        Handler.UpdateMessage(chats.get(index));

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

    }

    @Override
    public void sendGIF(int index) {
        SendGIF(index,chats.get(index));
    }

    @Override
    public void downloadGIF(int index) {
        new DownloadGIF(index,chats.get(index)).execute(chats.get(index).getMessage());
    }

    @Override
    public void sendSticker(int index) {

    }

    @Override
    public void downloadSticker(int index) {

    }

    public void SendGIF(final int index,final MessageModel model)
    {
        ApplicationClass.PendingRequests.add(groupKey);
        final int[] x = {0};

        model.setDownloaded(201);
        Handler.UpdateMessage(model);

        chats.get(index).setDownloaded(201);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

        final String push= FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("gif")
                .child(membernumber.get(0)).push().getKey();
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(FirebaseAuth
                .getInstance().getCurrentUser().getPhoneNumber()).child(push).setValue(membernumber.size()+"mgif");

        final String message = chats.get(index).getMessage();

        for(int i=0;i<membernumber.size();i++) {
            FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("gif").child(membernumber.get(i)).

                    child(push).setValue(model.getTime() + model.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + model.getMessage().substring(model.getMessage().lastIndexOf(" ")+1))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (x[0] == 0) {
                                x[0] = 1;
                                model.setMessage(message.substring(0,message.lastIndexOf(" ")));
                                model.setFirebaseId(push);
                                model.setDownloaded(202);
                                Handler.UpdateMessage(model);

                                if (!MessageActivity2.this.isDestroyed()) {
                                    chats.get(index).setMessage(message.substring(0,message.lastIndexOf(" ")));
                                    chats.get(index).setFirebaseId(push);
                                    chats.get(index).setDownloaded(202);
                                    sent.play();

                                    if (!Messages.isComputingLayout())
                                        adapter.notifyItemChanged(index);
                                }

                                if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                                        Intent intent = getIntent();
                                        ((Activity) ApplicationClass.MessageActivity2Context).finish();
                                        startActivity(intent);
                                        sent.play();

                                        overridePendingTransition(0, 0);
                                    }
                                }
                            }
                            ApplicationClass.PendingRequests.remove(groupKey);
                        }
                    });
        }

    }

    public void SendMessage(final int index, final MessageModel model)
            {
                ApplicationClass.PendingRequests.add(groupKey);
                final int[] x = {0};

            model.setDownloaded(-3);
            Handler.UpdateMessage(model);

            chats.get(index).setDownloaded(-3);

            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);

            final String push= FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages")
                    .child(membernumber.get(0)).push().getKey();
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(FirebaseAuth
                        .getInstance().getCurrentUser().getPhoneNumber()).child(push).setValue(membernumber.size()+"mtext");

            for(int i=0;i<membernumber.size();i++) {
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("messages")
                        .child(membernumber.get(i)).child(push).setValue(
                        model.getTime() + model.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + model.getMessage())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (x[0] == 0) {
                                    x[0] = 1;
                                    model.setFirebaseId(push);
                                    model.setDownloaded(-1);
                                    Handler.UpdateMessage(model);

                                    if (!MessageActivity2.this.isDestroyed()) {
                                        chats.get(index).setFirebaseId(push);
                                        chats.get(index).setDownloaded(-1);
                                        sent.play();

                                        if (!Messages.isComputingLayout())
                                            adapter.notifyItemChanged(index);
                                    }

                                    if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                                        if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                                            Intent intent = getIntent();
                                            ((Activity) ApplicationClass.MessageActivity2Context).finish();
                                            startActivity(intent);
                                            sent.play();

                                            overridePendingTransition(0, 0);
                                        }
                                    }
                                }
                                ApplicationClass.PendingRequests.remove(groupKey);
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

    @Override
    public void ImageClicked(final int index) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Glide.with(MessageActivity2.this)
                .download(gifurl.get(index).substring(1))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {

                        File imagesFolder = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Sent");
                        if(!imagesFolder.exists())
                            imagesFolder.mkdirs();

                        File file = new File(imagesFolder,System.currentTimeMillis()+".gif");

                        try{
                            InputStream in = new FileInputStream(resource);
                            OutputStream out = new FileOutputStream(file);
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            in.close();
                            dialog.dismiss();
                            prepareGif(Uri.fromFile(file),gifurl.get(index).substring(1));

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            out.close();
                        }

                        return false;
                    }
                }).submit();
    }

    @Override
    public void StickerClicked(int index) {

    }

    public void prepareGif(Uri uri, String url)
    {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

        MessageModel model = new MessageModel(-349,sender,"null",uri+" "+url,
                "gif",200,simpleDateFormat.format(date).substring(0, 8) + simpleDateFormat.format(date).substring(9), date1.toString(), groupname,"null");

        if (chats.size() != 0) {
            if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname,"null");
                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);
                chats.add(messageModel);
            }
        } else {
            if ((!(defaultvalue.equals("private")))) {
                MessageModel messageModel = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
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

    class CompressImage extends AsyncTask<Uri,Void,Uri>
    {
        @Override
        protected Uri doInBackground(Uri... uris) {
            String filePath = getPath(MessageActivity2.this,uris[0]);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Sent");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }

            // Create a file to save the image
            File file = new File(imagesFolder, System.currentTimeMillis()+".jpg");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return Uri.fromFile(file);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            Date date=new Date();
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm:ss.SSS");

            long millis=System.currentTimeMillis();
            java.sql.Date date1=new java.sql.Date(millis);

            MessageModel messageModel = new MessageModel(-1, sender, "nul", uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,8)+simpleDateFormat.format(date).substring(9),date1.toString(),groupname,"null");

            if(chats.size()!=0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(),groupname,"null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }
            else {
                if(!(defaultvalue.equals("private"))) {
                    MessageModel message = new MessageModel(54, "null", "null", "null", "Date", 60, "null", date1.toString(), groupname,"null");
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

    private class DownloadGIF extends AsyncTask<String, Void, Uri>
    {
        int index;
        MessageModel message;

        DownloadGIF(int position,MessageModel message)
        {
            index = position;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ApplicationClass.PendingRequests.add(groupKey);

            message.setDownloaded(204);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(204);
            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);

        }

        @Override
        protected Uri doInBackground(String... strings) {
            InputStream urlInputStream = null;

            URLConnection urlConnection;

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"ChattingApp/Received/"+System.currentTimeMillis()+".gif");

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

            if (uri != null) {

                message.setDownloaded(202);
                message.setMessage(uri.toString());

                Handler.UpdateMessage(message);

                if(!MessageActivity2.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(202);
                    chats.get(index).setMessage(uri.toString());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else {
                message.setDownloaded(203);
                Handler.UpdateMessage(message);

                if (!MessageActivity2.this.isDestroyed()) {
                    chats.get(index).setDownloaded(203);
                    adapter.notifyItemChanged(index);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);
                    builder.setTitle("Could not download gif");
                    builder.setMessage("Please ask " + pref.getString(message.getSender(), message.getSender()) +
                            " to resend the gif")
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
            ApplicationClass.PendingRequests.remove(groupKey);
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

            ApplicationClass.PendingRequests.add(groupKey);

            message.setDownloaded(104);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(104);
            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);
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
                        fos.write(buffer);
                        fos.flush();
                        fos.close();
                      deletevideo=  new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").child(dataSnapshot.getKey())
                                                .setValue(( Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("h")-10))-1)+dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")-10))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").child(message.getTime()+message.getDate()
                                                        +message.getSender())
                                                        .removeEventListener(deletevideo);

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                       FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").child(message.getTime()+message.getDate()
                                 +message.getSender())
                                .addListenerForSingleValueEvent(deletevideo);

                        return Uri.fromFile(file);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        /*Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();*/
                    }
                } else {
                    //Could not download the file...

                }
            }catch (FileNotFoundException e) {
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
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                    if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        ((Activity) ApplicationClass.MessageActivity2Context).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else{
                message.setDownloaded(101);
                Handler.UpdateMessage(message);

                if(!MessageActivity2.this.isDestroyed()) {
                    chats.get(index).setDownloaded(101);
                    adapter.notifyItemChanged(index);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);
                    builder.setTitle("Could not download Video");
                    builder.setMessage("Please ask " + pref.getString(message.getSender(), message.getSender()) +
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
            ApplicationClass.PendingRequests.remove(groupKey);
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

            ApplicationClass.PendingRequests.add(groupKey);
            message.setDownloaded(4);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(4);
            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);
        }

        protected Uri doInBackground(URL...urls){
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
                        fos.write(buffer);
                        fos.flush();
                        fos.close();
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deleteimages").child(message.getTime()+message.getDate()
                        +message.getSender())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deleteimages").child(dataSnapshot.getKey())
                                                .setValue(( Integer.parseInt(dataSnapshot.getValue().toString().substring(0,dataSnapshot.getValue().toString().indexOf("h")-10))-1)+dataSnapshot.getValue().toString().substring(dataSnapshot.getValue().toString().indexOf("h")-10));
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

                }
            }catch (FileNotFoundException e)
            {
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

                if(!MessageActivity2.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(1);
                    chats.get(index).setMessage(result.toString());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                    if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        ((Activity) ApplicationClass.MessageActivity2Context).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else
            {
                message.setDownloaded(0);
                Handler.UpdateMessage(message);

                if(!MessageActivity2.this.isDestroyed()) {
                    chats.get(index).setDownloaded(0);
                    adapter.notifyItemChanged(index);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity2.this);
                    builder.setTitle("Could not download Image");
                    builder.setMessage("Please ask " + pref.getString(message.getSender(), message.getSender()) +
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
            ApplicationClass.PendingRequests.remove(groupKey);
        }
    }

    public void UploadImage(final int index, final MessageModel message)
    {
        ApplicationClass.PendingRequests.add(groupKey);
        final int[] y = {0};

        message.setDownloaded(3);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(3);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

        rf.child(groupKey).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(groupKey).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deleteimages").

                                        child(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(numberOfMembers + message.getDate()+  uri.toString());

                                 String push=FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(membernumber.get(0)).

                                          push().getKey();

                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(FirebaseAuth
                                .getInstance().getCurrentUser().getPhoneNumber()).child(push).setValue(membernumber.size()+"mimage");


                                for(int i=0;i<membernumber.size();i++) {

                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("images").child(membernumber.get(i)).

                                            child(push).setValue(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + uri.toString());

                                    if(y[0] ==0) {
                                        y[0] =1;
                                        message.setFirebaseId(push);
                                        message.setDownloaded(1);
                                        Handler.UpdateMessage(message);

                                        if (!MessageActivity2.this.isDestroyed()) {
                                            chats.get(index).setFirebaseId(push);
                                            chats.get(index).setDownloaded(1);
                                            sent.play();

                                            if (!Messages.isComputingLayout()) {
                                                adapter.notifyItemChanged(index);
                                            }
                                        }

                                        if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                                            if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                                                Intent intent = getIntent();
                                                ((Activity) ApplicationClass.MessageActivity2Context).finish();
                                                startActivity(intent);

                                                sent.play();

                                                overridePendingTransition(0, 0);
                                            }
                                        }
                                    }
                                }
                                ApplicationClass.PendingRequests.remove(groupKey);
                            }
                       });

            }
        });
    }


    public void uploadVideo(final int index, final MessageModel message)
    {
        ApplicationClass.PendingRequests.add(groupKey);
        final int[] z = {0};
        message.setDownloaded(103);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(103);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

        rf.child(groupKey).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.parse(message.getMessage())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(groupKey).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String push= FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).
                                        child("videos").child(membernumber.get(0)).
                                        push().getKey();
                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(FirebaseAuth
                                        .getInstance().getCurrentUser().getPhoneNumber()).child(push).setValue(membernumber.size()+"mvideo");
                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("deletevideos").

                                        child(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() ).setValue(numberOfMembers+message.getDate() + uri.toString());

                                for(int i=0;i<membernumber.size();i++) {

                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).
                                            child("videos").child(membernumber.get(i)).
                                            child(push).setValue(message.getTime() + message.getDate() + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + uri.toString());

                                    if (z[0] == 0) {
                                        z[0] = 1;
                                        message.setFirebaseId(push);
                                        message.setDownloaded(102);
                                        Handler.UpdateMessage(message);


                                        if (!MessageActivity2.this.isDestroyed()) {
                                            chats.get(index).setFirebaseId(push);
                                            chats.get(index).setDownloaded(102);
                                            sent.play();

                                            if (!Messages.isComputingLayout()) {
                                                adapter.notifyItemChanged(index);
                                            }
                                        }

                                        if (MessageActivity2.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivity2Context).isDestroyed()) {
                                            if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver))
                                            {
                                                Intent intent = getIntent();
                                                ((Activity) ApplicationClass.MessageActivity2Context).finish();
                                                startActivity(intent);

                                                sent.play();

                                                overridePendingTransition(0, 0);
                                            }
                                        }
                                    }
                                }
                                ApplicationClass.PendingRequests.remove(groupKey);
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

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("gif").child(
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
        ).removeEventListener(gifreceiver);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("seenmessages").child(FirebaseAuth
                .getInstance().getCurrentUser().getPhoneNumber()).removeEventListener(seenmessages);

        adapter.unregisterAdapterDataObserver(observer);
        ApplicationClass.CurrentReceiver="";
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
            return null;
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String selection = null;
        String[] selectionArgs = null;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (fullPath != "") {
                    return fullPath;
                } else {
                    return null;
                }
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final String id;
                    Cursor cursor = null;
                    try {
                        cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));

                         /*   final Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));*/

                                return getDataColumn(context, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                //In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }


                    }

                } else {
                    Uri contentUri = null;
                    final String id = DocumentsContract.getDocumentId(uri);
                    final boolean isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null);
                    }
                }


            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};


                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            } else if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
        }


        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
            if( Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
            {
                // return getFilePathFromURI(context,uri);
                return getMediaFilePathForN(uri, context);
                // return getRealPathFromURI(context,uri);
            }else
            {

                return getDataColumn(context, uri, null, null);
            }


        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Check if a file exists on device
     *
     * @param filePath The absolute file path
     */
    private static boolean fileExists(String filePath) {
        File file = new File(filePath);

        return file.exists();
    }


    /**
     * Get full file path from external storage
     *
     * @param pathData The storage type and the relative path
     */
    private static String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        return fullPath;
    }

    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
        }
        return file.getPath();
    }

    private static String getMediaFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
        }
        return file.getPath();
    }


    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
