package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

public class MessageActivity extends AppCompatActivity {

    EditText etMessage;
    ImageView ivSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        setTitle(String.valueOf(getIntent().getStringExtra("title")));

        etMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSend);
    }

}
