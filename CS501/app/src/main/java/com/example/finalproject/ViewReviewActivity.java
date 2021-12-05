package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ViewReviewActivity extends AppCompatActivity {
    private String myflag="ViewReview";
    private RecyclerView rv;
    private ArrayList<Review> reviewlist=new ArrayList<Review>();
    private ArrayList<String> titlelist=new ArrayList<>();
    private ArrayList<String> bodylist=new ArrayList<>();
    private ArrayList<String> userlist=new ArrayList<>();
    private String product_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);
        rv=(RecyclerView) findViewById(R.id.viewReviewRV);

        //get product id
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        product_id=extras.getString("product_id");
        Log.i(myflag,product_id);

        //filter product in review database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reviewdb=db.collection("reviews");
        CollectionReference usersdb=db.collection("users");
        Query query=reviewdb.whereEqualTo("product_id", product_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
//                        Log.i(myflag,String.valueOf(dataMap.get("review_title")));
                        titlelist.add(String.valueOf(dataMap.get("review_title")));
                        bodylist.add(String.valueOf(dataMap.get("review_body")));
                        userlist.add(String.valueOf(dataMap.get("user_id")));
//                        Log.i(myflag,titlelist.toString());
                    }
                    Log.i(myflag,String.valueOf(titlelist.size()));
                    for(int i=0;i<titlelist.size();i++) {
                        Query userq = usersdb.whereEqualTo("user_id", userlist.get(i));
                        String t = titlelist.get(i);
                        String b = bodylist.get(i);
                        Log.i(myflag,"in loop: "+t);
                        userq.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> udataMap = document.getData();
                                        String u_name = String.valueOf(udataMap.get("username"));
                                        Log.i(myflag, t);
                                        Log.i(myflag, b);
                                        Log.i(myflag, u_name);
                                        Review r = new Review(t, b, u_name);
                                        reviewlist.add(r);
                                    }
                                    ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getBaseContext(), reviewlist);
                                    // below line is for setting a layout manager for our recycler view.
                                    // here we are creating vertical list so we will provide orientation as vertical
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);

                                    // in below two lines we are setting layoutmanager and adapter to our recycler view.
                                    rv.setLayoutManager(linearLayoutManager);
                                    rv.setAdapter(reviewsAdapter);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}

//RecyclView Adapter
class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.Viewholder> {

    private Context context;
    private ArrayList<Review> reviews;

    // Constructor
    public ReviewsAdapter(Context acontext, ArrayList<Review> ReviewsArrayList) {
        this.context = acontext;
        this.reviews = ReviewsArrayList;
    }

    @NonNull
    @Override
    public ReviewsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_reviews_card, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        Review r = reviews.get(position);
        holder.reviewtitle.setText(r.get_title());
        holder.reviewbody.setText(r.get_body());
        holder.reviewusername.setText(r.get_username());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return reviews.size();
    }

    // View holder class for initializing of
    // your views such as TextView.

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView reviewtitle, reviewbody,reviewusername;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            reviewtitle = itemView.findViewById(R.id.reviewCardTitle);
            reviewbody = itemView.findViewById(R.id.reviewCardBody);
            reviewusername = itemView.findViewById(R.id.reviewCardUsername);
        }
    }
}
class Review{
    private String review_title;
    private String review_body;
    private String review_username;
    public Review(String title,String body,String username){
        this.review_title=title;
        this.review_body=body;
        this.review_username=username;
    }

    public String get_title(){
        return review_title;
    }

    public String get_body(){
        return review_body;
    }

    public String get_username(){
        return review_username;
    }
}