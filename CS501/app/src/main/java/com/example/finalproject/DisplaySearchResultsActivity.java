package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplaySearchResultsActivity extends AppCompatActivity {
    // Algolia
    Client client = new Client("OPKL0UNSXG", "f525aa0f60394c3013ef966117e91313");
    Index index = client.initIndex("products");

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String search_result_keyword;
    private String user_id;
    private String search_result_product_name;
    private String search_result_product_size;
    private String search_result_product_price;
    private String search_result_product_id;
    private String search_result_product_seller;
    private String search_result_product_img_url;
    private ListView search_result_listView;
    private boolean search_result_availability;
    private ImageView return_icon;
    private TextView search_result_title;
    private TextView search_result_intro;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_results);

        Intent intent = getIntent();
        search_result_keyword = intent.getStringExtra("search_keyword");

        search_result_listView = (ListView) findViewById(R.id.search_result_listView);
        search_result_title = (TextView) findViewById(R.id.search_result_title);
        search_result_title.setText(getResources().getString(R.string.keyword) + search_result_keyword);
        search_result_intro = (TextView) findViewById(R.id.search_introduction);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        return_icon=(ImageView)findViewById(R.id.search_result_return);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //process the results from the search api
        CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try{
                    ArrayList<Product> productList = new ArrayList<Product>();
                    JSONArray hits = content.getJSONArray("hits");
                    for (int i = 0; i < hits.length(); i++) {
                        JSONObject jsonObject = hits.getJSONObject(i);
                        search_result_availability = jsonObject.getBoolean("product_availability");
                        search_result_product_name = jsonObject.getString("product_name");
                        search_result_product_size = jsonObject.getString("product_size");
                        search_result_product_price = jsonObject.getString("product_price");
                        search_result_product_id = jsonObject.getString("objectID");
                        search_result_product_img_url = jsonObject.getString("product_img_url");
                        search_result_product_seller = jsonObject.getString("product_seller");

                        if (search_result_availability && !user_id.equals(search_result_product_seller)) {
                            Product product = new Product(
                                    search_result_product_name,
                                    search_result_product_size,
                                    search_result_product_price,
                                    search_result_product_id,
                                    search_result_product_img_url
                            );

                            productList.add(product);
                        }
                    }

                    if (productList.size() == 0) {
                        search_result_intro.setText(getResources().getString(R.string.wrong_search));
                    }

                    ProductsLVAdapter productsLVAdapter = new ProductsLVAdapter(DisplaySearchResultsActivity.this, productList);
                    search_result_listView.setAdapter(productsLVAdapter);


                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        index.searchAsync(new com.algolia.search.saas.Query(search_result_keyword), completionHandler);


    }
}

