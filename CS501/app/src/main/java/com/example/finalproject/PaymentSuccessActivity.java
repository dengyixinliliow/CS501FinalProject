package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PaymentSuccessActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {

    private final Double ZERO = 0.00;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;

    private String payment_success_order_time;
    private Double payment_success_order_total;
    private String payment_success_order_id;
    private String payment_success_owner_id;
    private ArrayList<String> payment_success_items_list;
    private Map map = new HashMap();
    private String product_owner = "123";

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

        for (int i = 0; i < payment_success_items_list.size(); i++) {
            getProductOwnerId(payment_success_items_list.get(i), db);
        }
    }

    private void getProductOwnerId (String product_id, FirebaseFirestore db) {
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef.whereEqualTo("product_id", product_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        product_owner = String.valueOf(dataMap.get("seller_id"));
                        map.put(product_id, product_owner);
                    }
                    addOrder(db, map);
                    addMessage(db, map);
                } else {
                    Log.e("test", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void addOrder (FirebaseFirestore db, Map map) {
        Map<String, Object> order = new HashMap<>();
        order.put("order_id", payment_success_order_id);
        order.put("order_total", payment_success_order_total);
        order.put("order_time", payment_success_order_time);
        order.put("order_owner", payment_success_owner_id);
        order.put("order_items", map);

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

    private void addMessage (FirebaseFirestore db, Map map) {
        Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            Map<String, Object> message = new HashMap<>();
            message.put("seller_id", entry.getValue());
            message.put("product_id", entry.getKey());
            message.put("renter_id", payment_success_owner_id);
            message.put("type", "order placed");

            db.collection("messages").add(message)
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

    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}
