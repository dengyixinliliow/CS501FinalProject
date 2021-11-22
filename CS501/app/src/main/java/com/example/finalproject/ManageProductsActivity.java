package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ManageProductsActivity extends AppCompatActivity {

    //variables for getting currentUser
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private FirebaseFirestore db;
    ArrayList<Product> productList;

    private String user_id;
    private ListView product_list_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        product_list_view = (ListView) findViewById(R.id.manage_products_list_view);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        fetchSellerProducts();
    }

    private void fetchSellerProducts() {
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef.whereEqualTo("seller_id", user_id);

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
                                String.valueOf(dataMap.get("product_id"))
                        );
                        productList.add(product);
                    }

                    ProductsLVAdapter productsLVAdapter = new ProductsLVAdapter(ManageProductsActivity.this, productList);
                    product_list_view.setAdapter(productsLVAdapter);

                } else {
                    Log.e("test", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_product:
                Intent intent = new Intent(ManageProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
