package com.wastetracking.wastetracking;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Elijah on 4/1/2018.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private static final int COLLECTED_COLOUR = Color.argb(255, 100, 255, 100);
    private static final int MISSING_COLOUR = Color.WHITE;

    private ArrayList<String> mCollectedAddresses;

    public CustomAdapter(Context context, int resourceID, ArrayList<String> list) {
        super(context, resourceID , list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        setViewBackgroundColor(view);
        return view;
    }

    private void setViewBackgroundColor(View view) {
        TextView textView = (TextView) view;
        String text = textView.getText().toString();

        if (text != null && mCollectedAddresses.contains(text))
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
