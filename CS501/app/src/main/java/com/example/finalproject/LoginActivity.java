package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    FirebaseUser auth_user;

    private Button btn_login;
    private Button btn_signup;
    private EditText edt_email;
    private EditText edt_password;

    private String email;
    private String password;

    private TextView txt_errorMsg;
    private String error_message = "Email or Password is not correct! Please try again!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);


        mAuth = FirebaseAuth.getInstance();

        btn_login = (Button) findViewById(R.id.login_btnLogin);

        edt_email = (EditText) findViewById(R.id.login_edtEmail);
        edt_password = (EditText) findViewById(R.id.login_edtPassword);

        txt_errorMsg = (TextView) findViewById(R.id.login_txtErrorMsg);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edt_email.getText().toString();
                password = edt_password.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    txt_errorMsg.setText("All fields are required!");
                    return;
                }

                signIn(email, password);
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
                            txt_errorMsg.setText(task.getException().getMessage().toString() + "login");
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified
//            finish();
            Log.d(TAG, "signInWithEmail:success");
            txt_errorMsg.setText("success");
            // Move to personalInfo page
            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivity(intent);
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();
            Log.d(TAG, "signInWithEmail:failure, email not verified!");
            txt_errorMsg.setText("signInWithEmail:failure, email not verified!");

//            //restart this activity
//            restartActivity(LoginActivity.this);

        }
    }
    public static void restartActivity(Activity activity){
        activity.recreate();
    }
}