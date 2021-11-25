package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DisplayFilterResultsActivity extends AppCompatActivity {

    private String category;
    private String type;
    private String title;

    private TextView filter_result_title;

    // Firebase
    private FirebaseFirestore db;

    ArrayList<Product> productList;
    private ListView filter_result_lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dislay_filter_results);

        filter_result_lv = (ListView) findViewById(R.id.filter_result_lv);

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

        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        fetchFilterResult(category, type);

    }

    private void fetchFilterResult(String category, String type) {
        CollectionReference productsRef = db.collection("products");
        Query query;

        if (!category.equals("none")) {
            query = productsRef.whereEqualTo("product_category", category).
                    whereEqualTo("product_type", type);
        } else {
            query = productsRef. whereEqualTo("product_type", type);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        Log.d("MissionActivity", document.getId() + " => " + dataMap);
                        Product product = new Product(
                                String.valueOf(dataMap.get("product_name")),
                                String.valueOf(dataMap.get("product_size")),
                                String.valueOf(dataMap.get("product_price")),
                                String.valueOf(dataMap.get("product_id")),
                                String.valueOf(dataMap.get("product_img_url"))
                        );
                        productList.add(product);
                    }

                    ProductsLVAdapter productsLVAdapter = new ProductsLVAdapter(DisplayFilterResultsActivity.this, productList);
                    filter_result_lv.setAdapter(productsLVAdapter);

                } else {
                    Log.e("test", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
