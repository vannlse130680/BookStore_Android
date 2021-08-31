package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookstore.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgetPasswordActivity extends AppCompatActivity {
    Button btnNext;
    EditText edtPhone;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        btnNext = (Button) findViewById(R.id.next_btn_forgot);
        edtPhone = (EditText) findViewById(R.id.forgot_acc_phone);
        loadingBar = new ProgressDialog(this);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = edtPhone.getText().toString();
                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Please write your phone account  ...", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("Check Phone");
                    loadingBar.setMessage("Please wait, while we are checking credential.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    validatePhoneNumber(phone);

                }


            }
        });
    }
    private void validatePhoneNumber(final String phone) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("Users")).child(phone).exists()) {

                    loadingBar.dismiss();

                    Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                    intent.putExtra("phoneNo", phone);

                    startActivity(intent);
                } else {
                    Toast.makeText(ForgetPasswordActivity.this,"This " + phone + " not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
