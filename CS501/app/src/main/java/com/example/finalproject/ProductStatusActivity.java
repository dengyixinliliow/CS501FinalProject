package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProductStatusActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private TextView pstatus_status;
    private TextView pstatus_pname;
    private Button pstatus_contact;
    private Button pstatus_receive;

    private String pid;
    private String pname;
    private Boolean cur_user_status;

    public static final String USER_ID = "user_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_TYPE = "product_type";
    public static final String PRODUCT_SIZE = "product_size";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String PRODUCT_COLOR = "product_color";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String PRODUCT_CONDITION = "product_condition";
    public static final String PRODUCT_DESCRIPTION = "product_description";
    public static final String PRODUCT_IMG_URL = "product_img_url";
    public static final String PRODUCT_IS_AVAILABLE = "product_is_available";
    public static final String SELLER_ID = "seller_id";
    public static final String SELLER_USERNAME = "seller_username";
    public static final String USERNAME = "username";
    public static final String FIRST_MESSAGE = "Hello!";
    public static final String IS_AVAILABLE = "is_available";
    private ImageView return_icon;

    private FirebaseAuth mAuth;
    FirebaseUser auth_user;
    private String user_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_status);

        return_icon=(ImageView)findViewById(R.id.pstatus_return);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        Intent intent = getIntent();
        pid = intent.getStringExtra("product_id");
        pname = intent.getStringExtra("product_name");


        pstatus_status = (TextView) findViewById(R.id.pstatus_pstatus);
        pstatus_pname = (TextView) findViewById(R.id.pstatus_pname);
        pstatus_contact = (Button) findViewById(R.id.pstatus_contact);
        pstatus_receive = (Button) findViewById(R.id.pstatus_receive);

        getProductById(pid);

        pstatus_pname.setText(pname);

        pstatus_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductStatus(pid);
                addMessage(pid);
                try {
                    updateProductStatusAlgolia(pid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                                Map<String, Object> cur_user = document.getData();

                                cur_user_status = (Boolean) cur_user.get(IS_AVAILABLE);

                                if (cur_user_status) {
                                    pstatus_status.setText("Available");

                                    pstatus_contact.setVisibility(View.GONE);
                                    pstatus_receive.setVisibility(View.GONE);
                                } else {
                                    pstatus_status.setText("Unavailable");
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    public void updateProductStatus(String product_id) {
        // [START get_multiple]
        db.collection("products")
            .whereEqualTo("product_id", product_id)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().update(IS_AVAILABLE, true);
                            getProductById(pid);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    private void updateProductStatusAlgolia(String product_id) throws JSONException {
        Client client = new Client("OPKL0UNSXG", "f525aa0f60394c3013ef966117e91313");
        Index index = client.initIndex("products");

        CompletionHandler completeHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                Log.e("test", "test");
            }
        };

        index.partialUpdateObjectAsync(
                new JSONObject("{\"product_availability\": true }"),
                product_id,
                completeHandler
        );
    }



    private void addMessage (String product_id) {
        // get renter_id
        // [START get_multiple]
        db.collection("products")
                .whereEqualTo("product_id", product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> cur_product = document.getData();

                                // set renter_id
                                Object product_obj = cur_product.get("renter_id");
                                String cur_product_renter_id;
                                if(product_obj == null) {
                                    cur_product_renter_id = "null";
                                } else {
                                    cur_product_renter_id = product_obj.toString();
                                }

                                Map<String, Object> message = new HashMap<String, Object>();

                                message.put("seller_id", user_id);
                                message.put("product_id", pid);
                                message.put("renter_id", cur_product_renter_id);
                                message.put("type", "product returned");

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
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}
