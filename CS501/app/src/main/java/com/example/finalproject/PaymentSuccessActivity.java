package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PaymentSuccessActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {

    private static final String TAG = "EmailPassword";

    private final Double ZERO = 0.00;
    private final long ZERO1 = 0;

    private FirebaseAuth mAuth;
    FirebaseUser auth_user;
    private String user_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private long payment_success_order_time;
    private Double payment_success_order_total;
    private String payment_success_order_id;
    private String payment_success_owner_id;
    private ArrayList<String> getPayment_success_seller_id;
    private ArrayList<String> payment_success_items_list;
    private Map map=new HashMap();
    private String product_owner = "123";

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        //Get info passed from payment activity
        Intent intent = getIntent();
        payment_success_order_time = intent.getLongExtra("order_time", ZERO1);
        payment_success_order_total = intent.getDoubleExtra("order_total", ZERO);
        payment_success_order_id = intent.getStringExtra("order_id");
        payment_success_items_list = intent.getStringArrayListExtra("products_list");
        getPayment_success_seller_id = intent.getStringArrayListExtra("products_seller_list");

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        // connect to database
        Log.i("test",String.valueOf(getPayment_success_seller_id.size()));
        Log.i("test",String.valueOf(getPayment_success_seller_id.toString()));
        Log.i("test",String.valueOf(payment_success_items_list.size()));
        Log.i("test",String.valueOf(payment_success_items_list.toString()));
        if (getPayment_success_seller_id.size() == payment_success_items_list.size()) {
            for (int i = 0; i < payment_success_items_list.size(); i++) {
                String cur_product_id = payment_success_items_list.get(i);
                String cur_seller_id = getPayment_success_seller_id.get(i);
                map.put(cur_product_id, cur_seller_id);

                // update product status and add product renter id
                updateProduct(cur_product_id);
            }
        }

        addOrder(map);
        addMessage(map);
    }

    private void updateProduct(String product_id) {
        db.collection("products")
                .whereEqualTo("product_id", product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update(IS_AVAILABLE, false);
                                document.getReference().update("renter_id", user_id);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addOrder (Map map) {
        Map<String, Object> order = new HashMap<>();
        order.put("order_id", payment_success_order_id);
        order.put("order_total", payment_success_order_total);
        order.put("order_time", payment_success_order_time);
        order.put("order_owner", payment_success_owner_id);
        order.put("order_items", map);

        db.collection("orders").add(order)
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

    private void addMessage (Map map) {
        Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            Map<String, Object> message = new HashMap<>();
            message.put("seller_id", entry.getValue());
            message.put("product_id", entry.getKey());
            message.put("renter_id", payment_success_owner_id);
            message.put("type", "order placed");

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
    }


    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}
