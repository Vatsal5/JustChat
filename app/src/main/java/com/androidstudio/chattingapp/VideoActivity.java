package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        vv=findViewById(R.id.vv);
        vv.setVideoURI(Uri.parse(getIntent().getStringExtra("uri")));
        vv.requestFocus();
        Log.d("playuri",getIntent().getStringExtra("uri"));
        vv.start();
    }
}
