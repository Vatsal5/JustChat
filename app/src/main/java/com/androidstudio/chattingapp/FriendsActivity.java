package com.androidstudio.chattingapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity implements FriendsAdapter.itemSelected{

    RecyclerView lv;
    FirebaseDatabase database;
    String currentUserNumber;
    TextView tvCreateGroup,tvtitle;
    int x=0;
    ValueEventListener check,profile;
    DatabaseReference reference;
    ArrayList <String> members;
    FriendsAdapter userAdapter;
    ArrayList<UserDetail> contacts;
    ChildEventListener childEvent,Group;
    ArrayList<UserDetailWithStatus> contacts1;
    LinearLayout toolbar;
    CardView cvCreate;
    ArrayList<String> number1,membersToadd;
    ArrayList<String> keyid;
    androidx.appcompat.widget.SearchView searchView;

    AdView mAdView;

    int yes=0;
    int c=0;
    int k=0,z,z1;
    String groupkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        tvtitle=findViewById(R.id.tvhead);
        cvCreate=findViewById(R.id.ivCreate);

        keyid = new ArrayList<>();
        searchView = findViewById(R.id.SearchView);
        if (getIntent().getIntExtra("path", 2) == 1) {

            tvtitle.setText("Forward");

        }



        toolbar = findViewById(R.id.toolbar);
        SharedPreferences preftheme;
        preftheme=getSharedPreferences("theme",0);

        String theme=preftheme.getString("theme","red");



        switch (theme)
        { case "orange":

            toolbar.setBackgroundColor(getResources().getColor(R.color.Orange));
            ViewCompat.setBackgroundTintList(cvCreate, ColorStateList.valueOf(Color.parseColor("#d6514a")));
            break;

            case "blue":

                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#283470")));
                break;


            case "bluish":
                toolbar.setBackgroundColor(getResources().getColor(R.color.bluish));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#292f3b")));
                break;

            case "deepred":
                toolbar.setBackgroundColor(getResources().getColor(R.color.deepred));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#e24a3c")));
                break;

            case "faintpink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.faintpink));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#f25c65")));
                break;

            case "darkblue":
                toolbar.setBackgroundColor(getResources().getColor(R.color.darkblue));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#2b3050")));
                break;


            case "green":
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#f2a37a")));
                break;

            case "lightorange":
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightorange));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#f2a37a")));
                break;

            case "lightred":
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightred));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#e9776c")));
                break;

            case "mustard":
                toolbar.setBackgroundColor(getResources().getColor(R.color.mustard));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#eba54d")));
                break;
            case "pink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pink));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#e91e63")));
                break;

            case "pureorange":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pureorange));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#ff5722")));

                break;
            case "purepink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purepink));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#f57268")));
                break;
            case "purple":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#49264e")));
                break;
            default:
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                ViewCompat.setBackgroundTintList(cvCreate,ColorStateList.valueOf(Color.parseColor("#d6514a")));
        }
        tvCreateGroup=findViewById(R.id.tvCreate);
        members=new ArrayList<>();
        groupkey=getIntent().getStringExtra("groupkey");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });


        setTitle(null);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            number1= new ArrayList<String>();
        membersToadd= new ArrayList<String>();
            contacts = new ArrayList<>();
            contacts1 = new ArrayList<>();

            database=FirebaseDatabase.getInstance();

            reference=database.getReference();
            currentUserNumber= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

            lv=findViewById(R.id.lv);
            LinearLayoutManager layoutManager=new LinearLayoutManager(this);
            lv.setLayoutManager(layoutManager);
        userAdapter = new FriendsAdapter(FriendsActivity.this,contacts1 );
        lv.setAdapter(userAdapter);
        lv.addItemDecoration(new DividerItemDecoration(lv.getContext(),DividerItemDecoration.VERTICAL));

            if(ContextCompat.checkSelfPermission(FriendsActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(FriendsActivity.this,new String[]{Manifest.permission.READ_CONTACTS},1);
            }
            else
            {
                getcontact();
            }

            if(ApplicationClass.activity==1) {
                ApplicationClass.activity=0;
                FirebaseDatabase.getInstance().getReference().child("groups").child(groupkey).child("members").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        members.add(dataSnapshot.getValue().toString());

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
            }

            cvCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(tvCreateGroup.getText().equals("ADD"))
                    {
                        ApplicationClass.addmembers=0;
                        for(int index=0; index<contacts1.size();index++) {
                            if(contacts1.get(index).getSelected()==1) {
                                Toast.makeText(getApplicationContext(),contacts1.get(index).getPh_number(),Toast.LENGTH_LONG).show();

                                for(int i=0; i<ApplicationClass.groupmembers.size();i++) {
                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupkey).child("layout").child(ApplicationClass.groupmembers.get(i) ).push().setValue("addedmember " + contacts1.get(index).getPh_number());
                                }
                                FirebaseDatabase.getInstance().getReference().child("groups").child(getIntent().getStringExtra("groupkey"))
                                   .child("members").push().setValue(contacts1.get(index).getPh_number());
                                FirebaseDatabase.getInstance().getReference().child("users").child(contacts1.get(index).getPh_number()).child("deletedgroups").
                                        child(getIntent().getStringExtra("groupkey")).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                                FirebaseDatabase.getInstance().getReference().child("users").child(contacts1.get(index).getPh_number()).child("groups").
                                            child(getIntent().getStringExtra("groupkey")).setValue(getIntent().getStringExtra("groupname"));
                            }
                        }

                        Intent intent=new Intent(FriendsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        FriendsActivity.this.finish();

                        startActivity(intent);

                    }
                    else
                    {

                    for(int index=0; index<contacts1.size();index++) {
                        if(contacts1.get(index).getSelected()==1) {
                                ApplicationClass.members.add(contacts1.get(index).getPh_number());

                        }
                    }

                    FriendsActivity.this.finish();
                    startActivity(new Intent(FriendsActivity.this,CreateGroup.class));
                }}
            });

        }

        public class num
        {

        public void checkauth( final String key, final int index)
        {

            check= new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        if (index == 0) {
                            if (dataSnapshot.child("profile").exists()) {
                                contacts1.add(new UserDetailWithStatus(contacts.get(index).getPh_number(), contacts.get(index).getuID(), dataSnapshot.child("profile").getValue(String.class),
                                        dataSnapshot.child("status").getValue(String.class),0,null));
                                keyid.add(contacts.get(index).getPh_number());
                                userAdapter.notifyItemInserted(contacts1.size()-1);

                            } else {

                                contacts1.add(new UserDetailWithStatus(contacts.get(index).getPh_number(), contacts.get(index).getuID(), "null",
                                        dataSnapshot.child("status").getValue(String.class),0,null));
                                keyid.add(contacts.get(index).getPh_number());
                                userAdapter.notifyItemInserted(contacts1.size()-1);

                            }
                        //    (reference.child("users").child(currentUserNumber).child(contacts.get(index).getPh_number()).child("message")).setValue("/null");
                            //  (reference.child("users").child(currentUserNumber).child(contacts.get(i).getPh_number()).child("activeStatus")).setValue("online");
                            c = 1;
                        } else {
                            for (int j = 0; j < c; j++) {
                                if (contacts.get(index).getPh_number().equals(contacts1.get(j).getPh_number())) {
                                    k = 1;
                                    break;
                                }
                            }
                            if (k == 0) {
                                if (dataSnapshot.child("profile").exists()) {
                              //      Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                                  //  Log.d("myapp", dataSnapshot.child("profile").getValue(String.class));
                                    contacts1.add(new UserDetailWithStatus(contacts.get(index).getPh_number(), contacts.get(index).getuID(), dataSnapshot.child("profile").getValue(String.class),
                                            dataSnapshot.child("status").getValue(String.class),0,null));
                                    keyid.add(contacts.get(index).getPh_number());
                                    userAdapter.notifyItemInserted(contacts1.size()-1);

                                } else {

                                    contacts1.add(new UserDetailWithStatus(contacts.get(index).getPh_number(), contacts.get(index).getuID(), "null", dataSnapshot.child("status").getValue(String.class),0,null));
                                    keyid.add(contacts.get(index).getPh_number());
                                    userAdapter.notifyItemInserted(contacts1.size()-1);

                                }
                          //      (reference.child("users").child(currentUserNumber).child(contacts.get(index).getPh_number()).child("message")).setValue("/null");

                                c++;
                            }
                        }
                        k = 0;
                    }
                    reference.child("users").child(key).removeEventListener(check);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.child("users").child(key).addListenerForSingleValueEvent(check);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getcontact();
            }
        }
    }
    public class listener {
        int index;

        listener(int index) {
            this.index = index;
        }

        public void profilelistener() {
            profile = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Log.d("abcd",dataSnapshot.getValue().toString());

                        contacts1.get(index).setUrl(dataSnapshot.getValue().toString());


//                                                contacts1.add(new UserDetailwithUrl(key, "", dataSnapshot.getValue().toString(), 2
//                                                        , "", "",null,null));
                        //   userAdapter.notifyItemInserted(contacts1.size()-1);


                        userAdapter.notifyItemChanged(contacts1.size()-1);


                        //   Log.d("asdf",contacts1.get(contacts1.size()-1).getUrl());
                    }

                    reference.child("users").child(contacts1.get(index).getPh_number()).child("profile").removeEventListener(profile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            reference.child("users").child(contacts1.get(index).getPh_number()).child("profile").addListenerForSingleValueEvent(profile);
            // userAdapter.notifyDataSetChanged();

        }
    }

    public  void getcontact()
    {
        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME );
        while (cursor.moveToNext())
        {

            final String name= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            final String number= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(IsValid(number)==0) {
                if (number.substring(0, 3).equals("+91")) {
                    if (!(number.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
                        contacts.add(new UserDetail(number, name));

                        number1.add(number);

//                        if(!pref.getString(number,number).equals(name)) {
//                            edit.putString(number, name);
//                            edit.apply();
//                        }

                    }
                } else {

                    if (!(("+91" + number).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {

                        contacts.add(new UserDetail("+91"+number, name));

                        number1.add("+91"+number);

//                        if(!pref.getString("+91"+number,"+91"+number).equals(name)) {
//                            edit.putString("+91"+number, name);
//                            edit.apply();
//                        }

                    }
                }

            }

        }


                for(int i=0;i<contacts.size();i++) {
                  //  Log.d("asdf",dataSnapshot.getValue().toString());


                          new num().checkauth(contacts.get(i).getPh_number(),i);




                }


                childEvent = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (!(dataSnapshot.getKey().equals("name") || dataSnapshot.getKey().equals("groups") || dataSnapshot.getKey().equals("profile") ||
                                dataSnapshot.getKey().equals("status"))) {
                            //  Log.d("contacts",dataSnapshot.getKey());
                            int tell = 0;


                            for (int i = 0; i < contacts1.size(); i++) {

                                // Log.d("contact",contacts1.get(i).getPh_number());


                                    if (contacts1.get(i).getPh_number().equals(dataSnapshot.getKey())) {
                                        tell = 1;
                                        break;
                                    }



                            }
                            if (tell == 0) {
                                if (dataSnapshot.child("info").exists() ) {
                                        contacts1.add(new UserDetailWithStatus(dataSnapshot.getKey(), "", "null",
                                                "",0,null));

                                        userAdapter.notifyItemInserted(contacts1.size()-1);
                                        keyid.add(dataSnapshot.getKey());
                                        new listener(contacts1.size()-1).profilelistener();
                                    }

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
        if (getIntent().getIntExtra("path", 2) == 1) {
            ApplicationClass.contacts1.get(ApplicationClass.keyid1.indexOf(ApplicationClass.keyid2)).setMessagenum(2);

            tvtitle.setText("Forward");
            reference.child("users").child(currentUserNumber).addChildEventListener(childEvent);
        }


                if (getIntent().getIntExtra("path", 2) == 1) {

                    Group = new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            if(!(dataSnapshot.getValue().toString().length()>10  &&  dataSnapshot.getValue().toString().substring(0,10).equals("/*delete*/")) ){
                                contacts1.add(new UserDetailWithStatus(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), dataSnapshot.getValue().toString(), "null", ""
                                        , 0, dataSnapshot.getKey()));
                                keyid.add(dataSnapshot.getKey());
                                userAdapter.notifyItemInserted(contacts1.size() - 1);


                                reference.child("groups").child(dataSnapshot.getKey()).child("profile").addValueEventListener(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    contacts1.get(contacts1.size() - 1).setUrl(dataSnapshot.getValue().toString());
                                                    userAdapter.notifyItemChanged(contacts1.size() - 1);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        }
                                );
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

                    reference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").addChildEventListener(Group);

                }
               // Log.d("tag",contacts1.get(0).getPh_number());




    }
    public int IsValid(String number)
    {int c=0;
        for(int i=0; i<number.length();i++)
        {
            if(number.charAt(i)=='#' || number.charAt(i)=='$' || number.charAt(i)=='.' || number.charAt(i)=='[' || number.charAt(i)==']')
            {
                c=1;
                break;
            }

        }
        if(c==1)
        {
            return 1;
        }
        else
            return 0;
    }

    @Override
    public void onItemSelected(String key) {

        int index = keyid.indexOf(key);


        if (getIntent().getIntExtra("createGroup", 2) == 1) {
            yes=1;

            tvCreateGroup.setText("Create Group");



            if(contacts1.get(keyid.indexOf(key)).getSelected()==0) {
                contacts1.get(keyid.indexOf(key)).setSelected(1);
                z++;
                userAdapter.notifyItemChanged(keyid.indexOf(key));
            }
            else {
                contacts1.get(keyid.indexOf(key)).setSelected(0);
                z--;
                userAdapter.notifyItemChanged(keyid.indexOf(key));
            }


            if(z>=2) {
                tvCreateGroup.setVisibility(View.VISIBLE);
                cvCreate.setVisibility(View.VISIBLE);
            }
            else {
                tvCreateGroup.setVisibility(View.GONE);
                cvCreate.setVisibility(View.GONE);
            }

        }
        else if(ApplicationClass.addmembers==1)
        {


    for(int i=0;i<members.size();i++)
    {


    if(members.get(i).equals(contacts1.get(index).getPh_number()) )
    {
        x=1;
        break;
    }}
    if(x==0) {
        if (contacts1.get(index).getSelected() == 0) {
            contacts1.get(index).setSelected(1);

            z1++;
        }
        else {
            contacts1.get(index).setSelected(0);
z1--;
        }
        userAdapter.notifyItemChanged(index);
        tvCreateGroup.setText("ADD");
    }
    else{
        Toast.makeText(getApplicationContext(),"Already a member of this group",Toast.LENGTH_LONG).show();
        x=0;
    }
            tvCreateGroup.setText("ADD");
    if(z1>=1) {
        tvCreateGroup.setVisibility(View.VISIBLE);
        cvCreate.setVisibility(View.VISIBLE);
    }
    else {
        tvCreateGroup.setVisibility(View.GONE);
        cvCreate.setVisibility(View.GONE);
    }




        }

        else {

            ApplicationClass.keyid=key;

            if (contacts1.get(index).getKey() == null) {
                // tvtitle.setText("Forward To");
                //   ******  To forward a message in messageactivity ****
                Intent intent = new Intent(FriendsActivity.this, MessageActivity.class);
                intent.putExtra("title", contacts1.get(index).getPh_number());

                //  intent.putExtra("title","+91"+contacts1.get(index).getPh_number());
                // Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                //else
                //intent.putExtra("title", contacts1.get(index).getuID());

                intent.putExtra("phone", contacts1.get(index).getPh_number());

                if (getIntent().getIntExtra("path", 2) == 1) {



//                if(contacts1.get(index).getPh_number().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()) ||
//                        ("+91"+contacts1.get(index).getPh_number()).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
//                {
//                    Intent intent1=new Intent(FriendsActivity.this,MessageActivity2.class);
//                    intent1.putExtra("groupKey", contacts1.get(index).getuID());
//
//                    intent1.putExtra("groupkey",contacts1.get(index).getKey());
//                    intent1.putExtra("type", getIntent().getStringExtra("type"));
//                    intent1.putExtra("path", 2);
//                    intent1.putExtra("message", getIntent().getStringExtra("message"));
//                    this.finish();
//                    startActivity(intent1);
//
//                }

                    intent.putExtra("path", 2);
                    intent.putExtra("type", getIntent().getStringExtra("type"));
                    intent.putExtra("message", getIntent().getStringExtra("message"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if(MessageActivity2.messageActivity2!=null)
                        MessageActivity2.messageActivity2.finish();
                    this.finish();

                }
                intent.putExtra("profile", contacts1.get(index).getUrl());
                startActivity(intent);
            }
            else
            {


                //   ******  To forward a message in messageactivity2 ****
                Intent intent = new Intent(FriendsActivity.this, MessageActivity2.class);
                intent.putExtra("title", contacts1.get(index).getPh_number());
                intent.putExtra("path", 2);
                intent.putExtra("groupkey",contacts1.get(index).getKey());
                intent.putExtra("groupName",contacts1.get(index).getuID());
                intent.putExtra("type", getIntent().getStringExtra("type"));
                intent.putExtra("message", getIntent().getStringExtra("message"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(MessageActivity.messageActivity!=null)
                    MessageActivity.messageActivity.finish();
                this.finish();
                intent.putExtra("profile", contacts1.get(index).getUrl());
                startActivity(intent);
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvtitle.setText("Contacts");
        tvCreateGroup.setText("Create Group");
        ApplicationClass.addmembers=0;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.profile,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        int id = item.getItemId();
//
//        switch (id)
//        {
//            case R.id.Settings:
//                startActivity(new Intent(FriendsActivity.this,Settings.class));
//                break;
//
//            case android.R.id.home:
//                FriendsActivity.this.finish();
//                break;
//
//            case R.id.CreateGroup:
//                startActivity(new Intent(FriendsActivity.this,CreateGroup.class));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        tvCreateGroup.setText("Create Group");
        ApplicationClass.members.clear();

    }
}
