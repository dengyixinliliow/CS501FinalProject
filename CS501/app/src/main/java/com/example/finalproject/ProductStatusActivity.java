package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProductStatusActivity extends AppCompatActivity {

    private TextView pstatus_status;
    private TextView pstatus_pname;
    private Button pstatus_contact;
    private Button pstatus_receive;
    private String pname;
    private String status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_status);

        Intent intent = getIntent();
        pname = intent.getStringExtra("product_name");

        pstatus_status = (TextView) findViewById(R.id.pstatus_pstatus);
        pstatus_pname = (TextView) findViewById(R.id.pstatus_pname);
        pstatus_contact = (Button) findViewById(R.id.pstatus_contact);
        pstatus_receive = (Button) findViewById(R.id.pstatus_receive);

        status = "unavailable";

        pstatus_status.setText("Available");
        pstatus_pname.setText(pname);

        if (status.equals("available")) {
           pstatus_contact.setVisibility(View.GONE);
           pstatus_receive.setVisibility(View.GONE);
        }



    }
}
