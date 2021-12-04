package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentSuccessActivity extends AppCompatActivity {

    private final Double ZERO = 0.00;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;

    private String payment_success_order_time;
    private Double payment_success_order_total;
    private String payment_success_order_id;
    private String payment_success_owner_id;
    private ArrayList<String> payment_success_items_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        //Get info passed from payment activity
        Intent intent = getIntent();
        payment_success_order_time = intent.getStringExtra("order_time");
        payment_success_order_total = intent.getDoubleExtra("order_total", ZERO);
        payment_success_order_id = intent.getStringExtra("order_id");
        payment_success_items_list = intent.getStringArrayListExtra("products_list");
        //used for database
        FirebaseStorage storage;
        StorageReference storageReference;
        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        payment_success_owner_id = auth_user.getUid();
        // connect to database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        addOrder(db);
    }

    private void addOrder (FirebaseFirestore db) {
        Map<String, Object> order = new HashMap<>();
        order.put("order_id", payment_success_order_id);
        order.put("order_total", payment_success_order_total);
        order.put("order_time", payment_success_order_time);
        order.put("order_owner", payment_success_owner_id);
        order.put("order_items", payment_success_items_list);

        db.collection("orders").add(order)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("test", "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("test", "Error adding document", e);
            }
        });
    }
}
