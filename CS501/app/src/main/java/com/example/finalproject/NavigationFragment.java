package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class NavigationFragment extends Fragment {

    private ImageButton navigation_btnSearch;
    private ImageButton navigation_btnCart;
    private ImageButton navigation_btnProfile;

    public NavigationFragment() {
        // Required empty public constructor
    }

    public interface NavigationFragmentListener {
        public void SwitchActivity(String page_name);
    }

    NavigationFragment.NavigationFragmentListener NF;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        NF = (NavigationFragmentListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        navigation_btnSearch = (ImageButton) view.findViewById(R.id.navigation_btnSearch);
        navigation_btnCart = (ImageButton) view.findViewById(R.id.navigation_btnCart);
        navigation_btnProfile = (ImageButton) view.findViewById(R.id.navigation_btnProfile);

        navigation_btnSearch.setOnClickListener(new BtnOnClickListener());
        navigation_btnCart.setOnClickListener(new BtnOnClickListener());
        navigation_btnProfile.setOnClickListener(new BtnOnClickListener());

        return view;
    }
    class BtnOnClickListener implements View.OnClickListener {
        @SuppressLint({"ResourceType", "NonConstantResourceId"})
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.navigation_btnSearch:
                    NF.SwitchActivity(getString(R.string.SEARCH_PAGE));
                    break;
                case R.id.navigation_btnCart:
                    NF.SwitchActivity(getString(R.string.CART_PAGE));
                    break;
                case R.id.navigation_btnProfile:
                    NF.SwitchActivity(getString(R.string.PROFILE_PAGE));
                    break;
            }
        }
    }

    public void setOrginActivity(String page_name, Context context) {
        if(page_name.equals(getString(R.string.SEARCH_PAGE))) {
            // Move to Search page
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);
        } else if(page_name.equals(getString(R.string.CART_PAGE))) {
            // Move to Cart page
            Intent intent = new Intent(context, CartActivity.class);
            startActivity(intent);
        } else {
            // Move to Profile page
            Intent intent = new Intent(context, ProfileActivity.class);
            startActivity(intent);
        }
    }
}