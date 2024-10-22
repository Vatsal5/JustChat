package com.androidstudio.chattingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static java.lang.System.in;
import static java.lang.System.out;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.ImageSelected , gif_adapter.ItemSelected {

    EmojiconEditText etMessage;
    ImageView ivSend,ivProfile,ivBack,ivStatus;
    String RecieverPhone;
    FirebaseDatabase database;
    DatabaseReference reference;
    String lastpath;
    SharedPreferences pref,wallpaper;
    SharedPreferences preftheme;
    Integer HandlerIndex;

    ArrayList<String> gifurl;

    StorageReference rf;
    SharedPreferences messagesent;
    int messagecount;
    ConstraintLayout llMessageActivity;
    LinearLayout ll;
    SharedPreferences pref1;

    PopupWindow popupWindow;

    TextView title;
    String to = "";
    RecyclerView Messages;
    String sender;
    LinearLayoutManager manager;
    MessageAdapter adapter;
    ArrayList<MessageModel> chats;
    ChildEventListener chreceiver,videoreceiver,gifreceiver,stickerreceiver,messageseen,pdfreceiver;

    gif_adapter gif_adapter;
    DBHandler Handler;
    SearchView searchview;
    ImageView emojibtn;
    int l;
    int flag=0;
    ChildEventListener imagereceiver;
    ValueEventListener Status;

    Ringtone sent,received;

    OnCompleteListener SendMesage;
    String defaultvalue;

    public static MessageActivity messageActivity;

    boolean flag1 = false;
    String dpUrl ="null";
    ValueEventListener dp;

    RecyclerView.AdapterDataObserver observer;

    public void getMessages() {
        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Mode", 0);
        defaultvalue = pref.getString("mode" + RecieverPhone, "null");
    //    Log.d("mode", defaultvalue);

        if (defaultvalue.equals("private")) {
            if (chats.size() != 0) {
                chats.clear();
            }
            HandlerIndex = -1;
            if (!Messages.isComputingLayout())
                adapter.notifyDataSetChanged();
        } else {
            if (chats.size() != 0) {
                chats.clear();
                if(!Messages.isComputingLayout())
                    adapter.notifyDataSetChanged();
            }
            Pair<ArrayList<MessageModel>,Integer> pair = Handler.getMessages(RecieverPhone,0);
            HandlerIndex = pair.second;

            chats.addAll(pair.first);
            if (!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size() - 1);
        }

        if (flag1) {
            chats.add(new MessageModel(-678, "null  ", "null  ", "jgvjhv", "typing", 45, "null  ", date1.toString(), "null","null"));

            if(!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size()-1);
        }
    }

    public  void gifSearch(String query){

        gifurl.clear();
        gif_adapter.notifyDataSetChanged();
        RequestQueue r = Volley.newRequestQueue(MessageActivity.this);
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


                        JSONObject j4 = (JSONObject) j3.getJSONObject("fixed_width_downsampled");
                        // Log.d("asdf",names.get(j).toString());
                        // JSONObject j3 = (JSONObject) j2.getJSONObject("images");
                        String url= j4.getString("url");
                        gifurl.add("g"+url);
                        gif_adapter.notifyItemInserted(gifurl.size()-1);

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
        gif_adapter.notifyDataSetChanged();
        RequestQueue r = Volley.newRequestQueue(MessageActivity.this);
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



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        RecieverPhone = getIntent().getStringExtra("phone");
        ApplicationClass.CurrentSender = RecieverPhone;
        ApplicationClass.CurrentReceiver = RecieverPhone;

        sent = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://"+getPackageName()+"/raw/sharp"));
        received = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://"+getPackageName()+"/raw/received"));

        ll=findViewById(R.id.ll);

        messageActivity=this;

        ivSend = findViewById(R.id.ivSend);
        preftheme=getSharedPreferences("theme",0);
        emojibtn=findViewById(R.id.emoji_btn);

        pref1 = getSharedPreferences("Names",0);
        wallpaper = getSharedPreferences("Wallpaper",0);
        String theme=preftheme.getString("theme","red");

        Drawable mDrawable = getResources().getDrawable(R.drawable.attachment); //Your drawable image
        mDrawable = DrawableCompat.wrap(mDrawable);


        switch (theme) {
            case "orange":
                ll.setBackgroundColor(getResources().getColor(R.color.Orange));
                mDrawable.setTint(getResources().getColor(R.color.Orange));
                ivSend.setColorFilter(getResources().getColor(R.color.Orange));
                break;
            case "blue":
                ll.setBackgroundColor(getResources().getColor(R.color.blue));
                mDrawable.setTint(getResources().getColor(R.color.blue));
                ivSend.setColorFilter(getResources().getColor(R.color.blue));
                break;
            case "bluish":
                ll.setBackgroundColor(getResources().getColor(R.color.bluish));
                mDrawable.setTint(getResources().getColor(R.color.bluish));
                ivSend.setColorFilter(getResources().getColor(R.color.bluish));
                break;
            case "deepred":
                ll.setBackgroundColor(getResources().getColor(R.color.deepred));
                mDrawable.setTint(getResources().getColor(R.color.deepred));
                ivSend.setColorFilter(getResources().getColor(R.color.deepred));
                break;
            case "faintpink":
                ll.setBackgroundColor(getResources().getColor(R.color.faintpink));
                mDrawable.setTint(getResources().getColor(R.color.faintpink));
                ivSend.setColorFilter(getResources().getColor(R.color.faintpink));
                break;
            case "darkblue":
                ll.setBackgroundColor(getResources().getColor(R.color.darkblue));
                mDrawable.setTint(getResources().getColor(R.color.darkblue));
                ivSend.setColorFilter(getResources().getColor(R.color.darkblue));
                break;
            case "green":
                ll.setBackgroundColor(getResources().getColor(R.color.green));
                mDrawable.setTint(getResources().getColor(R.color.green));
                ivSend.setColorFilter(getResources().getColor(R.color.green));
                break;
            case "lightorange":
                ll.setBackgroundColor(getResources().getColor(R.color.lightorange));
                mDrawable.setTint(getResources().getColor(R.color.lightorange));
                ivSend.setColorFilter(getResources().getColor(R.color.lightorange));
                break;
            case "lightred":
                ll.setBackgroundColor(getResources().getColor(R.color.lightred));
                mDrawable.setTint(getResources().getColor(R.color.lightred));
                ivSend.setColorFilter(getResources().getColor(R.color.lightred));
                break;
            case "mustard":
                ll.setBackgroundColor(getResources().getColor(R.color.mustard));
                mDrawable.setTint(getResources().getColor(R.color.mustard));
                ivSend.setColorFilter(getResources().getColor(R.color.mustard));
                break;
            case "pink":
                ll.setBackgroundColor(getResources().getColor(R.color.pink));
                mDrawable.setTint(getResources().getColor(R.color.pink));
                ivSend.setColorFilter(getResources().getColor(R.color.pink));
                break;
            case "pureorange":
                ll.setBackgroundColor(getResources().getColor(R.color.pureorange));
                mDrawable.setTint(getResources().getColor(R.color.pureorange));
                ivSend.setColorFilter(getResources().getColor(R.color.pureorange));

                break;
            case "purepink":
                ll.setBackgroundColor(getResources().getColor(R.color.purepink));
                mDrawable.setTint(getResources().getColor(R.color.purepink));
                ivSend.setColorFilter(getResources().getColor(R.color.purepink));
                break;
            case "purple":
                ll.setBackgroundColor(getResources().getColor(R.color.purple));
                mDrawable.setTint(getResources().getColor(R.color.purple));
                ivSend.setColorFilter(getResources().getColor(R.color.purple));

                break;
            default:
                ll.setBackgroundColor(getResources().getColor(R.color.red));
                mDrawable.setTint(getResources().getColor(R.color.red));
                ivSend.setColorFilter(getResources().getColor(R.color.red));

                break;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setOverflowIcon(getDrawable(R.drawable.overflow));
        }

        final int[] index = new int[1];

        title = findViewById(R.id.title);
        Handler = new DBHandler(MessageActivity.this);
        Handler.Open();
        messagecount=getIntent().getIntExtra("messagecount",2);

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        title.setText(pref1.getString(getIntent().getStringExtra("title"),getIntent().getStringExtra("title")));

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sender = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        chats = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users").child(sender).child("profile").addListenerForSingleValueEvent(dp);


        pref= getApplicationContext().getSharedPreferences("Mode",0);
        defaultvalue = pref.getString("mode"+RecieverPhone,"null");
        etMessage = findViewById(R.id.etMessage);

        etMessage.setCompoundDrawablesWithIntrinsicBounds(null,null,mDrawable,null);

        ivProfile = findViewById(R.id.ivProfile);
        ivBack = findViewById(R.id.ivBack);
        ivStatus = findViewById(R.id.ivStatus);
        llMessageActivity = findViewById(R.id.llMessageActivity);

        EmojIconActions  emojIcon=new EmojIconActions(this,llMessageActivity,etMessage,emojibtn);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard,R.drawable.smiley);

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
                        chats.add(new MessageModel(-678, "null  ", "null  ", "jgvjhv", "typing", 45, "null  ", date1.toString(),"null","null"));
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
                                adapter.notifyItemRemoved(chats.size()-1);

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
                                adapter.notifyItemRemoved(chats.size()-1);

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

                String [] choices = {"Image","Video","GIF","Stickers","Record Video","PDF","Capture Image"};

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etMessage.getRight() - etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        etMessage.setShowSoftInputOnFocus(false);


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
                                            case 2:

                                                LayoutInflater inflater = (LayoutInflater)
                                                        getSystemService(LAYOUT_INFLATER_SERVICE);
                                                View popupView = inflater.inflate(R.layout.popup_layout, null);

                                                // create the popup window
                                                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                                                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                                boolean focusable = true; // lets taps outside the popup also dismiss it
                                                popupWindow = new PopupWindow(popupView, width, height,focusable);

                                                popupWindow.setAnimationStyle(R.style.DialogTheme);
                                                popupWindow.setOutsideTouchable(true);

                                                popupWindow.showAsDropDown(ll,0,-4*ll.getHeight()-24,Gravity.TOP);
                                                searchview=popupView.findViewById(R.id.SearchView);

                                                RecyclerView rvgif= popupView.findViewById(R.id.rvgif);
                                                rvgif.setHasFixedSize(true);
                                                gifurl=new ArrayList<>();

                                                GridLayoutManager manager = new GridLayoutManager(MessageActivity.this,2);
                                                rvgif.setLayoutManager(manager);

                                                gif_adapter=new gif_adapter(MessageActivity.this,gifurl);
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

                                                RequestQueue r = Volley.newRequestQueue(MessageActivity.this);
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


                                                                JSONObject j4 = (JSONObject) j3.getJSONObject("fixed_width_downsampled");
                                                                // Log.d("asdf",names.get(j).toString());
                                                                // JSONObject j3 = (JSONObject) j2.getJSONObject("images");
                                                                String url= j4.getString("url");
                                                                gifurl.add("g"+url);
                                                                gif_adapter.notifyItemInserted(gifurl.size()-1);


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

                                                popupWindow.setAnimationStyle(R.style.DialogTheme);
                                                popupWindow.setOutsideTouchable(true);

                                                popupWindow.showAsDropDown(ll,0,-4*ll.getHeight()-24,Gravity.TOP);
                                                searchview=popupView1.findViewById(R.id.SearchView);

                                                RecyclerView rvgif1= popupView1.findViewById(R.id.rvgif);
                                                rvgif1.setHasFixedSize(true);
                                                gifurl=new ArrayList<>();

                                                GridLayoutManager manager1 = new GridLayoutManager(MessageActivity.this,3);
                                                rvgif1.setLayoutManager(manager1);

                                                gif_adapter=new gif_adapter(MessageActivity.this,gifurl);
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

                                                RequestQueue r1 = Volley.newRequestQueue(MessageActivity.this);
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

                                            case 4:

                                                if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.CAMERA}, 999);
                                                }
                                                else {
                                                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 15 * 1024 * 1024L);
                                                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
                                                    startActivityForResult(intent, 100);
                                                }
                                                break;

                                            case 5:
                                                Intent intent = new Intent();
                                                intent.setType("application/pdf");
                                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                                startActivityForResult(Intent.createChooser(intent,"Select"), 55);
                                                break;

                                            case 6:

                                                if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.CAMERA}, 299);
                                                }
                                                else {
                                                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                    startActivityForResult(intent1, 10);
                                                }
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

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (etMessage.getText().toString().trim().length() > 1) {

                    if (!(flag == 1)) {
                        FirebaseDatabase.getInstance().getReference("UserStatus").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue("typing " + RecieverPhone);
                        flag = 1;
                    }

                    if(checkUrl(etMessage.getText().toString().trim())) {
                        etMessage.setTypeface(etMessage.getTypeface(), Typeface.ITALIC);
                    }
                    else {
                        etMessage.setTypeface(etMessage.getTypeface(), Typeface.NORMAL);
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

                    ApplicationClass.messagesent=1;
                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    long millis = System.currentTimeMillis();
                    java.sql.Date date1 = new java.sql.Date(millis);

                    MessageModel model = new MessageModel(-1, sender, RecieverPhone, etMessage.getText().toString(), "text", -2, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null","null");
                    etMessage.setText(null);

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    }
                    else
                    {
                        if((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
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

                    if(!Messages.isComputingLayout())
                        adapter.notifyItemInserted(chats.size() - 1);}

                    sendFCMPush(model.getMessage());
                }
            }
        });

//*******************************************************************************************************************************************************

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

//        ((SimpleItemAnimator) Messages.getItemAnimator()).setSupportsChangeAnimations(false);

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

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(false);
        Messages.setLayoutManager(manager);

//        for (int i = 0; i < chats.size(); i++) {
//            Log.d("messageme", chats.get(i).getDate()+"");
//        }

        if(!wallpaper.getString("value","null").equals("null"))
        {
            if(getBackground(Uri.parse(wallpaper.getString("value","null")))!=null)
                getWindow().setBackgroundDrawable(getBackground(Uri.parse(wallpaper.getString("value","null"))));
        }

        adapter = new MessageAdapter(MessageActivity.this, chats);
        adapter.setHasStableIds(true);

        if((defaultvalue.equals("null"))){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("mode"+RecieverPhone, "public");
            editor.apply();
        }

       // Log.d("mode",defaultvalue);

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

//                Messages.smoothScrollToPosition(chats.size()-1);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);

            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

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

        Messages.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }



            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if(manager.findFirstCompletelyVisibleItemPosition()==0){

                    if(HandlerIndex!=-1) {

                        final int pos = manager.findLastVisibleItemPosition();

                        final Pair<ArrayList<MessageModel>, Integer> pair = Handler.getMessages(RecieverPhone, HandlerIndex);
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

        if(messagecount>2) {
            if (chats.size() == 0) {
                if (flag1)
                    chats.add(chats.size() - 1, new MessageModel(-65, "null", "null", "null123", "unread", 1234, "null", "null", "null", "null"));
                else
                    chats.add(new MessageModel(-65, "null", "null", "null123", "unread", 1234, "null","null", "null", "null"));
            }
            else
            {
                if (flag1)
                    chats.add(chats.size() - 1, new MessageModel(-65, "null", "null", "null123", "unread", 1234, "null", chats.get(chats.size()-1).getDate(), "null", "null"));
                else
                    chats.add(new MessageModel(-65, "null", "null", "null123", "unread", 1234, "null",chats.get(chats.size()-1).getDate(), "null", "null"));
            }
        }


        // to forward a message

  if(getIntent().getIntExtra("path",1)==2) {
      ApplicationClass.messagesent = 1;
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


            MessageModel model = new MessageModel(-1, sender, RecieverPhone, message1, "text", -2, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null","null");
            etMessage.setText(null);
            sendFCMPush(model.getMessage());

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            } else {
                if ((!(defaultvalue.equals("private")))) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            }

            int id = Handler.addMessage(model);
            model.setId(id);
            chats.add(model);
            if(!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size() - 1);


        } else if (type.equals("image")) {

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, message1, "image", 2, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null","null");

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            } else {
                if (!(defaultvalue.equals("private"))) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            adapter.notifyItemInserted(chats.size() - 1);
        }else if (type.equals("gif")) {

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, message1, "gif", 200, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null","null");

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            } else {
                if (!(defaultvalue.equals("private"))) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            if(!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size() - 1);
        }else if (type.equals("pdf")) {

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, message1, "pdf", 400, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null","null");

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            } else {
                if (!(defaultvalue.equals("private"))) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            if(!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size() - 1);
        }else if (type.equals("sticker")) {

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, message1, "sticker", 300, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null","null");

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            } else {
                if (!(defaultvalue.equals("private"))) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }

            int id = Handler.addMessage(messageModel);
            messageModel.setId(id);

            chats.add(messageModel);

            if(!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size() - 1);
        } else {
            MessageModel model = new MessageModel(1190, sender, RecieverPhone, message1, "video", 100, simpleDateFormat.format(date).substring(0, 5), date1.toString(),"null","null");

            if (chats.size() != 0) {
                if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            } else {
                if ((!(defaultvalue.equals("private")))) {
                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    chats.add(messageModel);
                }
            }

            int id = Handler.addMessage(model);
            model.setId(id);
            chats.add(model);

            if(!Messages.isComputingLayout())
                adapter.notifyItemInserted(chats.size() - 1);
        }
    }
}

//**************************************************************************************************************************************************************************

        imagereceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;

                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);

                Log.d("Received","Image");


                MessageModel messageModel = new MessageModel(-1, RecieverPhone, sender, dataSnapshot.getValue(String.class).substring(15), "image", 0,time,date,"null","null");
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if(!(defaultvalue.equals("private")))
                    {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }

                if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {


                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                            child(RecieverPhone).child("info").
                            child("seenmessages").child(dataSnapshot.getKey()).setValue("image");

                    dataSnapshot.getRef().removeValue();

                    chats.add(chats.size()-1,messageModel);

//                adapter.notifyDataSetChanged();
                    adapter.notifyItemInserted(chats.size() - 1);
                }
                else
                {
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                            child(RecieverPhone).child("info").
                            child("seenmessages").child(dataSnapshot.getKey()).setValue("image");



                        dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);

//                adapter.notifyDataSetChanged();
                    if(!Messages.isComputingLayout())
                        adapter.notifyItemInserted(chats.size() - 1);
                }
                if(messagecount==2)
                    received.play();
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

        stickerreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;

                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);

                Log.d("Received","Image");


                MessageModel messageModel = new MessageModel(-1, RecieverPhone, sender, dataSnapshot.getValue(String.class).substring(15), "sticker", 303,time,date,"null","null");
                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if(!(defaultvalue.equals("private")))
                    {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }

                if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {


                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                            child(RecieverPhone).child("info").
                            child("seenmessages").child(dataSnapshot.getKey()).setValue("sticker");

                    dataSnapshot.getRef().removeValue();

                    chats.add(chats.size()-1,messageModel);

//                adapter.notifyDataSetChanged();
                    if(!Messages.isComputingLayout())
                        adapter.notifyItemInserted(chats.size() - 1);
                }
                else
                {
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                            child(RecieverPhone).child("info").
                            child("seenmessages").child(dataSnapshot.getKey()).setValue("sticker");



                    dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);

//                adapter.notifyDataSetChanged();
                    if(!Messages.isComputingLayout())
                        adapter.notifyItemInserted(chats.size() - 1);
                }
                if(messagecount==2)
                    received.play();
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

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("sticker").addChildEventListener(stickerreceiver);


        gifreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;
                String uri;


                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);
                uri=dataSnapshot.getValue(String.class).substring(15);
                Log.d("abcde",uri);

                MessageModel messageModel = new MessageModel(-1,RecieverPhone,sender,uri,"gif",203,time,date,"null","null");


                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if (!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {

                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    if(flag1==true) {
                        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                child(RecieverPhone).child("info").
                                child("seenmessages").child(dataSnapshot.getKey()).setValue("gif");


                        dataSnapshot.getRef().removeValue();

                    chats.add(chats.size()-1,messageModel);}
                }
                else
                {
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                            child(RecieverPhone).child("info").
                            child("seenmessages").child(dataSnapshot.getKey()).setValue("gif");


                    dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);
                }

//                adapter.notifyDataSetChanged();
                if(!Messages.isComputingLayout())
                    adapter.notifyItemInserted(chats.size()-1);
                if(messagecount==2)
                    received.play();
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

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("gif").addChildEventListener(gifreceiver);



        videoreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;
                String uri;


                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);
                uri=dataSnapshot.getValue(String.class).substring(15);

                MessageModel messageModel = new MessageModel(-1,RecieverPhone,sender,uri,"video",101,time,date,"null","null");


                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if (!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {

                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    if(flag1==true) {
                        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                child(RecieverPhone).child("info").
                                child("seenmessages").child(dataSnapshot.getKey()).setValue("video");


                        dataSnapshot.getRef().removeValue();

                        chats.add(chats.size()-1,messageModel);}
                }
                else
                {
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                            child(RecieverPhone).child("info").
                            child("seenmessages").child(dataSnapshot.getKey()).setValue("video");


                    dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);
                }

//                adapter.notifyDataSetChanged();
                if(!Messages.isComputingLayout())
                    adapter.notifyItemInserted(chats.size()-1);
                if(messagecount==2)
                    received.play();
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

        pdfreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String time,date;
                String uri;


                time=dataSnapshot.getValue(String.class).substring(0,5);
                date=dataSnapshot.getValue(String.class).substring(5,15);
                uri=dataSnapshot.getValue(String.class).substring(15);

                MessageModel messageModel = new MessageModel(-1,RecieverPhone,sender,uri,"pdf",403,time,date,"null","null");


                //messageModel.setUri(Uri.parse(dataSnapshot.getValue(String.class)));

                if(chats.size()!=0) {
                    if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                else {
                    if (!(defaultvalue.equals("private"))) {
                        MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                        int id = Handler.addMessage(message);
                        message.setId(id);
                        chats.add(message);
                    }
                }
                if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {

                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);

                    if(flag1==true) {
                        reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                child(RecieverPhone).child("info").
                                child("seenmessages").child(dataSnapshot.getKey()).setValue("pdf");


                        dataSnapshot.getRef().removeValue();

                        chats.add(chats.size()-1,messageModel);}
                }
                else
                {
                    int id = Handler.addMessage(messageModel);
                    messageModel.setId(id);
                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                            child(RecieverPhone).child("info").
                            child("seenmessages").child(dataSnapshot.getKey()).setValue("pdf");


                    dataSnapshot.getRef().removeValue();

                    chats.add(messageModel);
                }

//                adapter.notifyDataSetChanged();
                if(!Messages.isComputingLayout())
                    adapter.notifyItemInserted(chats.size()-1);
                if(messagecount==2)
                    received.play();
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

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("pdf").addChildEventListener(pdfreceiver);


//********************************************************************************************************************************************************

        messageseen = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                String snmessage = dataSnapshot.getKey();
                String type = dataSnapshot.getValue(String.class);

                Handler.UpdateMessageByFirebaseID(snmessage,type,1);

                for(int i=chats.size()-1;i>=0;i--)
                {

                    if(chats.get(i).getFirebaseId().equals(snmessage))
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
                        else if(type.equals("sticker")){

                            chats.get(i).setDownloaded(306);

                        }
                        else if(type.equals("pdf")){
                            chats.get(i).setDownloaded(406);
                        }

                        if(!Messages.isComputingLayout())
                            adapter.notifyItemChanged(i);

                        dataSnapshot.getRef().removeValue();

                        break;
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

        reference.child("users").child(RecieverPhone).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("info").child("seenmessages").addChildEventListener(messageseen);


        chreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                String time, date;

                if (!(dataSnapshot.getKey().equals("message"))) {
                    if (dataSnapshot.getKey().equals("info")) {
                        if (!(dataSnapshot.child("friend").exists())) {
                            reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");
                        }
                    } else if (!(dataSnapshot.getKey().equals("info"))) {

                        time=dataSnapshot.getValue().toString().substring(0,5);
                        date=dataSnapshot.getValue().toString().substring(5,15);

                        reference.child("users").child(sender).child(RecieverPhone).child("info").child("friend").setValue("yes");

                        MessageModel messageModel = new MessageModel(435, RecieverPhone, sender, dataSnapshot.getValue().toString().substring(15), "text", -1,time,date,"null","null");

                        if(chats.size()!=0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate())) {
                                MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
                                int id = Handler.addMessage(message);
                                message.setId(id);
                                chats.add(message);
                            }
                        }
                        else {
                            if(!(defaultvalue.equals("private")))
                            {
                                MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date,"null","null");
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
                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(RecieverPhone).child("info").
                                        child("seenmessages").child(dataSnapshot.getKey()).setValue("text");

                                dataSnapshot.getRef().removeValue();
                            }
                        }
                        else{
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);

                            chats.add(messageModel);
                            reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                    child(RecieverPhone).child("info").
                                    child("seenmessages").child(dataSnapshot.getKey()).setValue("text");

                            dataSnapshot.getRef().removeValue();
                        }

                        if(!Messages.isComputingLayout())
                            adapter.notifyItemInserted(chats.size()-1);
                        // adapter.notifyItemRangeInserted(chats.size()-1,1);
                        if(messagecount==2)
                            received.play();
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
            Boolean flag2=true;
            int counter=0;

            for(int i = chats.size()-1;i>=0;i--)
            {
                if(chats.get(i).getDownloaded()==2 || chats.get(i).getDownloaded()==3 || chats.get(i).getDownloaded()==4 || chats.get(i).getDownloaded()==-2
                    || chats.get(i).getDownloaded()==-3 || chats.get(i).getDownloaded()==100 || chats.get(i).getDownloaded()==103 || chats.get(i).getDownloaded()==104
                || chats.get(i).getDownloaded()==201 || chats.get(i).getDownloaded()==204 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 304|| chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 301
                        || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 404|| chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 401) {
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
                        if (chats.get(pos - 1).getType().equals("Date") && chats.get(pos + 1).getType().equals("Date")) {
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
                        if (chats.get(pos - 1).getType().equals("Date")) {
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
            }else
            {
                adapter.notifyItemChanged(pos);
                Toast.makeText(MessageActivity.this, "Messages cannot be deleted if there are pending messages", Toast.LENGTH_SHORT).show();
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
            if(!(chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 103 ||chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 3 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 4
                    || chats.get(viewHolder.getAdapterPosition()).getType().equals("Date") || chats.get(viewHolder.getAdapterPosition()).getType().equals("typing")
            || chats.get(viewHolder.getAdapterPosition()).getType().equals("unread") || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == -3 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 204||
                    chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 201 || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 304|| chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 301
            || chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 404|| chats.get(viewHolder.getAdapterPosition()).getDownloaded() == 401)){

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
        ApplicationClass.CurrentReceiver="";
        ApplicationClass.condition2=false;

        adapter.unregisterAdapterDataObserver(observer);

        //Handler.close();


        reference.child("users").child(sender).child(RecieverPhone).child("info").child("seenmessages").removeEventListener(messageseen);

        FirebaseDatabase.getInstance().getReference("UserStatus").child(RecieverPhone).removeEventListener(Status);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").removeEventListener(imagereceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("videos").removeEventListener(videoreceiver);
        FirebaseDatabase.getInstance().getReference("users").child(sender).child("profile").removeEventListener(dp);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("sticker").removeEventListener(stickerreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("gif").removeEventListener(gifreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("pdf").removeEventListener(pdfreceiver);
    }
//*****************************************************************************************************************************************************

    private void sendFCMPush(String message) {



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
            objData.put("icon", R.drawable.icon1); //   icon_name image must be there in drawable
            objData.put("tag", token);
            objData.put("priority", "high");



            //   Log.d("dp1",dpUrl+" back ");

            dataobjData = new JSONObject();
            dataobjData.put("text", message);
            dataobjData.put("sender", "null");
            dataobjData.put("sender1",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
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
                new Response.Listener<JSONObject>() {
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
                                dialogInterface.dismiss();
                                MessageActivity.this.finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        if(requestCode==999)
        {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Permission Required")
                        .setMessage("Permission is required to Open Camera")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.CAMERA},999);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
            {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 15 * 1024 * 1024);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
                startActivityForResult(intent, 100);
            }
        }

        if(requestCode==299)
        {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Permission Required")
                        .setMessage("Permission is required to Open Camera")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.CAMERA},299);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
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

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        ApplicationClass.condition2=true;

        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").removeEventListener(imagereceiver);
        reference.child("users").child(RecieverPhone).child(sender).removeEventListener(chreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("gif").removeEventListener(gifreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("sticker").removeEventListener(stickerreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("pdf").removeEventListener(pdfreceiver);


        reference.child("users").child(sender).child(RecieverPhone).child("info").child("seenmessages").removeEventListener(messageseen);
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

        ApplicationClass.condition2=false;
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();


        reference.child("users").child(RecieverPhone).child(sender).child("info").child("images").addChildEventListener(imagereceiver);
        reference.child("users").child(RecieverPhone).child(sender).addChildEventListener(chreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("gif").addChildEventListener(gifreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("sticker").addChildEventListener(stickerreceiver);
        reference.child("users").child(RecieverPhone).child(sender).child("info").child("pdf").addChildEventListener(pdfreceiver);


        reference.child("users").child(sender).child(RecieverPhone).child("info").child("seenmessages").addChildEventListener(messageseen);
        //reference.child("users").child(sender).removeEventListener(chsender);

        //Handler.close();



        reference.child("users").child(RecieverPhone).child(sender).child("info").child("videos").addChildEventListener(videoreceiver);


    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //*****************************************************************************************************************************************************************
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

                        new CompressImage().execute(uri);
                    }
                }
                else
                    Toast.makeText(this, "You Cannot send more than 15 images at a time", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(data.getData()!=null) {
                    Uri uri = data.getData();
                    new CompressImage().execute(uri);
                }
                else {
                    Uri uri=getImageUri(this,(Bitmap) data.getExtras().get("data"));

                    new CompressImage().execute(uri);
                }
            }

        }

        if(requestCode==100)
        {
            if(resultCode==RESULT_OK)
            {

                if(data.getClipData() != null) {

                    if(data.getClipData().getItemCount()<=5) {
                        ApplicationClass.messagesent=1;

                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            ClipData.Item videoItem = data.getClipData().getItemAt(i);
                            Uri videoURI = videoItem.getUri();

                            File file = new File(getPath(MessageActivity.this,videoURI));

                            long fileSizeInBytes = file.length();
                            long fileSizeInKB = fileSizeInBytes / 1024;
                            long fileSizeInMB = fileSizeInKB / 1024;


                            if (fileSizeInMB >= 15) {
                                Toast.makeText(this,"Video files lesser than 15MB are allowed",Toast.LENGTH_LONG).show();

                            }

                            else{
                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                            long millis = System.currentTimeMillis();
                            java.sql.Date date1 = new java.sql.Date(millis);

                            MessageModel model = new MessageModel(1190, sender, RecieverPhone, videoURI.toString(), "video", 100, simpleDateFormat.format(date).substring(0, 5), date1.toString(), "null","null");

                            if (chats.size() != 0) {
                                if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                                    int id = Handler.addMessage(messageModel);
                                    messageModel.setId(id);
                                    chats.add(messageModel);
                                }
                            } else {
                                if ((!(defaultvalue.equals("private")))) {
                                    MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null","null");
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

                                if(!Messages.isComputingLayout())
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

                    ApplicationClass.messagesent=1;

                    File file = new File(getPath(MessageActivity.this,videoURI));

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

                    MessageModel model = new MessageModel(1190,sender,RecieverPhone,videoURI.toString(),"video",100,simpleDateFormat.format(date).substring(0,5),date1.toString(),"null","null");

                    if (chats.size() != 0) {
                        if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                            int id = Handler.addMessage(messageModel);
                            messageModel.setId(id);
                            chats.add(messageModel);
                        }
                    } else {
                        if ((!(defaultvalue.equals("private")))) {
                            MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null","null");
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

                        if(!Messages.isComputingLayout())
                            adapter.notifyItemInserted(chats.size()-1);


                    }
                }
            }
        }

        if(requestCode==1500)
        {
            getMessages();
        }

        if(requestCode==55)
        {
            if(resultCode==RESULT_OK)
            {

                if(data.getClipData() != null) {

                    if(data.getClipData().getItemCount()<=5) {
                        ApplicationClass.messagesent=1;

                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            ClipData.Item pdfItem = data.getClipData().getItemAt(i);
                            Uri pdfURI = pdfItem.getUri();

                            File file = new File(getPath(MessageActivity.this,pdfURI));

                            long fileSizeInBytes = file.length();
                            long fileSizeInKB = fileSizeInBytes / 1024;
                            long fileSizeInMB = fileSizeInKB / 1024;


                            if (fileSizeInMB >= 20) {
                                Toast.makeText(this,"PDFs lesser than 20MB are allowed",Toast.LENGTH_LONG).show();

                            }
                            else {

                                Date date = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                                long millis = System.currentTimeMillis();
                                java.sql.Date date1 = new java.sql.Date(millis);

                                String path = getPath(MessageActivity.this, pdfURI);

                                MessageModel model = new MessageModel(1190, sender, RecieverPhone, path, "pdf", 400, simpleDateFormat.format(date).substring(0, 5), date1.toString(), "null", "null");

                                if (chats.size() != 0) {
                                    if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                                        MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null", "null");
                                        int id = Handler.addMessage(messageModel);
                                        messageModel.setId(id);
                                        chats.add(messageModel);
                                    }
                                } else {
                                    if ((!(defaultvalue.equals("private")))) {
                                        MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null", "null");
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

                                if (!Messages.isComputingLayout())
                                    adapter.notifyItemInserted(chats.size() - 1);
                            }

                        }
                    }
                    else
                    {
                        Toast.makeText(this, "You cannot send more than 5 PDFs at a time", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Uri pdfURI = data.getData();

                    ApplicationClass.messagesent=1;

                    File file = new File(getPath(MessageActivity.this,pdfURI));

                    long fileSizeInBytes = file.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;


                    if (fileSizeInMB >= 20) {
                        Toast.makeText(this,"PDFs lesser than 20MB are allowed",Toast.LENGTH_LONG).show();

                    }
                    else {

                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                        long millis = System.currentTimeMillis();
                        java.sql.Date date1 = new java.sql.Date(millis);

                        String path = getPath(MessageActivity.this, pdfURI);

                        MessageModel model = new MessageModel(1190, sender, RecieverPhone, path, "pdf", 400, simpleDateFormat.format(date).substring(0, 5), date1.toString(), "null", "null");

                        if (chats.size() != 0) {
                            if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                                MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null", "null");
                                int id = Handler.addMessage(messageModel);
                                messageModel.setId(id);
                                chats.add(messageModel);
                            }
                        } else {
                            if ((!(defaultvalue.equals("private")))) {
                                MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null", "null");
                                int id = Handler.addMessage(messageModel);
                                messageModel.setId(id);
                                chats.add(messageModel);
                            }
                        }

                        int id = Handler.addMessage(model);
                        model.setId(id);
                        if (chats.size() > 0 && chats.get(chats.size() - 1).getType().equals("typing")) {
                            if (flag1)
                                chats.add(chats.size() - 1, model);
                        } else
                            chats.add(model);

                        if (!Messages.isComputingLayout())
                            adapter.notifyItemInserted(chats.size() - 1);

                    }
                }

            }
        }
    }

    @Override
    public void ImageClicked(final int index) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Glide.with(MessageActivity.this)
                .download(gifurl.get(index).substring(1))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {

                        File imagesFolder = new File(Environment.getExternalStorageDirectory(),"JustChat/Sent");
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
    public void StickerClicked(final int index) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Glide.with(MessageActivity.this)
                .download(gifurl.get(index).substring(1))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {

                        File imagesFolder = new File(Environment.getExternalStorageDirectory(),"JustChat/Sent");
                        if(!imagesFolder.exists())
                            imagesFolder.mkdirs();

                        File file = new File(imagesFolder,System.currentTimeMillis()+".png");

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
                            prepareSticker(Uri.fromFile(file),gifurl.get(index).substring(1));

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

    public class CompressImage extends AsyncTask<Uri,Void,Uri>
    {
        @Override
        protected Uri doInBackground(Uri... uris) {
            String filePath = getPath(MessageActivity.this,uris[0]);
            Bitmap scaledBitmap = null;
            ApplicationClass.messagesent=1;

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
                Log.d("EXIF", "Exif: " + orientation);
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

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "JustChat/Sent");
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
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("HH:mm");

            long millis=System.currentTimeMillis();
            java.sql.Date date1=new java.sql.Date(millis);

            MessageModel messageModel = new MessageModel(-1, sender, RecieverPhone, uri.toString(), "image", 2,simpleDateFormat.format(date).substring(0,5),date1.toString(),"null","null");

            if(chats.size()!=0) {
                if (!chats.get(chats.size() - 1).getDate().equals(messageModel.getDate()) || chats.size() == 0) {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }
            }
            else {
                if(!(defaultvalue.equals("private")))
                {
                    MessageModel message = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                    int id = Handler.addMessage(message);
                    message.setId(id);
                    chats.add(message);
                }}


            if(chats.size()>0 && chats.get(chats.size()-1).getType().equals("typing")) {

                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                if(flag1==true) {

                    chats.add(chats.size() - 1, messageModel);

                    if(!Messages.isComputingLayout())
                        adapter.notifyItemInserted(chats.size() - 1);
                }
            }
            else
            {
                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);

                chats.add(messageModel);

                if(!Messages.isComputingLayout())
                    adapter.notifyItemInserted(chats.size() - 1);
            }
        }
    }

    public void uploadVideo(final int index, final MessageModel message)
    {
        message.setDownloaded(103);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(103);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

        Uri VideoUri;
            VideoUri = Uri.parse(message.getMessage());

        ApplicationClass.PendingRequests.add(RecieverPhone);

        rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(VideoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("videos/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String push=reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("videos").push().getKey();
                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("videos").child(push).setValue(message.getTime()+message.getDate()+uri.toString());


                                message.setFirebaseId(push);
                                message.setDownloaded(102);
                                Handler.UpdateMessage(message);

                                sendFCMPush("Video");

                                if(!MessageActivity.this.isDestroyed())
                                {
                                    chats.get(index).setFirebaseId(push);
                                    chats.get(index).setDownloaded(102);
                                    sent.play();

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyItemChanged(index);
                                    }
                                }

                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("deletevideos").child(push).setValue(message.getTime()+message.getDate()+uri.toString());


                                if(MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                                    if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                                        Intent intent = getIntent();
                                        intent.putExtra("type"," ");
                                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                                        startActivity(intent);

                                        sent.play();


                                        overridePendingTransition(0, 0);
                                    }
                                }
                                ApplicationClass.PendingRequests.remove(RecieverPhone);
                            }
                        });

            }
        });
    }

    public void UploadImage(final int index, final MessageModel message)
    {
        ApplicationClass.PendingRequests.add(RecieverPhone);

        message.setDownloaded(3);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(3);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);


        Uri ImageUri = Uri.parse(message.getMessage());

        rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("images/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String push=reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("images").push().getKey();

                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("images").child(push).setValue(message.getTime()+message.getDate()+uri.toString());
                                

                                message.setFirebaseId(push);
                                message.setDownloaded(1);
                                Handler.UpdateMessage(message);

                                sendFCMPush("Image");

                                if(!MessageActivity.this.isDestroyed())
                                {
                                    chats.get(index).setFirebaseId(push);
                                    chats.get(index).setDownloaded(1);
                                    sent.play();

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyItemChanged(index);
                                    }
                                }


                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("deleteimages").child(push).setValue(message.getTime()+message.getDate()+uri.toString());

                                if(MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {

                                    if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                                        Intent intent = getIntent();
                                        intent.putExtra("type"," ");
                                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                                        startActivity(intent);

                                        sent.play();

                                        overridePendingTransition(0, 0);
                                    }
                                }
                                ApplicationClass.PendingRequests.remove(RecieverPhone);
                            }
                        });

            }
        });
    }

    public void prepareSticker(Uri uri,String url)
    {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

        ApplicationClass.messagesent=1;
        MessageModel model = new MessageModel(-349,sender,RecieverPhone,uri+" "+url,
                "sticker",300,simpleDateFormat.format(date).substring(0, 5), date1.toString(), "null","null");

        if (chats.size() != 0) {
            if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);
                chats.add(messageModel);
            }
        } else {
            if ((!(defaultvalue.equals("private")))) {
                MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null","null");
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

        if(!Messages.isComputingLayout())
            adapter.notifyItemInserted(chats.size()-1);
    }

    public void SendSticker(final int index, final MessageModel model)
    {
        ApplicationClass.PendingRequests.add(RecieverPhone);
        model.setDownloaded(301);
        Handler.UpdateMessage(model);

        chats.get(index).setDownloaded(301);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);


        SendMesage = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                model.setDownloaded(302);
                Handler.UpdateMessage(model);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(302);
                    sent.play();

                    if(!Messages.isComputingLayout())
                        adapter.notifyItemChanged(index);
                }

                sendFCMPush("Sticker");

                if(MessageActivity.this.isDestroyed()  && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);
                        sent.play();

                        overridePendingTransition(0, 0);
                    }
                }
                ApplicationClass.PendingRequests.remove(RecieverPhone);
            }
        };

        String push= reference.child("users").child(sender).child(RecieverPhone).child("info").child("sticker").push().getKey();
        model.setFirebaseId(push);
        chats.get(index).setFirebaseId(push);

        reference.child("users").child(sender).child(RecieverPhone).child("info").child("sticker").child(push).setValue(model.getTime()+date.toString() +model.getMessage().substring(model.getMessage().lastIndexOf(" ")+1)).addOnCompleteListener(SendMesage);
    }



    public void prepareGif(Uri uri, String url)
    {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        ApplicationClass.messagesent=1;
        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

        MessageModel model = new MessageModel(-349,sender,RecieverPhone,uri+" "+url,
                "gif",200,simpleDateFormat.format(date).substring(0, 5), date1.toString(), "null","null");

        if (chats.size() != 0) {
            if (!chats.get(chats.size() - 1).getDate().equals(model.getDate())) {
                MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(),"null","null");
                int id = Handler.addMessage(messageModel);
                messageModel.setId(id);
                chats.add(messageModel);
            }
        } else {
            if ((!(defaultvalue.equals("private")))) {
                MessageModel messageModel = new MessageModel(54, "null", RecieverPhone, "null", "Date", 60, "null", date1.toString(), "null","null");
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

        if(!Messages.isComputingLayout())
            adapter.notifyItemInserted(chats.size()-1);
    }

    public void SendGIF(final int index, final MessageModel model)
    {
        ApplicationClass.PendingRequests.add(RecieverPhone);
        model.setDownloaded(201);
        Handler.UpdateMessage(model);

        chats.get(index).setDownloaded(201);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);


        SendMesage = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {


                model.setDownloaded(202);
                Handler.UpdateMessage(model);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(202);
                    sent.play();

                    if(!Messages.isComputingLayout())
                        adapter.notifyItemChanged(index);
                }

                sendFCMPush("GIF");

                if(MessageActivity.this.isDestroyed()  && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);
                        sent.play();

                        overridePendingTransition(0, 0);
                    }
                }
                ApplicationClass.PendingRequests.remove(RecieverPhone);
            }
        };

        String push= reference.child("users").child(sender).child(RecieverPhone).child("info").child("gif").push().getKey();
        model.setFirebaseId(push);
        chats.get(index).setFirebaseId(push);

        reference.child("users").child(sender).child(RecieverPhone).child("info").child("gif").child(push).setValue(model.getTime()+date.toString() +model.getMessage().substring(model.getMessage().lastIndexOf(" ")+1)).addOnCompleteListener(SendMesage);
    }

    public void UploadPDF(final int index, final MessageModel message)
    {
        ApplicationClass.PendingRequests.add(RecieverPhone);

        message.setDownloaded(401);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(401);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

        File file = new File(message.getMessage());

        final String name = message.getMessage().substring(message.getMessage().lastIndexOf("/")+1,message.getMessage().lastIndexOf("."));

        rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("pdf/" + Uri.parse(message.getMessage()).getLastPathSegment()).
                putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                rf.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/" + message.getReciever()).child("pdf/" + Uri.parse(message.getMessage()).getLastPathSegment()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String push=reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("pdf").push().getKey();

                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("pdf").child(push).setValue(message.getTime()+message.getDate()+uri.toString()+" "+name);


                                message.setFirebaseId(push);
                                message.setDownloaded(402);
                                Handler.UpdateMessage(message);

                                sendFCMPush("PDF");

                                if(!MessageActivity.this.isDestroyed())
                                {
                                    chats.get(index).setFirebaseId(push);
                                    chats.get(index).setDownloaded(402);
                                    sent.play();

                                    if(!Messages.isComputingLayout())
                                    {
                                        adapter.notifyItemChanged(index);
                                    }
                                }


                                reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                        child(message.getReciever()).child("info").
                                        child("deletepdf").child(push).setValue(message.getTime()+message.getDate()+uri.toString());

                                if(MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {

                                    if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                                        Intent intent = getIntent();
                                        intent.putExtra("type"," ");
                                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                                        startActivity(intent);

                                        sent.play();

                                        overridePendingTransition(0, 0);
                                    }
                                }
                                ApplicationClass.PendingRequests.remove(RecieverPhone);
                            }
                        });

            }
        });
    }

    @Override
    public void pdfclicked(int index) {

        if(chats.get(index).getDownloaded()!=403 && chats.get(index).getDownloaded()!=404) {
            try {
                File file = new File(chats.get(index).getMessage());
                Uri pdfURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfOpenintent.setDataAndType(pdfURI, "application/pdf");
                startActivity(pdfOpenintent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MessageActivity.this, "You have no app to view this type of content", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MessageActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //***********************************************************************************************************************************************
    @Override
    public void showImage(int index) {

        if(chats.get(index).getDownloaded()!=0 && chats.get(index).getDownloaded()!=4 && chats.get(index).getDownloaded()!=203 && chats.get(index).getDownloaded()!=204 && !chats.get(index).getType().equals("video")  && !chats.get(index).getType().equals("pdf") && !chats.get(index).getType().equals("sticker")) {
          //  Log.d("URIURI",index +" "+ chats.get(index).getMessage())

            Intent intent = new Intent(MessageActivity.this, ShowImage.class);

            if(chats.get(index).getType().equals("gif") && !chats.get(index).getMessage().equals("null"))
                intent.putExtra("source", chats.get(index).getMessage().substring(0,chats.get(index).getMessage().lastIndexOf(" ")));
            else
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

        if (!chats.get(index).getMessage().equals("null") && chats.get(index).getDownloaded()!=0 && chats.get(index).getDownloaded()!=4
            && chats.get(index).getDownloaded()!=104 && chats.get(index).getDownloaded()!=101 &&chats.get(index).getDownloaded()!=203 && chats.get(index).getDownloaded()!=204
         && chats.get(index).getDownloaded()!=303 && chats.get(index).getDownloaded()!=304 && chats.get(index).getDownloaded()!=403 && chats.get(index).getDownloaded()!=404) {

            if (!(chats.get(index).getType().equals("video") || chats.get(index).getType().equals("image") || chats.get(index).getType().equals("gif") || chats.get(index).getType().equals("sticker") || chats.get(index).getType().equals("pdf"))) {
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

                Boolean condition = true;
                File file = null;

                if(chats.get(index).getType().equals("pdf")) {
                    file = new File(chats.get(index).getMessage());


                    if(!file.exists()) {
                        condition = false;
                        Toast.makeText(MessageActivity.this, "This file has been deleted or moved", Toast.LENGTH_SHORT).show();
                    }

                }

                if(condition) {
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
    }

    @Override
    public void OnFileDeleted(int index) {
            chats.get(index).setMessage("null");

            Handler.UpdateMessage(chats.get(index));

            if (!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);
        }

    @Override
    public void sendGIF(int index) {
        SendGIF(index,chats.get(index));
    }

    @Override
    public void downloadGIF(int index) {
        String message = chats.get(index).getMessage();
        new DownloadGIF(index,chats.get(index)).execute(message);
    }

    @Override
    public void sendSticker(int index) {
        SendSticker(index,chats.get(index));
    }

    @Override
    public void downloadSticker(int index) {
        String message = chats.get(index).getMessage();
        new DownloadSticker(index,chats.get(index)).execute(message);
    }

    @Override
    public void UrlClicked(int index) {
        if(checkUrl(chats.get(index).getMessage()))
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(chats.get(index).getMessage()));
            startActivity(i);
        }
    }

    @Override
    public void sendPdf(int index) {
        UploadPDF(index,chats.get(index));
    }

    @Override
    public void downloadPdf(int index) {
        new DownloadPdf(index,chats.get(index)).execute(chats.get(index).getMessage().substring(0,chats.get(index).getMessage().lastIndexOf(" ")));
    }

    //***********************************************************************************************************************************************

    private class DownloadPdf extends AsyncTask<String, Void, Uri>
    {
        int index;
        MessageModel message;

        DownloadPdf(int position,MessageModel message)
        {
            index = position;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ApplicationClass.PendingRequests.add(RecieverPhone);

            message.setDownloaded(404);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(404);
            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);

        }

        @Override
        protected Uri doInBackground(String... strings) {
            InputStream urlInputStream = null;

            URLConnection urlConnection;

            String message1 = message.getMessage();
            String name = message1.substring(message1.lastIndexOf(" ")+1);

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"JustChat/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"JustChat/Received/"+name+".pdf");

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

                message.setDownloaded(402);
                message.setMessage(getPath(MessageActivity.this,uri));

                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(402);
                    chats.get(index).setMessage(getPath(MessageActivity.this,uri));

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else {
                message.setDownloaded(403);
                Handler.UpdateMessage(message);

                if (!MessageActivity.this.isDestroyed()) {
                    chats.get(index).setDownloaded(403);
                    adapter.notifyItemChanged(index);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setTitle("Could not download PDF");
                    builder.setMessage("Please ask " + pref1.getString(getIntent().getStringExtra("title"), getIntent().getStringExtra("title")) +
                            " to resend the PDF")
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
            ApplicationClass.PendingRequests.remove(RecieverPhone);
        }
    }

    private class DownloadSticker extends AsyncTask<String, Void, Uri>
    {
        int index;
        MessageModel message;

        DownloadSticker(int position,MessageModel message)
        {
            index = position;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ApplicationClass.PendingRequests.add(RecieverPhone);

            message.setDownloaded(304);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(304);
            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);

        }

        @Override
        protected Uri doInBackground(String... strings) {
            InputStream urlInputStream = null;

            URLConnection urlConnection;

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"JustChat/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"JustChat/Received/"+System.currentTimeMillis()+".png");

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
                String message1 = message.getMessage();

                message.setDownloaded(302);
                message.setMessage(uri.toString()+" "+message1);

                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(302);
                    chats.get(index).setMessage(message.getMessage());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else {
                message.setDownloaded(303);
                Handler.UpdateMessage(message);

                if (!MessageActivity.this.isDestroyed()) {
                    chats.get(index).setDownloaded(303);
                    adapter.notifyItemChanged(index);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setTitle("Could not download Sticker");
                    builder.setMessage("Please ask " + pref1.getString(getIntent().getStringExtra("title"), getIntent().getStringExtra("title")) +
                            " to resend the sticker")
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
            ApplicationClass.PendingRequests.remove(RecieverPhone);
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

            ApplicationClass.PendingRequests.add(RecieverPhone);

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

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"JustChat/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"JustChat/Received/"+System.currentTimeMillis()+".gif");

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

                String message1 = message.getMessage();

                message.setDownloaded(202);
                message.setMessage(uri.toString()+" "+message1);

                Handler.UpdateMessage(message);

                if(!MessageActivity.this.isDestroyed())
                {
                    chats.get(index).setDownloaded(202);
                    chats.get(index).setMessage(message.getMessage());

                    if(!Messages.isComputingLayout())
                    {
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else {
                message.setDownloaded(203);
                Handler.UpdateMessage(message);

                if (!MessageActivity.this.isDestroyed()) {
                    chats.get(index).setDownloaded(203);
                    adapter.notifyItemChanged(index);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setTitle("Could not download gif");
                    builder.setMessage("Please ask " + pref1.getString(getIntent().getStringExtra("title"), getIntent().getStringExtra("title")) +
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
            ApplicationClass.PendingRequests.remove(RecieverPhone);
        }
    }


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

            ApplicationClass.PendingRequests.add(RecieverPhone);

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

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"JustChat/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"JustChat/Received/"+System.currentTimeMillis()+".mp4");

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
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else {
                message.setDownloaded(101);
                Handler.UpdateMessage(message);

                if (!MessageActivity.this.isDestroyed()) {
                    chats.get(index).setDownloaded(101);
                    adapter.notifyItemChanged(index);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setTitle("Could not download Video");
                    builder.setMessage("Please ask " + pref1.getString(getIntent().getStringExtra("title"), getIntent().getStringExtra("title")) +
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
            ApplicationClass.PendingRequests.remove(RecieverPhone);
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
            ApplicationClass.PendingRequests.add(RecieverPhone);
            message.setDownloaded(4);
            Handler.UpdateMessage(message);

            chats.get(index).setDownloaded(4);
            if(!Messages.isComputingLayout())
                adapter.notifyItemChanged(index);
        }

        protected Uri doInBackground(URL...urls){
            InputStream urlInputStream = null;

            URLConnection urlConnection;

            File imagesfolder = new File(Environment.getExternalStorageDirectory(),"JustChat/Received");

            if(!imagesfolder.exists())
                imagesfolder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),"JustChat/Received/"+System.currentTimeMillis()+".jpg");

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
                        adapter.notifyItemChanged(index);
                    }
                }

                if (MessageActivity.this.isDestroyed() && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if(ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);

                        overridePendingTransition(0, 0);
                    }
                }

            }
            else {
                message.setDownloaded(0);
                Handler.UpdateMessage(message);

                if (!MessageActivity.this.isDestroyed()) {
                    chats.get(index).setDownloaded(0);
                    adapter.notifyItemChanged(index);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                    builder.setTitle("Could not download Image");
                    builder.setMessage("Please ask " + pref1.getString(getIntent().getStringExtra("title"), getIntent().getStringExtra("title")) +
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
            ApplicationClass.PendingRequests.remove(RecieverPhone);
        }
    }

    public void SendMessage(final int index, final MessageModel message)
    {
        ApplicationClass.PendingRequests.add(RecieverPhone);
        message.setDownloaded(-3);
        Handler.UpdateMessage(message);

        chats.get(index).setDownloaded(-3);

        if(!Messages.isComputingLayout())
            adapter.notifyItemChanged(index);

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
                    sent.play();

                    if(!Messages.isComputingLayout())
                        adapter.notifyItemChanged(index);
                }

                if(MessageActivity.this.isDestroyed()  && !((Activity) ApplicationClass.MessageActivityContext).isDestroyed()) {
                    if (ApplicationClass.PendingRequests.contains(ApplicationClass.CurrentReceiver)) {
                        Intent intent = getIntent();
                        intent.putExtra("type"," ");
                        ((Activity) ApplicationClass.MessageActivityContext).finish();
                        startActivity(intent);
                        sent.play();

                        overridePendingTransition(0, 0);
                    }
                }
                ApplicationClass.PendingRequests.remove(RecieverPhone);
            }
        };

        String push= reference.child("users").child(sender).child(RecieverPhone).push().getKey();
        message.setFirebaseId(push);
        chats.get(index).setFirebaseId(push);

        reference.child("users").child(sender).child(RecieverPhone).child(push).setValue(message.getTime()+date.toString() +message.getMessage().trim()).addOnCompleteListener(SendMesage);
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

    public static String getPath(final Context context, final Uri uri) {

        Log.d("asdf",uri+"");
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
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
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
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if(popupWindow!=null && popupWindow.isShowing())
                popupWindow.dismiss();
            else
                MessageActivity.this.finish();
            return true;
        }

        // If it wasn't the Back key, bubble up to the default
        // system behavior
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messageactivity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case R.id.Mode:
                Intent intent = new Intent(MessageActivity.this,Mode.class);
                intent.putExtra("number",RecieverPhone);
                startActivityForResult(intent,1500);
                break;

            case R.id.Delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete all messages?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                defaultvalue = pref.getString("mode" + RecieverPhone, "null");

                                if(defaultvalue.equals("public"))
                                {
                                    if(chats.size()>0) {
                                        Handler.DeleteMessagesofReceiver(RecieverPhone);
                                        chats.clear();
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(MessageActivity.this, "All Messages deleted", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(MessageActivity.this, "There are no messages to be deleted", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(MessageActivity.this, "You cannot delete messages in private mode", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Boolean checkUrl(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
        }

        return false;
    }
}
