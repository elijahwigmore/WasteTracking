package com.wastetracking.wastetracking.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xcode on 2/23/18.
 */

public class DateValue extends RealmObject {

    public DateValue(){}

    public DateValue(String date, String rfidValue) {
        this.Date = date;
        this.RFIDValue = rfidValue;
    }

    public String getDate() {
        return this.Date;
    }

    public String getRFIDValue() {
        return this.RFIDValue;
    }

    @PrimaryKey
    private String Date;

    private String RFIDValue;
}
