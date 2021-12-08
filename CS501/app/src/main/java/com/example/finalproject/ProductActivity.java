package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.UUID;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "PRODUCT_ACTIVITY";

    private ImageView product_ivProduct;
    private TextView product_txtName;
    private TextView product_txtType;
    private TextView product_txtSize;
    private TextView product_txtPrice;
    private TextView product_txtCategory;
    private TextView product_txtCondition;
    private TextView product_txtDescription;
    private TextView product_txtSellerUsername;
    private TextView product_tvReviewCount;
    private Button product_btnAddToBag;
    private Button product_btnReviews;
    private Button product_btnContactSeller;
    private Button product_btnBack;

    private static final String USER_ID = "user_id";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_NAME = "product_name";
    private static final String PRODUCT_TYPE = "product_type";
    private static final String PRODUCT_SIZE = "product_size";
    private static final String PRODUCT_PRICE = "product_price";
    private static final String PRODUCT_CATEGORY = "product_category";
    private static final String PRODUCT_CONDITION = "product_condition";
    private static final String PRODUCT_DESCRIPTION = "product_description";
    private static final String PRODUCT_IMG_URL = "product_img_url";
    private static final String SELLER_ID = "seller_id";
    private static final String USERNAME = "username";

    private Map<String, Object> product;
    private String product_id;
    private String seller_id;
    private Boolean product_exist_in_cart = false;

    // Firebase data
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
        product_txtDescription = (TextView) findViewById(R.id.product_txtDescription);
        product_txtSellerUsername = (TextView) findViewById(R.id.product_txtSellerUsername);
        product_tvReviewCount=(TextView)findViewById(R.id.product_tvReviewCount);

        product_btnAddToBag = (Button) findViewById(R.id.product_btnAddToBag);
        product_btnReviews = (Button) findViewById(R.id.product_btnReviews);
        product_btnContactSeller = (Button) findViewById(R.id.product_btnContactSeller);
        product_btnBack = (Button) findViewById(R.id.product_btnBack);

        product_ivProduct=(ImageView)findViewById(R.id.product_ivProduct);

        if (intent.getStringExtra("action_taker").equals("owner")) {
            product_btnAddToBag.setVisibility(View.GONE);
        };

        getProductById(product_id);

        product_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        product_btnAddToBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProductExist();
            }
        });

        product_btnContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to Contact page
                Intent intent = new Intent(getBaseContext(), ContactActivity.class);
                intent.putExtra(SELLER_ID, seller_id);
                startActivity(intent);
            }
        });

        product_btnReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(), ViewReviewActivity.class);
                intent.putExtra(PRODUCT_ID,product_id);
                startActivity(intent);
            }
        });
    }

    public void getProductById(String product_id) {
        // [START get_multiple]
        db.collection("products")
                .whereEqualTo(PRODUCT_ID, product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                product = document.getData();

                                seller_id = product.get(SELLER_ID).toString();
                                getUserById(seller_id);

                                // Set Product Info in EditText
                                product_txtName.setText(product.get(PRODUCT_NAME).toString());
                                product_txtType.setText(product.get(PRODUCT_TYPE).toString());
                                product_txtSize.setText(product.get(PRODUCT_SIZE).toString());
                                product_txtPrice.setText("$" + product.get(PRODUCT_PRICE).toString());
                                product_txtCategory.setText(product.get(PRODUCT_CATEGORY).toString());
                                product_txtCondition.setText(product.get(PRODUCT_CONDITION).toString());
                                product_txtDescription.setText(product.get(PRODUCT_DESCRIPTION).toString());

                                Glide.with(getBaseContext()).load(product.get(PRODUCT_IMG_URL).toString()).into(product_ivProduct);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
        db.collection("reviews")
                .whereEqualTo(PRODUCT_ID,product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int tasklength=task.getResult().size();
                            if(tasklength<=1){
                                product_tvReviewCount.setText(String.valueOf(tasklength)+" review");
                            }
                            else{
                                product_tvReviewCount.setText(String.valueOf(tasklength)+" reviews");
                            }
                        }
                    }
                });
    }

    /*
        Check whether the product exist in carts database
     */
    public void checkProductExist() {
        product = new HashMap<String, Object>();
        product.put(USER_ID, user_id);
        product.put(PRODUCT_ID, product_id);

        // [START get_multiple]
        db.collection("carts")
//                .whereEqualTo(PRODUCT_ID, product.get(PRODUCT_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                Map<String, Object> current_data = document.getData();
                                String current_user_id = current_data.get(USER_ID).toString();
                                String current_product_id = current_data.get(PRODUCT_ID).toString();

                                if(current_user_id.equals(user_id) && current_product_id.equals(product_id)) {
                                    CharSequence text = "Product already in cart!";
                                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                                    toast.show();
                                    product_exist_in_cart = true;
                                }
                            }
                            if(!product_exist_in_cart) {
                                addProductToCart();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    /*
        Add product in carts database
     */
    public void addProductToCart() {
        product = new HashMap<String, Object>();
        product.put(USER_ID, user_id);
        product.put(PRODUCT_ID, product_id);

        db.collection("carts")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        CharSequence text = "Product added to cart!";
                        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                        toast.show();

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

    public void getUserById(String user_id) {
        // [START get_multiple]
        db.collection("users")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                Map<String, Object> current_user = document.getData();
                                product_txtSellerUsername.setText(current_user.get(USERNAME).toString());
                                // Set User Info in EditText
//                                personalInfo_edtUsername.setText(current_user.get(USERNAME).toString());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}