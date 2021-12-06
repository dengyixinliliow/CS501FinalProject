package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

public class ProfileActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener{

    private static final String TAG = "profilePage";

    //variables for getting currentUser
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private FirebaseFirestore db;
    private String user_id;
    private Map<String, Object> current_user;

    private TextView name;
    private ListView lvSettings;

    private Button profile_btnSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String[] options = {
                getString(R.string.setting1),
                getString(R.string.setting3),
                getString(R.string.setting4)};

        name = (TextView) findViewById(R.id.name);
        lvSettings = (ListView) findViewById(R.id.lvSettings);

        profile_btnSignout = (Button) findViewById(R.id.profile_btnSignout);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        db = FirebaseFirestore.getInstance();
        fetchUserProfile();

        ArrayAdapter optionsListAdapter = new ArrayAdapter<String>(getApplicationContext(),           //Context
                android.R.layout.simple_list_item_1, //type of list (simple)
                options);

        lvSettings.setAdapter(optionsListAdapter);

        lvSettings.setDivider(null);

        lvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Option;
                Option = String.valueOf(parent.getItemAtPosition(position));

                if (Option.equals(options[0])) {            // Manage your account
                    Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                    startActivity(intent);
                } else if (Option.equals(options[1])) {     // Manage Rentals
                    Intent intent = new Intent(getBaseContext(), ManageProductsActivity.class);
                    startActivity(intent);
                } else if (Option.equals(options[2])) {     // Manage Rentals
                    Intent intent = new Intent(getBaseContext(), OrdersActivity.class);
                    startActivity(intent);
                }
            }
        });

        profile_btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                // Move to Login Page
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
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
                        name.setText("Welcome, " + current_user.get("username").toString() + "!");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}