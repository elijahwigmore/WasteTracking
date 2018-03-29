package com.wastetracking.wastetracking;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xcode on 3/29/18.
 */

public class DetailedViewFragment extends Fragment{

    public static final String TAG = "DViewFrag";

    View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        // Make sure this fragment is persisted
        this.setRetainInstance(true);

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.detailed_view_fragment, container, false);

        return mRootView;
    }
}
