package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Settings extends AppCompatActivity {

    Toolbar toolbar;TextView tvtitle;
    ImageView ivProfile,ivBack,ivBackground;
    TextView tvName,tvStatus;
    ValueEventListener details;
    Toolbar ll;

    LinearLayout llProfile,llTheme,llWallpaper,llsettings,llBackup,llPrivacyPolicy;
    Boolean flag=false;

    SharedPreferences wallpaper;
    SharedPreferences preftheme;
    SharedPreferences.Editor editor1;
    ScrollView theme;
    AdView mAdView;
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
        llBackup = findViewById(R.id.llBackup);
        llWallpaper = findViewById(R.id.llWallpaper);
        ivBackground = findViewById(R.id.ivBackground);
        llPrivacyPolicy = findViewById(R.id.llPrivacyPolicy);

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

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        MobileAds.initialize(this,"ca-app-pub-9646082776252494~9445573279");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        String theme1=preftheme.getString("theme","red");

        switch (theme1) {
            case "orange":
                ll.setBackgroundColor(getResources().getColor(R.color.Orange));
                break;
            case "blue":
                ll.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case "bluish":
                ll.setBackgroundColor(getResources().getColor(R.color.bluish));

                break;
            case "deepred":
                ll.setBackgroundColor(getResources().getColor(R.color.deepred));

                break;
            case "faintpink":
                ll.setBackgroundColor(getResources().getColor(R.color.faintpink));

                break;
            case "darkblue":
                ll.setBackgroundColor(getResources().getColor(R.color.darkblue));

                break;
            case "green":
                ll.setBackgroundColor(getResources().getColor(R.color.green));

                break;
            case "lightorange":
                ll.setBackgroundColor(getResources().getColor(R.color.lightorange));

                break;
            case "lightred":
                ll.setBackgroundColor(getResources().getColor(R.color.lightred));

                break;
            case "mustard":
                ll.setBackgroundColor(getResources().getColor(R.color.mustard));

                break;
            case "pink":
                ll.setBackgroundColor(getResources().getColor(R.color.pink));

                break;
            case "pureorange":
                ll.setBackgroundColor(getResources().getColor(R.color.pureorange));

                break;
            case "purepink":
                ll.setBackgroundColor(getResources().getColor(R.color.purepink));

                break;
            case "purple":
                ll.setBackgroundColor(getResources().getColor(R.color.purple));

                break;
            default:
                ll.setBackgroundColor(getResources().getColor(R.color.red));


                break;
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

        llPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this,PrivacyPolicy.class));
            }
        });

        llBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setMessage("Are you sure want to backup chats?")
                        .setTitle("Backup")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                if (ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 550);
                                }
                                else {
                                        encrypt();
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
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
                            CropImage.startPickImageActivity(Settings.this);
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
                                                    CropImage.startPickImageActivity(Settings.this);
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
                new CompressImage().execute(result.getUri());
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
                .setActivityTitle("Select Wallpaper")
                .setAspectRatio(25,25)
                .start(this);
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

                AlertDialog dialog1 = dialog.create();
                dialog1.show();

            }
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                CropImage.startPickImageActivity(Settings.this);
            }
        }

        if(requestCode==550)
        {
            if(grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

                builder.setMessage("This Permission is important to access image files from the gallery!").setTitle("Permission Required!");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},550);

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
                encrypt();
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

    public class CompressImage extends AsyncTask<Uri,Void,Uri> {
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

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "JustChat/Wallpaper");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }

            // Create a file to save the image
            File file = new File(imagesFolder,  "Wallpaper.jpg");

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

            editor.putString("value",uri.toString());
            editor.apply();

            ivBackground.setImageResource(0);
            ivBackground.setImageURI(uri);

            Toast.makeText(Settings.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
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


    public void encrypt()  {

        final ProgressDialog dialog = new ProgressDialog(Settings.this);
        dialog.setMessage("Please Wait...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        // Here you read the cleartext.
        try {

            File db = new File(Environment.getDataDirectory(), "/data/" + getPackageName() + "/databases/" + "CHATS_DATABASE");
            FileInputStream fis = new FileInputStream(db);

            File file = new File(Environment.getExternalStorageDirectory(),"JustChat/Databases");
            if (!file.exists())
                file.mkdirs();

            File encrypted = new File(Environment.getExternalStorageDirectory(),"/JustChat/Databases/database");
            // This stream write the encrypted text. This stream will be wrapped by another stream.
            FileOutputStream fos = new FileOutputStream(encrypted);

            // Length is 16 byte
            // Careful when taking user input!!! https://stackoverflow.com/a/3452620/1188357
            SecretKeySpec sks = new SecretKeySpec("sdb3vuhwefvb4uv6".getBytes(), "AES");
            // Create cipher
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            // Write bytes
            int b;
            byte[] d = new byte[8];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();

            dialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            builder.setTitle("Backup Successful")
                    .setMessage("Your chats have been saved")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog dialog1 = builder.create();
            dialog1.show();
        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
