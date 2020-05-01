package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.ProgressBar;
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
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
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

    ImageView ivProfile,ivClick,ivBack;
    ListView list;
    profile_listitem_adapter adapter;
    ArrayList<String> data;

    Uri uri;
    LayoutInflater inflater;
    LinearLayout llProfile;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference reference;

    ProgressBar progress;

    String source;
    View view;
    private static  final int REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences preftheme;

        Toolbar toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);
        ivClick = findViewById(R.id.ivClick);

        preftheme=getSharedPreferences("theme",0);
        String theme=preftheme.getString("theme","red");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.this.finish();
            }
        });




        switch (theme) {
            case "orange":

                toolbar.setBackgroundColor(getResources().getColor(R.color.Orange));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#d6514a")));

                break;

            case "blue":

                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#283470")));

                break;


            case "bluish":
                toolbar.setBackgroundColor(getResources().getColor(R.color.bluish));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#292f3b")));
                break;


            case "deepred":
                toolbar.setBackgroundColor(getResources().getColor(R.color.deepred));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#e24a3c")));
                break;

            case "faintpink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.faintpink));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#f25c65")));

                break;

            case "darkblue":
                toolbar.setBackgroundColor(getResources().getColor(R.color.darkblue));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#2b3050")));
                break;


            case "green":
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#6ebd52")));
                break;

            case "lightorange":
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightorange));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#f2a37a")));

                break;

            case "lightred":
                toolbar.setBackgroundColor(getResources().getColor(R.color.lightred));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#e9776c")));
                break;


            case "mustard":
                toolbar.setBackgroundColor(getResources().getColor(R.color.mustard));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#eba54d")));
                break;

            case "pink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pink));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#e91e63")));
                break;

            case "pureorange":
                toolbar.setBackgroundColor(getResources().getColor(R.color.pureorange));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#ff5722")));
                break;

            case "purepink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purepink));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#f57268")));
                break;

            case "purple":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#49264e")));
                break;

            default:
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                ViewCompat.setBackgroundTintList(ivClick,ColorStateList.valueOf(Color.parseColor("#d6514a")));
        }

        setTitle(null);

        reference= FirebaseStorage.getInstance().getReference("docs/");
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        data = new ArrayList<>();

        ivProfile = findViewById(R.id.ivProfile);
        progress = findViewById(R.id.progress);

        llProfile = findViewById(R.id.llProfile);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        list = findViewById(R.id.list);

        ivClick.setOnClickListener(new View.OnClickListener() {
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

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this,ShowImage.class);
                intent.putExtra("source",source);
                startActivity(intent);
            }
        });

        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getKey().equals("profile"))
                        {
                            source = dataSnapshot.getValue(String.class);
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
        switch (index) {
            case 0:
//                tvHeading.setText("Enter Your Name");
//                window.showAtLocation(llProfile,Gravity.BOTTOM,0,0);
//                break;

                LayoutInflater inflater = LayoutInflater.from(Profile.this);
                View v = inflater.inflate(R.layout.edittext, null);

                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
                builder.setTitle("Enter Your Name");

                final EditText etTitle = v.findViewById(R.id.etGroupTitle);
                etTitle.setText(data.get(index));
                etTitle.setSelection(data.get(index).length());

                builder.setView(v);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.remove(index);
                        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("name").setValue(etTitle.getText().toString().trim());
                        data.add(index, etTitle.getText().toString().trim());
                        adapter.notifyDataSetChanged();
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case 1:
                LayoutInflater inflater1 = LayoutInflater.from(Profile.this);
                View v1 = inflater1.inflate(R.layout.edittext, null);

                final androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
                builder1.setTitle("Enter Your Status");

                final EditText etStatus = v1.findViewById(R.id.etGroupTitle);
                etStatus.setText(data.get(index));
                etStatus.setSelection(data.get(index).length());

                builder1.setView(v1);
                builder1.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        data.remove(index);
                        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("status").setValue(etStatus.getText().toString().trim());
                        data.add(index, etStatus.getText().toString().trim());
                        adapter.notifyDataSetChanged();
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                androidx.appcompat.app.AlertDialog dialog1 = builder1.create();
                dialog1.show();
                break;

        }
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
                progress.setVisibility(View.VISIBLE);
                uri = result.getUri();
                File from= new File(uri.getLastPathSegment(),"old");
                File to= new File("dp");
                from.renameTo(to);
                UploadTask uploadTask=reference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/").child("images/dp").
                        putFile(uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/").child("images/dp").getDownloadUrl().
                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        source = uri.toString();
                                        Glide.with(Profile.this)
                                                .load(uri.toString())
                                                .into(ivProfile);
                                        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                                                child("profile").setValue(uri.toString());
                                        progress.setVisibility(View.GONE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.setVisibility(View.GONE);
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
}
