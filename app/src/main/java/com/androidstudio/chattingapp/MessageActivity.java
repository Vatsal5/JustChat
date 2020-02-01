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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    EditText etMessage;
    ImageView ivSend;
    String RecieverPhone;
    FirebaseDatabase database;
    DatabaseReference reference;

    RecyclerView Messages;
    LinearLayoutManager manager;
    MessageAdapter adapter;
    String senderNumber;

    ArrayList<MessageModel> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        setTitle(String.valueOf(getIntent().getStringExtra("title")));


        RecieverPhone = getIntent().getStringExtra("phone");
        senderNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();


        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);


        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(true);
        Messages.setLayoutManager(manager);

        chats = new ArrayList<>();
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("users").child(senderNumber).child(RecieverPhone).child("message").setValue(etMessage.getText().toString());
                chats.add(new MessageModel(senderNumber,RecieverPhone,etMessage.getText().toString()));
                adapter = new MessageAdapter(MessageActivity.this,chats);
                Messages.setAdapter(adapter);
                ;
            }
        });




       ( reference.child("users").child(RecieverPhone).child(senderNumber)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //    Toast.makeText(getApplicationContext(),dataSnapshot.child("message").getValue().toString(),Toast.LENGTH_LONG).show();

               if(!(dataSnapshot.child("message").getValue()).equals("null"))
                {
                    // Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),dataSnapshot.child("message").getValue().toString(),Toast.LENGTH_LONG).show();
                    chats.add(new MessageModel(RecieverPhone,senderNumber,dataSnapshot.child("message").getValue().toString()));
                    adapter = new MessageAdapter(MessageActivity.this,chats);
                    Messages.setAdapter(adapter);

               }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                chats.add(new MessageModel(RecieverPhone,senderNumber,dataSnapshot.child("message").getValue().toString()));
                adapter = new MessageAdapter(MessageActivity.this,chats);
                Messages.setAdapter(adapter);


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




        //Log.d("Reciever",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
    }

}
