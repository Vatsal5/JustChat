package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    EditText etMessage;
    ImageView ivSend;
    String RecieverPhone;

    RecyclerView Messages;
    LinearLayoutManager manager;
    MessageAdapter adapter;

    ArrayList<MessageModel> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        setTitle(String.valueOf(getIntent().getStringExtra("title")));

        RecieverPhone = getIntent().getStringExtra("phone");

        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chats.add(new MessageModel(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),RecieverPhone,etMessage.getText().toString()));
                etMessage.setText(null);
                Messages.scrollToPosition(chats.size()-1);
                adapter.notifyDataSetChanged();
            }
        });

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(true);
        Messages.setLayoutManager(manager);

        chats = new ArrayList<>();

        adapter = new MessageAdapter(MessageActivity.this,chats);
        Messages.setAdapter(adapter);

    }

}