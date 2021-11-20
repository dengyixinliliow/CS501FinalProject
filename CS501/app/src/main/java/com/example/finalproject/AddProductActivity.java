package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.admin.v1.Index;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    final String TAG = "test";

    private EditText sellerId;
    private EditText productName;
    private EditText renterId;
    private EditText tags;
    private EditText size;
    private EditText price;
    private EditText rentDate;
    private EditText returnDate;
    private Button submit;
    private Button listP;

    private int sellerIdNum, renterIdNum, priceNum;
    private String tagsStr, productNameStr, sizeStr, rentDateStr, returnDateStr, productIdNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        sellerId = (EditText) findViewById(R.id.sellerIDEdit);
        productName = (EditText) findViewById(R.id.productIDEdit);
        renterId = (EditText) findViewById(R.id.renterIDEdit);
        tags = (EditText) findViewById(R.id.tagsEdit);
        size = (EditText) findViewById(R.id.sizeEdit);
        price = (EditText) findViewById(R.id.priceEdit);
        returnDate = (EditText) findViewById(R.id.returnDate);
        rentDate = (EditText) findViewById(R.id.rentDate);
        submit = (Button) findViewById(R.id.button);
        listP = (Button) findViewById(R.id.button2);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
                addProduct(db);
            }
        });

//        listP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent GotoActivity2 = new Intent(getApplicationContext(), MainActivity2.class);
//                startActivity(GotoActivity2);
//            }
//        });
    }

    //This function will get the data from screen
    private void getData() {
        sellerIdNum = Integer.parseInt(sellerId.getText().toString());
        renterIdNum = Integer.parseInt(renterId.getText().toString());
        priceNum = Integer.parseInt(price.getText().toString());

        sizeStr = size.getText().toString();
        tagsStr = tags.getText().toString();
        rentDateStr = rentDate.getText().toString();
        returnDateStr = returnDate.getText().toString();
        productNameStr = productName.getText().toString();
    }

    //add the product object to firebase database
    private void addProduct (FirebaseFirestore db) {
        Map<String, Object> product = new HashMap<>();
        product.put("product_name", productNameStr );
        product.put("seller_id", sellerIdNum);
        product.put("size", sizeStr);
        product.put("price", priceNum);
        product.put("tags", tagsStr);
        product.put("renter_id", renterIdNum);
        product.put("rent_date", rentDateStr);
        product.put("return_date", returnDateStr);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        productIdNum = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
