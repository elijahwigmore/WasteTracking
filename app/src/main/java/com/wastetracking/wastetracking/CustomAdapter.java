package com.wastetracking.wastetracking;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Elijah on 4/1/2018.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private static final int COLLECTED_COLOUR = Color.argb(46, 100, 204, 113); // #2ecc71
    private static final int MISSING_COLOUR = Color.WHITE;

    private ArrayList<String> mAllAddresses;
    private ArrayList<String> mCollectedAddresses;

    public CustomAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.single_list_item, list);
        mAllAddresses = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.single_list_item, null, true);

        setText(rowView, position);
        setImage(rowView, position);

        setViewBackgroundColor(rowView, position);
        return rowView;
    }

    private void setText(View view, int position) {
        TextView textView = (TextView) view.findViewById(R.id.list_text);
        textView.setText(mAllAddresses.get(position));
    }

    private void setImage(View view, int position) {
        ImageView imageView = (ImageView) view.findViewById(R.id.list_image);
        if (mCollectedAddresses.contains(mAllAddresses.get(position)))
            imageView.setImageResource(R.drawable.ic_check_box_black_24dp);
        else
            imageView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
    }

    private void setViewBackgroundColor(View view, int position) {
        if (mCollectedAddresses.contains(mAllAddresses.get(position)))
            view.setBackgroundColor(COLLECTED_COLOUR);
        else
            view.setBackgroundColor(MISSING_COLOUR);
    }

    public void setCollectedAddresses(ArrayList<String> addresses) {
        mCollectedAddresses = addresses;
    }

    public boolean addCollectedAddress(String address) {
        if (mCollectedAddresses.contains(address))
            return false;

        mCollectedAddresses.add(address);
        return true;
    }

    public int getCollectedAddressCount() {
        return mCollectedAddresses.size();
    }
}
