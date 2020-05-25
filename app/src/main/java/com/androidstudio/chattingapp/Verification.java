package com.androidstudio.chattingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity {

    EditText etOTP;
    Button btnVerify;
    TextView tvResend;
    ProgressDialog dialog;
    String VerificationId;
    FirebaseDatabase database;
    DatabaseReference reference;


    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        etOTP = findViewById(R.id.etOTP);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);

        VerificationId = getIntent().getStringExtra("VerificationId");
        token = (PhoneAuthProvider.ForceResendingToken) getIntent().getSerializableExtra("token");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Verifying");
        dialog.setMessage("Please Wait..");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        phone = getIntent().getStringExtra("phone");

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected()) {

                    String OTP = etOTP.getText().toString().trim();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, OTP);
                    signInWithPhoneAuthCredential(credential);

                    dialog.setTitle("Verifying");
                    dialog.show();
                }
                else
                    showInternetWarning();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d("myapp","Verification Completed");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                VerificationId = s;
                token = forceResendingToken;

                dialog.dismiss();
                showResend();
            }
        };

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvResend.setVisibility(View.GONE);

                dialog.setTitle("Resending OTP");
                dialog.show();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        Verification.this,               // Activity (for callback binding)
                        mCallbacks,         // OnVerificationStateChangedCallbacks
                        token);             // ForceResendingToken from callbacks
            }
        });
        showResend();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            dialog.dismiss();
                            FirebaseUser user = task.getResult().getUser();
                            (reference.child("users").child(phone).child("name")).setValue("Enter Your Name");

                            (reference.child("users").child(phone).child("status")).setValue("What's Your Status");

                            startActivity(new Intent(Verification.this,Profile.class).putExtra("Registration",true));
                            Verification.this.finish();
                            // ...
                        } else {
                            dialog.dismiss();
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(Verification.this, "Wrong OTP entered", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void showResend()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvResend.setVisibility(View.VISIBLE);
            }
        },60000);
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Verification.this);
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
}
