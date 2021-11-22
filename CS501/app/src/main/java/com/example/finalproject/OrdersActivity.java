package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OrdersActivity extends AppCompatActivity {
    private Button orders_btnSearch;
    private Button orders_btnInbox;
    private Button orders_btnOrders;
    private Button orders_btnProfile;

    private String user_id;
    public static final String USERNAME = "username";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("USERID");

        orders_btnSearch = (Button) findViewById(R.id.orders_btnSearch);
        orders_btnInbox = (Button) findViewById(R.id.orders_btnInbox);
        orders_btnOrders = (Button) findViewById(R.id.orders_btnOrders);
        orders_btnProfile = (Button) findViewById(R.id.orders_btnProfile);


        orders_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to inbox activity
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });

        orders_btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to inbox page
                Intent intent = new Intent(getBaseContext(), InboxActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });

        orders_btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to personalInfo page
                Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });

        orders_btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move to cart page
                Intent intent = new Intent(getBaseContext(), CartActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });
    }
}