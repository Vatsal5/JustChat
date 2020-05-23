package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class SendMessage extends AppCompatActivity {

    ImageView ivSend,ivVideo;
    TextView tvSendTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        ivSend = findViewById(R.id.ivSend);
        ivVideo = findViewById(R.id.ivVideomessage);
        tvSendTo = findViewById(R.id.tvSendTo);

        Glide.with(SendMessage.this).load(getIntent().getStringExtra("source")).into(ivVideo);

        tvSendTo.setText("Send to "+getIntent().getStringExtra("receiver"));

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK,new Intent().setData(Uri.parse(getIntent().getStringExtra("source"))));
                finish();
            }
        });
    }
}
