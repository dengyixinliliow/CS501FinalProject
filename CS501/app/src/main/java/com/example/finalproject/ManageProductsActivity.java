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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageProductsActivity extends AppCompatActivity {

    //variables for getting currentUser
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private FirebaseFirestore db;
    ArrayList<Product> productList;

    private String user_id;
    private ListView product_list_view;
    private Button manage_product_add_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        product_list_view = (ListView) findViewById(R.id.manage_products_list_view);
        manage_product_add_btn = (Button) findViewById(R.id.manage_products_add_btn);
        product_list_view.setDivider(null);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        fetchSellerProducts();

        manage_product_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
    }

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

class ManageProductListViewAdapter extends ArrayAdapter<Product> {

    private Context cont;
    private String product_id;

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

        return listItemView;
    }
}
