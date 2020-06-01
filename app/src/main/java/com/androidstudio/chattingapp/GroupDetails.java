package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

public class GroupDetails extends AppCompatActivity implements ParticipantsAdapter.itemSelected{

    ImageView ivGroupDP,ivEdit;
    TextView tvCreatedBy,tvGroupTitle;
    LinearLayout llAddMembers,llExitGroup,llDeleteGroup;
    RecyclerView Participants;
    String groupKey,admin;
    RecyclerView.LayoutManager manager;
    ParticipantsAdapter adapter;

    ArrayList<UserDetailWithStatus> users;
    SharedPreferences pref;
    ChildEventListener DeleteGroup,exitGroup,newname,deletegroupdp;
    ChildEventListener admin1;
    ChildEventListener remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        groupKey=getIntent().getStringExtra("groupkey");
        ApplicationClass.groupmembers.clear();
        ivEdit = findViewById(R.id.ivEdit);
        ivEdit.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white1)));


        users= new ArrayList<>();
        ivGroupDP = findViewById(R.id.ivGroupDP);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvGroupTitle = findViewById(R.id.tvGroupTitle);
        llAddMembers = findViewById(R.id.llAddMembers);
        llExitGroup = findViewById(R.id.llExitGroup);
        Participants = findViewById(R.id.Participants);
        llDeleteGroup = findViewById(R.id.llDeleteGroup);



        pref= getApplicationContext().getSharedPreferences("Names",0);



        Participants.setHasFixedSize(true);


               admin1= new ChildEventListener(){
                   @Override
                   public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       if(dataSnapshot.getKey().equals("admin")) {

                               admin = dataSnapshot.getValue().toString();

                               if (admin.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                                   tvCreatedBy.setText("Created By You");

                               else {
                                   if (pref.getString(admin, "null").equals("null"))
                                       tvCreatedBy.setText("Created By " + admin);
                                   else
                                       tvCreatedBy.setText("Created By " + pref.getString(admin, "null"));
                               }


                               if (dataSnapshot.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                                   llExitGroup.setVisibility(View.GONE);
                                   llAddMembers.setVisibility(View.VISIBLE);
                                   llDeleteGroup.setVisibility(View.VISIBLE);
                               } else {
                                   llAddMembers.setVisibility(View.GONE);
                               }

                           FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).removeEventListener(admin1);
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
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).addChildEventListener(admin1);







        if(getIntent().getStringExtra("profile").equals("null"))
            ivGroupDP.setImageResource(R.drawable.group);
        else
            Glide.with(this).load(getIntent().getStringExtra("profile")).into(ivGroupDP);

        tvGroupTitle.setText(getIntent().getStringExtra("groupName"));


        manager = new LinearLayoutManager(this);
        Participants.setLayoutManager(manager);
        adapter= new ParticipantsAdapter(this,users);
        Participants.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot1, @Nullable String s) {
                        ApplicationClass.groupmembers.add(dataSnapshot1.getValue(String.class));
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(dataSnapshot1.getValue(String.class)).child("profile").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot1.getValue(String.class).equals(admin)) {
                                            users.add(new UserDetailWithStatus(dataSnapshot1.getValue(String.class), null, dataSnapshot.getValue(String.class), null, 0, null));

                                        }
                                        else {
                                            users.add(new UserDetailWithStatus(dataSnapshot1.getValue(String.class), null, dataSnapshot.getValue(String.class), "admin", 0, null));

                                        }//  Log.d("USERSS",dataSnapshot1.getValue(String.class));
                                        adapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );


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

        ivGroupDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.startPickImageActivity(GroupDetails.this);
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater inflater = LayoutInflater.from(GroupDetails.this);
                        View v = inflater.inflate(R.layout.edittext,null);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);
                        final EditText edittext = new EditText(GroupDetails.this);

                        builder.setTitle("Title");

                        final EditText etGroupTitle = v.findViewById(R.id.etGroupTitle);
                        etGroupTitle.setText(tvGroupTitle.getText());
                        etGroupTitle.setSelection(tvGroupTitle.getText().length());

                        builder.setView(v);
                        edittext.setText(tvGroupTitle.getText());
                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(isConnected()) {
                                    if (etGroupTitle.getText().toString().trim().length() > 0) {
                                        ApplicationClass.RenameGroup = etGroupTitle.getText().toString();
                                        tvGroupTitle.setText(etGroupTitle.getText().toString());
                                        for (int f = 0; f < users.size(); f++) {
                                            FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("layout").child(users.get(f).getPh_number()).push().setValue("rename " + etGroupTitle.getText().toString());
                                        }
                                        newname = new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                FirebaseDatabase.getInstance().getReference().child("users").
                                                        child(dataSnapshot.getValue().toString()).child("groups").child(groupKey).setValue(etGroupTitle.getText().toString());


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
                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").addChildEventListener(newname);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Please anter a valid name", Toast.LENGTH_LONG).show();
                                    }


                                }
                                else{
                                    showInternetWarning();
                                }
                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });
            }
        });

       deletegroupdp= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot1, @Nullable String s) {


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                StorageReference file1;
                        file1=FirebaseStorage.getInstance().getReferenceFromUrl(dataSnapshot.getValue().toString());
                        file1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("profile").removeEventListener(deletegroupdp);
                            }
                        });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("profile").addChildEventListener(deletegroupdp);



        llAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isConnected()) {
                    ApplicationClass.addmembers = 1;
                    ApplicationClass.activity = 1;
                    Intent intent = new Intent(GroupDetails.this, FriendsActivity.class);
                    intent.putExtra("groupkey", groupKey);
                    intent.putExtra("users", users.size());
                    intent.putExtra("groupname", getIntent().getStringExtra("groupName"));
                    startActivity(intent);
                }
                else
                    showInternetWarning();
            }
        });

        llDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

                builder.setTitle("Are you sure you want to delete this group ");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isConnected()) {
                            final ProgressDialog progressDialog = new ProgressDialog(GroupDetails.this);
                            progressDialog.setMessage("Please Wait");
                            progressDialog.show();

                        DeleteGroup = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                FirebaseDatabase.getInstance().getReference().child("users").
                                        child(dataSnapshot.getValue().toString()).child("groups").child(groupKey).getRef().removeValue();


                                FirebaseDatabase.getInstance().getReference().child("users").
                                        child(dataSnapshot.getValue().toString()).child("deletedgroups").child(groupKey).setValue(getIntent().getStringExtra("groupName"));

                                FirebaseDatabase.getInstance().getReference().child("users").
                                        child(dataSnapshot.getValue().toString()).child("deletedgroups").child(groupKey).setValue(getIntent().getStringExtra("groupName"));

                                MessageActivity2.getInstance().finish();

                               // FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").removeEventListener(DeleteGroup);
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


                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members").addChildEventListener(DeleteGroup);

//                        StorageReference file1;
//                        file1=FirebaseStorage.getInstance().getReferenceFromUrl(FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members"));
//                        file1.delete();

                            FirebaseDatabase.getInstance().getReference().child("groups").
                                    child(groupKey).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(GroupDetails.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    progressDialog.cancel();
                                    GroupDetails.this.finish();
                                    startActivity(intent);
                                }
                            });

                        }
                        else
                            showInternetWarning();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        llExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

                builder.setTitle("Are you sure you want to leave this group ");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isConnected()) {

                            exitGroup = new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    if (dataSnapshot.getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                                        dataSnapshot.getRef().removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                                .child("deletedgroups").child(groupKey).setValue(getIntent().getStringExtra("groupName"));

                                        FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                                .child("groups").child(groupKey).getRef().removeValue();
                                        for (int i = 0; i < users.size(); i++) {

                                            FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("layout").child(users.get(i).getPh_number()).push().setValue("exit " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                        }
                                        Intent intent = new Intent(GroupDetails.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        GroupDetails.this.finish();
                                        MessageActivity2.getInstance().finish();

                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey)
                                                .child("members").removeEventListener(exitGroup);
                                        startActivity(intent);

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

                            FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey)
                                    .child("members").addChildEventListener(exitGroup);
                        }
                        else
                            showInternetWarning();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

    }
    @Override
    public void onItemSelected(final int index) {
        if(!(users.get(index).getPh_number().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {

            if (admin.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                String[] choices = {"View", "Remove","Make Admin"};

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

                builder.setTitle("Choose")
                        .setItems(choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        Intent intent = new Intent(GroupDetails.this, MessageActivity.class);
                                        intent.putExtra("phone", users.get(index).getPh_number());
                                        intent.putExtra("messagecount", 2);
                                        intent.putExtra("profile", users.get(index).getUrl());
                                        intent.putExtra("title", users.get(index).getPh_number());
                                        ApplicationClass.groupusers = 1;
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

                                        builder.setTitle("Are you sure you want to remove this member ");
                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (isConnected()) {

                                                    adapter.notifyDataSetChanged();


                                                    remove = new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                            if (dataSnapshot.getValue().toString().equals(users.get(index).getPh_number())) {
                                                                dataSnapshot.getRef().removeValue().addOnSuccessListener(
                                                                        new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                FirebaseDatabase.getInstance().getReference().child("users")
                                                                                        .child(users.get(index).getPh_number())
                                                                                        .child("deletedgroups").child(groupKey).setValue(getIntent().getStringExtra("groupName"));

                                                                                for (int i = 0; i < users.size(); i++) {

                                                                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("layout").child(users.get(i).getPh_number()).push().setValue("remove " + users.get(index).getPh_number());
                                                                                }
                                                                                FirebaseDatabase.getInstance().getReference().child("users")
                                                                                        .child(users.get(index).getPh_number())
                                                                                        .child("groups").child(groupKey).getRef().removeValue().addOnSuccessListener(
                                                                                        new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                users.remove(index);
                                                                                                adapter.notifyItemRemoved(index);
                                                                                                FirebaseDatabase.getInstance().getReference().child("groups")
                                                                                                        .child(groupKey).child("members").removeEventListener(remove);

                                                                                            }
                                                                                        }
                                                                                );
                                                                            }
                                                                        }
                                                                );


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
                                                    FirebaseDatabase.getInstance().getReference().child("groups")
                                                            .child(groupKey).child("members").addChildEventListener(remove);


                                                }
                                                else
                                                    showInternetWarning();
                                            }
                                        });
                                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        if(users.size()>3)
                                        builder.show();
                                        else
                                            Toast.makeText(getApplicationContext(),"Group can't have less than three members",Toast.LENGTH_LONG).show();

                                        break;

                                    case 2:


                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupDetails.this);

                                        builder2.setTitle("Are you sure you want to make this member as admin ");
                                        builder2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (isConnected()) {
                                                    ProgressDialog dialog1 =new ProgressDialog(GroupDetails.this);
                                                    dialog1.setMessage("Please Wait");
                                                    dialog1.show();
                                                    dialog1.setCanceledOnTouchOutside(false);

                                                    FirebaseDatabase.getInstance().getReference().child("groups")
                                                            .child(groupKey).child("admin").setValue(users.get(index).getPh_number());
                                                    for (int i = 0; i < users.size(); i++) {

                                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("layout").child(users.get(i).getPh_number()).push().setValue("adminChanged " + users.get(index).getPh_number());
                                                    }


                                                    dialog1.dismiss();
                                                    GroupDetails.this.finish();


                                                }
                                                else
                                                    showInternetWarning();
                                            }
                                        });
                                        builder2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                            builder2.show();

                                            break;

                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                String []choices ={"View"};

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(GroupDetails.this, MessageActivity.class);
                                intent.putExtra("phone", users.get(index).getPh_number());
                                intent.putExtra("messagecount", 2);
                                intent.putExtra("profile", users.get(index).getUrl());
                                intent.putExtra("title", users.get(index).getPh_number());
                                ApplicationClass.groupusers = 1;
                                startActivity(intent);
                                break;

                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
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
                requestPermissions(new String []{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }else {
                startCrop(imageuri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if(isConnected()) {
                    Uri uri = result.getUri();
                    new CompressImage().execute(uri);
                }
                else{
                    showInternetWarning();
                }
            }
        }
    }
    class CompressImage extends AsyncTask<Uri,Void,Uri> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(GroupDetails.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Please Wait");
            dialog.show();

        }

        @Override
        protected Uri doInBackground(Uri... uris) {
            String filePath = getRealPathFromURI(uris[0]);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/ProfilePicsUploaded");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }

            // Create a file to save the image
            File file = new File(imagesFolder, System.currentTimeMillis()+".jpg");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);

//          write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return Uri.fromFile(file);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if(uri!=null) {

               // ApplicationClass.GroupDp = uri.toString();

                UploadTask uploadTask = FirebaseStorage.getInstance().getReference(groupKey).child("dp").
                        putFile(uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        FirebaseStorage.getInstance().getReference(groupKey).child("dp").getDownloadUrl().
                                addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).
                                                child("profile").setValue(uri.toString());
                                       // progress.setVisibility(View.GONE);

                                        dialog.dismiss();

                                        Glide.with(GroupDetails.this)
                                                .load(uri.toString())
                                                .into(ivGroupDP);
                                        for (int i=0;i<users.size();i++) {

                                            FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("layout").child(users.get(i).getPh_number()).push().setValue( "dpchanged " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                                        }



                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //progress.setVisibility(View.GONE);
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

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public void showInternetWarning()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);
        builder.setTitle("No Internet Connection")
                .setMessage("Check your internet connection and try again")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
