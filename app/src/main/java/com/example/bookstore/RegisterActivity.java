package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookstore.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements Serializable {

    private Button createAccountButton;
    private EditText inputName, inputPhoneNumber, inputPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = (Button) findViewById(R.id.register_btn);
        inputName = (EditText) findViewById(R.id.register_username_input);
        inputPassword = (EditText) findViewById(R.id.register_password_input);
        inputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }
    private boolean isValidMobile(String phone) {
        if(Pattern.matches("((09|03|07|08|05)+([0-9]{8}))", phone)) {
            return true;
        }
        return false;
    }
    private void createAccount() {
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your name ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your phone number ...", Toast.LENGTH_SHORT).show();
        } else if(!isValidMobile(phone)){
            Toast.makeText(this, "Invalid Phone ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password ...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Check Phone");
            loadingBar.setMessage("Please wait, while we are checking credential.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validatePhoneNumber(name, phone, password);

        }
    }

    private void validatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users")).child(phone).exists()) {
//                    HashMap<String, Object> userDataMap = new HashMap<>();
//                    userDataMap.put("phone" , phone);
//                    userDataMap.put("password" , password);
//                    userDataMap.put("name" , name);
//                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()) {
//                                        Toast.makeText(RegisterActivity.this,"Congratulations, your account have been created.", Toast.LENGTH_SHORT).show();
//                                        loadingBar.dismiss();
//                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                                        startActivity(intent);
//                                    } else {
//                                        loadingBar.dismiss();
//                                        Toast.makeText(RegisterActivity.this,"Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
                    loadingBar.dismiss();
                    Users users = new Users(name, phone, password);
                    Intent intent = new Intent(getApplicationContext(), VerifyPhoneNoActivity.class);
                    intent.putExtra("phoneNo", phone);
                    intent.putExtra("userInfor", users);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this,"This " + phone + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please again using another phone number.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
