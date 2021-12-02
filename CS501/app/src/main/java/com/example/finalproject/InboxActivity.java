package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class InboxActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private Button inbox_btnSearch;
    private Button inbox_btnInbox;
    private Button inbox_btnOrders;
    private Button inbox_btnProfile;

    private ListView inbox_lvContacts;
    //private ScrollView inbox_svMessages;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;
    public static final String USERNAME = "username";

    // https://www.thecrazyprogrammer.com/2016/10/android-real-time-chat-application-using-firebase-tutorial.html

    private ArrayList<Contact> contacts_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        inbox_btnSearch = (Button) findViewById(R.id.inbox_btnSearch);
        inbox_btnInbox = (Button) findViewById(R.id.inbox_btnInbox);
        inbox_btnOrders = (Button) findViewById(R.id.inbox_btnOrders);
        inbox_btnProfile = (Button) findViewById(R.id.inbox_btnProfile);

        inbox_lvContacts = (ListView) findViewById(R.id.inbox_lvContacts);

        contacts_list = new ArrayList<>();

        getContactsById(user_id);
//        // set the items in contacts listview
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InboxActivity.this, android.R.layout.simple_list_item_1, contacts);
//        inbox_lvContacts = (ListView) findViewById(R.id.inbox_lvContacts);
//        inbox_lvContacts.setAdapter(adapter);

        inbox_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to search page
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        inbox_btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to orders page
                Intent intent = new Intent(getBaseContext(), OrdersActivity.class);
                startActivity(intent);
            }
        });

        inbox_btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to personalInfo page
                Intent intent = new Intent(getBaseContext(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

//        inbox_lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Contact contact = contacts_list.get(i);
//                Intent intent = new Intent(InboxActivity.this, ContactInfoActivity.class);
//                intent.putExtra("receiver_id", contact.getReceiver_id());
//                startActivity(intent);
//            }
//        });
    }

    public void getContactsById(String user_id) {
        // [START get_multiple]
        db.collection("chats")
                .whereEqualTo("renter_id", user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // store info of the current user
                                Map<String, Object> database_chat = document.getData();

//                                // set the items in contacts listview
//                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(InboxActivity.this, android.R.layout.simple_list_item_1, contacts);
//                                inbox_lvContacts = (ListView) findViewById(R.id.inbox_lvContacts);
//                                inbox_lvContacts.setAdapter(adapter);

                                 getUserById(database_chat.get("seller_id").toString());
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
                                Map<String, Object> database_user = document.getData();
//                                contacts.add(database_user.get(USERNAME).toString());
                                Log.d(TAG, "contact: " + database_user.get(USERNAME).toString());

                                Contact contact = new Contact();
                                contact.setReceiver_id(user_id);
                                contact.setReceiver_name(database_user.get(USERNAME).toString());
//
                                contacts_list.add(contact);
                                Log.d(TAG, "contact list: " + contacts_list.toString());
                            }
                            ContactsAdapter contactsAdapter = new ContactsAdapter(InboxActivity.this, contacts_list);
                            inbox_lvContacts.setAdapter(contactsAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }


}

