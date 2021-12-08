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

import com.bumptech.glide.Glide;
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

    // Map Attributes
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker mMarker;
    private PopupWindow mPopupWindow;

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
    // Gets coordinates (latitude, longitutde) from a String address
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

        // Fetch documents from the collection "products" from Firebase
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

                        // Get all the necessary information regarding a product (to be rendered in the popup window)
                        LatLng addressPt = getLocationFromAddress(getBaseContext(), String.valueOf(dataMap.get("product_address")));
                        String productName = String.valueOf(dataMap.get("product_name"));
                        String productPrice = String.valueOf(dataMap.get("product_price"));
                        String productID = String.valueOf(dataMap.get("product_id"));
                        String productImg = String.valueOf(dataMap.get("product_img_url"));

                        if (addressPt != null) {
                            latlngs.add(new LatLng(addressPt.latitude, addressPt.longitude));
                            String[] productInfo = {productName, productPrice, productID, productImg};
                            nameAndAddress.put(addressPt, productInfo);
                        }
                        // Log.d("TAG", latlngs.toString());
                    }
                    onMapReady(mMap);

                    //INSTANTIATE LOCATION MANAGER TO ENABLE TRACKING OF USER'S CURRENT LOCATION
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    // check if network provider is enabled
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        // write new onLocationlistener on request location updates method
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                // get latitude
                                double latitude = location.getLatitude();

                                // get longitude
                                double longitude = location.getLongitude();

                                // instantiate class, LatLng
                                LatLng latLng = new LatLng(latitude, longitude);

                                // instantiate the class, Geocoder --> to get a meaningful address
                                Geocoder geocoder = new Geocoder(getApplicationContext());
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("CURRENT LOCATION")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                            }
                        });
                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                // get latitude
                                double latitude = location.getLatitude();

                                // get longitude
                                double longitude = location.getLongitude();

                                // instantiate class, LatLng
                                LatLng latLng = new LatLng(latitude, longitude);

                                // instantiate the class, Geocoder --> to get a meaningful address
                                Geocoder geocoder = new Geocoder(getApplicationContext());
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("CURRENT LOCATION")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

                            }
                        });
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.0902, -95.7129)));
                    }
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
            String[] info = entry.getValue(); // Product Name, Product Price, Product ID, Product Image URL

            // Add marker to map
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

            // Add all the necessary product info and attach as a tag for each marker
            mMarker.setTag(info);

            // Add an onClickListener for each marker (will be done after all markers have been added)
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    // Create View Layout for Popup window for each marker + instantiate all the views
                    View mainView = getLayoutInflater().inflate(R.layout.marker_info_window, null);
                    ViewFlipper markerInfoContainer = (ViewFlipper) mainView.findViewById(R.id.markerInfoContainer);
                    View viewContainer = getLayoutInflater().inflate(R.layout.marker_info_layout, null);
                    TextView map_productName = (TextView) viewContainer.findViewById(R.id.map_productName);
                    TextView map_productPrice = (TextView) viewContainer.findViewById(R.id.map_productPrice);
                    Button map_detailBtn = (Button) viewContainer.findViewById(R.id.map_detailBtn);
                    ImageView map_productImage = (ImageView) viewContainer.findViewById(R.id.map_productImage);

                    String[] productInfo = (String[]) marker.getTag();
                    map_productName.setText(productInfo[0]);
                    map_productPrice.setText("$" + productInfo[1]);
                    Glide.with(getBaseContext()).load(productInfo[3]).into(map_productImage);  // get Image

                    markerInfoContainer.addView(viewContainer);

                    PopupWindow popupWindow = new PopupWindow(mainView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.showAtLocation(findViewById(R.id.map), Gravity.CENTER_HORIZONTAL, 0, 0);

                    // Direct to specific product detail pages whenever a detail button within a marker is clicked
                    map_detailBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), ProductActivity.class);
                            intent.putExtra("product_id", productInfo[2]);
                            intent.putExtra("action_taker", "owner");
                            startActivity(intent);
                        }
                    });

                    // Close popupwindow when container is clicked
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