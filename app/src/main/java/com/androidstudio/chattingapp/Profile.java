package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

public class Profile extends AppCompatActivity implements profile_listitem_adapter.itemSelected {

    ImageView ivProfile,ivClick;
    ListView list;
    profile_listitem_adapter adapter;
    ArrayList<String> data;
    CardView cv;

    Uri uri;
    LayoutInflater inflater;
    LinearLayout llProfile;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView tvHeading,tvSave,tvCancel;
    EditText etData;
    PopupWindow window;
    StorageReference reference;

    View view;
    private static  final int REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference= FirebaseStorage.getInstance().getReference("docs/");
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        data = new ArrayList<>();




        ivProfile = findViewById(R.id.ivProfile);


        llProfile = findViewById(R.id.llProfile);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_layout,null,false);

        tvHeading = view.findViewById(R.id.tvHeading);
        tvSave = view.findViewById(R.id.tvSave);
        tvCancel = view.findViewById(R.id.tvCancel);
        etData = view.findViewById(R.id.etData);


        list = findViewById(R.id.list);


        window = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);

                }
                else{
                    //pick_image();
                    CropImage.startPickImageActivity(Profile.this);
                }
            }
        });
        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getKey().equals("profile"))
                        {
                            Glide.with(Profile.this)
                                    .load(dataSnapshot.getValue())
                                    .into(ivProfile);
                        }
                        if(dataSnapshot.getKey().equals("name"))
                        {

                            data.add(0,dataSnapshot.getValue().toString());


                        }
                        if(dataSnapshot.getKey().equals("status"))
                        {

                            data.add(1,dataSnapshot.getValue().toString());


                        }
                       adapter.notifyDataSetChanged();

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
                });
        data.add(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        adapter = new profile_listitem_adapter(Profile.this,data);
        list.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(final int index) {
        switch (index)
        {
            case 0:
                tvHeading.setText("Enter Your Name");
                window.showAtLocation(llProfile,Gravity.BOTTOM,0,0);
                break;
            case 1:
                tvHeading.setText("Enter your status");
                window.showAtLocation(llProfile,Gravity.BOTTOM,0,0);
                break;
        }
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(index);
                if(index==0)
                {
                    databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("name").setValue(etData.getText().toString().trim());
                }
                else if(index==1)
                {
                    databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("status").setValue(etData.getText().toString().trim());

                }
                data.add(index,etData.getText().toString().trim());
                adapter.notifyDataSetChanged();
                window.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
            }
        });

        etData.setText(data.get(index));
        etData.setSelection(data.get(index).length());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                dialog.setMessage("This Permission is important to access image files from the gallery!").setTitle("Permission Required!");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);

                    }
                });

                dialog.setNegativeButton("NO THANKS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Profile.this.finish();

                    }
                });
            }
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                CropImage.startPickImageActivity(Profile.this);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri imageuri = CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri))
            {
                uri = imageuri;
                requestPermissions(new String []{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else {
                startCrop(imageuri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                uri = result.getUri();
                File from= new File(uri.getLastPathSegment(),"old");
                File to= new File("dp");
                from.renameTo(to);
                UploadTask uploadTask=reference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/").child("images/dp").
                        putFile(uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"file uploaded", Toast.LENGTH_LONG).show();

                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/").child("images/dp").getDownloadUrl().
                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Toast.makeText(getApplicationContext(),"hi", Toast.LENGTH_LONG).show();
                                        Glide.with(Profile.this)
                                                .load(uri.toString())
                                                .into(ivProfile);
                                        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                                child("profile").setValue(uri.toString());
                                    }
                                });
                    }
                });
            }
        }
    }

    private void startCrop(Uri imageuri)
    {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAllowRotation(true)
                .setAspectRatio(25,25)
                .start(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                Profile.this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
