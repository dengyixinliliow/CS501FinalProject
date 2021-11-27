package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private Button inbox_btnSearch;
    private Button inbox_btnInbox;
    private Button inbox_btnOrders;
    private Button inbox_btnProfile;

    private ListView inbox_lvContacts;
    //private ScrollView inbox_svMessages;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    public static final String USERNAME = "username";

    // https://www.thecrazyprogrammer.com/2016/10/android-real-time-chat-application-using-firebase-tutorial.html
    private String[] contacts = {"a", "b", "c"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        inbox_btnSearch = (Button) findViewById(R.id.inbox_btnSearch);
        inbox_btnInbox = (Button) findViewById(R.id.inbox_btnInbox);
        inbox_btnOrders = (Button) findViewById(R.id.inbox_btnOrders);
        inbox_btnProfile = (Button) findViewById(R.id.inbox_btnProfile);

        // set the items in contacts listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InboxActivity.this, android.R.layout.simple_list_item_1, contacts);
        inbox_lvContacts = (ListView) findViewById(R.id.inbox_lvContacts);
        inbox_lvContacts.setAdapter(adapter);

        inbox_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to search page
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        inbox_btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to orders page
                Intent intent = new Intent(getBaseContext(), OrdersActivity.class);
                startActivity(intent);
            }
        });

        inbox_btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to personalInfo page
                Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
    }

}