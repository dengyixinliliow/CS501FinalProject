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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    // Firebase variables
    private FirebaseAuth mAuth;

    private Button btn_login;
    private EditText edt_email;
    private EditText edt_password;
    private ImageView return_icon;
    private TextView txt_errorMsg;

    private String email;
    private String password;

    private String SUCCESS = "Sign in success!";
    private String FIELDS_REQUIRED = "All fields are required!";
    private String error_message = "Email or Password is not correct! Please try again!";
    private String ERROR_VARIFIED = "signInWithEmail:failure, email not verified!";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        return_icon=(ImageView)findViewById(R.id.login_return);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                startActivity(intent);
            }
        });

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
                    txt_errorMsg.setText(FIELDS_REQUIRED);
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
                            String signin_fail = task.getException().getMessage() + "login";
                            txt_errorMsg.setText(signin_fail);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            txt_errorMsg.setText(SUCCESS);
            // Move to personalInfo page
            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivity(intent);
        }
        else
        {
            // email is not verified
            FirebaseAuth.getInstance().signOut();
            Log.d(TAG, ERROR_VARIFIED);
            txt_errorMsg.setText(ERROR_VARIFIED);
        }
    }
    public static void restartActivity(Activity activity){
        activity.recreate();
    }
}