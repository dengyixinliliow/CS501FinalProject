package com.example.finalproject;


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
    private ArrayList<String> renters;
    private ArrayList<String> pids;
    private ArrayList<Integer> itemImages;

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

        //get every item in cart names, prices, renters for later use
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cart = db.collection("carts");
        CollectionReference product = db.collection("products");
        Query query=cart.whereEqualTo("user_id", user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        String productid=String.valueOf(dataMap.get("product_id"));
                        productids.add(productid);
                        Log.i(myflag, productid);
                    }
                    Log.i(myflag,String.valueOf(productids.size()));
                    for(String productid:productids){
                        Query product_q=product.whereEqualTo("product_id", productid);
                        product_q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> pdataMap = document.getData();
                                        String s=String.valueOf(pdataMap.get("seller_id"));
                                        String pid=String.valueOf(pdataMap.get("product_id"));
                                        String n = String.valueOf(pdataMap.get("product_name"));
                                        String p = String.valueOf(pdataMap.get("product_price"));
                                        String is_avail=String.valueOf(pdataMap.get("is_available"));
                                        String url= String.valueOf(pdataMap.get("product_img_url"));
                                        Log.i(myflag, n);
                                        if(is_avail=="true" && s!=user_id){
                                            Cart_item item = new Cart_item(n, p, pid,url);
                                            itemlist.add_item(item);
                                        }
                                    }
                                    Log.i(myflag,String.valueOf(itemlist.get_list().size()));
                                    names=itemlist.get_names();
                                    prices=itemlist.get_price();
                                    pids=itemlist.get_pid();
                                    sum=0.0;
                                    for(String s:prices){
                                        sum+=Double.valueOf(s);
                                    }
                                    tvsum.setText("Total: "+String.valueOf(sum));
                                    lvAdapter=new CartListAdapter(CartActivity.this,itemlist,tvsum);
                                    lvItem.setAdapter(lvAdapter);
                                }
                                else{
                                    Log.e(myflag,"not success query");
                                }
                            }
                        });
                    }
                }
                else{
                    Log.e(myflag,"not success query");
                }
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                intent.putExtra("products_lists",pids);
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


//class for cart listview adapter
class CartListAdapter extends BaseAdapter{

    private
    String myflag="CartRowFlag";
    Cart_item_list itemlist;
    ArrayList<String> names;
    ArrayList<String> prices;
    ArrayList<String> pids;
    ArrayList<String> urls;
    Button btnRemove;
    Context context;

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
        tvprice.setText(prices.get(i));
        Glide.with(context).load(urls.get(i)).into(img);
//        imgEpisode.setImageResource(episodeImages.get(position).intValue());

        btnRemove= (Button) row.findViewById(R.id.cart_btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference cart = db.collection("carts");
                String deletedItem = pids.get(i);
                Query query=cart.whereEqualTo("user_id",user_id).whereEqualTo("product_id",pids.get(i));
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_LONG).show();
                                Log.d(myflag, "deleted!");
                                itemlist.remove_item(i);
                                names.remove(i);
                                prices.remove(i);
                                pids.remove(i);
                                urls.remove(i);
                                sum=0.0;
                                for(String s:prices){
                                    sum+=Double.valueOf(s);
                                }
                                tvsum.setText("Total: "+String.valueOf(sum));
                                notifyDataSetChanged();
                            }
                        }
                        else{
                            Log.e(myflag,"not success query");
                        }
                    }
                });
            }
        });
        return row;
    }
}

class Cart_item{
    private
    String name;
    String pid;
    String price;
    String renter;
    String imgurl;
    public Cart_item(String n,String p,String productid,String url){
        this.name=n;
        this.price=p;
        this.pid=productid;
        this.imgurl=url;
    }

    public String get_name(){return name;}
    public String get_price(){return price;}
    public String get_productid(){return pid;}
    public String get_img(){return imgurl;}
}

class Cart_item_list{
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
}