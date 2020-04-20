package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class GroupDetails extends AppCompatActivity implements ParticipantsAdapter.itemSelected{

    ImageView ivGroupDP,ivEdit;
    TextView tvCreatedBy,tvGroupTitle,tvParticipants;
    LinearLayout llAddMembers,llExitGroup,llDeleteGroup;
    RecyclerView Participants;
    String groupKey,admin;
    RecyclerView.LayoutManager manager;
    ParticipantsAdapter adapter;
    ArrayList <String> members;
    ArrayList<UserDetailWithStatus> users;
    SharedPreferences pref;
    ChildEventListener DeleteGroup,exitGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        groupKey=getIntent().getStringExtra("groupkey");
        users= new ArrayList<>();
        ivGroupDP = findViewById(R.id.ivGroupDP);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvGroupTitle = findViewById(R.id.tvGroupTitle);
        llAddMembers = findViewById(R.id.llAddMembers);
        llExitGroup = findViewById(R.id.llExitGroup);
        Participants = findViewById(R.id.Participants);
        llDeleteGroup = findViewById(R.id.llDeleteGroup);
        tvParticipants = findViewById(R.id.tvParticipants);
        ivEdit = findViewById(R.id.ivEdit);

        pref= getApplicationContext().getSharedPreferences("Names",0);


        members=new ArrayList<>();
        Participants.setHasFixedSize(true);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("admin").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        admin = dataSnapshot.getValue().toString();

                        if(admin.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                            tvCreatedBy.setText("Created By You");

                        else {
                            if (pref.getString(admin, "null").equals("null"))
                                tvCreatedBy.setText("Created By " + admin);
                            else
                                tvCreatedBy.setText("Created By " + pref.getString(admin, "null"));
                        }

                        if(dataSnapshot.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                        {
                            llAddMembers.setVisibility(View.VISIBLE);
                            llDeleteGroup.setVisibility(View.VISIBLE);
                        }
                        else{
                            llAddMembers.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


        ivGroupDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(GroupDetails.this);
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        if(getIntent().getStringExtra("profile").equals("null"))
            ivGroupDP.setImageResource(R.drawable.group);
        else
            Glide.with(this).load(getIntent().getStringExtra("profile")).into(ivGroupDP);

        tvGroupTitle.setText(getIntent().getStringExtra("groupname"));


        manager = new LinearLayoutManager(this);
        Participants.setLayoutManager(manager);
        adapter= new ParticipantsAdapter(this,users);
        Participants.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot1, @Nullable String s) {
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(dataSnapshot1.getValue(String.class)).child("profile").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot1.getValue(String.class).equals(admin))
                                            users.add(new UserDetailWithStatus(dataSnapshot1.getValue(String.class),null,dataSnapshot.getValue(String.class),null,0));
                                        else
                                            users.add(new UserDetailWithStatus(dataSnapshot1.getValue(String.class),null,dataSnapshot.getValue(String.class),"admin",0));

                                        adapter.notifyDataSetChanged();
                                        tvParticipants.setText(users.size()+" Participants");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );
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



        llAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationClass.addmembers=1;
                ApplicationClass.activity=1;
                Intent intent=new Intent(GroupDetails.this,FriendsActivity.class);
                intent.putExtra("groupkey",groupKey);
                intent.putExtra("groupname",getIntent().getStringExtra("groupname"));
                startActivity(intent);

            }
        });

        llDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

                builder.setTitle("Are you sure you want to delete this group ");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DeleteGroup = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                FirebaseDatabase.getInstance().getReference().child("users").
                                        child(dataSnapshot.getValue().toString()).child("groups").child(groupKey).getRef().removeValue();

                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").removeEventListener(DeleteGroup);
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


                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").addChildEventListener(DeleteGroup);

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseDatabase.getInstance().getReference().child("groups").
                                        child(groupKey).getRef().removeValue();
                                GroupDetails.this.finish();
                                startActivity(new Intent(GroupDetails.this,MainActivity.class));

                            }
                        },2000);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        llExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

                builder.setTitle("Are you sure you want to leave this group ");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        exitGroup = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                                    dataSnapshot.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                            .child("groups").child(groupKey).getRef().removeValue();
                                    GroupDetails.this.finish();
                                    startActivity(new Intent(GroupDetails.this, MainActivity.class));

                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey)
                                            .child("members").removeEventListener(exitGroup);
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

                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey)
                                .child("members").addChildEventListener(exitGroup);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

    }
    @Override
    public void onItemSelected(final int index) {
        if(!(users.get(index).getPh_number().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                || ("+91"+users.get(index).getPh_number()).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
            String[] choices = {"View","Remove"};

            AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

            builder.setTitle("Choose")
                    .setItems(choices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    Intent intent = new Intent(GroupDetails.this, MessageActivity.class);
                                    intent.putExtra("phone", users.get(index).getPh_number());
                                    intent.putExtra("messagecount", 2);
                                    intent.putExtra("title",users.get(index).getPh_number() );
                                    ApplicationClass.groupusers=1;
                                    startActivity(intent);
                                case 1:

                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
}

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri imageuri = CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri))
            {
                requestPermissions(new String []{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else {
                startCrop(imageuri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
            }
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
}
