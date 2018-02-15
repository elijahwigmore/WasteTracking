package com.wastetracking.wastetracking.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Elijah on 2/14/2018.
 */

public class Scan extends RealmObject {

    @Required
    private String RFIDValue;
    @Required
    private Date timestamp;

    public String getRFIDValue() { return RFIDValue; }
    public void setRFIDValue(String RFIDValue) { this.RFIDValue = RFIDValue; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

}
