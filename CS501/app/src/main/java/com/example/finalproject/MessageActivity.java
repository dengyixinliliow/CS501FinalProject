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

public class MessageActivity extends AppCompatActivity implements NavigationFragment.NavigationFragmentListener {

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
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        inbox_lvContacts = (ListView) findViewById(R.id.message_lvContacts);

        messages_list = new ArrayList<>();

        getMessages(user_id);

    }

    private void getMessages (String user_id){
//        Message message = new Message();
//        messages_list.add(message);
//        messages_list.add(message);
//        MessagesAdapter messagesLVAdapter = new MessagesAdapter(MessageActivity.this, messages_list);
//        inbox_lvContacts.setAdapter(messagesLVAdapter);
        // user as a renter
         db.collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current message
                                Map<String, Object> cur_message_data = document.getData();

                                Object cur_seller_id_obj = cur_message_data.get("seller_id");
                                Object cur_renter_id_obj = cur_message_data.get("renter_id");
                                Object cur_product_id_obj = cur_message_data.get("product_id");
                                Object cur_type_obj = cur_message_data.get("type");

                                String cur_seller_id = "null";
                                String cur_renter_id = "null";
                                String cur_product_id = "null";
                                String cur_type = "null";

                                if(cur_seller_id_obj != null) {
                                    cur_seller_id = cur_seller_id_obj.toString();
                                }
                                if(cur_renter_id_obj != null) {
                                    cur_renter_id = cur_renter_id_obj.toString();
                                }
                                if(cur_product_id_obj != null) {
                                    cur_product_id = cur_product_id_obj.toString();
                                }
                                if(cur_type_obj != null) {
                                    cur_type = cur_type_obj.toString();
                                }

                                if(cur_renter_id.equals(user_id) || cur_seller_id.equals(user_id)) {
                                    Message message = new Message();
                                    message.setSeller_id(cur_seller_id);
                                    message.setRenter_id(cur_renter_id);
                                    message.setProduct_id(cur_product_id);
                                    message.setType(cur_type);

                                    messages_list.add(message);
                                }

                            }
                            MessagesAdapter messagesLVAdapter = new MessagesAdapter(MessageActivity.this, messages_list);
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

