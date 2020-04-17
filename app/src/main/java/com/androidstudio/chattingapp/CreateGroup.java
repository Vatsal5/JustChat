package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateGroup extends AppCompatActivity {

    ImageView ivGroupDP,ivClick;
    ProgressBar progress;
    TextView tvInstruct;
    EditText etGroupName;
    Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        ivGroupDP = findViewById(R.id.ivGroupDP);
        ivClick = findViewById(R.id.ivClick);
        tvInstruct=findViewById(R.id.tvInstruct);

        progress = findViewById(R.id.progress);

        etGroupName = findViewById(R.id.etGroupName);
        btnCreate = findViewById(R.id.btnCreate);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApplicationClass.Groupname=etGroupName.getText().toString();

                if(etGroupName.getText().toString().trim().length()>0)
                {
                    Intent intent=new Intent(CreateGroup.this, MainActivity.class);
                    CreateGroup.this.finish();
                    ApplicationClass.create=1;
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    tvInstruct.setVisibility(View.VISIBLE);
                }

            }
        });

        ivClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(CreateGroup.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(CreateGroup.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

                }
                else{
                    //pick_image();
                    CropImage.startPickImageActivity(CreateGroup.this);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
                ActivityCompat.requestPermissions(CreateGroup.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                startCrop(imageuri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                Uri uri = result.getUri();
                progress.setVisibility(View.VISIBLE);
                new CompressImage().execute( uri);




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
                .start(CreateGroup.this);
    }

    class CompressImage extends AsyncTask<Uri,Void,Uri> {
        @Override
        protected Uri doInBackground(Uri... uris) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(CreateGroup.this.getContentResolver(), uris[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bytes = stream.toByteArray();
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            String path = MediaStore.Images.Media.insertImage(CreateGroup.this.getContentResolver(), bitmap1, "Title", null);
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            uri = Uri.fromFile(file);
            ApplicationClass.GroupDp = uri.toString();

            File from= new File(uri.getLastPathSegment(),"old");
            File to= new File("dp");
            from.renameTo(to);
            UploadTask uploadTask= FirebaseStorage.getInstance().getReference(ApplicationClass.groupkey).child("dp").
                    putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    FirebaseStorage.getInstance().getReference(ApplicationClass.groupkey).child("dp").getDownloadUrl().
                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    FirebaseDatabase.getInstance().getReference().child("groups").child(ApplicationClass.groupkey).
                                            child("profile").setValue(uri.toString());
                                     progress.setVisibility(View.GONE);
                                    Glide.with(CreateGroup.this)
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



            return uri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);


        }
    }

}

