package com.androidstudio.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class Mode extends AppCompatActivity {

    EditText etEnterPassword, etAcceptPassword, etEnterUsername,etConfirm;
    TextView tvForgot,tvShowPassword,tvIncompleteDetails,tvdescription,tvmode;
    Button btnCreate, btnConfirm, btnConfirmUsername,btnWrongUderId, btnWrongPassword;
    Switch mode;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String defaultvalue, defaultpassword, username;

    LinearLayout llcreatePassword, llSelectMode, llConfirmPassword, llForgotPassword, llWrongId, llShowPassword, llWrongPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        etAcceptPassword= findViewById(R.id.etAcceptPassword);
        tvdescription=findViewById(R.id.tvdescription);
        tvmode=findViewById(R.id.tvmode);

        btnWrongUderId = findViewById(R.id.btnWrongUserId);

        btnWrongPassword=findViewById(R.id.btnWrongPassword);
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
        tvIncompleteDetails=findViewById(R.id.textView3);


        final String number = getIntent().getStringExtra("number");

         pref= getApplicationContext().getSharedPreferences("Mode",0);
         defaultvalue=pref.getString("mode"+number,"null");
          defaultpassword=pref.getString("password","null");
          username=pref.getString("username","null");
        editor = pref.edit();
        if((defaultvalue.equals("null"))){

            editor.putString("mode"+number, "public");
            editor.commit();
        }
        if(defaultvalue.equals("private"))
        {
            mode.setChecked(true);
            llSelectMode.setBackgroundColor(getResources().getColor(R.color.black));
            tvdescription.setTextColor(getResources().getColor(R.color.white));
            tvmode.setTextColor(getResources().getColor(R.color.white));

        }



        mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                  if((defaultpassword.equals("null")))
                    {
                        llSelectMode.setVisibility(View.GONE);
                        llcreatePassword.setVisibility(View.VISIBLE);
                    }
                  else{
                      editor.putString("mode"+number,"private");
                      editor.apply();
                      llSelectMode.setBackgroundColor(getResources().getColor(R.color.black));
                      tvdescription.setTextColor(getResources().getColor(R.color.white));
                      tvmode.setTextColor(getResources().getColor(R.color.white));
                  }
                }
                else
                {
                    llSelectMode.setVisibility(View.GONE);
                    llConfirmPassword.setVisibility(View.VISIBLE);

                }
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llcreatePassword.setVisibility(View.GONE);
                llConfirmPassword.setVisibility(View.GONE);
                llForgotPassword.setVisibility(View.VISIBLE);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llcreatePassword.setVisibility(View.GONE);

                if (!(defaultpassword.equals(etAcceptPassword.getText().toString().trim())))
                {
                    llWrongPassword.setVisibility(View.VISIBLE);
                    llConfirmPassword.setVisibility(View.GONE);
                    mode.setChecked(true);
                    editor.putString("mode" + number, "private");
                    editor.apply();
                }
                else
                {
                   llSelectMode.setVisibility(View.VISIBLE);
                   llConfirmPassword.setVisibility(View.GONE);
                   if(pref.getString("mode"+number,"null").equals("public") ||
                           pref.getString("mode"+number,"null").equals("null")) {
                       editor.putString("mode" + number, "private");
                       llSelectMode.setBackgroundColor(getResources().getColor(R.color.black));
                       tvdescription.setTextColor(getResources().getColor(R.color.white));
                       tvmode.setTextColor(getResources().getColor(R.color.white));
                   }
                   editor.putString("mode"+number,"public");
                   editor.apply();
                    llSelectMode.setBackgroundColor(getResources().getColor(R.color.white));
                    tvdescription.setTextColor(getResources().getColor(R.color.black));
                    tvmode.setTextColor(getResources().getColor(R.color.black));

                }
                etAcceptPassword.setText("");

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
                etConfirm.setText("");
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEnterPassword.getText().toString().trim().length()>0 && etEnterUsername.getText().toString().trim().length()>0) {

                    editor.putString("password", etEnterPassword.getText().toString().trim());
                    editor.putString("username", etEnterUsername.getText().toString().trim());
                    llSelectMode.setVisibility(View.VISIBLE);
                    llcreatePassword.setVisibility(View.GONE);
                    editor.putString("mode" + number, "private");
                    editor.apply();
                    llSelectMode.setBackgroundColor(getResources().getColor(R.color.black));
                    tvdescription.setTextColor(getResources().getColor(R.color.white));
                    tvmode.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    tvIncompleteDetails.setVisibility(View.VISIBLE);
                }
                defaultvalue=pref.getString("mode"+number,"null");
                defaultpassword=pref.getString("password","null");
                username=pref.getString("username","null");
            }
        });

        btnWrongPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               llWrongPassword.setVisibility(View.GONE);
               llConfirmPassword.setVisibility(View.VISIBLE);
            }
        });

        btnWrongUderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWrongId.setVisibility(View.GONE);
                llForgotPassword.setVisibility(View.VISIBLE);
            }
        });


    }
}
