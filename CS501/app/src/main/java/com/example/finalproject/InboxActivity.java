package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InboxActivity extends AppCompatActivity {

    private Button inbox_btnSearch;
    private Button inbox_btnInbox;
    private Button inbox_btnOrders;
    private Button inbox_btnProfile;

    private String user_id;
    public static final String USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("USERID");

        inbox_btnSearch = (Button) findViewById(R.id.inbox_btnSearch);
        inbox_btnInbox = (Button) findViewById(R.id.inbox_btnInbox);
        inbox_btnOrders = (Button) findViewById(R.id.inbox_btnOrders);
        inbox_btnProfile = (Button) findViewById(R.id.inbox_btnProfile);


        inbox_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to search page
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });

        inbox_btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to orders page
                Intent intent = new Intent(getBaseContext(), OrdersActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });

        inbox_btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to personalInfo page
                Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });
    }
}