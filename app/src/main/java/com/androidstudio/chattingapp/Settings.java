package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Settings extends AppCompatActivity {

    Toolbar toolbar;
    ImageView ivProfile,ivBack,ivBackground;
    TextView tvName,tvStatus;
    ValueEventListener details;
    LinearLayout llProfile,llTheme,llWallpaper;

    SharedPreferences wallpaper;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        wallpaper = getSharedPreferences("Wallpaper",0);
        editor = wallpaper.edit();

        ivProfile = findViewById(R.id.ivProfile);
        ivBack = findViewById(R.id.ivBack);
        tvName = findViewById(R.id.tvName);
        tvStatus = findViewById(R.id.tvStatus);
        toolbar = findViewById(R.id.toolbar);
        llProfile = findViewById(R.id.llProfile);
        llTheme = findViewById(R.id.llTheme);
        llWallpaper = findViewById(R.id.llWallpaper);
        ivBackground = findViewById(R.id.ivBackground);

        setSupportActionBar(toolbar);
        setTitle(null);

        details = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("profile").getValue(String.class)!=null)
                    Glide.with(Settings.this).load(dataSnapshot.child("profile").getValue(String.class)).into(ivProfile);
                else
                    ivProfile.setImageResource(R.drawable.person);

                tvName.setText(dataSnapshot.child("name").getValue(String.class));
                tvStatus.setText(dataSnapshot.child("status").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(details);

        if(!wallpaper.getString("value","null").equals("null"))
            ivBackground.setImageURI(Uri.parse(wallpaper.getString("value","null")));

        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Settings.this,Profile.class));

            }
        });

        llTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        llWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

                }
                    else {

                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
                    }
                }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10 && resultCode == RESULT_OK) {
            Uri imageuri = data.getData();

            editor.putString("value",imageuri.toString());
            editor.apply();

            ivBackground.setImageURI(imageuri);

            Toast.makeText(this, "Wallpaper set", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100)
        {
            if(grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                dialog.setMessage("This Permission is important to access image files from the gallery!").setTitle("Permission Required!");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

                    }
                });

                dialog.setNegativeButton("NO THANKS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
            }
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).removeEventListener(details);
    }

    public Drawable getBackground(Uri uri)
    {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return Drawable.createFromStream(inputStream, uri.toString() );
        } catch (FileNotFoundException e) {
            ivBackground.setBackground(null);
            SharedPreferences.Editor editor = wallpaper.edit();
            editor.putString("value",null);
            editor.apply();
        }
        return null;
    }

}
