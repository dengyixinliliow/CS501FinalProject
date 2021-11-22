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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

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

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        btn_login = (Button) findViewById(R.id.login_btnLogin);

        edt_email = (EditText) findViewById(R.id.login_edtEmail);
        edt_password = (EditText) findViewById(R.id.login_edtPassword);

        txt_errorMsg = (TextView) findViewById(R.id.login_txtErrorMsg);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edt_email.getText().toString();
                password = edt_password.getText().toString();

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
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success");
                            txt_errorMsg.setText("success");
                            FirebaseUser auth_user = mAuth.getCurrentUser();

                            // Move to personalInfo page
                            Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
//                            intent.putExtra("USERID", auth_user.getUid());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            txt_errorMsg.setText(task.getException().getMessage().toString());
                        }
                    }
                });
        // [END sign_in_with_email]
    }
}