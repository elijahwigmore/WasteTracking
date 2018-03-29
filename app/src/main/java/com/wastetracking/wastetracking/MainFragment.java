package com.wastetracking.wastetracking;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.maps.MapView;

import java.util.List;

/**
 * Created by xcode on 3/29/18.
 */

public class MainFragment extends Fragment{

    public static final String TAG = "MainFragment";

    View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        // Make sure this fragment is persisted
        this.setRetainInstance(true);

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.main_fragment, container, false);


        // Set up button listeners for all buttons

        View listButton = mRootView.findViewById(R.id.to_do_list_touch);

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnPage(0);
            }
        });

        View mapButton = mRootView.findViewById(R.id.map_overview_touch);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnPage(2);
            }
        });

        View phoneButton = mRootView.findViewById(R.id.phone_touch);

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent dialItent = new Intent(Intent.ACTION_DIAL, null);

                startActivity(dialItent);
            }
        });

        View messageButton = mRootView.findViewById(R.id.sms_touch);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));

                startActivity(sendIntent);
            }
        });


        return mRootView;
    }


    public void turnPage(int page) {
        try {
            ((MainActivity) getActivity()).setFragmentPage(page);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set OnClickListener!");
        }
    }

}
