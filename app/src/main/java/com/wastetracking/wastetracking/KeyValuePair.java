package com.wastetracking.wastetracking;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xcode on 1/31/18.
 */

public class KeyValuePair extends RealmObject {

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @PrimaryKey
    private String key;

    private String value;
}
