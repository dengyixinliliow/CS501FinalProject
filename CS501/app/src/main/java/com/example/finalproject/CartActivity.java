package com.example.finalproject;


import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.HashMap;
import java.util.Map;


public class CartActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {
    private String myflag="CartFlag";
    private ListView lvItem;     //Reference to the listview GUI component
    private ListAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.
    public TextView tvsum;
    public double sum;
    private Button btnCheckout;
    private String user_id;
    //from database
    private Cart_item_list itemlist=new Cart_item_list();
    private ArrayList<String> productids=new ArrayList<>();
    private ArrayList<String> names;
    private ArrayList<String> prices;
    private ArrayList<String> sellerids=new ArrayList<>();
    private ArrayList<String> pids;
    //field names
    private static final String USER_ID = "user_id";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_NAME = "product_name";
    private static final String PRODUCT_PRICE = "product_price";
    private static final String PRODUCT_IMG_URL = "product_img_url";
    private static final String SELLER_ID = "seller_id";
    private static final String CARTS="carts";
    private static final String PRODUCTS="products";
    private static final String IS_AVAILABLE="is_available";

    //for intent
    private static final String PRODUCT_LIST="products_list";
    private static final String TOTAL_AMOUNT="total_amount";
    private static final String PRODUCTS_SELLER_LIST="products_seller_list";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        lvItem=(ListView) findViewById(R.id.cart_listview);
        tvsum=(TextView) findViewById(R.id.car_sum);
        btnCheckout=(Button) findViewById(R.id.cart_checkout);
        //get user id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser auth_user = mAuth.getCurrentUser();
        user_id=auth_user.getUid();
        Log.i(myflag,user_id);



        //connect to database and collections: carts, products
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cart = db.collection(CARTS);
        CollectionReference product = db.collection(PRODUCTS);
        //filter documents in carts collection by user id
        Query query=cart.whereEqualTo(USER_ID, user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        //get the products id in certain user's cart
                        String productid=String.valueOf(dataMap.get(PRODUCT_ID));
                        productids.add(productid);
                        Log.i(myflag, productid);
                    }
                    Log.i(myflag,String.valueOf(productids.size()));
                    for(String productid:productids){
                        //for each product in user's cart, go to products database and filter the database by product id
                        Query product_q=product.whereEqualTo(PRODUCT_ID, productid);
                        product_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> pdataMap = document.getData();
                                        //get the information fo reach product
                                        String s=String.valueOf(pdataMap.get(SELLER_ID));
                                        String pid=String.valueOf(pdataMap.get(PRODUCT_ID));
                                        String n = String.valueOf(pdataMap.get(PRODUCT_NAME));
                                        String p = String.valueOf(pdataMap.get(PRODUCT_PRICE));
                                        String is_avail=String.valueOf(pdataMap.get(IS_AVAILABLE));
                                        String url= String.valueOf(pdataMap.get(PRODUCT_IMG_URL));
                                        Log.i(myflag, n);
                                        if(is_avail=="true" && s!=user_id){
                                            //if the product is available, add it for later use
                                            Cart_item item = new Cart_item(n, p, pid,url,s);
                                            itemlist.add_item(item);
                                        }
                                    }
                                    Log.i(myflag,String.valueOf(itemlist.get_list().size()));
                                    names=itemlist.get_names();
                                    prices=itemlist.get_price();
                                    pids=itemlist.get_pid();
                                    sellerids=itemlist.get_sellers();
                                    sum=0.0;
                                    //calculate total price
                                    for(String s:prices){
                                        sum+=Double.valueOf(s);
                                    }
                                    //final
                                    tvsum.setText("Total: $" + sum);
                                    //render products
                                    lvAdapter=new CartListAdapter(CartActivity.this,itemlist,tvsum);
                                    lvItem.setAdapter(lvAdapter);
                                }
                            }
                        });
                    }
                }
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send information for checkout
                if(sum!=0){
                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra(PRODUCT_LIST,pids);
                    intent.putExtra(TOTAL_AMOUNT, sum);
                    intent.putExtra(PRODUCTS_SELLER_LIST,sellerids);
                    startActivity(intent);
                }
            }
        });

    }

    public void updateData(Double price,ArrayList<String> p_list, ArrayList<String> s_list){
        //receive data changes

        sum=price;
        pids=p_list;
        sellerids=s_list;
    }

    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }

}


//class for cart listview adapter
class CartListAdapter extends BaseAdapter{

    private String myflag="CartRowFlag";
    private Cart_item_list itemlist;
    private ArrayList<String> names;
    private ArrayList<String> prices;
    private ArrayList<String> pids;
    private ArrayList<String> urls;
    private Button btnRemove;
    private Context context;

    private static final String CARTS="carts";
    private static final String USER_ID = "user_id";
    private static final String PRODUCT_ID = "product_id";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser auth_user = mAuth.getCurrentUser();
    String user_id=auth_user.getUid();

    Double sum;
    TextView tvsum;

    public CartListAdapter(Context aContext,Cart_item_list list,TextView tv) {
        context=aContext;
        this.itemlist=list;
        this.names=list.get_names();
        this.prices=list.get_price();
        this.pids=list.get_pid();
        this.urls=list.get_imgs();
        this.tvsum=tv;

    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int i) {
        return names.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View row, ViewGroup viewGroup) {
//        View row;
        if (row == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.activity_cartrow, viewGroup, false);
        }
//        else
//        {
//            row = view;
//        }
        ImageView img = (ImageView) row.findViewById(R.id.img);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
        TextView tvname = (TextView) row.findViewById(R.id.cart_tvName);
        TextView tvprice = (TextView) row.findViewById(R.id.cart_tvPrice);

        tvname.setText(names.get(i));
        tvprice.setText("Price: $" + prices.get(i));
        //set image stored online
        Glide.with(context).load(urls.get(i)).into(img);
//        imgEpisode.setImageResource(episodeImages.get(position).intValue());

        btnRemove= (Button) row.findViewById(R.id.cart_btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference cart = db.collection(CARTS);
                String deletedItem = pids.get(i);
                Query query=cart.whereEqualTo(USER_ID,user_id).whereEqualTo(PRODUCT_ID,pids.get(i));
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //delete from cart database
                                document.getReference().delete();
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_LONG).show();
                                Log.d(myflag, "deleted!");
                                //deleter for adapter
                                itemlist.remove_item(i);
                                names.remove(i);
                                prices.remove(i);
                                pids.remove(i);
                                urls.remove(i);
                                ArrayList<String> sel=itemlist.get_sellers();
                                sum=0.0;
                                //recalculate the total price
                                for(String s:prices){
                                    sum+=Double.valueOf(s);
                                }
                                tvsum.setText("Total: $"+String.valueOf(sum));
                                //notify adapter data changes
                                notifyDataSetChanged();
                                //notify activity data changes
                                ((CartActivity)context).updateData(sum,pids,sel);
                            }
                        }
                    }
                });
            }
        });
        return row;
    }
}

class Cart_item{
    //store one single product in cart
    private String name,pid,price,imgurl,seller_id;
    public Cart_item(String n,String p,String productid,String url, String seller_id){
        this.name=n;
        this.price=p;
        this.pid=productid;
        this.imgurl=url;
        this.seller_id=seller_id;
    }

    public String get_name(){return name;}
    public String get_price(){return price;}
    public String get_productid(){return pid;}
    public String get_img(){return imgurl;}
    public String get_seller(){return seller_id;}
}

class Cart_item_list{
    //store a lists of products in cart
    private ArrayList<Cart_item> items;
    public Cart_item_list(){
        items=new ArrayList<Cart_item>();
    }
    public Cart_item_list(ArrayList<Cart_item> i){
        this.items=i;
    }

    public void add_item(Cart_item i){
        items.add(i);
    }
    public void remove_item(int i){items.remove(i);}

    public ArrayList<Cart_item> get_list(){return items;}


    public ArrayList<String> get_names(){
        ArrayList<String> out=new ArrayList<>();
        for(Cart_item i:items){
            out.add(i.get_name());
        }
        return out;
    }
    public ArrayList<String> get_price(){
        ArrayList<String> out=new ArrayList<>();
        for(Cart_item i:items){
            out.add(i.get_price());
        }
        return out;
    }


    public ArrayList<String> get_pid() {
        ArrayList<String> out = new ArrayList<>();
        for (Cart_item i : items) {
            out.add(i.get_productid());
        }
        return out;
    }
    public ArrayList<String> get_imgs() {
        ArrayList<String> out = new ArrayList<>();
        for (Cart_item i : items) {
            out.add(i.get_img());
        }
        return out;
    }
    public ArrayList<String> get_sellers() {
        ArrayList<String> out = new ArrayList<>();
        for (Cart_item i : items) {
            out.add(i.get_seller());
        }
        return out;
    }
}