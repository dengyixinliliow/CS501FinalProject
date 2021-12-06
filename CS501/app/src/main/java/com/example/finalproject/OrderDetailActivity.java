package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {
    //put seller id as extra to contact
    private String myflag="OrderDetailActivity";
    private TextView order_number,order_price;
    private String o_number,o_price;
    private ArrayList<String> product_ids,seller_ids;
    private ListView lvOrderDetail;
    private ListAdapter lvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        //get all extras passed by previous activity
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        o_number=extras.getString("order_number");
        o_price=extras.getString("order_total");
        product_ids=extras.getStringArrayList("product_ids");
        seller_ids=extras.getStringArrayList("seller_ids");
        //connect textview and list view and set text
        order_number=(TextView) findViewById(R.id.orderDetailNumber);
        order_price=(TextView) findViewById(R.id.orderDetailTotal);
        order_number.setText("Order Number: "+o_number);
        order_price.setText("$"+o_price);

        lvOrderDetail=(ListView)findViewById(R.id.orderDetailLV);
        //connect database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productdb=db.collection("products");
        CollectionReference userdb=db.collection("users");
        for(String pid:product_ids){
            Query query=productdb.whereEqualTo("product_id",pid);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> dataMap = document.getData();
                            String p_name=String.valueOf(dataMap.get("product_name"));
                            String p_price=String.valueOf(dataMap.get("product_price"));
                            String p_size=String.valueOf(dataMap.get("product_size"));
                            String url= String.valueOf(dataMap.get("product_img_url"));
                            String seller_id=String.valueOf(dataMap.get("seller_id"));

                        }
                    }
                }
            });
        }

    }
}

class OrderDetail_item{
    private
    String name;
    String pid;
    String price;
    String imgurl;
    String seller_id;
    String seller_name;
    public OrderDetail_item(String n,String p,String productid,String url, String seller_id,String seller_name){
        this.name=n;
        this.price=p;
        this.pid=productid;
        this.imgurl=url;
        this.seller_id=seller_id;
        this.seller_name=seller_name;
    }

    public String get_name(){return name;}
    public String get_price(){return price;}
    public String get_productid(){return pid;}
    public String get_img(){return imgurl;}
    public String get_seller_id(){return seller_id;}
    public String get_seller_name(){return seller_name;}
}