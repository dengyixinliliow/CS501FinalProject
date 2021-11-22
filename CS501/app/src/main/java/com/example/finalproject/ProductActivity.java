package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private ImageView product_ivProduct;

    private TextView product_txtName;
    private TextView product_txtTags;
    private TextView product_txtSize;
    private TextView product_txtPrice;
    private TextView product_txtRentDate;
    private TextView product_txtReturnDate;

    private Button product_btnAddToBag;
    private Button product_btnReviews;

    public static final String USERID = "user_id";
    public static final String PRODUCTID = "product_id";

    private Map<String, Object> product;
    private String product_id;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

//        // get product id
//        Intent intent = getIntent();
//        product_id = intent.getStringExtra(PRODUCTID);

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        product_ivProduct = (ImageView) findViewById(R.id.product_ivProduct);

        product_txtName = (TextView) findViewById(R.id.product_txtName);
        product_txtTags = (TextView) findViewById(R.id.product_txtTags);
        product_txtSize = (TextView) findViewById(R.id.product_txtSize);
        product_txtPrice = (TextView) findViewById(R.id.product_txtPrice);
        product_txtRentDate = (TextView) findViewById(R.id.product_txtRentDate);
        product_txtReturnDate = (TextView) findViewById(R.id.product_txtReturnDate);

        product_btnAddToBag = (Button) findViewById(R.id.product_btnAddToBag);
        product_btnReviews = (Button) findViewById(R.id.product_btnReviews);

        product_btnAddToBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToCart();
            }
        });
    }

    public void addProductToCart() {

        db.collection("carts");

        // add product in carts database
        product = new HashMap<String, Object>();
        product.put(USERID, user_id);
        product.put(PRODUCTID, product_id);

        db.collection("carts")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "onSuccess: product is added into cart " + user_id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }
}