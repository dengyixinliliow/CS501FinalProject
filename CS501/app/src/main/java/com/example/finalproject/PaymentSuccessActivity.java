package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PaymentSuccessActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {

    private static final String TAG = "PAYMENT_SUCCESS";

    private final Double ZERO = 0.00;
    private final long ZERO1 = 0;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private long payment_success_order_time;
    private Double payment_success_order_total;
    private String payment_success_order_id;
    private String payment_success_owner_id;
    private ArrayList<String> getPayment_success_seller_id;
    private ArrayList<String> payment_success_items_list;
    private Map map=new HashMap();

    private Button to_orderdetail;

    private static final String IS_AVAILABLE = "is_available";
    private static final String ORDER_NUMBER = "order_number";
    private static final String ORDER_TOTAL = "order_total";
    private static final String PRODUCT_IDS = "product_ids";
    private static final String SELLER_IDS = "seller_ids";

    private static final String CARTS = "carts";
    private static final String USER_ID = "user_id";
    private static final String PRODUCTS = "products";
    private static final String PRODUCT_ID = "product_id";
    private static final String RENTER_ID = "renter_id";

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
        payment_success_owner_id = user_id;

        to_orderdetail = (Button) findViewById(R.id.payment_success_btn);

        // connect to database
        if (getPayment_success_seller_id.size() == payment_success_items_list.size()) {
            for (int i = 0; i < payment_success_items_list.size(); i++) {
                String cur_product_id = payment_success_items_list.get(i);
                String cur_seller_id = getPayment_success_seller_id.get(i);
                map.put(cur_product_id, cur_seller_id);

                // update product status and add product renter id
                updateProduct(cur_product_id);

                // update the algolia database product status
                try {
                    updateAlgoliaProduct(cur_product_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // add order to order database
        addOrder(map);

        // clear user's shopping bag after checkout
        clearCart(user_id);

        // Move to order detail page
        to_orderdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to_od=new Intent(getBaseContext(),OrderDetailActivity.class);
                to_od.putExtra(ORDER_NUMBER, payment_success_order_id);
                to_od.putExtra(ORDER_TOTAL, payment_success_order_total.toString());
                to_od.putExtra(PRODUCT_IDS, payment_success_items_list);
                to_od.putExtra(SELLER_IDS, getPayment_success_seller_id);
                startActivity(to_od);
            }
        });
    }

    /*
        Clear cart that with the same user id
     */
    private void clearCart(String user_id) {
        db.collection(CARTS)
                .whereEqualTo(USER_ID, user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /*
        Update product status
     */
    private void updateProduct(String product_id) {
        db.collection(PRODUCTS)
                .whereEqualTo(PRODUCT_ID, product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update(IS_AVAILABLE, false);
                                document.getReference().update(RENTER_ID, user_id);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /*
        Update status in algolia database
     */
    private void updateAlgoliaProduct(String product_id) throws JSONException {
        Client client = new Client("OPKL0UNSXG", "f525aa0f60394c3013ef966117e91313");
        Index index = client.initIndex("products");

        CompletionHandler completeHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                Log.e(TAG, "test");
            }
        };

        index.partialUpdateObjectAsync(
                new JSONObject("{\"product_availability\": false }"),
                product_id,
                completeHandler
        );
    }

    //add order to order database
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
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
            }
        });
    }

    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}
