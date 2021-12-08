package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.algolia.search.saas.AbstractQuery;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageProductsActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    //variables for getting currentUser
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private FirebaseFirestore db;
    ArrayList<Product> productList;

    private String user_id;
    private ListView product_list_view;
    private Button manage_product_add_btn;
    private ImageView return_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);
        return_icon=(ImageView)findViewById(R.id.manage_product_return);
        product_list_view = (ListView) findViewById(R.id.manage_products_list_view);
        manage_product_add_btn = (Button) findViewById(R.id.manage_products_add_btn);
        product_list_view.setDivider(null);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        fetchSellerProducts();

        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        manage_product_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
    }

    //get all products listed by the seller
    private void fetchSellerProducts() {
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef.whereEqualTo("seller_id", user_id);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        Log.d("MissionActivity", document.getId() + " => " + dataMap);
                        Product product = new Product(
                                String.valueOf(dataMap.get("product_name")),
                                String.valueOf(dataMap.get("product_size")),
                                String.valueOf(dataMap.get("product_price")),
                                String.valueOf(dataMap.get("product_id")),
                                String.valueOf(dataMap.get("product_img_url"))
                        );
                        productList.add(product);
                    }

                    ManageProductListViewAdapter adapter = new ManageProductListViewAdapter(ManageProductsActivity.this, productList);
                    product_list_view.setAdapter(adapter);

                } else {
                    Log.e("test", "Error getting documents: ", task.getException());
                }
            }
        });
    }

}

//list view adapter
class ManageProductListViewAdapter extends ArrayAdapter<Product> {
    private static final String TAG = "EmailPassword";

    private Context cont;
    private String product_id;

    // Firebase data
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ManageProductListViewAdapter(@NonNull Context context, List<Product> productsArrayList) {
        super(context, 0, productsArrayList);
        cont = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_manage_productrow, parent, false);
        }

        // Get the User ID
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        ImageView mproduct_img = (ImageView) listItemView.findViewById(R.id.managep_img);
        TextView mproduct_name = (TextView) listItemView.findViewById(R.id.managep_pname);
        Button mproduct_btnDetail = (Button) listItemView.findViewById(R.id.managep_detailBtn);
        Button mproduct_btnStatus = (Button) listItemView.findViewById(R.id.managep_statusBtn);
        Button mproduct_btnDelete = (Button) listItemView.findViewById(R.id.managep_deleteBtn);

        Product product = (Product) getItem(position);

        mproduct_name.setText(product.getProductName());
        Glide.with(cont).load(product.getProductImgURL()).into(mproduct_img);

        mproduct_btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_id = product.getProductId();
                Intent intent = new Intent(cont, ProductActivity.class);
                intent.putExtra("product_id", product_id);
                intent.putExtra("action_taker", "owner");
                cont.startActivity(intent);
                //pass id as the intent
                Log.e("test", "clicked");
            }
        });

        mproduct_btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_id = product.getProductId();
                Intent intent = new Intent(cont, ProductStatusActivity.class);
                intent.putExtra("product_id", product_id);
                intent.putExtra("product_name", product.getProductName());
                cont.startActivity(intent);
            }
        });

        mproduct_btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFromAlgolia(product.getProductId());

                deleteFromFirestore(product.getProductId());


            }

            //delete the data from search api
            private void deleteFromAlgolia(String product_id) {
                Client client = new Client("OPKL0UNSXG", "f525aa0f60394c3013ef966117e91313");
                Index index = client.initIndex("products");

                CompletionHandler completeHandler = new CompletionHandler() {
                    @Override
                    public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                        Log.e("test", "deleted successfully");
                    }
                };

//                com.algolia.search.saas.Query query = new com.algolia.search.saas.Query().setFilters("product_id:4bfcb520-206e-4351-b7d9-8f2d845e7e8e")
//                        .setAroundLatLng(new AbstractQuery.LatLng(40.71, -74.01));
                index.deleteObjectAsync(product_id, completeHandler);
            }
        });

        return listItemView;
    }

    //delete from firestore database
    public void deleteFromFirestore(String product_id) {
        // [START get_multiple]
        db.collection("products")
                .whereEqualTo("product_id", product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // delete the current product
                                document.getReference().delete();

                                // Move to Profile page
                                Intent intent = new Intent(cont, ProfileActivity.class);
                                cont.startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}
