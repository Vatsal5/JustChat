package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;

public class GroupDetails extends AppCompatActivity {

    ImageView ivGroupDP;
    TextView tvCreatedBy,tvGroupTitle;
    LinearLayout llAddMembers,llExitGroup,llDeleteGroup;
    RecyclerView Participants;
    String groupKey;
    RecyclerView.LayoutManager manager;
    ParticipantsAdapter adapter;
    ArrayList <String> members;
    ArrayList<UserDetailWithStatus> users;

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
        members=new ArrayList<>();


        Participants.setHasFixedSize(true);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("admin").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
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




        if(getIntent().getStringExtra("profile").equals("null"))
            ivGroupDP.setImageResource(R.drawable.person);
        else
            Glide.with(this).load(getIntent().getStringExtra("profile")).into(ivGroupDP);

        tvGroupTitle.setText(getIntent().getStringExtra("groupname"));

        tvCreatedBy.setText("Created By You");


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
                                        users.add(new UserDetailWithStatus(dataSnapshot1.getValue(String.class),null,dataSnapshot.getValue(String.class),null,0));
                                        adapter.notifyDataSetChanged();
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


                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                FirebaseDatabase.getInstance().getReference().child("users").
                                        child(dataSnapshot.getValue().toString()).child("groups").child(groupKey).getRef().removeValue();
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
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey)
                                .child("members").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if(dataSnapshot.getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                                {
                                    dataSnapshot.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                            .child("groups").child(groupKey).getRef().removeValue();
                                    GroupDetails.this.finish();
                                    startActivity(new Intent(GroupDetails.this,MainActivity.class));
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
                        });
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
}
