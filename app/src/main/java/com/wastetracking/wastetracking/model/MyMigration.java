package com.wastetracking.wastetracking.model;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by Elijah on 2/14/2018.
 */

public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.create("Scan")
                    .addField("RFIDValue", String.class, FieldAttribute.REQUIRED)
                    .addField("Timestamp", Date.class, FieldAttribute.REQUIRED);
            oldVersion++;
        }
    }
}
