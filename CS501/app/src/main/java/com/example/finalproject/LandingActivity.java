package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity {

    private Button landing_btnLogin;
    private Button landing_btnSignup;
    private ConstraintLayout CL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        landing_btnLogin = (Button) findViewById(R.id.landing_btnLogin);
        landing_btnSignup = (Button) findViewById(R.id.landing_btnSignup);

        CL = (ConstraintLayout) findViewById(R.id.landing_layoutBg);

        landing_btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to login page
                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        landing_btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to signup page
                Intent intent = new Intent(LandingActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}