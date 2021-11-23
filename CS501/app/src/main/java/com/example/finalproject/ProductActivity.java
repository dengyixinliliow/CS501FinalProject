package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private ImageView product_ivProduct;

    private TextView product_txtName;
    private TextView product_txtType;
    private TextView product_txtSize;
    private TextView product_txtPrice;
    private TextView product_txtCategory;
    private TextView product_txtCondition;

    private Button product_btnAddToBag;
    private Button product_btnReviews;

    public static final String USER_ID = "user_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String PRODUCT_SIZE = "product_size";
    public static final String PRODUCT_TYPE = "product_type";
    public static final String PRODUCT_COLOR = "product_color";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String PRODUCT_CONDITION= "product_condition";
    public static final String PRODUCT_IMG_URL = "product_img_url";
    public static final String PRODUCT_IS_AVAILABLE = "product_is_available";


    private Map<String, Object> product;
    private String product_id;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // get product id
        Intent intent = getIntent();
        product_id = intent.getStringExtra(PRODUCT_ID);
        Log.e(TAG, "onSuccess: product id is " + product_id);

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        product_ivProduct = (ImageView) findViewById(R.id.product_ivProduct);

        product_txtName = (TextView) findViewById(R.id.product_txtName);
        product_txtType = (TextView) findViewById(R.id.product_txtType);
        product_txtSize = (TextView) findViewById(R.id.product_txtSize);
        product_txtPrice = (TextView) findViewById(R.id.product_txtPrice);
        product_txtCategory = (TextView) findViewById(R.id.product_txtCategory);
        product_txtCondition = (TextView) findViewById(R.id.product_txtCondition);

        product_btnAddToBag = (Button) findViewById(R.id.product_btnAddToBag);
        product_btnReviews = (Button) findViewById(R.id.product_btnReviews);

        getProductById(product_id);

        product_btnAddToBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToCart();
            }
        });
    }

    public void addProductToCart() {

        db.collection("carts");

        // add product in carts database
        product = new HashMap<String, Object>();
        product.put(USER_ID, user_id);
        product.put(PRODUCT_ID, product_id);

        db.collection("carts")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "onSuccess: product is added into cart " + user_id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }

    public void getProductById(String product_id) {
        // [START get_multiple]
        db.collection("products")
                .whereEqualTo("product_id", product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                product = document.getData();

                                // Set Product Info in EditText
                                product_txtName.setText(product.get(PRODUCT_NAME).toString());
                                product_txtType.setText(product.get(PRODUCT_TYPE).toString());
                                product_txtSize.setText(product.get(PRODUCT_SIZE).toString());
                                product_txtPrice.setText(product.get(PRODUCT_PRICE).toString());
                                product_txtCategory.setText(product.get(PRODUCT_CATEGORY).toString());
                                product_txtCondition.setText(product.get(PRODUCT_CONDITION).toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}