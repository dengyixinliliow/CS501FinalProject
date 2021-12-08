package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "MYTAG";

    private TextView contact_txtContactIntro;
    private Button contact_btnMessage;
    private Button contact_btnCall;
    private Button contact_btnEmail;
    private ImageView return_icon;

    private static final String SELLER_ID = "seller_id";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";

    private String seller_id;
    private String contactintro_message = "Get in touch with ";
    private String contact_option;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser auth_user;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // get current user id
        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        contact_txtContactIntro = (TextView) findViewById(R.id.contact_txtContactIntro);
        contact_btnMessage = (Button) findViewById(R.id.contact_btnMessage);
        contact_btnCall = (Button) findViewById(R.id.contact_btnCall);
        contact_btnEmail = (Button) findViewById(R.id.contact_btnEmail);
        return_icon=(ImageView)findViewById(R.id.ContactReturn);

        // Get the ID of the contact
        Intent intent = getIntent();
        seller_id = intent.getStringExtra(SELLER_ID);

        // set the contact info on screen
        getUsernameById(seller_id);

        // set button click listener
        contact_btnMessage.setOnClickListener(new ContactsButton());
        contact_btnCall.setOnClickListener(new ContactsButton());
        contact_btnEmail.setOnClickListener(new ContactsButton());
        return_icon.setOnClickListener(new ContactsButton());

    }

    private class ContactsButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.contact_btnMessage:
                    contact_option = "message";
                    getUserById(seller_id);
                    break;
                case R.id.contact_btnCall:
                    contact_option = "call";
                    getUserById(seller_id);
                    break;
                case R.id.contact_btnEmail:
                    contact_option = "email";
                    getUserById(seller_id);
                    break;
                case R.id.ContactReturn:
                    finish();
                    break;
            }
        }
    }

    /*
        Get user information by ID
     */
    public void getUsernameById(String user_id) {
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
                                Map<String, Object> cur_user = document.getData();

                                String cur_username = cur_user.get(USERNAME).toString();
                                contactintro_message = contactintro_message + cur_username;
                                contact_txtContactIntro.setText(contactintro_message);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }

    /*
        Get user information by ID
     */
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
                                Map<String, Object> cur_user = document.getData();

                                String cur_user_email = cur_user.get(EMAIL).toString();
                                String cur_user_phone = cur_user.get(PHONE).toString();

                                // send message
                                if(contact_option.equals("message")) {
                                    try {
                                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                        sendIntent.putExtra("address", cur_user_phone);
                                        sendIntent.setType("vnd.android-dir/mms-sms");
                                        startActivity(sendIntent);
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                "SMS faild, please try again later!",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                // make a phone call
                                } else if(contact_option.equals("call")) {
                                    try {
                                        Intent phoneCall = new Intent(Intent.ACTION_DIAL);
                                        phoneCall.setData(Uri.parse("tel:"+cur_user_phone));
                                        startActivity(phoneCall);
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                "CALL faild, please try again later!",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                // send email
                                } else {
                                    try {
                                        Uri uri = Uri.parse("mailto:" + cur_user_email);
                                        String[] email = {cur_user_email};
                                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                                        startActivity(Intent.createChooser(intent, "choose email app"));
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                "EMAIL faild, please try again later!",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // [END get_multiple]
    }
}