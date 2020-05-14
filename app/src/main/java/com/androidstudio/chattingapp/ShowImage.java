package com.androidstudio.chattingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ShowImage extends AppCompatActivity{

    ImageView ivShowImage;
    ImageView ivBack;
    TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        ivShowImage = findViewById(R.id.ivShowImage);
        ivBack = findViewById(R.id.ivBack);
        tvName = findViewById(R.id.tvName);

        if(getIntent().getStringExtra("title")!=null)
            tvName.setText(getIntent().getStringExtra("title"));
        else
            tvName.setText(null);

        String source = getIntent().getStringExtra("source");

        Glide.with(ShowImage.this).load(source).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImage.this);
                builder.setTitle("Load Failed")
                        .setMessage("Sorry,this image appears to be missing from your phone")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                ShowImage.this.finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(ivShowImage);
        ivShowImage.setOnTouchListener(new ImageMatrixTouchHandler(ShowImage.this));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImage.this.finish();
            }
        });

    }
}
