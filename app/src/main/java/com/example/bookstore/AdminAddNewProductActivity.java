package com.example.bookstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    private  String categoryName , description, price, productName, saveCurrentDate, saveCurrentTime;
    private Button addNewProduct;
    private EditText inputProductName, inputProductDes, inputProductPrice;
    private ImageView inputProductImage;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImageRef;
    private  DatabaseReference productsRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        addNewProduct = (Button) findViewById(R.id.add_new_product);
        inputProductName = (EditText) findViewById(R.id.product_name);
        inputProductDes = (EditText) findViewById(R.id.product_discription);
        inputProductPrice = (EditText) findViewById(R.id.product_price);
        inputProductImage = (ImageView) findViewById(R.id.select_product_image);
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);

        categoryName = getIntent().getExtras().get("category").toString();
        //Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });
    }

    private void validateProductData() {
        description = inputProductDes.getText().toString();
        price = inputProductPrice.getText().toString();
        productName = inputProductName.getText().toString();

        if(imageUri == null) {
            Toast.makeText(this, "Book image is mandarory ...", Toast.LENGTH_SHORT).show();;

        } else if(TextUtils.isEmpty(productName)) {
            Toast.makeText(this, "Please write book name ...", Toast.LENGTH_SHORT).show();;
        } else if(TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please write book description ...", Toast.LENGTH_SHORT).show();;
        } else if(TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please write book price ...", Toast.LENGTH_SHORT).show();;
        } else  {
            storeProductInformation();
        }
    }

    private void storeProductInformation() {
        loadingBar.setTitle("Adding New Boook");
        loadingBar.setMessage("Dear Admin, please wait, while we are adding new book.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Image upload successfully..." , Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()) {
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "got the book image URL successfully...", Toast.LENGTH_SHORT).show();
                            saveProductInforToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void saveProductInforToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("name", productName);
        productMap.put("price", price);
        productsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);
                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Book is add successfull..", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == galleryPick && resultCode == RESULT_OK && data!= null) {
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);

        }
    }
}
