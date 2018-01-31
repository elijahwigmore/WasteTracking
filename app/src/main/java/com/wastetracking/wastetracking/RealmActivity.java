package com.wastetracking.wastetracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

/**
 * Created by xcode on 1/31/18.
 */

public class RealmActivity extends AppCompatActivity{

    private static final String mRealmUrl = "realm://35.153.34.189:9080/~/test";
    private static final String TAG = "RealmActivity";

    private SyncUser mUser;
    private Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting RealmActivity");

    }

    @Override
    protected void onStart() {
        super.onStart();

        mUser = getLoggedInUser();

        if (mUser == null) {
            return;
        }

        // Create a Realm Configuration
        SyncConfiguration config = new SyncConfiguration.Builder(mUser, mRealmUrl)
                .build();

        mRealm = Realm.getInstance(config);

        if (mRealm != null){
            Log.d(TAG, "We got something!");
            Log.d(TAG, mRealm.toString());
            Log.d(TAG, mRealm.getPath());
        } else {
            Log.d(TAG, "We didn't get the Realm!");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        closeRealm();
    }

    private SyncUser getLoggedInUser() {
        SyncUser user = null;

        try {
            user = SyncUser.currentUser();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }

        if (user == null) {
            Log.d(TAG, "We didn't get a user!");
        }

        return user;
    }

    // Close Realm if it's not already closed.
    private void closeRealm() {
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }
}
