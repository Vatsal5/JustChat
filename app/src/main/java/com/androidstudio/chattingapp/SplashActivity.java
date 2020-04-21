package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = getSharedPreferences("Numbers",0);
        editor = preferences.edit();

        if(preferences.getString("Number","null").equals("null"))
        {
            startActivity(new Intent(SplashActivity.this,Registration.class));
            SplashActivity.this.finish();
        }
        else
        {
            startActivity(new Intent(SplashActivity.this,Registration.class));
            SplashActivity.this.finish();
        }

    }
}
