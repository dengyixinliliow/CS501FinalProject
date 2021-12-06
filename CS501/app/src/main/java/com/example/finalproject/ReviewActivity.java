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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        product_id=extras.getString("product_id");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser auth_user = mAuth.getCurrentUser();
        user_id=auth_user.getUid();

        CollectionReference productdb = db.collection("products");

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
        //render product detail
        Query query=productdb.whereEqualTo("product_id",product_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        String img_url=String.valueOf(dataMap.get("product_img_url"));
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
                submit_review(user_id,product_id,title_string,body_string);
                finish();
            }
        });

    }

    private static void submit_review(String user_id,String product_id,String title,String body){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reviewdb = db.collection("reviews");
        Map<String, Object> review = new HashMap<>();
        review.put("user_id",user_id);
        review.put("product_id",product_id);
        review.put("review_title",title);
        review.put("review_body",body);
        reviewdb.add(review).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.i(myflag,"review has been added");
            }
        });
    }
}