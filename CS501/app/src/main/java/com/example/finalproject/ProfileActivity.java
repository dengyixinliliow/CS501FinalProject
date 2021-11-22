package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "profilePage";

    //variables for getting currentUser
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private FirebaseFirestore db;
    private String user_id;
    private Map<String, Object> current_user;

    private TextView name;
    private TextView rateText;
    private ImageView profilePic;
    private ListView lvSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String[] options = {
                getString(R.string.setting1),
                getString(R.string.setting2),
                getString(R.string.setting3),
                getString(R.string.setting4),
                getString(R.string.setting5),
                getString(R.string.setting6)};

        name = (TextView) findViewById(R.id.name);
        rateText = (TextView) findViewById(R.id.rateText);
        profilePic = (ImageView) findViewById(R.id.profilePic);
        lvSettings = (ListView) findViewById(R.id.lvSettings);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        db = FirebaseFirestore.getInstance();
        fetchUserProfile();

        ArrayAdapter optionsListAdapter = new ArrayAdapter<String>(getApplicationContext(),           //Context
                android.R.layout.simple_list_item_1, //type of list (simple)
                options);

        lvSettings.setAdapter(optionsListAdapter);

        lvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Option;
                Option = String.valueOf(parent.getItemAtPosition(position));

                if (Option.equals(options[0])) {            // Manage Account

                } else if (Option.equals(options[1])) {     // Payments

                } else if (Option.equals(options[2])) {     // Manage Reviews

                } else if (Option.equals(options[3])) {     // Manage Rentals
                    Intent intent = new Intent(getBaseContext(), ManageProductsActivity.class);
                    startActivity(intent);
                } else if (Option.equals(options[4])) {     // Manage Orders
                    Intent intent = new Intent(getBaseContext(), OrdersActivity.class);
                    startActivity(intent);
                } else if (Option.equals(options[5])) {     // Manage Messages

                }
            }
        });

    }

    private void fetchUserProfile() {
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("user_id", user_id);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // store info of the current user
                        current_user = document.getData();
                        name.setText(current_user.get("username").toString());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}