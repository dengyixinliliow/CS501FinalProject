package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.finalproject.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "GoogleMaps";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker mMarker;
    private PopupWindow mPopupWindow;
    private int mWidth;
    private int mHeight;

    // for multiple locations
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    List<Address> addressList;

    LocationManager locationManager;

    private Map<String, Object> current_user;
    private Map<LatLng, String[]> nameAndAddress = new HashMap<LatLng, String[]>();
    private String user_id;
    public static final String ADDRESS = "address";

    private FirebaseAuth mAuth;
    FirebaseUser auth_user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Source stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            if (address.size() == 0) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(getBaseContext(), "Can't find latitude and longitude.", Toast.LENGTH_SHORT).show();
        }

        return p1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CollectionReference productsRef = db.collection("products");
        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();
                        /**
                         if (dataMap.get("product_address") != null) {
                         addressList.add((Address) dataMap.get("product_address"));
                         }
                         */
                        LatLng addressPt = getLocationFromAddress(getBaseContext(), String.valueOf(dataMap.get("product_address")));
                        String productName = String.valueOf(dataMap.get("product_name"));
                        String productPrice = String.valueOf(dataMap.get("product_price"));
                        String productID = String.valueOf(dataMap.get("product_id"));
                        // Log.d("TAG", String.valueOf(dataMap.get("product_address")));
                        // Log.d("TAG", addressList.toString());
                        if (addressPt != null) {
                            latlngs.add(new LatLng(addressPt.latitude, addressPt.longitude));
                            //Log.d("TAG", String.valueOf(latlngs.get(0).latitude));
                            //Log.d("TAG", String.valueOf(latlngs.get(0).longitude));
                            // Log.d("TAG", String.valueOf(addressPt.latitude));
                            // Log.d("TAG", String.valueOf(addressPt.longitude));
                            String[] productInfo = {productName, productPrice, productID};
                            nameAndAddress.put(addressPt, productInfo);
                        }
                        // Log.d("TAG", latlngs.toString());
                    }
                    onMapReady(mMap);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.0902, -95.7129)));
                }
            }

        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // LatLng test = new LatLng(42.349340999999995, -71.1039816);
        // mMap.addMarker(new MarkerOptions().position(test));
        for (Map.Entry<LatLng, String[]> entry: nameAndAddress.entrySet()) {
            LatLng address = entry.getKey();
            String[] info = entry.getValue(); // Product Name, Product Price, Product ID
            Log.d("TAG", "after getting key" + info[0]);
            String productName = info[0];
            String productPrice = info[1];

            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

            );
            mMarker.setTag(info);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    View mainView = getLayoutInflater().inflate(R.layout.marker_info_window, null);
                    ViewFlipper markerInfoContainer = (ViewFlipper) mainView.findViewById(R.id.markerInfoContainer);
                    View viewContainer = getLayoutInflater().inflate(R.layout.marker_info_layout, null);
                    TextView map_productName = (TextView) viewContainer.findViewById(R.id.map_productName);
                    TextView map_productPrice = (TextView) viewContainer.findViewById(R.id.map_productPrice);
                    Button map_detailBtn = (Button) viewContainer.findViewById(R.id.map_detailBtn);
                    //Log.d("TAG", "setting on click" + name);
                    String[] productInfo = (String[]) marker.getTag();
                    map_productName.setText(productInfo[0]);
                    map_productPrice.setText("$" + productInfo[1]);
                    // Log.d("TAG", map_productName.getText().toString());

                    markerInfoContainer.addView(viewContainer);

                    PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.showAtLocation(findViewById(R.id.map), Gravity.CENTER_HORIZONTAL, 0, 0);

                    map_detailBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), ProductActivity.class);
                            intent.putExtra("product_id", productInfo[2]);
                            intent.putExtra("action_taker", "owner");
                            startActivity(intent);
                        }
                    });

                    mainView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });

                    return false;
                }
            });

        }

    }


}