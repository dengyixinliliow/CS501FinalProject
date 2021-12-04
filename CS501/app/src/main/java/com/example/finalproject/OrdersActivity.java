package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrdersActivity extends AppCompatActivity {
    private Button orders_btnSearch;
    private Button orders_btnInbox;
    private Button orders_btnOrders;
    private Button orders_btnProfile;
    private Button orders_btnreview;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    public static final String USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        orders_btnSearch = (Button) findViewById(R.id.orders_btnSearch);
        orders_btnInbox = (Button) findViewById(R.id.orders_btnInbox);
        orders_btnOrders = (Button) findViewById(R.id.orders_btnOrders);
        orders_btnProfile = (Button) findViewById(R.id.orders_btnProfile);
        orders_btnreview=(Button)findViewById(R.id.orders_btnReview);


        orders_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to inbox activity
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        orders_btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to inbox page
                Intent intent = new Intent(getBaseContext(), InboxActivity.class);
                startActivity(intent);
            }
        });

        orders_btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to personalInfo page
                Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

        orders_btnreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move to cart page
                Intent intent = new Intent(getBaseContext(), ReviewActivity.class);
                intent.putExtra("product_id","c0bf9e11-a50f-4e3a-9ac0-11b2f8516cec");
                startActivity(intent);
            }
        });
    }
}