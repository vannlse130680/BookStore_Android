package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookstore.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn, paypalBtn;
    private TextView finalPrice;
    private String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shipment_name);
        phoneEditText = (EditText) findViewById(R.id.shipment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shipment_address);
        cityEditText = (EditText) findViewById(R.id.shipment_city);
        finalPrice = (TextView) findViewById(R.id.final_total_price);
        paypalBtn = (Button) findViewById(R.id.pay_with_paypal_btn);
        finalPrice.setText("Total price: $"+totalAmount);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

        paypalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameEditText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter your name!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter your phone number!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(addressEditText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter your address!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(cityEditText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter your city name!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(ConfirmFinalOrderActivity.this, PaypalActivity.class);
                    intent.putExtra("Total Price", String.valueOf(totalAmount));
                    intent.putExtra("name", nameEditText.getText().toString());
                    intent.putExtra("phone", phoneEditText.getText().toString());
                    intent.putExtra("address", addressEditText.getText().toString());
                    intent.putExtra("city", cityEditText.getText().toString());
                    intent.putExtra("phone", phoneEditText.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }

    private void check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this,"Please enter your name!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this,"Please enter your phone number!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this,"Please enter your address!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(this,"Please enter your city name!", Toast.LENGTH_SHORT).show();
        }
        else{
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");
        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this,"Your order is success!",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}
