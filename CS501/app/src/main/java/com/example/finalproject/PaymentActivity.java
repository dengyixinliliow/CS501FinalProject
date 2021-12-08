package com.example.finalproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {

    //Reference:
    //Code adapted from : https://codingwithtashi.medium.com/stripe-payment-integration-with-android-4c588e78f3ea

    // 10.0.2.2 is the Android emulator's alias to localhost
    // If you are testing in real device with usb connected to same network then use your IP address
//    private static final String BACKEND_URL = "http://10.0.2.2:4242/"; //4242 is port mentioned in server i.e index.js
//    private static final String BACKEND_URL = "http://155.41.45.161:4242/";
    private static final String BACKEND_URL = "http://168.122.11.208:4242/";

    private final int CENT_TO_DOLLAR = 100;
    private final String CURRENCY = "USD";
    private double amount;
    private String orderTime;
    private String orderId;
    private TextView payment_amount;
    private CardInputWidget payment_cardInputWidget;
    private Button payment_btn;
    private ArrayList<String> products;
    private ArrayList<String> products_sellers;
    private ImageView return_icon;


    // we need paymentIntentClientSecret to start transaction
    private String paymentIntentClientSecret;
    //declare stripe
    private Stripe stripe;

    private OkHttpClient httpClient;

    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        amount = intent.getDoubleExtra("total_amount", 1000);
        products = intent.getStringArrayListExtra("products_list");
        products_sellers = intent.getStringArrayListExtra("products_seller_list");

        payment_amount = (TextView) findViewById(R.id.payment_amount);
        payment_cardInputWidget = (CardInputWidget) findViewById(R.id.payment_cardInputWidget);
        payment_btn = (Button) findViewById(R.id.payment_btn);

        payment_amount.setText("Here is your total amount: " + Double.toString(amount));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in progress");
        progressDialog.setCancelable(false);
        httpClient = new OkHttpClient();

        return_icon=(ImageView)findViewById(R.id.payment_return);
        return_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51Jzx1tGWzbmRRq6XV28s4mr2ob8O6OlbYmnHy7HOSYAPh1Z3e4f6UmajWm5Mv6y9PD0Th3FnbFZR2tBEvfesV1Ys00vFzYT91q")
        );

        payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                startCheckout();
            }
        });
    }



    private void startCheckout() {
        {
            // Create a PaymentIntent by calling the server's endpoint.
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
//        String json = "{"
//                + "\"currency\":\"usd\","
//                + "\"items\":["
//                + "{\"id\":\"photo_subscription\"}"
//                + "]"
//                + "}";
            Map<String, Object> payMap = new HashMap<>();
            Map<String, Object> itemMap = new HashMap<>();
            List<Map<String, Object>> itemList = new ArrayList<>();
            payMap.put("currency", CURRENCY);
            itemMap.put("id", "clothes rental");
            itemMap.put("amount", amount * CENT_TO_DOLLAR);
            itemList.add(itemMap);
            payMap.put("items", itemList);
            String json = new Gson().toJson(payMap);
            RequestBody body = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url(BACKEND_URL + "create-payment-intent")
                    .post(body)
                    .build();
            httpClient.newCall(request)
                    .enqueue(new PayCallback(this));

        }
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<PaymentActivity> activityRef;
        PayCallback(@NonNull PaymentActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            progressDialog.dismiss();
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        Log.e("test", "11");
        paymentIntentClientSecret = responseMap.get("clientSecret");

        //once you get the payment client secret start transaction
        //get card detail
        PaymentMethodCreateParams params = payment_cardInputWidget.getPaymentMethodCreateParams();

        if (params == null) {

            Log.e("error", "error");
            progressDialog.dismiss();
        }

        if (params != null) {
            //now use paymentIntentClientSecret to start transaction
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            //start payment
            stripe.confirmPayment(PaymentActivity.this, confirmParams);
        }
        Log.i("TAG", "onPaymentSuccess: "+paymentIntentClientSecret);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<PaymentActivity> activityRef;
        PaymentResultCallback(@NonNull PaymentActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        //If Payment is successful
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            progressDialog.dismiss();
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                // generate order time
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

                Date date = new Date(System.currentTimeMillis());
                orderTime = formatter.format(date);
                // generate unique order id
                orderId = UUID.randomUUID().toString();

                Toast toast =Toast.makeText(activity, "Ordered Successful" + orderId + amount, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                intent.putExtra("order_time", System.currentTimeMillis());
                intent.putExtra("order_total", amount);
                intent.putExtra("order_id", orderId);
                intent.putExtra("products_list", products);
                intent.putExtra("products_seller_list", products_sellers);
                startActivity(intent);

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        //If Payment is not successful
        @Override
        public void onError(@NonNull Exception e) {
            progressDialog.dismiss();
            final PaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }

    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

}
