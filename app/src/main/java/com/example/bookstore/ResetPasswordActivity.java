package com.example.bookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText newPasswordInput, confirmPasswordInput;
    Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        newPasswordInput = (EditText) findViewById(R.id.reset_password_input);
        confirmPasswordInput = (EditText) findViewById(R.id.confirm_password_input);
        btnNext = (Button) findViewById(R.id.next_reset_btn);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePassword();
            }
        });

    }
    // check password
    private void validatePassword() {
        String phoneNo = getIntent().getStringExtra("phoneNo");
        String password = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please enter your confirm password ...", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password not matched ...", Toast.LENGTH_SHORT).show();
        } else {

            Intent intent = new Intent(getApplicationContext(), VerifyPhoneResetPasswordActivity.class);
            intent.putExtra("phoneNo", phoneNo);
            intent.putExtra("newPassword", password);
            startActivity(intent);


        }

    }
}
