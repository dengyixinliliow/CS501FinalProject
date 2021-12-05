package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter<Message> {

    Context context;

    public MessagesAdapter(Context context, List<Message> constacts_list) {
        super(context, 0, constacts_list);
        context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View row;
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_message_row, parent, false);
        }

        TextView message_product_id = (TextView) listItemView.findViewById(R.id.message_txtProductId);
        TextView message_type = (TextView) listItemView.findViewById(R.id.message_txtType);

        Message message = getItem(position);

        message_product_id.setText(message.getProduct_id());
        message_type.setText(message.getType());

        return listItemView;
    }
}
