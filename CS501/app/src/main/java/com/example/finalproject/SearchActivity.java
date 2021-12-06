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
import android.widget.ImageButton;
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

public class SearchActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {
    private static final String TAG = "EmailPassword";

    private ImageButton search_btnMen;
    private ImageButton search_btnWomen;
    private ImageButton search_btnKids;
    private Button search_btnjackets_coats;
    private Button search_btn_onepieces_dresses;
    private Button search_btn_shirts;
    private Button search_btn_tops_tshirts;
    private Button search_btn_hoodies_sweatshirts;
    private Button search_btn_jeans_pants;
    private Button search_btn_shoes;
    private Button search_btn_bags;
    private Button search_btn_accessories;
    private Button search_btnMap;

    private boolean women;
    private boolean men;
    private boolean kids;

    private EditText search_search_box;
    private ImageButton search_search_btn;

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

        search_btnMen = (ImageButton) findViewById(R.id.search_btnMen);
        search_btnWomen = (ImageButton) findViewById(R.id.search_btnWomen);
        search_btnKids = (ImageButton) findViewById(R.id.search_btnKids);

        search_btnjackets_coats = (Button) findViewById(R.id.search_btn_jackets_coats);
        search_btn_onepieces_dresses = (Button) findViewById(R.id.search_btn_onepieces_dresses);
        search_btn_shirts = (Button) findViewById(R.id.search_btn_shirts);
        search_btn_tops_tshirts = (Button) findViewById(R.id.search_btn_tops_tshirts);
        search_btn_hoodies_sweatshirts = (Button) findViewById(R.id.search_btn_hoodies_sweatshirts);
        search_btn_jeans_pants = (Button) findViewById(R.id.search_btn_jeans_pants);
        search_btn_shoes = (Button) findViewById(R.id.search_btn_shoes);
        search_btn_bags = (Button) findViewById(R.id.search_btn_bags);
        search_btn_accessories = (Button) findViewById(R.id.search_btn_accessories);

        search_btnMap = (Button) findViewById(R.id.search_btnMap);

        search_search_box = (EditText) findViewById(R.id.search_search_box);
        search_search_btn = (ImageButton) findViewById(R.id.search_search_btn);

        women = false;
        men = false;
        kids = false;

        search_btnMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (men == true) {
                    men = false;
                    women = false;
                    kids = false;
                } else {
                    men = true;
                    women = false;
                    kids = false;
                }

                if (men == true && women == false && kids == false) {
                    search_btnMen.setImageResource(R.drawable.men_selected);
                    search_btnWomen.setImageResource(R.drawable.women_icon);
                    search_btnKids.setImageResource(R.drawable.baby_icon);
                    category_to_filter = "men";
                } else {
                    search_btnMen.setImageResource(R.drawable.men_icon);
                    search_btnWomen.setImageResource(R.drawable.women_icon);
                    search_btnKids.setImageResource(R.drawable.baby_icon);
                    category_to_filter = "none";
                }

            }
        });

        search_btnWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (women == true) {
                    women = false;
                    men = false;
                    kids = false;
                } else {
                    women = true;
                    men = false;
                    kids = false;
                }

                if (women == true && !men && !kids) {
                    search_btnWomen.setImageResource(R.drawable.women_selected);
                    search_btnMen.setImageResource(R.drawable.men_icon);
                    search_btnKids.setImageResource(R.drawable.baby_icon);
                    category_to_filter = "women";
                } else {
                    search_btnWomen.setImageResource(R.drawable.women_icon);
                    search_btnMen.setImageResource(R.drawable.men_icon);
                    search_btnKids.setImageResource(R.drawable.baby_icon);
                    category_to_filter = "none";
                }


            }
        });

        search_btnKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (kids == true) {
                    kids = false;
                    women = false;
                    men = false;
                } else {
                    kids = true;
                    women = false;
                    men = false;
                }

                if (kids == true) {
                    search_btnKids.setImageResource(R.drawable.baby_selected);
                    search_btnWomen.setImageResource(R.drawable.women_icon);
                    search_btnMen.setImageResource(R.drawable.men_icon);
                    category_to_filter = "kids";
                } else {
                    search_btnKids.setImageResource(R.drawable.baby_icon);
                    search_btnWomen.setImageResource(R.drawable.women_icon);
                    search_btnMen.setImageResource(R.drawable.men_icon);
                    category_to_filter = "none";
                }
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

        search_btn_accessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, DisplayFilterResultsActivity.class);
                intent.putExtra("category", category_to_filter);
                intent.putExtra("type", "accessories");
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

        search_btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}