package com.wastetracking.wastetracking;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.log.RealmLog;

/**
 * Created by xcode on 1/31/18.
 *
 * This is needed because Realm init must be here.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(Log.DEBUG);
        }
    }

}
