package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private Button inbox_btnSearch;
    private Button inbox_btnInbox;
    private Button inbox_btnOrders;
    private Button inbox_btnProfile;

    private EditText contact_edtInput;
    private Button contact_btnSend;

    public static final String USER_ID = "user_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_TYPE = "product_type";
    public static final String PRODUCT_SIZE = "product_size";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String PRODUCT_COLOR = "product_color";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String PRODUCT_CONDITION = "product_condition";
    public static final String PRODUCT_IMG_URL = "product_img_url";
    public static final String PRODUCT_IS_AVAILABLE = "product_is_available";
    public static final String SELLER_ID = "seller_id";
    public static final String USERNAME = "username";

    private String seller_id;

    private String message;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        contact_edtInput = (EditText) findViewById(R.id.contact_edtInput);
        contact_btnSend = (Button) findViewById(R.id.contact_btnSend);

        Intent intent = getIntent();
        seller_id = intent.getStringExtra(SELLER_ID);

        contact_btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = contact_edtInput.getText().toString();
                if(message.isEmpty()) {
                    return;
                }

                getUserById(seller_id);
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
                                Map<String, Object> cur_user = document.getData();

                                String cur_user_email = cur_user.get("email").toString();

//                                // send email
//                                // 必须明确使用mailto前缀来修饰邮件地址,如果使用   intent.putExtra(Intent.EXTRA_EMAIL, email)，结果将匹配不到任何应用
//                                Uri uri = Uri.parse("mailto:" + cur_user_email);
//                                String[] email = {cur_user_email};
//                                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
////                                intent.putExtra(Intent.EXTRA_CC, email); // cc
//                                intent.putExtra(Intent.EXTRA_SUBJECT, "topic"); // topic
//                                intent.putExtra(Intent.EXTRA_TEXT, message); // content
//                                startActivity(Intent.createChooser(intent, "choose email app"));

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}