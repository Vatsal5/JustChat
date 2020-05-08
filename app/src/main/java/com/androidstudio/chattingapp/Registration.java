package com.androidstudio.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {

    EditText etPhone;
    Button btnVerify;

    String phone;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken token;
    String VerificationId;

    ProgressDialog dialog;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etPhone = findViewById(R.id.etPhone);
        btnVerify = findViewById(R.id.btnSubmit);

        reference = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
              //  Log.d("myapp","Verification Completed");

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                VerificationId = s;
                token = forceResendingToken;

                Intent intent = new Intent(Registration.this,Verification.class);
                intent.putExtra("VerificationId",VerificationId);
                intent.putExtra("token",token);
                intent.putExtra("phone",phone);

                Registration.this.finish();
                startActivity(intent);
                dialog.dismiss();
            }
        };

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = "+91"+etPhone.getText().toString();

                if(phone.isEmpty())
                    Toast.makeText(Registration.this, "Please enter phone number", Toast.LENGTH_LONG).show();
                if(phone.length() !=13)
                    Toast.makeText(Registration.this, "Enter Valid Phone number", Toast.LENGTH_LONG).show();

                if(!phone.isEmpty() && phone.length() ==13)
                {
                    dialog.setTitle("Sending OTP");
                    dialog.setMessage("Please Wait");
                    dialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phone,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Registration.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            dialog.dismiss();

                            (reference.child("users").child(phone).child("contact")).setValue(phone);
                            (reference.child("users").child(phone).child("name")).setValue("Enter Your Name");

                            (reference.child("users").child(phone).child("status")).setValue("What's Your Status");



                            startActivity(new Intent(Registration.this,MainActivity.class));
                            Registration.this.finish();
                            // ...
                        } else {
                            dialog.dismiss();
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(Registration.this, "Wrong OTP entered", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0,0);
    }
}
