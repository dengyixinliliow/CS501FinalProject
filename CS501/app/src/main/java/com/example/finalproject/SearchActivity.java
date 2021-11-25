package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private TextView search_txtUsername;

    private Button search_btnSearch;
    private Button search_btnInbox;
    private Button search_btnOrders;
    private Button search_btnProfile;
    private Button search_btnMen;
    private Button search_btnWomen;
    private Button search_btnKids;
    private Button search_btnjackets_coats;
    private Button search_btn_onepieces_dresses;
    private Button search_btn_shirts;
    private Button search_btn_tops_tshirts;
    private Button search_btn_hoodies_sweatshirts;
    private Button search_btn_jeans_pants;
    private Button search_btn_shoes;
    private Button search_btn_bags;
    private Button search_btnCart;

    private EditText search_search_box;
    private Button search_search_btn;

    private Map<String, Object> current_user;
    public static final String USERNAME = "username";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    private String category_to_filter = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        search_btnCart = (Button)findViewById(R.id.search_btnCart);
        search_btnMen = (Button) findViewById(R.id.search_btnMen);
        search_btnWomen = (Button) findViewById(R.id.search_btnWomen);
        search_btnKids = (Button) findViewById(R.id.search_btnKids);

        search_btnjackets_coats = (Button) findViewById(R.id.search_btn_jackets_coats);
        search_btn_onepieces_dresses = (Button) findViewById(R.id.search_btn_onepieces_dresses);
        search_btn_shirts = (Button) findViewById(R.id.search_btn_shirts);
        search_btn_tops_tshirts = (Button) findViewById(R.id.search_btn_tops_tshirts);
        search_btn_hoodies_sweatshirts = (Button) findViewById(R.id.search_btn_hoodies_sweatshirts);
        search_btn_jeans_pants = (Button) findViewById(R.id.search_btn_jeans_pants);
        search_btn_shoes = (Button) findViewById(R.id.search_btn_shoes);
        search_btn_bags = (Button) findViewById(R.id.search_btn_bags);

        search_btnSearch = (Button) findViewById(R.id.search_btnSearch);
        search_btnInbox = (Button) findViewById(R.id.search_btnInbox);
        search_btnOrders = (Button) findViewById(R.id.search_btnOrders);
        search_btnProfile = (Button) findViewById(R.id.search_btnProfile);

        search_search_box = (EditText) findViewById(R.id.search_search_box);
        search_search_btn = (Button) findViewById(R.id.search_search_btn);

        search_txtUsername = (TextView) findViewById(R.id.search_txtUsername);

        getUserById(user_id);

        search_btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        search_btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to inbox page
                Intent intent = new Intent(getBaseContext(), InboxActivity.class);
                startActivity(intent);
            }
        });
        search_btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to orders page
                Intent intent = new Intent(getBaseContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        search_btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to personalInfo page
                Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
        search_btnMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_btnMen.setBackgroundColor(0xFFFFC0CB);
                search_btnWomen.setBackgroundColor(0xFF6200EE);
                search_btnKids.setBackgroundColor(0xFF6200EE);
                category_to_filter = "men";
            }
        });

        search_btnWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_btnWomen.setBackgroundColor(0xFFFFC0CB);
                search_btnMen.setBackgroundColor(0xFF6200EE);
                search_btnKids.setBackgroundColor(0xFF6200EE);
                category_to_filter = "women";
            }
        });

        search_btnKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_btnKids.setBackgroundColor(0xFFFFC0CB);
                search_btnMen.setBackgroundColor(0xFF6200EE);
                search_btnWomen.setBackgroundColor(0xFF6200EE);
                category_to_filter = "kids";
            }
        });

        search_btnjackets_coats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "jackets & coats");
                startActivity(intent);
            }
        });

        search_btn_onepieces_dresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "one pieces & dresses");
                startActivity(intent);
            }
        });

        search_btn_shirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "shirts");
                startActivity(intent);
            }
        });

        search_btn_tops_tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "tops & t-shirts");
                startActivity(intent);
            }
        });

        search_btn_hoodies_sweatshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "hoodies & sweatshirts");
                startActivity(intent);
            }
        });

        search_btn_jeans_pants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "jeans & pants");
                startActivity(intent);
            }
        });

        search_btn_shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "shoes");
                startActivity(intent);
            }
        });

        search_btn_bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "bags");
                startActivity(intent);
            }
        });

        search_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(search_search_box.getText().toString())) {
                    Toast.makeText(SearchActivity.this, "empty search is not allowed",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(SearchActivity.this, DisplaySearchResultsActivity.class);
                    intent.putExtra("search_keyword", search_search_box.getText().toString());
                    startActivity(intent);
                }
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
                                current_user = document.getData();  // store info of the current user
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                search_txtUsername.setText("Welcome " + current_user.get(USERNAME).toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}