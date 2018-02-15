package com.wastetracking.wastetracking;

import android.app.Application;
import android.util.Log;

import com.wastetracking.wastetracking.model.MyMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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
        RealmConfiguration config = new RealmConfiguration.Builder()
                // .name("my_realm.realm")
                .schemaVersion(1)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(config);

        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(Log.DEBUG);
        }
    }

}
