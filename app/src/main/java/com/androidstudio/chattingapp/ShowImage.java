package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

public class ShowImage extends AppCompatActivity{

    ImageView ivShowImage;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        ivShowImage = findViewById(R.id.ivShowImage);
        ivBack = findViewById(R.id.ivBack);

        String source = getIntent().getStringExtra("source");

        Glide.with(ShowImage.this).load(source).into(ivShowImage);
        ivShowImage.setOnTouchListener(new ImageMatrixTouchHandler(ShowImage.this));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImage.this.finish();
            }
        });

    }
}
