package com.example.finalproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class MessagesAdapter extends ArrayAdapter<Message> {

    private static final String TAG = "EmailPassword";

    private TextView message_txtMessageInfo;

    private String cur_seller_id;
    private String cur_renter_id;
    private String cur_product_id;
    private String cur_type;

    private String PRODUCT_NAME = "product_name";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;

    private Message message;

    Context context;

    public MessagesAdapter(Context context, List<Message> constacts_list) {
        super(context, 0, constacts_list);
        context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View row;
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_message_row, parent, false);
        }

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        message_txtMessageInfo = (TextView) listItemView.findViewById(R.id.message_txtMessageInfo);

        message = getItem(position);
        cur_seller_id = message.getSeller_id();
        cur_renter_id = message.getRenter_id();
        cur_product_id = message.getProduct_id();
        cur_type = message.getType();


        getProductById(cur_product_id);


        return listItemView;
    }

    public void getProductById(String product_id) {
        // [START get_multiple]
        db.collection("products")
                .whereEqualTo("product_id", product_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                Map<String, Object> product = document.getData();
                                String product_name = product.get(PRODUCT_NAME).toString();


                                String new_message = "";
                                if(cur_type.equals("order placed")) {
                                    if(user_id.equals(cur_seller_id)) {
                                        new_message = "Your product " + product_name + " has been ordered!";
                                    } else {
                                        new_message = "Your order " + product_name + " has been placed!";
                                    }

                                } else {
                                    if(user_id.equals(cur_seller_id)) {
                                        new_message = "You received your " + product_name + "!";
                                    } else {
                                        new_message = "Your order " + product_name + " has been received!";
                                    }
                                }

                                message_txtMessageInfo.setText(new_message);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    public void getUserById(String user_id) {
        // [START get_multiple]
        db.collection("users")
                .whereEqualTo("user_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                Map<String, Object> current_user = document.getData();


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}
