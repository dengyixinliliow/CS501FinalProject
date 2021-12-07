package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private ArrayList<OrderDetail_item> odItemlist=new ArrayList<>();
    private ListView lvOrderDetail;
    private ListAdapter lvAdapter;
    private ImageView return_icon;

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
        order_number.setText(o_number);
        order_price.setText("$"+o_price);
        //clickable return img
        return_icon=(ImageView)findViewById(R.id.orderDetailReturn);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),OrdersActivity.class);
                startActivity(intent);
            }
        });

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
                            Log.i(myflag,p_name);
                            Query uquery=userdb.whereEqualTo("user_id",seller_id);
                            uquery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> udataMap = document.getData();
                                            String user_name=String.valueOf(udataMap.get("username"));
                                            OrderDetail_item odItem=new OrderDetail_item(p_name,p_price,pid,url,seller_id,user_name,p_size);
                                            odItemlist.add(odItem);
                                        }
//                                        Log.i(myflag,String.valueOf(odItemlist.size()));
                                        lvAdapter=new OrderDetailAdapter(OrderDetailActivity.this,odItemlist);
                                        lvOrderDetail.setAdapter(lvAdapter);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }

    }
}

class OrderDetail_item{
    private String name;
    private String pid;
    private String price;
    private String imgurl;
    private String seller_id;
    private String seller_name;
    private String size;
    public OrderDetail_item(String name,String price,String productid,String url, String seller_id,String seller_name,String size){
        this.name=name;
        this.price=price;
        this.pid=productid;
        this.imgurl=url;
        this.seller_id=seller_id;
        this.seller_name=seller_name;
        this.size=size;
    }

    public String get_name(){return name;}
    public String get_price(){return price;}
    public String get_productid(){return pid;}
    public String get_img(){return imgurl;}
    public String get_seller_id(){return seller_id;}
    public String get_seller_name(){return seller_name;}
    public String get_size(){return size;}
}

class OrderDetailAdapter extends BaseAdapter {
    private String myflag="OrderDetailAdapter";
    private Context context;
    private ArrayList<OrderDetail_item> odItemlist=new ArrayList<>();

    public OrderDetailAdapter(Context acontext, ArrayList<OrderDetail_item> list){
        this.context=acontext;
        this.odItemlist=list;
        Log.i(myflag,String.valueOf(odItemlist.size()));
    }

    @Override
    public int getCount() {
        return odItemlist.size();
    }

    @Override
    public Object getItem(int i) {
        return odItemlist.get(i).get_name();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View row, ViewGroup viewGroup) {
        if (row == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.activity_order_detail_card, viewGroup, false);
        }
        OrderDetail_item od=odItemlist.get(i);
        TextView odtvname=row.findViewById(R.id.orderDetailRowName);
        odtvname.setText(od.get_name());
        TextView odtvsize=row.findViewById(R.id.orderDetailRowSize);
        odtvsize.setText("Size: "+od.get_size());
        TextView odtvprice=row.findViewById(R.id.orderDetailRowPrice);
        odtvprice.setText("$"+od.get_price());
        TextView odtvseller=row.findViewById(R.id.orderDetailRowSeller);
        odtvseller.setText("Sell by: "+od.get_seller_name());
        ImageView odimg=row.findViewById(R.id.orderDetailRowImg);
        Log.i(myflag,"stop here");
        Glide.with(context).load(od.get_img()).into(odimg);

        Button btnContact=row.findViewById(R.id.orderDetailRowContact);
        Button btnReview=row.findViewById(R.id.orderDetailRowReview);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ContactActivity.class);
                intent.putExtra("seller_id",od.get_seller_id());
                context.startActivity(intent);
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ReviewActivity.class);
                intent.putExtra("product_id",od.get_productid());
                context.startActivity(intent);
            }
        });

        return row;
    }
}