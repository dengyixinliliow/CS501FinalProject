package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private Button signup_btnVerify;

    private EditText signup_edtEmail;
    private EditText signup_edtUsername;
    private EditText signup_edtPassword;
    private EditText signup_edtRepassword;
    private EditText signup_edtAddress;
    private EditText signup_edtPhone;
    private ImageView return_icon;

    private String edt_email;
    private String edt_username;
    private String edt_password;
    private String edt_rePassword;
    private String edt_address;
    private String edt_phone;

    private Boolean btnVerify_clicked = false;
    private TextView signup_txtErrorMsg;

    private Map<String, Object> user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;

    private static final String USERS = "users";
    private static final String EMAIL = "email";
    private static final String USERID = "user_id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String VERIFICATION_EMAIL_SENT = "Verification Email Has Been Sent!";
    private static final String EMAIL_NOT_VERIFIED = "Please verify your email first!";
    private static final String EMAIL_VERIFIED = "Email verified!";
    private static final String FIELDS_REQUIRED = "All fields are required!";
    private static final String PASSWORDS_NOT_MATCH = "Passwords are not match!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        signup_btnSignup = (Button) findViewById(R.id.signup_btnSignup);
        signup_btnVerify = (Button) findViewById(R.id.signup_btnVerify);

        signup_edtEmail = (EditText) findViewById(R.id.signup_edtEmail);
        signup_edtUsername = (EditText) findViewById(R.id.signup_edtUsername);
        signup_edtPassword = (EditText) findViewById(R.id.signup_edtPassword);
        signup_edtRepassword = (EditText) findViewById(R.id.signup_edtRepassword);
        signup_edtPhone = (EditText) findViewById(R.id.signup_edtPhone);
        signup_edtAddress = (EditText) findViewById(R.id.signup_edtAddress);
        signup_txtErrorMsg = (TextView) findViewById(R.id.signup_txtErrorMsg);

        return_icon=(ImageView)findViewById(R.id.signup_return);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        signup_btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_email = signup_edtEmail.getText().toString();
                edt_username = signup_edtUsername.getText().toString();
                edt_password = signup_edtPassword.getText().toString();
                edt_rePassword = signup_edtRepassword.getText().toString();
                edt_phone = signup_edtPhone.getText().toString();
                edt_address = signup_edtAddress.getText().toString();

                if(edt_email.isEmpty() || edt_password.isEmpty() || edt_username.isEmpty() || edt_address.isEmpty()) {
                    signup_txtErrorMsg.setVisibility(View.VISIBLE);
                    signup_txtErrorMsg.setText(FIELDS_REQUIRED);
                    return;
                }
                if(!edt_password.equals(edt_rePassword)) {
                    signup_txtErrorMsg.setVisibility(View.VISIBLE);
                    signup_txtErrorMsg.setText(PASSWORDS_NOT_MATCH);
                    return;
                }

                btnVerify_clicked = true;

                // create account in firebase auth and add user into filestore
                createAccount(edt_email, edt_password);
            }
        });

        signup_btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btnVerify_clicked) {
                    signup_txtErrorMsg.setVisibility(View.VISIBLE);
                    signup_txtErrorMsg.setText(EMAIL_NOT_VERIFIED);
                    return;
                }
                // get the current user
                auth_user = mAuth.getCurrentUser();

                signIn(edt_email, edt_password);
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
                            auth_user = mAuth.getCurrentUser();
                            user_id = auth_user.getUid();

                            // send verification email
                            auth_user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                signup_txtErrorMsg.setVisibility(View.VISIBLE);
                                                signup_txtErrorMsg.setText(VERIFICATION_EMAIL_SENT);
                                            } else {
                                                signup_txtErrorMsg.setVisibility(View.VISIBLE);
                                                signup_txtErrorMsg.setText(task.getException().getMessage().toString());
                                            }
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            signup_txtErrorMsg.setVisibility(View.VISIBLE);
                            signup_txtErrorMsg.setText(task.getException().getMessage().toString());
                        }
                    }
                });
        // [END create_user_with_email]
    }


    public void addUserToDatabase() {

        DocumentReference documentReference = db.collection(USERS).document(user_id);
        // add user in firebase firestore
        user = new HashMap<String, Object>();
        user.put(EMAIL, edt_email);
        user.put(USERNAME, edt_username);
        user.put(USERID, auth_user.getUid());
        user.put(PASSWORD, edt_password);
        user.put(PHONE, edt_phone);
        user.put(ADDRESS, edt_address);

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

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, check if the email is verified.
                            checkIfEmailVerified();
                        } else {
                            // If sign in fails, display a message to the user.
                            signup_txtErrorMsg.setVisibility(View.VISIBLE);
                            String signin_fail = task.getException().getMessage().toString() + "login";
                            signup_txtErrorMsg.setText(signin_fail);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void checkIfEmailVerified() {

        if (auth_user.isEmailVerified()) {
            signup_txtErrorMsg.setVisibility(View.VISIBLE);
            signup_txtErrorMsg.setText(EMAIL_VERIFIED);

            // add user in firebase firestore
            addUserToDatabase();

            // log out the user.
            FirebaseAuth.getInstance().signOut();

            // Move to Login page
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }
        else {
            signup_txtErrorMsg.setVisibility(View.VISIBLE);
            signup_txtErrorMsg.setText(EMAIL_NOT_VERIFIED);
        }
    }
}