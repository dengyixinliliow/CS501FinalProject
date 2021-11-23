package com.example.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;

    // views
    private Button addProduct_btnSelect;
    private Button addProduct_btnUpload;
    private Button getAddProduct_btnSubmit;
    private EditText addProduct_edtPName;
    private Spinner addProduct_edtPType;
    private EditText addProduct_edtPColor;
    private Spinner addProduct_edtPCategory;
    private EditText addProduct_edtPSize;
    private EditText addProduct_edtPCondition;
    private EditText addProduct_edtPPrice;
    private ImageView addProduct_imageView;

    //Variable
    private String
            img_id,
            random_product_id,
            product_img_url,
            product_name,
            product_type,
            product_color,
            product_category,
            product_size,
            product_condition,
            product_price;

    // Uri indicates, where the image will be picked from
    private Uri filePath;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //generate random id for each product
        random_product_id = UUID.randomUUID().toString();

        addProduct_btnSelect = findViewById(R.id.addProduct_btnChoose);
        addProduct_btnUpload = findViewById(R.id.addProduct_btnUpload);
        getAddProduct_btnSubmit = findViewById(R.id.addProduct_btnSubmit);
        addProduct_imageView = findViewById(R.id.addProduct_imgView);

        addProduct_edtPName = findViewById(R.id.addProduct_edtPName);
        addProduct_edtPType = (Spinner) findViewById(R.id.addProduct_edtPType);
        addProduct_edtPColor = findViewById(R.id.addProduct_edtPColor);
        addProduct_edtPSize = (EditText) findViewById(R.id.addProduct_edtPSize);
        addProduct_edtPCondition = findViewById(R.id.addProduct_edtPCondition);
        addProduct_edtPPrice = findViewById(R.id.addProduct_edtPPrice);

        addProduct_edtPCategory = (Spinner) findViewById(R.id.addProduct_edtPCategory);
        populateSpinnerCategory();
        populateSpinnerType();

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // connect to database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // on pressing btnSelect SelectImage() is called
        addProduct_btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SelectImage();
                img_id = "images/" + random_product_id;
            }
        });

        // on pressing btnUpload uploadImage() is called
        addProduct_btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                uploadImage();
            }
        });

        //when submit, data will be stored in firebase.
        getAddProduct_btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                addProduct(db);
            }
        });

    }

    /**
     Upload img code reference: https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
     */

    // Select Image method
    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                addProduct_imageView.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(img_id);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(AddProductActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    Log.e("test", img_id);

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            product_img_url = uri.toString();
                                            Log.e("test", product_img_url);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("test", "error");
                                        }
                                    });
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(AddProductActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }

    private void populateSpinnerCategory() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.category));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addProduct_edtPCategory.setAdapter(categoryAdapter);
    }

    private void populateSpinnerType() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.type));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addProduct_edtPType.setAdapter(typeAdapter);
    }

    private void getData() {
        product_name = addProduct_edtPName.getText().toString();
        product_type = addProduct_edtPType.getSelectedItem().toString();
        product_color = addProduct_edtPColor.getText().toString();
        product_category = addProduct_edtPCategory.getSelectedItem().toString().toLowerCase();
        product_size = addProduct_edtPSize.getText().toString();
        product_condition = addProduct_edtPCondition.getText().toString();
        product_price = addProduct_edtPPrice.getText().toString();
    }

    private void addProduct (FirebaseFirestore db) {
        Map<String, Object> product = new HashMap<>();

        product.put("seller_id", user_id);
        product.put("product_id", random_product_id);
        product.put("product_img_url", product_img_url);
        product.put("product_name", product_name);
        product.put("product_type", product_type);
        product.put("product_color", product_color);
        product.put("product_category", product_category);
        product.put("product_size", product_size);
        product.put("product_condition", product_condition);
        product.put("product_price", product_price);
        product.put("renter_id", null);
        product.put("is_available", true);
//        product.put("rent_date", null);
//        product.put("return_date", null);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("test", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("test", "Error adding document", e);
                    }
                });
    }
}


