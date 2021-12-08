package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {
    //user_id, review_title,review_body,product_id
    private static String myflag="ReviewActivity";
    private String product_id;
    private String user_id;
    private EditText review_title;
    private EditText review_body;
    private Button submit;
    private Button review_btnBack;
    private ImageView img;
    private String title_string;
    private String body_string;

    private static final String PRODUCT_ID = "product_id";
    private static final String USER_ID = "user_id";
    private static final String REVIEWS="reviews";
    private static final String REVIEW_TITLE="review_title";
    private static final String REVIEW_BODY="review_body";
    private static final String PRODUCTS="products";
    private static final String PRODUCT_IMG_URL = "product_img_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        //connect to database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //get the product id which user are going to write review for from intent
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        product_id=extras.getString(PRODUCT_ID);
        //get current user id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser auth_user = mAuth.getCurrentUser();
        user_id=auth_user.getUid();

        CollectionReference productdb = db.collection(PRODUCTS);

        review_title=(EditText) findViewById(R.id.review_title);
        review_body=(EditText) findViewById(R.id.review_body);
        submit=(Button)findViewById(R.id.review_submit);
        review_btnBack = (Button) findViewById(R.id.review_btnBack);

        review_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        review_title.addTextChangedListener(new TextWatcher() {
            //read the review title input by user
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                title_string=review_title.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        review_body.addTextChangedListener(new TextWatcher() {
            //read the review body input by user
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                body_string=review_body.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //filter product database by product id
        Query query=productdb.whereEqualTo(PRODUCT_ID,product_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //render product information for user
                        Map<String, Object> dataMap = document.getData();
                        String img_url=String.valueOf(dataMap.get(PRODUCT_IMG_URL));
                        Log.i(myflag,img_url);
                        img=(ImageView) findViewById(R.id.review_Img);
                        Glide.with(ReviewActivity.this).load(img_url).into(img);
                    }
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //submit the review and end activity
                submit_review(user_id,product_id,title_string,body_string);
                finish();
            }
        });

    }

    private static void submit_review(String user_id,String product_id,String title,String body){
        //connect to review database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reviewdb = db.collection(REVIEWS);
        //create a hashmap and put information as (field name, value)
        Map<String, Object> review = new HashMap<>();
        review.put(USER_ID,user_id);
        review.put(PRODUCT_ID,product_id);
        review.put(REVIEW_TITLE,title);
        review.put(REVIEW_BODY,body);
        //add new review to database
        reviewdb.add(review).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.i(myflag,"review has been added");
            }
        });
    }
}