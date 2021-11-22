package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

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
    }
}