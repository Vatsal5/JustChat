package com.androidstudio.chattingapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    ChildEventListener chreceiver, chsender;

    DBHandler Handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        Handler = new DBHandler(MessageActivity.this);
        Handler.Open();

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        setTitle(String.valueOf(getIntent().getStringExtra("title")));
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

                if(etMessage.getText().toString().trim().isEmpty())
                    Toast.makeText(MessageActivity.this, "Please enter a message", Toast.LENGTH_LONG).show();
                else
                    {
                    reference.child("users").child(sender).child(RecieverPhone).push().setValue(etMessage.getText().toString());
                    //   String pushKey= reference.child("users").child(sender).child(RecieverPhone).push().getKey();
                    chats.add(new MessageModel(sender, RecieverPhone, etMessage.getText().toString()));
                    Handler.addMessage(new MessageModel(sender, RecieverPhone, etMessage.getText().toString()));
                    // reference.child("users").child(sender).child(RecieverPhone).child("message"+m).setValue(etMessage.getText().toString());
                    //m++;

                    adapter.notifyDataSetChanged();
                    Messages.scrollToPosition(chats.size() - 1);
                    etMessage.setText(null);
                }
            }
        });

        Messages = findViewById(R.id.Messages);
        Messages.setHasFixedSize(true);

        manager = new LinearLayoutManager(MessageActivity.this);
        manager.setStackFromEnd(true);
        Messages.setLayoutManager(manager);

        chats = Handler.getMessages(RecieverPhone);

        adapter = new MessageAdapter(MessageActivity.this,chats);
        Messages.setAdapter(adapter);

//        chsender = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if (dataSnapshot.getKey().equals(RecieverPhone)) {//chats.clear();
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//                        if (!(child.getKey().equals("message"))) {
//
//                            chats.add(new MessageModel(sender, RecieverPhone, child.getValue().toString()));
//
//                            adapter.notifyDataSetChanged();
//                            Messages.scrollToPosition(chats.size() - 1);
//                        }
//
//                    }
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        reference.child("users").child(sender).addChildEventListener(chsender);

        chreceiver = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();

                if (!(dataSnapshot.getKey().equals("message") )) {
                    chats.add(new MessageModel(RecieverPhone, sender, dataSnapshot.getValue().toString()));
                    Handler.addMessage(new MessageModel(RecieverPhone, sender, dataSnapshot.getValue().toString()));
                    dataSnapshot.getRef().removeValue();


                    adapter.notifyDataSetChanged();
                    Messages.scrollToPosition(chats.size()-1);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.child("users").child(RecieverPhone).child(sender).removeEventListener(chreceiver);
        //reference.child("users").child(sender).removeEventListener(chsender);
        chats.clear();
        Handler.close();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case android.R.id.home:
                MessageActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}