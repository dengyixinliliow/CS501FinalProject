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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class ContactsAdapter extends ArrayAdapter<Contact> {

    Context context;

    public ContactsAdapter(Context context, List<Contact> constacts_list) {
        super(context, 0, constacts_list);
        context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View row;
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_contact_row, parent, false);
        }

        TextView contact_name = (TextView) listItemView.findViewById(R.id.contact_name);

        Contact contact = getItem(position);

        contact_name.setText(contact.getReceiver_name());

        return listItemView;
    }
}
