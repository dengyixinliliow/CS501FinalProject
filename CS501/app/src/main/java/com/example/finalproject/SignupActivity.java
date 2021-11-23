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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private Button signup_btnSignup;

    private EditText signup_edtEmail;
    private EditText signup_edtUsername;
    private EditText signup_edtPassword;
    private EditText signup_edtRepassword;

    private String edt_email;
    private String edt_username;
    private String edt_password;
    private String edt_rePassword;

    private TextView signup_txtErrorMsg;
    private String error_message = "Email or Password is not correct! Please try again!";

    private Map<String, Object> user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;

    public static final String EMAIL = "email";
    public static final String USERID = "user_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ADDRESS = "address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signup_btnSignup = (Button) findViewById(R.id.signup_btnSignup);

        signup_edtEmail = (EditText) findViewById(R.id.signup_edtEmail);
        signup_edtUsername = (EditText) findViewById(R.id.signup_edtUsername);
        signup_edtPassword = (EditText) findViewById(R.id.signup_edtPassword);
        signup_edtRepassword = (EditText) findViewById(R.id.signup_edtRepassword);

        signup_txtErrorMsg = (TextView) findViewById(R.id.signup_txtErrorMsg);

        signup_btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_email = signup_edtEmail.getText().toString();
                edt_username = signup_edtUsername.getText().toString();
                edt_password = signup_edtPassword.getText().toString();
                edt_rePassword = signup_edtRepassword.getText().toString();

                if(edt_email.isEmpty() || edt_password.isEmpty() || edt_username.isEmpty()) {
                    signup_txtErrorMsg.setText("All fields are required!");
                    return;
                }
                if(!edt_password.equals(edt_rePassword)) {
                    signup_txtErrorMsg.setText("Passwords are not match!");
                    return;
                }

                // create account in firebase auth and add user into filestore
                createAccount(edt_email, edt_password);
            }
        });
    }



    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "createUserWithEmail:success");
                            auth_user = mAuth.getCurrentUser();
                            user_id = auth_user.getUid();

                            // send verification email
                            auth_user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                Toast.makeText(getBaseContext(), "Verification Email Has Been Sent.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Log.d(TAG, "Email not sent.");
                                            }
                                        }
                                    });

                            // add user in firebase firestore
                            addUserToDatabase();

                            // Move to personalInfo page
                            Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                            //intent.putExtra("USERID", auth_user.getUid());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            signup_txtErrorMsg.setText(task.getException().getMessage().toString());
                        }
                    }
                });
        // [END create_user_with_email]
    }


    public void addUserToDatabase() {

        DocumentReference documentReference = db.collection("users").document(user_id);
        // add user in firebase firestore
        user = new HashMap<String, Object>();
        user.put(EMAIL, edt_email);
        user.put(USERNAME, edt_username);
        user.put(USERID, auth_user.getUid());
        user.put(PASSWORD, edt_password);
        user.put(ADDRESS, null);

        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e(TAG, "onSuccess: user profile is created for " + user_id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
}