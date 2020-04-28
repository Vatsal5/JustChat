package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    TextView tvCreatedBy,tvGroupTitle,tvParticipants;
    LinearLayout llAddMembers,llExitGroup,llDeleteGroup;
    RecyclerView Participants;
    String groupKey,admin;
    RecyclerView.LayoutManager manager;
    ParticipantsAdapter adapter;
    ArrayList <String> members;
    ArrayList<UserDetailWithStatus> users;
    SharedPreferences pref;
    ChildEventListener DeleteGroup,exitGroup,newname;
    ValueEventListener admin1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        groupKey=getIntent().getStringExtra("groupkey");


        users= new ArrayList<>();
        ivGroupDP = findViewById(R.id.ivGroupDP);
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvGroupTitle = findViewById(R.id.tvGroupTitle);
        llAddMembers = findViewById(R.id.llAddMembers);
        llExitGroup = findViewById(R.id.llExitGroup);
        Participants = findViewById(R.id.Participants);
        llDeleteGroup = findViewById(R.id.llDeleteGroup);
        tvParticipants = findViewById(R.id.tvParticipants);
        ivEdit = findViewById(R.id.ivEdit);

        pref= getApplicationContext().getSharedPreferences("Names",0);


        members=new ArrayList<>();
        Participants.setHasFixedSize(true);


               admin1= new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        admin = dataSnapshot.getValue().toString();

                        if(admin.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                            tvCreatedBy.setText("Created By You");

                        else {
                            if (pref.getString(admin, "null").equals("null"))
                                tvCreatedBy.setText("Created By " + admin);
                            else
                                tvCreatedBy.setText("Created By " + pref.getString(admin, "null"));
                        }

                        if(dataSnapshot.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                        {
                            llAddMembers.setVisibility(View.VISIBLE);
                            llDeleteGroup.setVisibility(View.VISIBLE);
                        }
                        else{
                            llAddMembers.setVisibility(View.GONE);
                        }
                        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("admin").removeEventListener(admin1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("admin").addValueEventListener(admin1);




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
                        builder.setIcon(R.drawable.title);
                        builder.setTitle("Title");

                        final EditText etGroupTitle = v.findViewById(R.id.etGroupTitle);
                        etGroupTitle.setText(tvGroupTitle.getText());
                        etGroupTitle.setSelection(tvGroupTitle.getText().length());

                        builder.setView(v);
                        edittext.setText(tvGroupTitle.getText());
                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(etGroupTitle.getText().toString().trim().length()>0) {
                                    ApplicationClass.RenameGroup=etGroupTitle.getText().toString();
                                    tvGroupTitle.setText(etGroupTitle.getText().toString());
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
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Please anter a valid name",Toast.LENGTH_LONG).show();
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


        if(getIntent().getStringExtra("profile").equals("null"))
            ivGroupDP.setImageResource(R.drawable.group);
        else
            Glide.with(this).load(getIntent().getStringExtra("profile")).into(ivGroupDP);

        tvGroupTitle.setText(getIntent().getStringExtra("groupname"));


        manager = new LinearLayoutManager(this);
        Participants.setLayoutManager(manager);
        adapter= new ParticipantsAdapter(this,users);
        Participants.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey).child("members")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot1, @Nullable String s) {
                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(dataSnapshot1.getValue(String.class)).child("profile").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(!dataSnapshot1.getValue(String.class).equals(admin))
                                            users.add(new UserDetailWithStatus(dataSnapshot1.getValue(String.class),null,dataSnapshot.getValue(String.class),null,0,null));
                                        else
                                            users.add(new UserDetailWithStatus(dataSnapshot1.getValue(String.class),null,dataSnapshot.getValue(String.class),"admin",0,null));

                                        Log.d("USERSS",dataSnapshot1.getValue(String.class));
                                        adapter.notifyDataSetChanged();
                                        tvParticipants.setText(users.size()+" Participants");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );

                        for(int i=0;i<users.size();i++)
                        {

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
                });



        llAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationClass.addmembers=1;
                ApplicationClass.activity=1;
                Intent intent=new Intent(GroupDetails.this,FriendsActivity.class);
                intent.putExtra("groupkey",groupKey);
                intent.putExtra("groupname",getIntent().getStringExtra("groupname"));
                startActivity(intent);

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

                        DeleteGroup = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                FirebaseDatabase.getInstance().getReference().child("users").
                                        child(dataSnapshot.getValue().toString()).child("groups").child(groupKey).getRef().removeValue();

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

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseDatabase.getInstance().getReference().child("groups").
                                        child(groupKey).getRef().removeValue();
                                GroupDetails.this.finish();
                                startActivity(new Intent(GroupDetails.this,MainActivity.class));

                            }
                        },2000);
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

                        exitGroup = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                                    dataSnapshot.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                            .child("groups").child(groupKey).getRef().removeValue();
                                    GroupDetails.this.finish();
                                    startActivity(new Intent(GroupDetails.this, MainActivity.class));

                                    FirebaseDatabase.getInstance().getReference().child("groups").child(groupKey)
                                            .child("members").removeEventListener(exitGroup);
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
        if(!(users.get(index).getPh_number().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                || ("+91"+users.get(index).getPh_number()).equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {
            String[] choices = {"View","Remove"};

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
                                    intent.putExtra("profile",users.get(index).getUrl());
                                    intent.putExtra("title",users.get(index).getPh_number() );
                                    ApplicationClass.groupusers=1;
                                    startActivity(intent);
                                case 1:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetails.this);

                                    builder.setTitle("Are you sure you want to remove this member ");
                                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            adapter.notifyDataSetChanged();

                                      FirebaseDatabase.getInstance().getReference().child("groups")
                                              .child(groupKey).child("members").addChildEventListener(
                                              new ChildEventListener() {
                                                  @Override
                                                  public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                      if(dataSnapshot.getValue().toString().equals(users.get(index).getPh_number()))
                                                      {
                                                          dataSnapshot.getRef().removeValue().addOnSuccessListener(
                                                                  new OnSuccessListener<Void>() {
                                                                      @Override
                                                                      public void onSuccess(Void aVoid) {
                                                                          FirebaseDatabase.getInstance().getReference().child("users")
                                                                                  .child(users.get(index).getPh_number())
                                                                                  .child("groups").child(groupKey).getRef().removeValue().addOnSuccessListener(
                                                                                  new OnSuccessListener<Void>() {
                                                                                      @Override
                                                                                      public void onSuccess(Void aVoid) {
                                                                                          users.remove(index);
                                                                                          adapter.notifyDataSetChanged();
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
                                              }
                                      );


                                        }
                                    });
                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.show();


                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
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
                Uri uri = result.getUri();
                new CompressImage().execute(uri);

            }
        }
    }
    class CompressImage extends AsyncTask<Uri,Void,Uri> {
        @Override
        protected Uri doInBackground(Uri... uris) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(GroupDetails.this.getContentResolver(), uris[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bytes = stream.toByteArray();
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            String path = MediaStore.Images.Media.insertImage(GroupDetails.this.getContentResolver(), bitmap1, "Title", null);
            Uri uri = Uri.parse(path);


            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/Sent");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }

            // Create a file to save the image
            File file = new File(imagesFolder, new Timestamp(System.currentTimeMillis()) + ".jpg");

            try {
                InputStream in = getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();

                uri = Uri.fromFile(file);
                return uri;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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
                                        Glide.with(GroupDetails.this)
                                                .load(uri.toString())
                                                .into(ivGroupDP);



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
}
