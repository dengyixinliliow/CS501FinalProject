package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {

    private static final String TAG = "PERSONALINFO";

    private String edt_email;
    private String edt_username;
    private String edt_password;
    private String edt_phone;
    private String edt_address;

    private EditText personalInfo_edtUsername;
    private EditText personalInfo_edtEmail;
    private EditText personalInfo_edtPassword;
    private EditText personalInfo_edtPhone;
    private EditText personalInfo_edtAddress;

    private Button personalInfo_btnBack;
    private Button personalInfo_btnUpdate;

    private Map<String, Object> current_user;
    private String user_id;

    private static final String EMAIL = "email";
    private static final String USERID = "user_id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String PROFILE_UPDATED = "Profile Updated!";


    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        personalInfo_edtUsername = (EditText) findViewById(R.id.personalInfo_edtUsername);
        personalInfo_edtEmail = (EditText) findViewById(R.id.personalInfo_edtEmail);
        personalInfo_edtPassword = (EditText) findViewById(R.id.personalInfo_edtPassword);
        personalInfo_edtPhone = (EditText) findViewById(R.id.personalInfo_edtPhone);
        personalInfo_edtAddress = (EditText) findViewById(R.id.personalInfo_edtAddress);
        personalInfo_btnBack = (Button) findViewById(R.id.personalInfo_btnBack);
        personalInfo_btnUpdate = (Button) findViewById(R.id.personalInfo_btnUpdate);

        personalInfo_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get and set the user's information
        getUserById(user_id);

        personalInfo_btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the newest user's information
                edt_email = personalInfo_edtEmail.getText().toString();
                edt_username = personalInfo_edtUsername.getText().toString();
                edt_password = personalInfo_edtPassword.getText().toString();
                edt_phone = personalInfo_edtPhone.getText().toString();
                edt_address = personalInfo_edtAddress.getText().toString();

                updateDatabase(edt_email, edt_username, edt_password, edt_phone, edt_address);
            }
        });
    }


    public void getUserById(String user_id) {
        // [START get_multiple]
        db.collection("users")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                current_user = document.getData();

                                // Set User Info in EditText
                                personalInfo_edtUsername.setText(current_user.get(USERNAME).toString());
                                personalInfo_edtEmail.setText(current_user.get(EMAIL).toString());
                                personalInfo_edtPassword.setText(current_user.get(PASSWORD).toString());
                                if (current_user.get(PHONE) != null) {
                                    personalInfo_edtPhone.setText(current_user.get(PHONE).toString());
                                }
                                if (current_user.get(ADDRESS) != null) {
                                    personalInfo_edtAddress.setText(current_user.get(ADDRESS).toString());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    /*
        Update the user's information in users database
     */
    public void updateDatabase(String username, String email, String password, String phone, String address) {
        DocumentReference documentReference = db.collection("users").document(user_id);
        Map<String, Object> updates = new HashMap<>();
        updates.put(EMAIL, edt_email);
        updates.put(USERNAME, edt_username);
        updates.put(USERID, auth_user.getUid());
        updates.put(PASSWORD, edt_password);
        updates.put(PHONE, edt_phone);
        updates.put(ADDRESS, edt_address);

        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getBaseContext(), PROFILE_UPDATED, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess: user profile is created for " + user_id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });

    }

    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}