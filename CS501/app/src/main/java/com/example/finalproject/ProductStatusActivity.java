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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProductStatusActivity extends AppCompatActivity {

    private static final String TAG = "PRODUCT_STATUS_PAGE";

    private TextView pstatus_txtRenter;
    private TextView pstatus_txtRenterName;
    private TextView pstatus_status;
    private TextView pstatus_pname;
    private Button pstatus_contact;
    private Button pstatus_receive;

    private ImageView return_icon;

    private String pid;
    private String pname;
    private Boolean cur_user_status;

    private static final String USERS = "users";
    private static final String USER_ID = "user_id";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_NAME = "product_name";
    private static final String SELLER_ID = "seller_id";
    private static final String RENTER_ID = "renter_id";
    private static final String USERNAME = "username";
    private static final String IS_AVAILABLE = "is_available";
    private static final String PRODUCTS = "products";
    private static final String AVAILABLE = "Available";

    // firebase variables
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
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
        pid = intent.getStringExtra(PRODUCT_ID);
        pname = intent.getStringExtra(PRODUCT_NAME);

        pstatus_txtRenter = (TextView) findViewById(R.id.pstatus_txtRenter);
        pstatus_txtRenterName = (TextView) findViewById(R.id.pstatus_txtRenterName);
        pstatus_status = (TextView) findViewById(R.id.pstatus_pstatus);
        pstatus_pname = (TextView) findViewById(R.id.pstatus_pname);
        pstatus_contact = (Button) findViewById(R.id.pstatus_contact);
        pstatus_receive = (Button) findViewById(R.id.pstatus_receive);

        // Get the renter username by the product id
        getProductById(pid);

        pstatus_pname.setText(pname);

        pstatus_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // [START get_multiple]
                db.collection(PRODUCTS)
                    .whereEqualTo(PRODUCT_ID, pid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // store info of the current user
                                    Map<String, Object> cur_product = document.getData();

                                    String cur_renter_id = cur_product.get(RENTER_ID).toString();

                                    // Move to Contact page
                                    Intent intent = new Intent(getBaseContext(), ContactActivity.class);
                                    intent.putExtra(SELLER_ID, cur_renter_id);
                                    startActivity(intent);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                // [END get_multiple]
            }
        });

        pstatus_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductStatus(pid);
                try {
                    updateProductStatusAlgolia(pid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setRenterName(String product_id) {
        // [START get_multiple]
        db.collection(PRODUCTS)
                .whereEqualTo(PRODUCT_ID, product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                Map<String, Object> product = document.getData();
                                String renter_id = product.get(RENTER_ID).toString();

                                // get and set the renter's username based on the renter_id
                                getUserById(renter_id);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    public void getUserById(String user_id) {
        // [START get_multiple]
        db.collection(USERS)
                .whereEqualTo(USER_ID, user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                Map<String, Object> current_user = document.getData();
                                pstatus_txtRenterName.setText(current_user.get(USERNAME).toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    public void getProductById(String product_id) {
        // [START get_multiple]
        db.collection(PRODUCTS)
                .whereEqualTo(PRODUCT_ID, product_id)
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
                                    pstatus_status.setText(AVAILABLE);

                                    pstatus_contact.setVisibility(View.GONE);
                                    pstatus_receive.setVisibility(View.GONE);

                                    pstatus_txtRenter.setVisibility(View.GONE);
                                    pstatus_txtRenterName.setVisibility(View.GONE);
                                } else {
                                    pstatus_txtRenter.setVisibility(View.VISIBLE);
                                    pstatus_txtRenterName.setVisibility(View.VISIBLE);
                                    pstatus_status.setText("Unavailable");
                                    setRenterName(pid);
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
        db.collection(PRODUCTS)
            .whereEqualTo(PRODUCT_ID, product_id)
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
                Log.e(TAG, "test");
            }
        };

        index.partialUpdateObjectAsync(
                new JSONObject("{\"product_availability\": true }"),
                product_id,
                completeHandler
        );
    }
}
