package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

    Toolbar toolbar;TextView tvtitle;
    ImageView ivProfile,ivBack,ivBackground;
    TextView tvName,tvStatus;
    ValueEventListener details;
    Toolbar ll;

    LinearLayout llProfile,llTheme,llWallpaper,llsettings;
    Boolean flag=false;

    SharedPreferences wallpaper;
    SharedPreferences preftheme;
    SharedPreferences.Editor editor1;
    ScrollView theme;
    CardView cvorange,cvpink,cvpureorange,cvpurepink,cvpurple,cvblue,cvbluish,cvred,cvfaintpink,cvlightred,cvdeepred,cvdarkblue,cvgreen,cvmustard,cvlightorange;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        theme=findViewById(R.id.theme);
        ll=findViewById(R.id.toolbar);
        preftheme=getApplicationContext().getSharedPreferences("theme",0);
        editor1=preftheme.edit();

        wallpaper = getSharedPreferences("Wallpaper",0);
        editor = wallpaper.edit();
        llsettings=findViewById(R.id.settings);
        tvtitle=findViewById(R.id.tvtitle);
        ivBack=findViewById(R.id.ivBack);

        cvblue=findViewById(R.id.cvblue);
        cvbluish=findViewById(R.id.cvbluish);
        cvdarkblue=findViewById(R.id.cvdarkblue);
        cvdeepred=findViewById(R.id.cvdeepred);
        cvfaintpink=findViewById(R.id.cvfaintpink);
        cvgreen=findViewById(R.id.cvgreen);
        cvlightorange=findViewById(R.id.cvlightorange);
        cvlightred=findViewById(R.id.cvlightred);
        cvmustard=findViewById(R.id.cvmustard);
        cvorange=findViewById(R.id.cvorange);
        cvpink=findViewById(R.id.cvpink);
        cvpureorange=findViewById(R.id.cvpureorange);
        cvpurepink=findViewById(R.id.cvpurepink);
        cvpurple=findViewById(R.id.cvpurple);
        cvred=findViewById(R.id.cvred);

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


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag)
                {
                    llsettings.setVisibility(View.VISIBLE);
                    theme.setVisibility(View.GONE);
                    tvtitle.setText("Settings");
                    flag=false;
                }
                else
                    Settings.this.finish();
            }
        });


        String theme1=preftheme.getString("theme","red");

        if(theme1.equals("orange"))
        {
            ll.setBackgroundColor(getResources().getColor(R.color.Orange));
        }

        else if(theme1.equals("blue"))
        {
            ll.setBackgroundColor(getResources().getColor(R.color.blue));
        }


        else if(theme1.equals("bluish")) {
            ll.setBackgroundColor(getResources().getColor(R.color.bluish));

        }

        else if(theme1.equals("deepred")) {
            ll.setBackgroundColor(getResources().getColor(R.color.deepred));

        }

        else if(theme1.equals("faintpink")) {
            ll.setBackgroundColor(getResources().getColor(R.color.faintpink));

        }

        else if(theme1.equals("darkblue")) {
            ll.setBackgroundColor(getResources().getColor(R.color.darkblue));

        }

        else if (theme1.equals("green")) {
            ll.setBackgroundColor(getResources().getColor(R.color.green));

        }

        else if (theme1.equals("lightorange")) {
            ll.setBackgroundColor(getResources().getColor(R.color.lightorange));

        }

        else  if (theme1.equals("lightred")) {
            ll.setBackgroundColor(getResources().getColor(R.color.lightred));

        }

        else if(theme1.equals( "mustard")) {
            ll.setBackgroundColor(getResources().getColor(R.color.mustard));

        }

        else if (theme1.equals("pink")) {
            ll.setBackgroundColor(getResources().getColor(R.color.pink));

        }

        else if(theme1.equals("pureorange")) {
            ll.setBackgroundColor(getResources().getColor(R.color.pureorange));

        }

        else if(theme1.equals( "purepink")) {
            ll.setBackgroundColor(getResources().getColor(R.color.purepink));

        }

        else if(theme1.equals( "purple")) {
            ll.setBackgroundColor(getResources().getColor(R.color.purple));

        }

        else {
            ll.setBackgroundColor(getResources().getColor(R.color.red));


        }

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
llsettings.setVisibility(View.GONE);
theme.setVisibility(View.VISIBLE);
tvtitle.setText("Select Theme");
flag=true;

            }
        });

        cvred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              editor1.putString("theme","red");
              editor1.apply();
              Settings.this.finish();
            }
        });
        cvpurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","purple");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvpurepink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","purepink");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvpureorange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","pureorange");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvpink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","pink");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvorange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","orange");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvmustard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","mustard");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvlightred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","lightred");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvlightorange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","lightorange");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvgreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","green");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvfaintpink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","faintpink");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvdeepred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","deepred");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvdarkblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","darkblue");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvbluish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","bluish");
                editor1.apply();
                Settings.this.finish();
            }
        });

        cvblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("theme","blue");
                editor1.apply();
                Settings.this.finish();
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
                        if(wallpaper.getString("value","null").equals("null"))
                        {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
                        }
                        else
                        {
                            String[] options = {"Remove Existing Wallpaper","Add Wallpaper"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                            builder.setTitle("Choose...")
                                    .setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            switch (i){
                                                case 0:
                                                    editor.putString("value","null");
                                                    editor.apply();
                                                    ivBackground.setImageResource(0);
                                                    Toast.makeText(Settings.this, "Wallpaper set to default!", Toast.LENGTH_SHORT).show();
                                                    break;

                                                case 1:
                                                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    intent.setType("image/*");
                                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
                                                    break;
                                            }
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            if(flag)
            {
                llsettings.setVisibility(View.VISIBLE);
                theme.setVisibility(View.GONE);
                tvtitle.setText("Settings");
                flag=false;
            }
            else
                Settings.this.finish();

            return true;
        }

        // If it wasn't the Back key, bubble up to the default
        // system behavior
        return super.onKeyDown(keyCode, event);
    }
}
