package com.androidstudio.chattingapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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

    TextView tvNext;
    ProgressBar progress;

    ChildEventListener Profile;

    String source=null;
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

        tvNext = findViewById(R.id.tvNext);

        preftheme=getSharedPreferences("theme",0);
        String theme=preftheme.getString("theme","red");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile.this.finish();
            }
        });

        if(getIntent().getBooleanExtra("Registration", false))
            tvNext.setVisibility(View.VISIBLE);
        else
            tvNext.setVisibility(View.GONE);

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(Environment.getExternalStorageDirectory(),"/ChattingApp/Databases/database");
                if(file.exists())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                    builder.setTitle("Backup Found")
                            .setMessage("Backup of chats is found on device.If not restored now it will not be restored later")
                            .setPositiveButton("RESTORE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (ContextCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 150);
                                    }
                                    else {
                                        decrypt();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    Intent intent = new Intent(Profile.this, MainActivity.class);
                                    startActivity(intent);
                                    Profile.this.finish();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Intent intent = new Intent(Profile.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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
                    if(source!=null)
                    {
                        String choices[] = {"Remove Photo","Change Profile Image"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                        builder.setTitle("Choose...")
                                .setItems(choices, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i)
                                        {
                                            case 0:
                                                if(isConnected()) {
                                                    FirebaseStorage.getInstance().getReferenceFromUrl(source).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                                                    .child("profile").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    ivProfile.setImageResource(R.drawable.person);
                                                                    source = null;
                                                                    Toast.makeText(Profile.this, "Profile Image Removed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                                else
                                                    showInternetWarning();
                                                break;
                                            case 1:
                                                CropImage.startPickImageActivity(Profile.this);
                                                break;
                                        }
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else
                        CropImage.startPickImageActivity(Profile.this);
                }
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(source!=null) {

                    Intent intent = new Intent(Profile.this, ShowImage.class);
                    intent.putExtra("source", source);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(Profile.this, "Please set your profile image and then try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Profile = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("profile"))
                {
                    source = dataSnapshot.getValue(String.class);
                    Glide.with(Profile.this)
                            .load(dataSnapshot.getValue(String.class))
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
        };

        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                addChildEventListener(Profile);
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
                if(data.get(index).equals("Enter Your Name"))
                    etTitle.setText("");
                else
                etTitle.setText(data.get(index));
//                etTitle.setSelection(data.get(index).length());

                builder.setView(v);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(isConnected()) {
                            if(!etTitle.getText().toString().trim().isEmpty()) {
                                data.remove(index);
                                databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("name").setValue(etTitle.getText().toString().trim());
                                data.add(index, etTitle.getText().toString().trim());
                                adapter.notifyDataSetChanged();
                            }
                            else
                                Toast.makeText(Profile.this, "Please enter name", Toast.LENGTH_SHORT).show();
                        }
                        else
                            showInternetWarning();
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

                        if (isConnected()) {
                            if(!etStatus.getText().toString().trim().isEmpty()) {
                                data.remove(index);
                                databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("status").setValue(etStatus.getText().toString().trim());
                                data.add(index, etStatus.getText().toString().trim());
                                adapter.notifyDataSetChanged();
                            }
                            else
                                Toast.makeText(Profile.this, "Please enter status", Toast.LENGTH_SHORT).show();
                        }
                        else
                            showInternetWarning();
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

                AlertDialog dialog1 = dialog.create();
                dialog1.show();
            }
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                CropImage.startPickImageActivity(Profile.this);
            }
        }

        if(requestCode == 150)
        {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                dialog.setMessage("This Permission is important to restore your backup").setTitle("Permission Required!");

                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 150);

                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                AlertDialog dialog1 = dialog.create();
                dialog1.show();
            }
            else
            {
                decrypt();
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
                if(isConnected()) {
                    progress.setVisibility(View.VISIBLE);
                    uri = result.getUri();

                    new CompressImage().execute(uri);
                }
                else
                    showInternetWarning();
            }
        }
    }

    private void startCrop(Uri imageuri)
    {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAllowRotation(true)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(25,25)
                .start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).
                removeEventListener(Profile);

    }

    class CompressImage extends AsyncTask<Uri,Void,Uri> {
        @Override
        protected Uri doInBackground(Uri... uris) {
//            String filePath = getPath(Profile.this,uris[0]);
//            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
//            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(uris[0].getPath(), options);

//            int actualHeight = options.outHeight;
//            int actualWidth = options.outWidth;
//
////      max Height and width values of the compressed image is taken as 816x612
//            float maxHeight = 816.0f;
//            float maxWidth = 612.0f;
//            float imgRatio = actualWidth / actualHeight;
//            float maxRatio = maxWidth / maxHeight;
//
////      width and height values are set maintaining the aspect ratio of the image
//            if (actualHeight > maxHeight || actualWidth > maxWidth) {
//                if (imgRatio < maxRatio) {
//                    imgRatio = maxHeight / actualHeight;
//                    actualWidth = (int) (imgRatio * actualWidth);
//                    actualHeight = (int) maxHeight;
//                } else if (imgRatio > maxRatio) {
//                    imgRatio = maxWidth / actualWidth;
//                    actualHeight = (int) (imgRatio * actualHeight);
//                    actualWidth = (int) maxWidth;
//                } else {
//                    actualHeight = (int) maxHeight;
//                    actualWidth = (int) maxWidth;
//                }
//            }
//
////      setting inSampleSize value allows to load a scaled down version of the original image
//            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
//
////      inJustDecodeBounds set to false to load the actual bitmap
//            options.inJustDecodeBounds = false;
//
////      this options allow android to claim the bitmap memory if it runs low on memory
//            options.inPurgeable = true;
//            options.inInputShareable = true;
//            options.inTempStorage = new byte[16 * 1024];
//
//            try {
////          load the bitmap from its path
//                bmp = BitmapFactory.decodeFile(filePath, options);
//            } catch (OutOfMemoryError exception) {
//                exception.printStackTrace();
//                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
//
//            }
//            try {
//                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
//            } catch (OutOfMemoryError exception) {
//                exception.printStackTrace();
//                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
//            }
//
//            float ratioX = actualWidth / (float) options.outWidth;
//            float ratioY = actualHeight / (float) options.outHeight;
//            float middleX = actualWidth / 2.0f;
//            float middleY = actualHeight / 2.0f;
//
//            Matrix scaleMatrix = new Matrix();
//            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
//
//            Canvas canvas = new Canvas(scaledBitmap);
//            canvas.setMatrix(scaleMatrix);
//            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//
////      check the rotation of the image and display it properly
//            ExifInterface exif;
//            try {
//                exif = new ExifInterface(filePath);
//
//                int orientation = exif.getAttributeInt(
//                        ExifInterface.TAG_ORIENTATION, 0);
//                Log.d("EXIF", "Exif: " + orientation);
//                Matrix matrix = new Matrix();
//                if (orientation == 6) {
//                    matrix.postRotate(90);
//                    Log.d("EXIF", "Exif: " + orientation);
//                } else if (orientation == 3) {
//                    matrix.postRotate(180);
//                    Log.d("EXIF", "Exif: " + orientation);
//                } else if (orientation == 8) {
//                    matrix.postRotate(270);
//                    Log.d("EXIF", "Exif: " + orientation);
//                }
//                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
//                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
//                        true);
//            } catch (IOException e) {
//                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ChattingApp/ProfilePicsUploaded");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }

            // Create a file to save the image
            File file = new File(imagesFolder, System.currentTimeMillis() + ".jpg");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);

//          write the compressed bitmap at the destination specified by filename.
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
                return Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            if (uri != null) {
                File from = new File(uri.getLastPathSegment(), "old");
                File to = new File("dp");
                from.renameTo(to);
                UploadTask uploadTask = reference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/").child("images/dp").
                        putFile(uri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/").child("images/dp").getDownloadUrl().
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
            else
            {
                progress.setVisibility(View.GONE);
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
        builder.setTitle("No Internet Connection")
                .setMessage("Check your internet connection and try again")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void decrypt()  {

        ProgressDialog dialog = new ProgressDialog(Profile.this);
        dialog.setMessage("Restoring Backup");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        try {
            File encrypteddb = new File(Environment.getExternalStorageDirectory(),"/ChattingApp/Databases/database");
            FileInputStream fis = new FileInputStream(encrypteddb);

            File originaldb = new File(Environment.getDataDirectory(),"//data//" + getPackageName()
                    + "//databases//" + "CHATS_DATABASE");

            FileOutputStream fos = new FileOutputStream(originaldb);
            SecretKeySpec sks = new SecretKeySpec("sdb3vuhwefvb4uv6".getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[8];
            while ((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();

            dialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Backup has been restored")
                    .setTitle("Backup")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            Intent intent = new Intent(Profile.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

            AlertDialog dialog1 = builder.create();
            dialog1.show();
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        Log.d("asdf",uri+"");
        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String selection = null;
        String[] selectionArgs = null;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (fullPath != "") {
                    return fullPath;
                } else {
                    return null;
                }
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final String id;
                    Cursor cursor = null;
                    try {
                        cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));

                         /*   final Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));*/

                                return getDataColumn(context, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                //In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }


                    }

                } else {
                    Uri contentUri = null;
                    final String id = DocumentsContract.getDocumentId(uri);
                    final boolean isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null);
                    }
                }


            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};


                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            } else if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
        }


        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
            if( Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
            {
                // return getFilePathFromURI(context,uri);
                return getMediaFilePathForN(uri, context);
                // return getRealPathFromURI(context,uri);
            }else
            {

                return getDataColumn(context, uri, null, null);
            }


        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Check if a file exists on device
     *
     * @param filePath The absolute file path
     */
    private static boolean fileExists(String filePath) {
        File file = new File(filePath);

        return file.exists();
    }


    /**
     * Get full file path from external storage
     *
     * @param pathData The storage type and the relative path
     */
    private static String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        return fullPath;
    }

    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    private static String getMediaFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

}
