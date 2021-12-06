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

    private static final String TAG = "EmailPassword";

    private Button inbox_btnSearch;
    private Button inbox_btnInbox;
    private Button inbox_btnOrders;
    private Button inbox_btnProfile;

    private TextView contact_txtContactIntro;
    private Button contact_btnMessage;
    private Button contact_btnCall;
    private Button contact_btnEmail;
    private ImageView return_icon;

    public static final String USER_ID = "user_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_TYPE = "product_type";
    public static final String PRODUCT_SIZE = "product_size";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String PRODUCT_COLOR = "product_color";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String PRODUCT_CONDITION = "product_condition";
    public static final String PRODUCT_IMG_URL = "product_img_url";
    public static final String PRODUCT_IS_AVAILABLE = "product_is_available";
    public static final String SELLER_ID = "seller_id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";

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

        mAuth = FirebaseAuth.getInstance();
        auth_user = mAuth.getCurrentUser();
        user_id = auth_user.getUid();

        contact_txtContactIntro = (TextView) findViewById(R.id.contact_txtContactIntro);
        contact_btnMessage = (Button) findViewById(R.id.contact_btnMessage);
        contact_btnCall = (Button) findViewById(R.id.contact_btnCall);
        contact_btnEmail = (Button) findViewById(R.id.contact_btnEmail);
        return_icon=(ImageView)findViewById(R.id.ContactReturn);

        Intent intent = getIntent();
        seller_id = intent.getStringExtra(SELLER_ID);

        getUsernameById(seller_id);

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

                                String cur_user_email = cur_user.get("email").toString();

                                String cur_username = cur_user.get(USERNAME).toString();
                                contactintro_message = contactintro_message + cur_username;
                                contact_txtContactIntro.setText(contactintro_message);

//                                // send email
//                                // 必须明确使用mailto前缀来修饰邮件地址,如果使用   intent.putExtra(Intent.EXTRA_EMAIL, email)，结果将匹配不到任何应用
//                                Uri uri = Uri.parse("mailto:" + cur_user_email);
//                                String[] email = {cur_user_email};
//                                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
////                                intent.putExtra(Intent.EXTRA_CC, email); // cc
//                                intent.putExtra(Intent.EXTRA_SUBJECT, "topic"); // topic
//                                intent.putExtra(Intent.EXTRA_TEXT, message); // content
//                                startActivity(Intent.createChooser(intent, "choose email app"));

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
                                Map<String, Object> cur_user = document.getData();

                                String cur_user_email = cur_user.get(EMAIL).toString();
                                String cur_user_phone = cur_user.get(PHONE).toString();

                                if(contact_option.equals("message")) {
                                    try {

//                                        String message = "";
                                        Log.d(TAG, "phone: " + cur_user_phone);
                                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                        sendIntent.putExtra("address", cur_user_phone);
//                                        sendIntent.putExtra("sms_body", message);
                                        sendIntent.setType("vnd.android-dir/mms-sms");
                                        startActivity(sendIntent);

                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                "SMS faild, please try again later!",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                } else if(contact_option.equals("call")) {
                                    Intent phoneCallMom = new Intent(Intent.ACTION_DIAL);  //or with two lines.
                                    phoneCallMom.setData(Uri.parse("tel:"+cur_user_phone));   //REALLY SHOULD NOT HARD CODE PHONE #
                                    startActivity(phoneCallMom);
                                } else {
                                    Uri uri = Uri.parse("mailto:" + cur_user_email);
                                    String[] email = {cur_user_email};
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                                    //intent.putExtra(Intent.EXTRA_CC, email); // cc
//                                    intent.putExtra(Intent.EXTRA_SUBJECT, "temple topic"); // topic
//                                    intent.putExtra(Intent.EXTRA_TEXT, "hello email"); // content
                                    startActivity(Intent.createChooser(intent, "choose email app"));
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