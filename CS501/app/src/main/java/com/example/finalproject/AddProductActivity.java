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

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

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
    private ImageView addProduct_imageView;
    private ImageView return_icon;

    //Variable
    private String
            img_id,
            random_product_id,
            product_img_url;

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
        return_icon=(ImageView)findViewById(R.id.add_product_return) ;
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //generate random id for each product
        random_product_id = UUID.randomUUID().toString();

        addProduct_btnSelect = findViewById(R.id.addProduct_btnChoose);
        addProduct_btnUpload = findViewById(R.id.addProduct_btnUpload);
        addProduct_imageView = findViewById(R.id.addProduct_imgView);

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
                                            Intent intent = new Intent(AddProductActivity.this, AddProductDetailActivity.class);
                                            intent.putExtra("product_img", product_img_url);
                                            startActivity(intent);
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
}


