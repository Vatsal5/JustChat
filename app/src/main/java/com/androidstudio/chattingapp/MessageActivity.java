package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    EditText etMessage;
    ImageView ivSend;
    String RecieverPhone;
    FirebaseDatabase database;
    DatabaseReference reference;

    RecyclerView Messages;
    String sender;
    LinearLayoutManager manager;
    MessageAdapter adapter;

    ArrayList<MessageModel> chats;
    int m=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        setTitle(String.valueOf(getIntent().getStringExtra("title")));

        RecieverPhone = getIntent().getStringExtra("phone");
        sender=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Messages = findViewById(R.id.Messages);
        chats = new ArrayList<>();

        Messages.setHasFixedSize(true);

        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("users").child(sender).child(RecieverPhone).child("message"+m).setValue(etMessage.getText().toString());
                m++;

            }
        });

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(true);
        Messages.setLayoutManager(manager);


        reference.child("users").child(sender).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           /*     if(!dataSnapshot.getValue(String.class).equals("/null"))
                {
                    chats.add(new MessageModel(RecieverPhone, sender, dataSnapshot.getValue(String.class)));
                    adapter = new MessageAdapter(MessageActivity.this, chats);
                    Messages.setAdapter(adapter);

                   // adapter.notifyDataSetChanged();
                }*/
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot child: dataSnapshot.getChildren()) {
                    chats.add(new MessageModel(sender, RecieverPhone, child.getValue().toString()));


                    adapter.notifyDataSetChanged();
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
        });

        reference.child("users").child(RecieverPhone).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

              /*  if(!dataSnapshot.getValue(String.class).equals("/null"))
                {
                    chats.add(new MessageModel(RecieverPhone,sender, dataSnapshot.getValue(String.class)));
                    adapter = new MessageAdapter(MessageActivity.this, chats);
                    Messages.setAdapter(adapter);

                  //  adapter.notifyDataSetChanged();
                }*/
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot child: dataSnapshot.getChildren())
                {
                chats.add(new MessageModel(RecieverPhone,sender,child.getValue().toString()));


                adapter.notifyDataSetChanged();}
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
        adapter = new MessageAdapter(MessageActivity.this,chats);
        Messages.setAdapter(adapter);




        //Log.d("Reciever",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
    }

}