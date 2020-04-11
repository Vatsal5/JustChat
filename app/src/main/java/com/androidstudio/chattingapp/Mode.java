package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class Mode extends AppCompatActivity {

    EditText etEnterPassword, etAcceptPassword, etAcceptUsername, etEnterUsername;
    TextView tvForgot;
    Button btnCreate, btnConfirm, btnConfirmUsername;
    Switch mode;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        etAcceptPassword= findViewById(R.id.etAcceptPassword);
        etAcceptUsername= findViewById(R.id.etAcceptUsername);
        etEnterPassword= findViewById(R.id.etEnterPassword);
        etEnterUsername= findViewById(R.id.etEnterUsername);
        tvForgot= findViewById(R.id.tvForgot);
        btnCreate= findViewById(R.id.btnCreate);
        btnConfirm=findViewById(R.id.btnConfirm);
        btnConfirmUsername=findViewById(R.id.btnConfirmUsername);
        mode=findViewById(R.id.changeMode);

        SharedPreferences pref= getApplicationContext().getSharedPreferences("Mode",0);
        String defaultvalue=pref.getString("mode","null");
        if(defaultvalue==null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("mode", "public");
            editor.commit();
        }



        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                }
                else
                {

                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnConfirmUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }
}
