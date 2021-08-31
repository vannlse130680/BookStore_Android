package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.model.Users;
import com.example.bookstore.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneResetPasswordActivity extends AppCompatActivity {
    Button verify_btn, resend_btn;
    EditText phoneNoEnteredByUser;
    //    ProgressBar progressBar;
    String verificationCodeBySystem;
    PhoneAuthProvider.ForceResendingToken token;
    TextView countdown;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_reset_password);
        loadingBar = new ProgressDialog(this);
        verify_btn = findViewById(R.id.verify_btn);
        resend_btn = findViewById(R.id.resend_btn);
        phoneNoEnteredByUser = findViewById(R.id.verification_code_entered_by_user);
        countdown = (TextView) findViewById(R.id.countdown);

        startCountdown();
        final String phoneNo = getIntent().getStringExtra("phoneNo");
        sendVerificationCodeToUser(phoneNo);

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.setTitle("Verify OTP");
                loadingBar.setMessage("Please wait, while we are checking OTP.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                String code = phoneNoEnteredByUser.getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    loadingBar.dismiss();
                    phoneNoEnteredByUser.setError("Wrong OTP...");
                    phoneNoEnteredByUser.requestFocus();
                    return;
                }
//                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);


            }
        });

        //resend OTP code
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountdown();
                resendVerificationCode(phoneNo, token);
            }
        });
    }
    //ham show countdown
    private  void startCountdown() {
        resend_btn.setVisibility(View.GONE);
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdown.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resend_btn.setVisibility(View.VISIBLE);
                countdown.setText("");
            }

        }.start();
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    //Get the code in global variable
                    verificationCodeBySystem = s;
                    token = forceResendingToken;
                }
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();

                    if (code != null) {
//                        progressBar.setVisibility(View.VISIBLE);
                        verifyCode(code);

                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(VerifyPhoneResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            };
    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);
    }
    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneResetPasswordActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            updatePassword();
//                            Toast.makeText(VerifyPhoneNoActivity.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();

                            //Perform Your required action here to either let the user sign In or do something required

//                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);

                        } else {
//                            Toast.makeText(VerifyPhoneNoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            phoneNoEnteredByUser.setError("Wrong OTP...");
                            phoneNoEnteredByUser.requestFocus();
                        }
                    }
                });
    }

    private void updatePassword() {
        String phoneNo = getIntent().getStringExtra("phoneNo");
        String newPassword = getIntent().getStringExtra("newPassword");
        System.out.println("dasda" + newPassword);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("password", newPassword);
        ref.child(phoneNo).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    loadingBar.dismiss();
                    startActivity(new Intent(VerifyPhoneResetPasswordActivity.this,LoginActivity.class));
                    Toast.makeText(VerifyPhoneResetPasswordActivity.this,"Password reset successfully!",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(VerifyPhoneResetPasswordActivity.this,"Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }
}
