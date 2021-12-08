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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrdersActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener{
    private ArrayList<Orders> orders=new ArrayList<>();
    private String myflag="OrdersAcvivity";
    private ListView lvOrders;     //Reference to the listview GUI component
    private ListAdapter lvAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    private Map<String,Map<String,Object>> datamaplist=new HashMap<>();
    private ImageView return_icon;

    private static final String ORDERS="orders";
    private static final String ORDER_OWNER="order_owner";
    private static final String ORDER_ID="order_id";
    private static final String ORDER_TIME="order_time";
    private static final String ORDER_TOTAL="order_total";
    private static final String ORDER_ITEMS="order_items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        lvOrders=(ListView)findViewById(R.id.ordersListView);
        //get current user id
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();
        //setup return icon
        return_icon=(ImageView)findViewById(R.id.order_return);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //connect to database and filter order database by user id to get the order histories
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference orderdb=db.collection(ORDERS);
        Query query = orderdb.whereEqualTo(ORDER_OWNER,user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        datamaplist.put(String.valueOf(dataMap.get(ORDER_TIME)),dataMap);
                    }
                    Log.i(myflag,String.valueOf(datamaplist.size()));
                    //sort all orders by time
                    //reference: https://howtodoinjava.com/java/sort/java-sort-map-by-values/
                    Map<String, Map<String,Object>> reverseSortedMap = new TreeMap<String, Map<String,Object>>(Collections.reverseOrder());
                    reverseSortedMap.putAll(datamaplist);
                    Log.i(myflag,reverseSortedMap.toString());
                    ArrayList<Map<String,Object>> maps=new ArrayList<>(reverseSortedMap.values());
                    for (Map<String,Object> dmap:maps){
                        //get information for each order
                        String order_id=String.valueOf(dmap.get(ORDER_ID));
                        Log.i(myflag,order_id);
                        String order_total=String.valueOf(dmap.get(ORDER_TOTAL));
                        String order_time=String.valueOf(dmap.get(ORDER_TIME));
                        Log.i(myflag,"items:"+order_time);
                        //millitimes to readable time
                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                        Date date = new Date(Long.valueOf(order_time));
                        String orderTime = formatter.format(date);
                        Log.i(myflag,"time: "+orderTime);
                        String order_owner=String.valueOf(dmap.get(ORDER_OWNER));
                        Map<String, String> items = new ObjectMapper().convertValue(dmap.get(ORDER_ITEMS),Map.class);
                        Log.i(myflag,"items:"+items.toString());
                        ArrayList<String> products=new ArrayList<>(items.keySet());
                        ArrayList<String> sellers=new ArrayList<>(items.values());
                        Log.i(myflag,"products:"+products.toString());
                        Log.i(myflag,"Sellers: "+sellers.toString());
                        Orders o=new Orders(order_id,order_owner,orderTime,order_total,products,sellers);
                        orders.add(o);
                    }
                    //init adapter and render order history
                    lvAdapter=new OrderAdapter(OrdersActivity.this,orders);
                    lvOrders.setAdapter(lvAdapter);
                }

            }
        });
    }


    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}


class OrderAdapter extends BaseAdapter {
    private Context context;
    private String myflag="OrdersAdapter";
    ArrayList<Orders> order;

    private static final String SELLER_IDS = "seller_ids";
    private static final String PRODUCT_IDS = "product_ids";
    private static final String ORDER_NUMBER="order_number";
    private static final String ORDER_TOTAL="order_total";



    public OrderAdapter(Context acontext,ArrayList<Orders> order){
        this.context=acontext;
        this.order=order;
    }

    @Override
    public int getCount() {
        return order.size();
    }

    @Override
    public Object getItem(int i) {
        return order.get(i).get_oid();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View row, ViewGroup viewGroup) {
        if (row == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.activity_order_row, viewGroup, false);
        }
        TextView tvorder_id=(TextView) row.findViewById(R.id.orderRowId);
        TextView tvorder_time=(TextView) row.findViewById(R.id.orderRowTime);
        TextView tvorder_total=(TextView) row.findViewById(R.id.orderRowTotal);
        Button btnDetail=(Button) row.findViewById(R.id.orderRowDetailbtn);
        Orders o=order.get(i);
        tvorder_id.setText("Order number: "+o.get_oid());
        tvorder_time.setText(o.get_time());
        tvorder_total.setText("Total: $"+o.get_total());
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,OrderDetailActivity.class);
                intent.putExtra(PRODUCT_IDS,o.get_products());
                intent.putExtra(SELLER_IDS,o.get_sellers());
                intent.putExtra(ORDER_NUMBER,o.get_oid());
                intent.putExtra(ORDER_TOTAL,o.get_total());
                context.startActivity(intent);
            }
        });

        return row;
    }
}

class Orders{
    private String order_id,order_owner,order_time,order_total;
    private ArrayList<String> products,sellers;
    public Orders(String order_id,String order_owner, String order_time,String order_total,ArrayList<String> products, ArrayList<String> sellers){
        this.order_id=order_id;
        this.order_owner=order_owner;
        this.order_time=order_time;
        this.order_total=order_total;
        this.products=products;
        this.sellers=sellers;
    }
    public void add_product(String pid){
        products.add(pid);
    }

    public String get_oid(){
        return order_id;
    }
    public String get_owner(){
        return order_owner;
    }
    public String get_time(){
        return order_time;
    }

    public String get_total(){
        return order_total;
    }
    public ArrayList<String> get_products(){
        return products;
    }
    public ArrayList<String> get_sellers(){
        return sellers;
    }
}