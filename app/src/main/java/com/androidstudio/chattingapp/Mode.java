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

    EditText etEnterPassword, etAcceptPassword, etAcceptUsername, etEnterUsername,etConfirm;
    TextView tvForgot,tvShowPassword;
    Button btnCreate, btnConfirm, btnConfirmUsername;
    Switch mode;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    LinearLayout llcreatePassword, llSelectMode, llConfirmPassword, llForgotPassword, llWrongId, llShowPassword, llWrongPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        etAcceptPassword= findViewById(R.id.etAcceptPassword);
        etAcceptUsername= findViewById(R.id.etAcceptUsername);
        etEnterPassword= findViewById(R.id.etEnterPassword);
        etEnterUsername= findViewById(R.id.etEnterUsername);
        tvForgot= findViewById(R.id.tvForgot);
        tvShowPassword=findViewById(R.id.tvShowPassword);
        btnCreate= findViewById(R.id.btnCreate);
        btnConfirm=findViewById(R.id.btnConfirm);
        btnConfirmUsername=findViewById(R.id.btnConfirmUsername);
        mode=findViewById(R.id.changeMode);
        llConfirmPassword=findViewById(R.id.confirmpassword);
        llcreatePassword=findViewById(R.id.CreatePassword);
        llSelectMode=findViewById(R.id.selectmode);
        llForgotPassword=findViewById(R.id.forgotPassword);
        llWrongId=findViewById(R.id.WrongId);
        llWrongPassword=findViewById(R.id.WrongPassword);
        llShowPassword=findViewById(R.id.Showpassword);
        etConfirm=findViewById(R.id.etConfirmUsername);

        final String number = getIntent().getStringExtra("number");

         pref= getApplicationContext().getSharedPreferences("Mode"+number,0);
        String defaultvalue=pref.getString("mode"+number,"null");
        final String defaultpassword=pref.getString("password","null");
        final String username=pref.getString("username","null");
        editor = pref.edit();
        if((defaultvalue.equals("null"))){

            editor.putString("mode"+number, "public");
            editor.commit();
        }
        if(defaultvalue.equals("private"))
        {
            mode.setChecked(true);
        }



        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    llSelectMode.setVisibility(View.GONE);
                  if((defaultpassword.equals("null")))
                    {
                        llcreatePassword.setVisibility(View.VISIBLE);
                    }
                  else{
                      llConfirmPassword.setVisibility(View.VISIBLE);

                  }


                }
                else
                {

                    llShowPassword.setVisibility(View.VISIBLE);

                }
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llConfirmPassword.setVisibility(View.GONE);
                llForgotPassword.setVisibility(View.VISIBLE);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(defaultpassword.equals(etAcceptPassword.getText().toString().trim())))
                {
                    llWrongPassword.setVisibility(View.VISIBLE);
                    llConfirmPassword.setVisibility(View.GONE);
                    mode.setChecked(false);
                }
                else
                {
                   llSelectMode.setVisibility(View.VISIBLE);
                   llConfirmPassword.setVisibility(View.GONE);
                   editor.putString("mode"+number,"private");
                   editor.apply();

                }

            }
        });
        btnConfirmUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llForgotPassword.setVisibility(View.GONE);

                if(username.equals(etConfirm.getText().toString().trim()))
                {
                    llShowPassword.setVisibility(View.VISIBLE);
                    tvShowPassword.setText("Your Password Is "+defaultpassword);
                }
                else{
                    llWrongId.setVisibility(View.VISIBLE);
                }
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("password",etEnterPassword.getText().toString().trim());
                editor.putString("username",etEnterUsername.getText().toString().trim());
                llSelectMode.setVisibility(View.VISIBLE);
                llcreatePassword.setVisibility(View.GONE);
                editor.putString("mode"+number,"private");
                editor.apply();
            }
        });



    }
}
