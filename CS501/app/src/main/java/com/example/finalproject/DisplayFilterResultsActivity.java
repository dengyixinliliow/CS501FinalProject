package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class DisplayFilterResultsActivity extends AppCompatActivity {

    private String category;
    private String type;
    private String title;

    private TextView filter_result_title;

    // Firebase
    private FirebaseFirestore db;
    ArrayList<Product> productList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dislay_filter_results);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        type = intent.getStringExtra("type");
        if (!category.equals("none")) {
            title = category + " " + type;
        } else {
            title = type;
        }

        filter_result_title = (TextView) findViewById(R.id.filter_result_title);
        filter_result_title.setText(title);

    }

    private void fetchFilterResult(String category, String type) {
        CollectionReference productsRef = db.collection("products");
        if (!category.equals("none")) {
            Query query = productsRef.whereEqualTo("product_category", category). whereEqualTo("product_type", type);
        } else {
            Query query = productsRef. whereEqualTo("product_type", type);
        }

    }
}
