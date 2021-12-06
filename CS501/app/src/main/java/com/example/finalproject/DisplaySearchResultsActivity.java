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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_results);

        Intent intent = getIntent();
        search_result_keyword = intent.getStringExtra("search_keyword");

        search_result_listView = (ListView) findViewById(R.id.search_result_listView);

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

//                    ProductRVAdapter productRVAdapter = new ProductRVAdapter(DisplaySearchResultsActivity.this, productList);
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DisplaySearchResultsActivity.this, LinearLayoutManager.VERTICAL, false);
//
//                    search_result_recycler_view.setLayoutManager(linearLayoutManager);
//                    search_result_recycler_view.setAdapter(productRVAdapter);

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

//interface RecyclerViewClickListener {
//
//    void onClick(View view, int position);
//}

//class ProductRVAdapter extends RecyclerView.Adapter<ProductRVAdapter.Viewholder> {
//
//    private Context context;
//    private ArrayList<Product> productArrayList;
//    private RecyclerViewClickListener listener;
//
//    // Constructor
//    public ProductRVAdapter(Context context, ArrayList<Product> productArrayList) {
//        this.listener = listener;
//        this.context = context;
//        this.productArrayList = productArrayList;
//    }
//
//    @NonNull
//    @Override
//    public ProductRVAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // to inflate the layout for each item of recycler view.
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_products, parent, false);
//        return new Viewholder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProductRVAdapter.Viewholder holder, int position) {
//        // to set data to textview and imageview of each card layout
//        Product model = productArrayList.get(position);
//        holder.productName.setText(model.getProductName());
//        holder.productSize.setText("" + model.getProductSize());
//        Glide.with(context).load(model.getProductImgURL()).into(holder.productImg);
////        holder.productDetailBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent intent = new Intent(context, ProductActivity.class);
////                intent.putExtra("product_id", model.getProductId());
////                context.startActivity(intent);
////            }
////        });
//    }
//
//    @Override
//    public int getItemCount() {
//        // this method is used for showing number
//        // of card items in recycler view.
//        return productArrayList.size();
//    }
//
//    // View holder class for initializing of
//    // your views such as TextView and Imageview.
//    public class Viewholder extends RecyclerView.ViewHolder {
//        private ImageView productImg;
//        private TextView productName, productSize;
//        private Button productDetailBtn;
//
//        public Viewholder(@NonNull View itemView) {
//            super(itemView);
//            productImg = itemView.findViewById(R.id.img);
//            productName = itemView.findViewById(R.id.txt1);
//            productSize = itemView.findViewById(R.id.txt2);
//            productDetailBtn = itemView.findViewById(R.id.product_btnDetail);
//        }
//
//
//    }
//}

