package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class ProductsLVAdapter extends ArrayAdapter<Product> {

    private Context cont;
    private String product_id;
    private static final String PRODUCT_ID = "product_id";
    private static final String ACTION_TAKER = "action_taker";


    public ProductsLVAdapter(@NonNull Context context, List<Product> productsArrayList) {
        super(context, 0, productsArrayList);
        cont = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_products, parent, false);
        }

        ImageView product_img = (ImageView) listItemView.findViewById(R.id.product_img);
        TextView product_name = (TextView) listItemView.findViewById(R.id.product_name);
        TextView product_size = (TextView) listItemView.findViewById(R.id.product_size);
        TextView product_price = (TextView) listItemView.findViewById(R.id.product_price);
        Button product_btnDetail = (Button) listItemView.findViewById(R.id.product_btnDetail);

        Product product = (Product) getItem(position);

        product_name.setText(product.getProductName());
        product_size.setText(product.getProductSize());
        product_price.setText(product.getProductPrice());
        Glide.with(cont).load(product.getProductImgURL()).into(product_img);

        product_btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_id = product.getProductId();
                Intent intent = new Intent(cont, ProductActivity.class);
                intent.putExtra(PRODUCT_ID, product_id);
                intent.putExtra(ACTION_TAKER, "non-owner");
                cont.startActivity(intent);
                //pass id as the intent
            }
        });

        return listItemView;
    }
}
