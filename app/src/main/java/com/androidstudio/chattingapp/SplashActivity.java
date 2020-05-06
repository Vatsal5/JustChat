package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                     intent = new Intent(SplashActivity.this, Registration.class);


                }
                else
                {
                     intent = new Intent(SplashActivity.this, MainActivity.class);

                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                startActivity(intent);

                overridePendingTransition(0, 0);
            }
        },1000);
        finish();

    }

}
