package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddProductDetailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;

    // views
    private Button getAddProduct_btnSubmit;
    private ImageView return_icon;
    private EditText addProduct_edtPName;
    private Spinner addProduct_edtPType;
    private EditText addProduct_edtPColor;
    private Spinner addProduct_edtPCategory;
    private EditText addProduct_edtPSize;
    private EditText addProduct_edtPCondition;
    private EditText addProduct_edtPPrice;
    private EditText addProduct_edtDescription;
    private EditText addProduct_edtAddress;

    //Variable
    private String
            img_url,
            random_product_id,
            product_name,
            product_type,
            product_color,
            product_category,
            product_size,
            product_condition,
            product_address,
            product_price,
            product_description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_detail);
        return_icon=(ImageView) findViewById(R.id.addProductDetailReturn);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        img_url = intent.getStringExtra("product_img");

        //generate random id for each product
        random_product_id = UUID.randomUUID().toString();

        getAddProduct_btnSubmit = findViewById(R.id.addProduct_btnSubmit);
        addProduct_edtPName = findViewById(R.id.addProduct_edtPName);
        addProduct_edtPType = (Spinner) findViewById(R.id.addProduct_edtPType);
        addProduct_edtPColor = findViewById(R.id.addProduct_edtPColor);
        addProduct_edtPSize = (EditText) findViewById(R.id.addProduct_edtPSize);
        addProduct_edtPCondition = findViewById(R.id.addProduct_edtPCondition);
        addProduct_edtPPrice = findViewById(R.id.addProduct_edtPPrice);
        addProduct_edtDescription = findViewById(R.id.addProduct_edtDescription);
        addProduct_edtAddress = findViewById(R.id.addProduct_edtAddress);
        addProduct_edtPCategory = (Spinner) findViewById(R.id.addProduct_edtPCategory);
        populateSpinnerCategory();
        populateSpinnerType();

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        // connect to database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //when submit, data will be stored in firebase.
        getAddProduct_btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((TextUtils.isEmpty(addProduct_edtPName.getText().toString()))
                ||(TextUtils.isEmpty(addProduct_edtPColor.getText().toString()))
                ||(TextUtils.isEmpty(addProduct_edtPSize.getText().toString()))
                ||(TextUtils.isEmpty(addProduct_edtPCondition.getText().toString()))
                ||(TextUtils.isEmpty(addProduct_edtPPrice.getText().toString()))
                ||(TextUtils.isEmpty(addProduct_edtAddress.getText().toString()))
                ||(TextUtils.isEmpty(addProduct_edtDescription.getText().toString()))) {
                    Toast.makeText(AddProductDetailActivity.this, "All fields are required!",
                            Toast.LENGTH_LONG).show();
                } else {
                    getData();
                    addProduct(db);
                    addToAlgolia();
                    Intent to_all=new Intent(getBaseContext(),ManageProductsActivity.class);
                    startActivity(to_all);
                }
            }
        });
    }

    //spinner for category and type
    private void populateSpinnerCategory() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.category));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addProduct_edtPCategory.setAdapter(categoryAdapter);
    }

    private void populateSpinnerType() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.type));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addProduct_edtPType.setAdapter(typeAdapter);
    }

    //get data from filed
    private void getData() {
        product_name = addProduct_edtPName.getText().toString();
        product_type = addProduct_edtPType.getSelectedItem().toString();
        product_color = addProduct_edtPColor.getText().toString();
        product_category = addProduct_edtPCategory.getSelectedItem().toString().toLowerCase();
        product_size = addProduct_edtPSize.getText().toString();
        product_condition = addProduct_edtPCondition.getText().toString();
        product_price = addProduct_edtPPrice.getText().toString();
        product_description = addProduct_edtDescription.getText().toString();
        product_address = addProduct_edtAddress.getText().toString();
    }

    //add data to firestore database
    private void addProduct (FirebaseFirestore db) {
        Map<String, Object> product = new HashMap<>();

        product.put("seller_id", user_id);
        product.put("product_id", random_product_id);
        product.put("product_img_url", img_url);
        product.put("product_name", product_name);
        product.put("product_type", product_type);
        product.put("product_color", product_color);
        product.put("product_category", product_category);
        product.put("product_size", product_size);
        product.put("product_condition", product_condition);
        product.put("product_price", product_price);
        product.put("product_description", product_description);
        product.put("product_address", product_address);
        product.put("is_available", true);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("test", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("test", "Error adding document", e);
                    }
                });
    }

    //store in algolia database
    private void addToAlgolia() {
        Client client = new Client("OPKL0UNSXG", "f525aa0f60394c3013ef966117e91313");

        Index index = client.initIndex("products");
        try {
            index.addObjectAsync(new JSONObject()
                    .put("objectID", random_product_id)
                    .put("product_img_url", img_url)
                    .put("product_name", product_name)
                    .put("product_type", product_type)
                    .put("product_color", product_color)
                    .put("product_category", product_category)
                    .put("product_size", product_size)
                    .put("product_description", product_description)
                    .put("product_seller", user_id)
                    .put("product_availability", true)
                                                                                                                                       .put("product_price", product_price), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
