package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private EditText personalInfo_edtUsername;
    private EditText personalInfo_edtEmail;
    private EditText personalInfo_edtPassword;

    private Button personalInfo_btnSearch;
    private Button personalInfo_btnInbox;
    private Button personalInfo_btnOrders;
    private Button personalInfo_btnProfile;
    private Button personalInfo_btnAdd;

    private TextView personalInfo_txtVerifyMsg;
    private Button personalInfo_btnVerify;

    private Map<String, Object> current_user;
    private String user_id;

    public static final String EMAIL = "email";
    public static final String USERID = "user_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private FirebaseAuth mAuth;
    FirebaseUser auth_user;
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

        personalInfo_btnSearch = (Button) findViewById(R.id.personalInfo_btnSearch);
        personalInfo_btnInbox = (Button) findViewById(R.id.personalInfo_btnInbox);
        personalInfo_btnOrders = (Button) findViewById(R.id.personalInfo_btnOrders);
        personalInfo_btnProfile = (Button) findViewById(R.id.personalInfo_btnProfile);

        personalInfo_btnAdd = (Button) findViewById(R.id.personalInfo_add_product);

        personalInfo_txtVerifyMsg = (TextView) findViewById(R.id.personalInfo_txtVerifyMsg);
        personalInfo_btnVerify = (Button) findViewById(R.id.personalInfo_btnVerify);


        if(!auth_user.isEmailVerified()) {
            personalInfo_txtVerifyMsg.setVisibility(View.VISIBLE);
            personalInfo_btnVerify.setVisibility(View.VISIBLE);

            personalInfo_btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send verification email
                    auth_user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(view.getContext(), "Verification Email Has Been Sent.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.d(TAG, "Email not sent.");
                                    }
                                }
                            });
                }
            });
        }

        getUserById(user_id);

        personalInfo_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to search page
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        personalInfo_btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to personalInfo page
                Intent intent = new Intent(getBaseContext(), OrdersActivity.class);
                startActivity(intent);
            }
        });

        personalInfo_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddProductActivity.class);
                intent.putExtra("USERID", user_id);
                startActivity(intent);
            }
        });

        personalInfo_btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to inbox page
                Intent intent = new Intent(getBaseContext(), InboxActivity.class);
                startActivity(intent);
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
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}