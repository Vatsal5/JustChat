package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
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
    ConstraintLayout clback;
    Button btnCreate,btnSkip;
    SharedPreferences preftheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        clback=findViewById(R.id.clback);


        preftheme=getSharedPreferences("theme",0);

        setBackground(clback);
        ivGroupDP = findViewById(R.id.ivGroupDP);
        ivClick = findViewById(R.id.ivClick);
        setBackground(ivClick);
        tvInstruct=findViewById(R.id.tvInstruct);

        progress = findViewById(R.id.progress);

        etGroupName = findViewById(R.id.etGroupName);
        btnCreate = findViewById(R.id.btnCreate);
        setBackground(btnCreate);
        btnSkip = findViewById(R.id.btnSkip);
        setBackground(btnSkip);

        final DatabaseReference[] reference = new DatabaseReference[1];

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CreateGroup.this, MainActivity.class);
                CreateGroup.this.finish();
                ApplicationClass.create=1;
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected()) {

                    ApplicationClass.Groupname = etGroupName.getText().toString().trim();


                    reference[0] = FirebaseDatabase.getInstance().getReference();

                    if (etGroupName.getText().toString().trim().length() > 0) {

                        ApplicationClass.groupkey = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").push().getKey();

                        ApplicationClass.create = 0;
                        reference[0].child("groups").child(ApplicationClass.groupkey).child("members").push().setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                        for (int i = 0; i < ApplicationClass.members.size(); i++) {
                            reference[0].child("groups").child(ApplicationClass.groupkey).child("members").push().setValue(ApplicationClass.members.get(i));
                            reference[0].child("users").child(ApplicationClass.members.get(i)).child("groups").child(ApplicationClass.groupkey).setValue(ApplicationClass.Groupname);

                        }
                        ApplicationClass.members.clear();
                        reference[0].child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("groups").child(ApplicationClass.groupkey).setValue(ApplicationClass.Groupname);


                        reference[0].child("groups").child(ApplicationClass.groupkey).child("admin").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                btnCreate.setVisibility(View.GONE);
                                etGroupName.setVisibility(View.GONE);
                                tvInstruct.setVisibility(View.GONE);

                                ivGroupDP.setVisibility(View.VISIBLE);
                                ivClick.setVisibility(View.VISIBLE);
                                btnSkip.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        tvInstruct.setVisibility(View.VISIBLE);
                    }

                }
                else
                {
                    showInternetWarning();
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

                if(isConnected()) {
                    Uri uri = result.getUri();
                    progress.setVisibility(View.VISIBLE);
                    new CompressImage().execute(uri);
                }
                else{
                    showInternetWarning();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationClass.members.clear();
    }

    private void startCrop(Uri imageuri)
    {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAllowRotation(true)
                .setActivityTitle("Choose Group icon")
                .setAspectRatio(25,25)
                .start(CreateGroup.this);
    }

    class CompressImage extends AsyncTask<Uri,Void,Uri> {
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

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "JustChat/ProfilePicsUploaded");
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

                ApplicationClass.GroupDp = uri.toString();

                UploadTask uploadTask = FirebaseStorage.getInstance().getReference(ApplicationClass.groupkey).child("dp").
                        putFile(Uri.parse(ApplicationClass.GroupDp));
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

                                        Intent intent=new Intent(CreateGroup.this, MainActivity.class);
                                        CreateGroup.this.finish();
                                        ApplicationClass.create=1;
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

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

    public void setBackground(View view)
    {
        String theme=preftheme.getString("theme","red");

        switch (theme)
        {
            case "orange":

                ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(Color.parseColor("#d6514a")));

                break;

            case "blue":

                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#283470")));

                break;


            case "bluish":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#292f3b")));
                break;


            case "deepred":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#e24a3c")));
                break;

            case "faintpink":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#f25c65")));

                break;

            case "darkblue":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#2b3050")));
                break;


            case "green":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#6ebd52")));
                break;

            case "lightorange":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#f2a37a")));

                break;

            case "lightred":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#e9776c")));
                break;


            case "mustard":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#eba54d")));
                break;

            case "pink":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#e91e63")));
                break;

            case "pureorange":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#ff5722")));
                break;

            case "purepink":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#f57268")));
                break;

            case "purple":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#49264e")));
                break;

            default:
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#d6514a")));
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
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

