package com.androidstudio.chattingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
                ApplicationClass.GroupDp=uri.toString();

                Glide.with(CreateGroup.this)
                        .load(uri.toString())
                        .into(ivGroupDP);



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
