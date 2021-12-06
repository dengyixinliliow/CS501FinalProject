package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class InboxActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {

    private static final String TAG = "EmailPassword";


    private ListView inbox_lvContacts;
    //private ScrollView inbox_svMessages;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    public static final String USERNAME = "username";

    // https://www.thecrazyprogrammer.com/2016/10/android-real-time-chat-application-using-firebase-tutorial.html

    private ArrayList<Message> messages_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        inbox_lvContacts = (ListView) findViewById(R.id.inbox_lvContacts);

        messages_list = new ArrayList<>();

        getMessages(user_id);

    }

    private void getMessages (String user_id){
        // user as a renter
         db.collection("messages")
                .whereEqualTo("renter_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current message
                                Map<String, Object> cur_message_data = document.getData();
                                String cur_seller_id = cur_message_data.get("seller_id").toString();
                                String cur_renter_id = cur_message_data.get("renter_id").toString();
                                String cur_product_id = cur_message_data.get("product_id").toString();
                                String cur_type = cur_message_data.get("type").toString();

                                Message message = new Message();
                                message.setSeller_id(cur_seller_id);
                                message.setRenter_id(cur_renter_id);
                                message.setProduct_id(cur_product_id);
                                message.setType(cur_type);

                                messages_list.add(message);
                            }

                            // user as a seller
                            db.collection("messages")
                                    .whereEqualTo("seller_id", user_id)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    // store info of the current message
                                                    Map<String, Object> cur_message_data = document.getData();
                                                    String cur_seller_id = cur_message_data.get("seller_id").toString();
                                                    String cur_renter_id = cur_message_data.get("renter_id").toString();
                                                    String cur_product_id = cur_message_data.get("product_id").toString();
                                                    String cur_type = cur_message_data.get("type").toString();

                                                    Message message = new Message();
                                                    message.setSeller_id(cur_seller_id);
                                                    message.setRenter_id(cur_renter_id);
                                                    message.setProduct_id(cur_product_id);
                                                    message.setType(cur_type);

                                                    messages_list.add(message);
                                                }


                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                            MessagesAdapter messagesLVAdapter = new MessagesAdapter(InboxActivity.this, messages_list);
                            inbox_lvContacts.setAdapter(messagesLVAdapter);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        // [END get_multiple]
    }

    @Override
    public void SwitchActivity(String page_name) {
        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_nagivation);
        navigationFragment.setOrginActivity(page_name, getBaseContext());
    }
}

