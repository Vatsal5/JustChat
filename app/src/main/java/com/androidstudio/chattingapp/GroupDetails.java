package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GroupDetails extends AppCompatActivity {

    ImageView ivGroupDP;
    TextView tvCreatedBy,tvGroupTitle;
    LinearLayout llAddMembers,llExitGroup;
    RecyclerView Participants;
    RecyclerView.LayoutManager manager;
    ParticipantsAdapter adapter;
    ArrayList<UserDetailWithStatus> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        ivGroupDP = findViewById(R.id.ivGroupDP);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvGroupTitle = findViewById(R.id.tvGroupTitle);
        llAddMembers = findViewById(R.id.llAddMembers);
        llExitGroup = findViewById(R.id.llExitGroup);
        Participants = findViewById(R.id.Participants);

        Participants.setHasFixedSize(true);

        if(getIntent().getStringExtra("profile").equals("null"))
            ivGroupDP.setImageResource(R.drawable.person);
        else
            Glide.with(this).load(getIntent().getStringExtra("profile")).into(ivGroupDP);

        tvGroupTitle.setText(getIntent().getStringExtra("groupname"));

        tvCreatedBy.setText("Created By You");

        manager = new LinearLayoutManager(this);
        Participants.setLayoutManager(manager);

    }
}
