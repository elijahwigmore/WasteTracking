package com.wastetracking.wastetracking.Model;

/**
 * Created by Elijah on 3/8/2018.
 */

// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models

import io.realm.RealmObject;
import io.realm.annotations.Required;
import java.util.Date;

public class RFIDScan extends RealmObject {
    @Required
    private String RFIDValue;
    @Required
    private Date Timestamp;

    public String getRFIDValue() { return RFIDValue; }

    public void setRFIDValue(String RFIDValue) { this.RFIDValue = RFIDValue; }

    public Date getTimestamp() { return Timestamp; }

    public void setTimestamp(Date Timestamp) { this.Timestamp = Timestamp; }
}
